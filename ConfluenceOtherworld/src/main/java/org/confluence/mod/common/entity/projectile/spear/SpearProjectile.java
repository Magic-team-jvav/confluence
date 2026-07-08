package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terraentity.api.entity.IAttackableProjectile;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.terraentity.api.entity.ICollisionAttackEntity;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.Optional;

/**
 * <h1>长矛弹射物基类</h1>
 * <p>
 * 子类应覆写 {@link #updateMotion()} 方法实现自定义运动曲线，
 * 可选覆写配置方法（{@link #getDamageFactor}、{@link #getAcceleration} 等）实现数据驱动。
 * 可选覆写 {@link #getTrailParticle()} 提供拖尾粒子效果。
 */
public abstract class SpearProjectile extends AbstractHurtingProjectile implements ICollisionAttackEntity {
    public int lifetime;
    public int pierceRemaining;
    protected float attackDamageFactor = 1.0F;
    protected float baseAttackDamage = 0.0F;
    protected float knockBack = 0.0F;
    protected float baseKnockBack = 0.0F;
    protected CollisionProperties collisionProperties = new CollisionProperties(1, 1, 0.5F);
    public final IAxisZRotate.Rotate rotate = new IAxisZRotate.Rotate();

    public int ticksAlive = 0;
    public Vec3 velocity = new Vec3(0, 0, 0);
    public Vec3 direction = new Vec3(0, 0, 0);
    public Vec3 initSpeed = new Vec3(0, 0, 0);
    public float gravity;

    protected ItemStack firedFromWeapon;
    protected LivingEntity target;

    public static final EntityDataAccessor<Vector3f> DATA_DIRECTION =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DATA_INIT_SPEED =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> DATA_INIT_GRAVITY =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.FLOAT);

    public SpearProjectile(EntityType<? extends SpearProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.gravity = getProjGravity();
        this.lifetime = getExistTicks();
        this.pierceRemaining = getPierceCount();
        this.entityData.set(DATA_INIT_GRAVITY, gravity);
        if (!level().isClientSide()) {
            this.direction = new Vec3(this.getRandom().nextFloat() - 0.5f,
                    this.getRandom().nextFloat() - 0.5f,
                    this.getRandom().nextFloat() - 0.5f);
            this.entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_INIT_SPEED, new Vector3f(0, 0, 0));
        builder.define(DATA_INIT_GRAVITY, 0.0F);
        builder.define(DATA_DIRECTION, new Vector3f());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (level().isClientSide) {
            if (data == DATA_INIT_SPEED) {
                this.initSpeed = new Vec3(entityData.get(DATA_INIT_SPEED));
                this.setDeltaMovement(initSpeed);
                this.velocity = initSpeed;
            } else if (data == DATA_INIT_GRAVITY) {
                this.gravity = entityData.get(DATA_INIT_GRAVITY);
            } else if (DATA_DIRECTION.equals(data)) {
                this.direction = new Vec3(entityData.get(DATA_DIRECTION));
                float yaw = (float) Mth.atan2(direction.x, direction.z) * Mth.RAD_TO_DEG;
                this.setYRot(yaw);
                yRotO = yaw;
            }
        }
    }

    // ===== 策略方法（子类覆写以自定义参数） =====

    protected float getDamageFactor() { return config.damageFactor; }
    public float getBaseSpeed() { return config.baseSpeed; }
    protected int getExistTicks() { return config.existTicks; }
    protected float getProjGravity() { return config.projGravity; }
    protected int getPierceCount() { return config.pierceCount; }
    protected float getAcceleration() { return config.acceleration; }
    protected Optional<ITrackType> getTrackType() { return config.trackType; }
    @Nullable
    protected IEffectStrategy getHitEffect() { return config.hitEffect; }

    /** 弹射物配置，子类构造时通过链式调用设置 */
    protected Config config = Config.DEFAULT;

    public static class Config {
        static final Config DEFAULT = new Config();

        float damageFactor = 1.0f;
        float baseSpeed = 1.0f;
        int existTicks = 40;
        float projGravity = 0.0f;
        int pierceCount = 1;
        float acceleration = 1.0f;
        Optional<ITrackType> trackType = Optional.empty();
        IEffectStrategy hitEffect;

        public Config damageFactor(float v) { this.damageFactor = v; return this; }
        public Config baseSpeed(float v) { this.baseSpeed = v; return this; }
        public Config existTicks(int v) { this.existTicks = v; return this; }
        public Config projGravity(float v) { this.projGravity = v; return this; }
        public Config pierceCount(int v) { this.pierceCount = v; return this; }
        public Config acceleration(float v) { this.acceleration = v; return this; }
        public Config trackType(ITrackType v) { this.trackType = Optional.ofNullable(v); return this; }
        public Config hitEffect(IEffectStrategy v) { this.hitEffect = v; return this; }
    }

    public void initFromOwner(LivingEntity owner) {
        this.baseAttackDamage = (float) owner.getAttributeValue(LibAttributes.getAttackDamage());
    }

    public void fireFromOwner(LivingEntity owner, Vec3 direction, float knockBack) {
        initFromOwner(owner);
        float speed = getBaseSpeed();
        AttributeInstance instance = owner.getAttribute(LibAttributes.getRangedVelocity());
        if (instance != null) speed *= (float) instance.getValue();
        fire(direction, speed, knockBack);
    }

    // ===== 运动逻辑 =====
    protected abstract void updateMotion();

    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(speed);
    }

    // ===== 粒子 =====

    @Override
    @Nullable
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        if (direction.lengthSqr() > 0.01) {
            float yaw = (float) Mth.atan2(direction.x, direction.z) * Mth.RAD_TO_DEG;
            float horizontalDist = Mth.sqrt((float)(direction.x * direction.x + direction.z * direction.z));
            float pitch = (float) Mth.atan2(-direction.y, horizontalDist) * Mth.RAD_TO_DEG;
            this.setYRot(yaw);
            this.yRotO = yaw;
            this.setXRot(pitch);
            this.xRotO = pitch;
        }

        if (!level().isClientSide) {
            updateMotion();

            if (gravity != 0) {
                velocity = velocity.add(0, -gravity, 0);
            }

            float accel = getAcceleration();
            if (accel != 1.0f) {
                velocity = velocity.scale(accel);
            }

            setDeltaMovement(velocity);

            Optional<ITrackType> track = getTrackType();
            if (track.isPresent() && target != null && target.isAlive()) {
                Vec3 dir = target.position()
                        .add(0, target.getBoundingBox().getYsize() * 0.5, 0)
                        .subtract(position())
                        .normalize()
                        .scale(velocity.length());
                double angle = Math.acos(velocity.dot(dir) / velocity.length() / dir.length());
                setDeltaMovement(track.get().calDeltaMovement(velocity, dir, angle));
                velocity = getDeltaMovement();
            }

            setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);

            doCollisionAttack(this::canHitEntity, this::doHurt);

            if (ticksAlive++ >= getExistTicks()) {
                discard();
            }
        } else {
            if (getTrailParticle() != null && random.nextInt(2) == 0) {
                level().addParticle(getTrailParticle(), getX(), getY(), getZ(), 0, 0, 0);
            }
            rotate.old = rotate.neo;
            rotate.neo += 1;
            if (rotate.neo > Mth.TWO_PI) {
                rotate.neo -= Mth.TWO_PI;
            }
        }
    }

    // ===== 碰撞与伤害 =====

    @Override
    protected boolean canHitEntity(Entity target) {
        if (pierceRemaining <= 0) {
            return false;
        }
        return LibUtils.canHitEntity(this, target);
    }

    @Override
    public boolean shouldDoCollision() {
        return true;
    }

    // ===== 伤害计算（子类可覆写） =====
    protected float getDamage() {
        return getBaseDamage() * getDamageFactor();
    }

    protected void applyHitEffect(LivingEntity owner, LivingEntity target) {
        IEffectStrategy effect = getHitEffect();
        if (effect != null) {
            effect.getEffect().accept(owner, target);
        }
    }

    protected void applyPenetration() {
        if (--pierceRemaining <= 0 && !level().isClientSide) {
            discard();
        }
    }

    protected boolean doHurt(Entity target) {
        if (LibUtils.canHitEntity(this, target)) {
            float damage = getDamage();
            DamageSource damageSource = damageSource();

            if (IAttackableProjectile.tryHit(target, damageSource)) {
                return true;
            }

            LivingEntity hurter;
            if (LibUtils.tryFindBeImpacted(target) instanceof LivingEntity living) {
                hurter = living;
            } else {
                return false;
            }

            if (getOwner() instanceof LivingEntity owner) {
                applyHitEffect(owner, hurter);
            }

            if (target.hurt(damageSource, damage)) {
                float attackKnockBack = getBaseKnockBack() + knockBack;
                VectorUtils.knockBackA2B(this, hurter, attackKnockBack * 0.5, 0.2);
                applyPenetration();
            }
            return true;
        }
        return false;
    }

    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.SPEAR_PROJECTILE, this, getOwner());
    }


    @Nullable
    public net.minecraft.resources.ResourceLocation getProjTexture() { return null; }

    @Nullable
    public net.minecraft.client.model.geom.ModelLayerLocation getModelLayer() { return null; }

    public float getSpinRotation(float partialTick) {
        return Mth.lerp(partialTick, rotate.old, rotate.neo);
    }

    public com.mojang.math.Axis getSpinAxis() {
        return com.mojang.math.Axis.ZP;
    }

    public void fire(Vec3 direction, float speed, float knockBack) {
        this.direction = direction;
        Vec3 initialVelocity = initVelocity(null, direction, speed);
        this.velocity = initialVelocity;
        this.initSpeed = initialVelocity;
        this.addKnockBack(knockBack);

        this.setDeltaMovement(initialVelocity);
        this.entityData.set(DATA_DIRECTION, direction.toVector3f());
        this.entityData.set(DATA_INIT_SPEED, initialVelocity.toVector3f());
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z,
                                   float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3 = shooter.getKnownMovement().scale(0.25f);
        this.setDeltaMovement(this.getDeltaMovement().add(
                vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
        this.velocity = getDeltaMovement();
        this.entityData.set(DATA_INIT_SPEED, getDeltaMovement().toVector3f());
    }

    // ===== 初始化 =====

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        var owner1 = getOwner();
        if (owner1 instanceof LivingEntity owner) {
            AttributeInstance instance = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);
            if (instance != null) {
                this.knockBack += (float) instance.getValue();
            }

            var entities = level().getEntities(this,
                    getBoundingBox().inflate(50),
                    e -> e instanceof LivingEntity living && living.isAlive() && e != owner1);
            entities.sort(Comparator.comparingDouble(a -> a.distanceToSqr(this)));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    target = living;
                    break;
                }
            }
        }
    }

    protected float getBaseDamage() {
        return baseAttackDamage;
    }

    protected float getBaseKnockBack() {
        return baseKnockBack;
    }

    public SpearProjectile addAttackDamage(float attackDamage) {
        this.baseAttackDamage += attackDamage;
        return this;
    }

    public SpearProjectile addKnockBack(float knockBack) {
        this.baseKnockBack += knockBack;
        return this;
    }

    @Nullable
    public ItemStack getWeaponItem() {
        return firedFromWeapon;
    }

    public void setWeapon(ItemStack weapon) {
        firedFromWeapon = weapon;
    }

    public SpearProjectile setExistTime(int time) {
        lifetime = time;
        return this;
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
        return 1;
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    @Override
    public CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    @Override
    protected void onHitBlock(net.minecraft.world.phys.BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
