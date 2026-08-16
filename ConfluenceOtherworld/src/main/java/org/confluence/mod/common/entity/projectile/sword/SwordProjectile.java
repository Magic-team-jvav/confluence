package org.confluence.mod.common.entity.projectile.sword;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.ProjectileCombatState;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.Objects;

/// 玩家近战武器剑气的统一实体核心。
///
/// <p>数值由服务端发射事务在生成时冻结为 {@link ProjectileCombatSnapshot}；实体只负责运动、
/// 原版移动向量 swept collision、目标过滤、命中预算和表现钩子。命中阶段不再读取玩家当前武器、
/// 前缀或攻击击退属性。</p>
///
/// <p>飞行剑气应沿用父类的原版移动向量碰撞；区域剑气可以接管扫描范围，但仍必须通过
/// {@link #doHurt(Entity)} 完成 UUID 记录、命中预算和击退结算，避免同一实体出现两套伤害规则。</p>
public abstract class SwordProjectile extends AbstractHurtingProjectile
        implements ProjectileCombatSnapshotCarrier {
    private static final String MOTION_TAG = "Motion";
    private static final String GRAVITY_TAG = "Gravity";
    private static final String DIRECTION_TAG = "Direction";
    private static final String INITIAL_SPEED_TAG = "InitialSpeed";
    private static final String COMPONENT_TAG = "Component";
    private static final String CAN_PENETRATE_BLOCK_TAG = "CanPenetrateBlock";

    public static final EntityDataAccessor<Vector3f> DATA_DIRECTION =
            SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> DATA_INIT_SPEED =
            SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Float> DATA_INIT_GRAVITY =
            SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> DATA_LIFETIME =
            SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.INT);

    // 可调参数
    public int lifetime = 40;
    /// 剩余成功命中预算；只在伤害实际生效后递减。
    public int hitCount = 1;
    protected float attackDamageFactor = 1.0F;
    protected float baseAttackDamage;
    protected float knockBack;
    protected float baseKnockBack;
    /// 原字段名保留兼容现有子类，语义是是否允许穿过方块。
    protected boolean canPenalize;
    protected @Nullable SwordProjectileComponent projComponent;
    protected @Nullable ItemStack firedFromWeapon;
    protected @Nullable LivingEntity target;

    protected float gravity;
    protected Vec3 initSpeed = Vec3.ZERO;
    public Vec3 direction = Vec3.ZERO;

    private final ProjectileCombatState combatState = new ProjectileCombatState();
    private int ownerResolutionTicks;

    protected SwordProjectile(EntityType<? extends SwordProjectile> entityType, Level level) {
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
        entityData.define(DATA_LIFETIME, lifetime);
    }

    /// 客户端只恢复渲染与运动数据，战斗判定始终留在服务端。
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (!level().isClientSide) {
            return;
        }
        if (data == DATA_INIT_SPEED) {
            initSpeed = new Vec3(entityData.get(DATA_INIT_SPEED));
            setDeltaMovement(initSpeed);
        } else if (data == DATA_INIT_GRAVITY) {
            gravity = entityData.get(DATA_INIT_GRAVITY);
        } else if (data == DATA_DIRECTION) {
            direction = new Vec3(entityData.get(DATA_DIRECTION));
            float yaw = (float) Mth.atan2(direction.x, direction.z) * Mth.RAD_TO_DEG;
            setYRot(yaw);
            yRotO = yaw;
        } else if (data == DATA_LIFETIME) {
            lifetime = entityData.get(DATA_LIFETIME);
        }
    }

    /// 保留原版 {@link AbstractHurtingProjectile#tick()} 的单一 swept collision 链。
    /// 追踪在本 tick 命中后调整下一 tick 的速度，保持现有剑气运动表现。
    @Override
    public void tick() {
        if (!level().isClientSide && combatState.discardIfInvalid(this)) {
            return;
        }
        if (waitForLoadedOwner()) {
            return;
        }
        if (!level().isClientSide && (hitCount == 0 || tickCount >= lifetime)) {
            discard();
            return;
        }

        super.tick();
        if (isRemoved()) {
            return;
        }

        if (projComponent != null) {
            applyGravity();
            if (target != null && target.isAlive() && projComponent.trackType().isPresent()) {
                Vec3 motion = getDeltaMovement();
                if (motion.lengthSqr() > 1.0E-8) {
                    Vec3 targetMotion = target.position()
                            .add(0.0, target.getBoundingBox().getYsize() * 0.5, 0.0)
                            .subtract(position())
                            .normalize()
                            .scale(motion.length());
                    double angle = LibMathUtils.angleBetween(motion, targetMotion);
                    setDeltaMovement(projComponent.trackType().get()
                            .calDeltaMovement(motion, targetMotion, angle));
                }
            }
        }
        if (!level().isClientSide && tickCount >= lifetime) {
            discard();
        }
    }

    protected boolean waitForLoadedOwner() {
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
        if (hitCount == 0 || !ProjectileHitRules.canHit(getOwner(), rawTarget)) {
            return false;
        }
        Entity targetEntity = ProjectileHitRules.impactedEntity(rawTarget);
        return combatState.canHit(targetEntity.getUUID(), allowsRepeatedHits());
    }

    /// 特殊持续弹幕可以覆写为 {@code true}，并自行提供命中冷却；普通剑气永久 UUID 去重。
    protected boolean allowsRepeatedHits() {
        return false;
    }

    /// 唯一实体命中入口，由原版移动向量碰撞调用。
    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            doHurt(result.getEntity());
        }
    }

    /// 执行一次服务端命中。只有伤害成功才应用效果、记录 UUID、击退并扣减预算。
    protected boolean doHurt(Entity rawTarget) {
        if (!canHitEntity(rawTarget)) {
            return false;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
        if (!impacted.hurt(damageSource(), resolvedBaseDamage())) {
            return false;
        }

        applyHitEffect(impacted);
        combatState.recordSuccessfulHit(impacted.getUUID());
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        float knockbackStrength = snapshot == null ? resolvedKnockback() * 0.5F : snapshot.knockback();
        ProjectileHitRules.applyResolvedKnockback(this, impacted, knockbackStrength, 0.2);
        if (hitCount > 0 && --hitCount == 0) {
            discard();
        }
        return true;
    }

    /// 子类命中特效钩子；基础实现暂不附加效果。
    protected void applyHitEffect(Entity target) {
        // 基础弹幕不附带命中特效；具体弹幕应覆写此方法，并只使用已冻结的弹幕状态。
    }

    protected float resolvedBaseDamage() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        return snapshot == null ? getBaseDamage() * attackDamageFactor : snapshot.baseDamage();
    }

    protected float resolvedKnockback() {
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        return snapshot == null ? getBaseKnockBack() + knockBack : snapshot.knockback();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!canPenalize && !level().isClientSide) {
            discard();
        }
    }

    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, getOwner());
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
        initSpeed = getDeltaMovement();
        direction = initSpeed.lengthSqr() < 1.0E-8 ? direction : initSpeed.normalize();
        entityData.set(DATA_INIT_SPEED, initSpeed.toVector3f());
        entityData.set(DATA_DIRECTION, direction.toVector3f());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity)) {
            return;
        }
        // 发射事务会在纯布局之后写入最终快照速度；此处捕获的才是保存和渲染应使用的初速度。
        Vec3 launchMotion = getDeltaMovement();
        if (combatState.snapshot() != null && launchMotion.lengthSqr() > 1.0E-8) {
            initSpeed = launchMotion;
            direction = launchMotion.normalize();
            entityData.set(DATA_INIT_SPEED, initSpeed.toVector3f());
            entityData.set(DATA_DIRECTION, direction.toVector3f());
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
        combatState.writeTo(entityTag, Math.max(lifetime - tickCount, 0), hitCount);
        if (!entityTag.contains(ProjectileCombatState.ROOT_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag combatTag = entityTag.getCompound(ProjectileCombatState.ROOT_TAG);
        CompoundTag motionTag = new CompoundTag();
        motionTag.putFloat(GRAVITY_TAG, gravity);
        motionTag.put(DIRECTION_TAG, writeVector(direction));
        motionTag.put(INITIAL_SPEED_TAG, writeVector(initSpeed));
        motionTag.putBoolean(CAN_PENETRATE_BLOCK_TAG, canPenalize);
        if (projComponent != null) {
            Tag componentTag = PortDataResultExtension.getOrThrow(
                    SwordProjectileComponent.CODEC.encodeStart(NbtOps.INSTANCE, projComponent),
                    message -> new IllegalStateException(
                            "Could not encode sword projectile component: " + message));
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
            requireTag(motionTag, CAN_PENETRATE_BLOCK_TAG, Tag.TAG_BYTE);

            gravity = finiteFloat(motionTag.getFloat(GRAVITY_TAG), "Sword projectile gravity");
            direction = readVector(motionTag.getCompound(DIRECTION_TAG), "Sword projectile direction");
            initSpeed = readVector(motionTag.getCompound(INITIAL_SPEED_TAG), "Sword projectile initial speed");
            canPenalize = motionTag.getBoolean(CAN_PENETRATE_BLOCK_TAG);
            if (motionTag.contains(COMPONENT_TAG)) {
                projComponent = PortDataResultExtension.getOrThrow(
                        SwordProjectileComponent.CODEC.parse(NbtOps.INSTANCE, motionTag.get(COMPONENT_TAG)),
                        message -> new IllegalArgumentException(
                                "Could not decode sword projectile component: " + message));
            } else {
                projComponent = null;
            }
            lifetime = budgets.remainingLifetime();
            hitCount = budgets.remainingHits();
            firedFromWeapon = Objects.requireNonNull(combatState.snapshot()).weapon();
            setDeltaMovement(initSpeed);
            entityData.set(DATA_INIT_GRAVITY, gravity);
            entityData.set(DATA_DIRECTION, direction.toVector3f());
            entityData.set(DATA_INIT_SPEED, initSpeed.toVector3f());
            entityData.set(DATA_LIFETIME, lifetime);
        } catch (RuntimeException exception) {
            combatState.invalidate(englishReason(exception, "Malformed sword projectile motion state"));
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
            throw new IllegalArgumentException("Missing or invalid sword projectile field: " + key);
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

    @Nullable
    public ItemStack getWeaponItem() {
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

    protected float getBaseDamage() {
        return baseAttackDamage;
    }

    protected float getBaseKnockBack() {
        return baseKnockBack;
    }

    public SwordProjectile addAttackDamage(float attackDamage) {
        baseAttackDamage += attackDamage;
        return this;
    }

    public SwordProjectile addKnockBack(float knockBack) {
        baseKnockBack += knockBack;
        return this;
    }

    public void setProjComponent(SwordProjectileComponent component) {
        projComponent = Objects.requireNonNull(component, "Projectile component must not be null");
        gravity = component.gravity();
        lifetime = component.existTicks();
        entityData.set(DATA_INIT_GRAVITY, gravity);
        entityData.set(DATA_LIFETIME, lifetime);
    }

    /// 由纯生成布局写入服务端视线方向；速度仍交给统一发射事务决定。
    public void setProjectileDirection(Vec3 launchDirection) {
        Objects.requireNonNull(launchDirection, "Projectile launch direction must not be null");
        if (launchDirection.lengthSqr() <= 1.0E-12) {
            throw new IllegalArgumentException("Projectile launch direction must not be zero");
        }
        direction = launchDirection.normalize();
        entityData.set(DATA_DIRECTION, direction.toVector3f());
    }

    public Vec3 getProjectileDirection() {
        return direction;
    }

    public SwordProjectile setExistTime(int time) {
        if (time < 0) {
            throw new IllegalArgumentException("Projectile lifetime must not be negative");
        }
        lifetime = time;
        entityData.set(DATA_LIFETIME, lifetime);
        return this;
    }

    @Override
    public double getDefaultGravity() {
        return gravity;
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

    @Override
    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NO_TRAIL.get();
    }
}
