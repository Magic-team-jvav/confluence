package org.confluence.mod.common.entity.projectile.spear;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.ProjectileCombatState;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.Objects;

/// 长矛武器衍生弹幕的统一实体核心。
///
/// <p>子类只实现 {@link #updateMotion()} 和必要表现。服务端先计算本 tick 速度，再交给原版
/// {@link AbstractHurtingProjectile} 对完整移动向量执行一次 swept collision，避免旧实现先移动一次、
/// 再手动移动一次却只检测第一段路径的问题。</p>
///
/// <p>发射事务安装快照后，伤害、击退、暴击与穿甲均来自冻结数据；换手、前缀变化或玩家属性变化
/// 不会影响飞行中的长矛弹幕。</p>
public abstract class SpearProjectile extends AbstractHurtingProjectile
        implements ProjectileCombatSnapshotCarrier {
    private static final String MOTION_TAG = "Motion";
    private static final String GRAVITY_TAG = "Gravity";
    private static final String DIRECTION_TAG = "Direction";
    private static final String INITIAL_SPEED_TAG = "InitialSpeed";
    private static final String VELOCITY_TAG = "Velocity";
    private static final String COMPONENT_TAG = "Component";

    public static final EntityDataAccessor<Vector3f> DATA_DIRECTION =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DATA_INIT_SPEED =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> DATA_INIT_GRAVITY =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.FLOAT);

    public int lifetime = 40;
    /// {@code -1} 表示不限；正数只在成功伤害后递减。
    public int pierceRemaining = 1;
    protected float attackDamageFactor = 1.0F;
    protected float baseAttackDamage;
    protected float knockBack;
    protected float baseKnockBack;
    protected @Nullable SpearProjectileComponent projComponent;
    public final IAxisZRotate.Rotate rotate = new IAxisZRotate.Rotate();

    public int ticksAlive;
    public Vec3 velocity = Vec3.ZERO;
    public Vec3 direction = Vec3.ZERO;
    public Vec3 initSpeed = Vec3.ZERO;
    public float gravity;

    protected @Nullable ItemStack firedFromWeapon;
    protected @Nullable LivingEntity target;

    private final ProjectileCombatState combatState = new ProjectileCombatState();
    private int ownerResolutionTicks;

    protected SpearProjectile(EntityType<? extends SpearProjectile> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            direction = new Vec3(
                    getRandom().nextFloat() - 0.5F,
                    getRandom().nextFloat() - 0.5F,
                    getRandom().nextFloat() - 0.5F);
            entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_INIT_SPEED, new Vector3f());
        entityData.define(DATA_INIT_GRAVITY, 0.0F);
        entityData.define(DATA_DIRECTION, new Vector3f());
    }

    /// 客户端同步只服务运动和渲染，不参与战斗数值判定。
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (!level().isClientSide) {
            return;
        }
        if (data == DATA_INIT_SPEED) {
            initSpeed = new Vec3(entityData.get(DATA_INIT_SPEED));
            velocity = initSpeed;
            setDeltaMovement(initSpeed);
        } else if (data == DATA_INIT_GRAVITY) {
            gravity = entityData.get(DATA_INIT_GRAVITY);
        } else if (data == DATA_DIRECTION) {
            direction = new Vec3(entityData.get(DATA_DIRECTION));
            updateModelRotation();
        }
    }

    /// 注入旧组件声明。当前武器族迁移完成前，尚未走统一事务的旧生成器仍在这里于“生成时”读取一次
    /// 所有者基础伤害；命中阶段不会再读取。统一事务安装快照后快照始终具有最高优先级。
    public void setProjComponent(SpearProjectileComponent component, @Nullable LivingEntity owner) {
        projComponent = Objects.requireNonNull(component, "Projectile component must not be null");
        gravity = component.gravity();
        lifetime = component.existTicks();
        pierceRemaining = component.pierceCount().orElse(1);
        entityData.set(DATA_INIT_GRAVITY, gravity);
        if (combatState.snapshot() == null && owner != null) {
            baseAttackDamage = (float) owner.getAttributeValue(LibAttributes.getAttackDamage());
        }
    }

    /// 子类更新 {@link #velocity}，不得在这里直接移动实体或另做碰撞扫描。
    protected abstract void updateMotion();

    /**
     * 计算初始速度。默认返回direction.scale(speed)
     * 子类可覆写以实现不同的初始速度计算方式。
     */
    protected Vec3 initVelocity(@Nullable LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(speed);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && combatState.discardIfInvalid(this)) {
            return;
        }
        if (waitForLoadedOwner()) {
            return;
        }
        if (!level().isClientSide && (pierceRemaining == 0 || ticksAlive >= lifetime)) {
            discard();
            return;
        }

        if (!level().isClientSide) {
            prepareServerMotion();
        }
        super.tick();
        if (isRemoved()) {
            return;
        }

        updateModelRotation();
        if (!level().isClientSide) {
            velocity = getDeltaMovement();
            if (++ticksAlive >= lifetime) {
                discard();
            }
        } else {
            ParticleOptions trail = getTrailParticle();
            if (trail != null && random.nextInt(2) == 0) {
                level().addParticle(trail, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            }
            rotate.old = rotate.neo;
            rotate.neo += 1.0F;
            if (rotate.neo > Mth.TWO_PI) {
                rotate.neo -= Mth.TWO_PI;
            }
        }
    }

    private void prepareServerMotion() {
        updateMotion();
        if (gravity != 0.0F) {
            velocity = velocity.add(0.0, -gravity, 0.0);
        }
        if (projComponent != null && projComponent.acceleration() != 1.0F) {
            velocity = velocity.scale(projComponent.acceleration());
        }
        if (projComponent != null && projComponent.trackType().isPresent()
                && target != null && target.isAlive() && velocity.lengthSqr() > 1.0E-8) {
            Vec3 targetMotion = target.position()
                    .add(0.0, target.getBoundingBox().getYsize() * 0.5, 0.0)
                    .subtract(position())
                    .normalize()
                    .scale(velocity.length());
            double angle = LibMathUtils.angleBetween(velocity, targetMotion);
            velocity = projComponent.trackType().get()
                    .calDeltaMovement(velocity, targetMotion, angle);
        }
        setDeltaMovement(velocity);
    }

    private void updateModelRotation() {
        if (direction.lengthSqr() <= 0.01) {
            return;
        }
        float yaw = (float) Mth.atan2(direction.x, direction.z) * Mth.RAD_TO_DEG;
        float horizontal = Mth.sqrt((float) (direction.x * direction.x + direction.z * direction.z));
        float pitch = (float) Mth.atan2(-direction.y, horizontal) * Mth.RAD_TO_DEG;
        setYRot(yaw);
        yRotO = yaw;
        setXRot(pitch);
        xRotO = pitch;
    }

    private boolean waitForLoadedOwner() {
        if (level().isClientSide || !combatState.wasLoadedFromTag()
                || combatState.snapshot() == null || getOwner() != null) {
            return false;
        }
        if (ownerResolutionTicks++ == 0) {
            return true;
        }
        combatState.invalidate("Projectile owner could not be resolved after loading");
        combatState.discardIfInvalid(this);
        return true;
    }

    @Override
    protected boolean canHitEntity(Entity rawTarget) {
        if (pierceRemaining == 0 || !ProjectileHitRules.canHit(getOwner(), rawTarget)) {
            return false;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
        return combatState.canHit(impacted.getUUID(), allowsRepeatedHits());
    }

    /// 持续云雾类子弹幕可开启重复命中，并必须自行实现明确的 tick 冷却。
    protected boolean allowsRepeatedHits() {
        return false;
    }

    /// 唯一实体命中入口，由原版 swept collision 调用。
    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            doHurt(result.getEntity());
        }
    }

    protected float getDamage() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        if (snapshot != null) {
            return snapshot.baseDamage();
        }
        float factor = projComponent == null ? attackDamageFactor : projComponent.damageFactor();
        return getBaseDamage() * factor;
    }

    /**
     * 应用击中特效。子类可覆写以自定义特效。
     */
    protected void applyHitEffect(LivingEntity owner, Entity target) {
        // 基础长矛不附带命中特效；具体长矛应覆写此方法，并沿用已确认的命中结果。
    }

    /// 只在伤害成功后扣减穿透预算。
    protected void applyPenetration() {
        if (pierceRemaining > 0 && --pierceRemaining == 0 && !level().isClientSide) {
            discard();
        }
    }

    /**
     * 造成伤害。编排子方法调用，子类可按需覆写 {@link #getDamage()} / {@link #applyHitEffect} / {@link #applyPenetration()}。
     */
    protected boolean doHurt(Entity rawTarget) {
        if (!canHitEntity(rawTarget)) {
            return false;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
        if (!impacted.hurt(damageSource(), getDamage())) {
            return false;
        }
        if (getOwner() instanceof LivingEntity owner) {
            applyHitEffect(owner, impacted);
        }
        combatState.recordSuccessfulHit(impacted.getUUID());
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        float knockbackStrength = snapshot == null ? resolvedKnockback() * 0.5F : snapshot.knockback();
        ProjectileHitRules.applyResolvedKnockback(this, impacted, knockbackStrength, 0.2);
        applyPenetration();
        return true;
    }

    protected float resolvedKnockback() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        return snapshot == null ? getBaseKnockBack() + knockBack : snapshot.knockback();
    }

    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.SPEAR_PROJECTILE, this, getOwner());
    }

    public void fire(Vec3 direction, float speed, float knockBack) {
        Objects.requireNonNull(direction, "Projectile direction must not be null");
        if (!Double.isFinite(direction.x) || !Double.isFinite(direction.y) || !Double.isFinite(direction.z)) {
            throw new IllegalArgumentException("Projectile direction must contain finite coordinates");
        }
        this.direction = direction;
        Vec3 initialVelocity = initVelocity(null, direction, speed);
        velocity = initialVelocity;
        initSpeed = initialVelocity;
        addKnockBack(knockBack);
        setDeltaMovement(initialVelocity);
        entityData.set(DATA_DIRECTION, direction.toVector3f());
        entityData.set(DATA_INIT_SPEED, initialVelocity.toVector3f());
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z,
                                  float velocity, float inaccuracy) {
        float horizontal = Mth.cos(x * Mth.DEG_TO_RAD);
        float motionX = -Mth.sin(y * Mth.DEG_TO_RAD) * horizontal;
        float motionY = -Mth.sin((x + z) * Mth.DEG_TO_RAD);
        float motionZ = Mth.cos(y * Mth.DEG_TO_RAD) * horizontal;
        shoot(motionX, motionY, motionZ, velocity, inaccuracy);
        Vec3 shooterMotion = shooter.getKnownMovement().scale(0.25F);
        setDeltaMovement(getDeltaMovement().add(
                shooterMotion.x, shooter.onGround() ? 0.0 : shooterMotion.y, shooterMotion.z));
        this.velocity = getDeltaMovement();
        initSpeed = this.velocity;
        direction = this.velocity.lengthSqr() < 1.0E-8 ? direction : this.velocity.normalize();
        entityData.set(DATA_INIT_SPEED, this.velocity.toVector3f());
        entityData.set(DATA_DIRECTION, direction.toVector3f());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity)) {
            return;
        }
        target = level().getEntities(this, getBoundingBox().inflate(50.0),
                        entity -> entity instanceof LivingEntity living && living.isAlive()
                                && ProjectileHitRules.canHit(owner, entity))
                .stream()
                .map(LivingEntity.class::cast)
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(this)))
                .orElse(null);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag entityTag) {
        super.addAdditionalSaveData(entityTag);
        combatState.writeTo(entityTag, Math.max(lifetime - ticksAlive, 0), pierceRemaining);
        if (!entityTag.contains(ProjectileCombatState.ROOT_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag combatTag = entityTag.getCompound(ProjectileCombatState.ROOT_TAG);
        CompoundTag motionTag = new CompoundTag();
        motionTag.putFloat(GRAVITY_TAG, gravity);
        motionTag.put(DIRECTION_TAG, writeVector(direction));
        motionTag.put(INITIAL_SPEED_TAG, writeVector(initSpeed));
        motionTag.put(VELOCITY_TAG, writeVector(velocity));
        if (projComponent != null) {
            Tag componentTag = PortDataResultExtension.getOrThrow(
                    SpearProjectileComponent.CODEC.encodeStart(NbtOps.INSTANCE, projComponent),
                    message -> new IllegalStateException(
                            "Could not encode spear projectile component: " + message));
            motionTag.put(COMPONENT_TAG, componentTag);
        }
        combatTag.put(MOTION_TAG, motionTag);
        entityTag.put(ProjectileCombatState.ROOT_TAG, combatTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag entityTag) {
        super.readAdditionalSaveData(entityTag);
        ProjectileCombatState.RestoredBudgets budgets = combatState.readFrom(entityTag);
        if (combatState.isInvalid()) {
            return;
        }
        try {
            CompoundTag combatTag = entityTag.getCompound(ProjectileCombatState.ROOT_TAG);
            requireTag(combatTag, MOTION_TAG, Tag.TAG_COMPOUND);
            CompoundTag motionTag = combatTag.getCompound(MOTION_TAG);
            requireTag(motionTag, GRAVITY_TAG, Tag.TAG_ANY_NUMERIC);
            requireTag(motionTag, DIRECTION_TAG, Tag.TAG_COMPOUND);
            requireTag(motionTag, INITIAL_SPEED_TAG, Tag.TAG_COMPOUND);
            requireTag(motionTag, VELOCITY_TAG, Tag.TAG_COMPOUND);

            gravity = finiteFloat(motionTag.getFloat(GRAVITY_TAG), "Spear projectile gravity");
            direction = readVector(motionTag.getCompound(DIRECTION_TAG), "Spear projectile direction");
            initSpeed = readVector(motionTag.getCompound(INITIAL_SPEED_TAG), "Spear projectile initial speed");
            velocity = readVector(motionTag.getCompound(VELOCITY_TAG), "Spear projectile velocity");
            if (motionTag.contains(COMPONENT_TAG)) {
                projComponent = PortDataResultExtension.getOrThrow(
                        SpearProjectileComponent.CODEC.parse(NbtOps.INSTANCE, motionTag.get(COMPONENT_TAG)),
                        message -> new IllegalArgumentException(
                                "Could not decode spear projectile component: " + message));
            } else {
                projComponent = null;
            }
            lifetime = budgets.remainingLifetime();
            pierceRemaining = budgets.remainingHits();
            ticksAlive = 0;
            firedFromWeapon = Objects.requireNonNull(combatState.snapshot()).weapon();
            setDeltaMovement(velocity);
            entityData.set(DATA_INIT_GRAVITY, gravity);
            entityData.set(DATA_DIRECTION, direction.toVector3f());
            entityData.set(DATA_INIT_SPEED, initSpeed.toVector3f());
        } catch (RuntimeException exception) {
            combatState.invalidate(englishReason(exception, "Malformed spear projectile motion state"));
        }
    }

    private static CompoundTag writeVector(Vec3 vector) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("X", vector.x);
        tag.putDouble("Y", vector.y);
        tag.putDouble("Z", vector.z);
        return tag;
    }

    private static Vec3 readVector(CompoundTag tag, String fieldName) {
        requireTag(tag, "X", Tag.TAG_ANY_NUMERIC);
        requireTag(tag, "Y", Tag.TAG_ANY_NUMERIC);
        requireTag(tag, "Z", Tag.TAG_ANY_NUMERIC);
        double x = tag.getDouble("X");
        double y = tag.getDouble("Y");
        double z = tag.getDouble("Z");
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException(fieldName + " must contain finite coordinates");
        }
        return new Vec3(x, y, z);
    }

    private static float finiteFloat(float value, String fieldName) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " must be finite");
        }
        return value;
    }

    private static void requireTag(CompoundTag tag, String key, int type) {
        if (!tag.contains(key, type)) {
            throw new IllegalArgumentException("Missing or invalid spear projectile field: " + key);
        }
    }

    private static String englishReason(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message != null && !message.isBlank()
                && message.chars().allMatch(character -> character < 128) ? message : fallback;
    }

    @Override
    public @Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return combatState.snapshot();
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        combatState.installSnapshot(snapshot);
        baseAttackDamage = snapshot.baseDamage();
        firedFromWeapon = snapshot.weapon();
    }

    protected final ProjectileCombatState combatState() {
        return combatState;
    }

    protected float getBaseDamage() {
        return baseAttackDamage;
    }

    protected float getBaseKnockBack() {
        return baseKnockBack;
    }

    public SpearProjectile addAttackDamage(float attackDamage) {
        baseAttackDamage += attackDamage;
        return this;
    }

    public SpearProjectile addKnockBack(float knockBack) {
        baseKnockBack += knockBack;
        return this;
    }

    public @Nullable ItemStack getWeaponItem() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        if (snapshot != null) {
            return snapshot.weapon();
        }
        return firedFromWeapon == null ? null : firedFromWeapon.copy();
    }

    public void setWeapon(ItemStack weapon) {
        Objects.requireNonNull(weapon, "Weapon must not be null");
        firedFromWeapon = weapon.copyWithCount(1);
    }

    public SpearProjectile setExistTime(int time) {
        if (time < 0) {
            throw new IllegalArgumentException("Projectile lifetime must not be negative");
        }
        lifetime = time;
        return this;
    }

    @Nullable
    public ResourceLocation getProjTexture() {
        return null;
    }

    public float getSpinRotation(float partialTick) {
        return Mth.lerp(partialTick, rotate.old, rotate.neo);
    }

    public com.mojang.math.Axis getSpinAxis() {
        return com.mojang.math.Axis.ZP;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NO_TRAIL.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    /// 重力由 {@link #prepareServerMotion()} 精确应用一次，阻止原版再次叠加。
    @Override
    public double getDefaultGravity() {
        return 0.0;
    }
}
