package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.entity.ICollisionAttackEntity;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;

/**
 * <h1>长矛弹射物基类</h1>
 * <p>
 * 子类应覆写{@link #updateMotion()} 方法实现自定义运动曲线，
 * 可选覆写{@link #getTrailParticle()} 提供拖尾粒子效果。
 */
public abstract class SpearProjectile extends AbstractHurtingProjectile implements ICollisionAttackEntity {
    // 可调参数
    public int lifetime = 40;
    public int pierceRemaining = 1;
    protected float attackDamageFactor = 1.0F;
    protected float baseAttackDamage = 0.0F;
    protected float knockBack = 0.0F;
    protected float baseKnockBack = 0.0F;
    protected CollisionProperties collisionProperties = new CollisionProperties(1, 1, 0.5F);
    protected SpearProjectileComponent projComponent;
    public final IAxisZRotate.Rotate rotate = new IAxisZRotate.Rotate();

    // 弹射物自身状态
    public int ticksAlive = 0;
    public Vec3 velocity = new Vec3(0, 0, 0);
    public Vec3 direction = new Vec3(0, 0, 0);
    public Vec3 initSpeed = new Vec3(0, 0, 0);
    public float gravity = 0.0F;

    protected ItemStack firedFromWeapon;
    protected LivingEntity target;

    // 数据同步
    public static final EntityDataAccessor<Vector3f> DATA_DIRECTION =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DATA_INIT_SPEED =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> DATA_INIT_GRAVITY =
            SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.FLOAT);

    public SpearProjectile(EntityType<? extends SpearProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        if (!level().isClientSide()) {
            this.direction = new Vec3(this.getRandom().nextFloat() - 0.5f,
                    this.getRandom().nextFloat() - 0.5f,
                    this.getRandom().nextFloat() - 0.5f);
            this.entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_INIT_SPEED, new Vector3f(0, 0, 0));
        this.entityData.define(DATA_INIT_GRAVITY, 0.0F);
        this.entityData.define(DATA_DIRECTION, new Vector3f());
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

    // ===== 配置注入 =====

    /**
     * 注入弹射物配置组件，初始化字段。
     */
    public void setProjComponent(SpearProjectileComponent projComponent, LivingEntity owner) {
        this.projComponent = projComponent;
        this.gravity = projComponent.gravity();
        this.lifetime = projComponent.existTicks();
        this.entityData.set(DATA_INIT_GRAVITY, gravity);
        this.pierceRemaining = projComponent.pierceCount().orElse(1);
        // 自动从持有者获取基础攻击伤害
        this.baseAttackDamage = (float) owner.getAttributeValue(LibAttributes.getAttackDamage());
    }

    // ===== 运动逻辑（子类覆写） =====

    /**
     * 更新当前速度向量。子类必须实现此方法以提供自定义运动曲线。
     */
    protected abstract void updateMotion();

    /**
     * 计算初始速度。默认返回direction.scale(speed)
     * 子类可覆写以实现不同的初始速度计算方式。
     */
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(speed);
    }

    // ===== 粒子 =====

    @Override
    @Nullable
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    // ===== 核心 Tick =====

    @Override
    public void tick() {
        super.tick();

        // 覆盖 AbstractHurtingProjectile.tick() 自动计算yRot/xRot
        // 始终使用发射时设定的 direction 作为朝向，避免因 deltaMovement 变化导致模型抖动
        if (direction.lengthSqr() > 0.01) {
            float yaw = (float) Mth.atan2(direction.x, direction.z) * Mth.RAD_TO_DEG;
            float horizontalDist = Mth.sqrt((float) (direction.x * direction.x + direction.z * direction.z));
            float pitch = (float) Mth.atan2(-direction.y, horizontalDist) * Mth.RAD_TO_DEG;
            this.setYRot(yaw);
            this.yRotO = yaw;
            this.setXRot(pitch);
            this.xRotO = pitch;
        }

        if (!level().isClientSide) {
            // 1. 更新运动（子类实现）
            updateMotion();

            // 2. 应用重力
            if (gravity != 0) {
                velocity = velocity.add(0, -gravity, 0);
            }

            // 3. 应用加速度
            if (projComponent != null && projComponent.acceleration() != 1.0f) {
                velocity = velocity.scale(projComponent.acceleration());
            }

            // 4. 应用速度
            setDeltaMovement(velocity);

            // 5. 追踪逻辑（如果有）
            if (projComponent != null && projComponent.trackType().isPresent()
                    && target != null && target.isAlive()) {
                Vec3 dir = target.position()
                        .add(0, target.getBoundingBox().getYsize() * 0.5, 0)
                        .subtract(position())
                        .normalize()
                        .scale(velocity.length());
                double angle = TEUtils.angleBetween(velocity, dir);
                setDeltaMovement(projComponent.trackType().get()
                        .calDeltaMovement(velocity, dir, angle));
                velocity = getDeltaMovement();
            }

            // 6. 移动实体
            setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);

            // 7. 碰撞检测
            doCollisionAttack(this::canHitEntity, this::doHurt);

            // 8. 超时销毁
            if (ticksAlive++ >= (projComponent != null ? projComponent.existTicks() : lifetime)) {
                discard();
            }
        } else {
            // 客户端：生成拖尾粒子
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
        return TEUtils.projectileCanHitEntityTest.test(this, target);
    }

    @Override
    public boolean shouldDoCollision() {
        return true;
    }

    // ===== 伤害计算（子类可覆写） =====

    /**
     * 计算伤害值。子类可覆写以实现自定义伤害公式。
     * 默认：基础攻击伤害× 组件伤害系数
     */
    protected float getDamage() {
        float factor = projComponent != null ? projComponent.damageFactor() : attackDamageFactor;
        return getBaseDamage() * factor;
    }

    /**
     * 应用击中特效。子类可覆写以自定义特效。
     */
    protected void applyHitEffect(LivingEntity owner, LivingEntity target) {
        if (projComponent != null) {
            projComponent.hitEffect().ifPresent(effect -> effect.applyAll(owner, target));
        }
    }

    /**
     * 处理穿透逻辑。减少剩余穿透次数，归零时销毁
     * 子类（如无限穿透的弹射物）可覆写为空实现。
     */
    protected void applyPenetration() {
        if (--pierceRemaining <= 0 && !level().isClientSide) {
            discard();
        }
    }

    /**
     * 造成伤害。编排子方法调用，子类可按需覆写 {@link #getDamage()} / {@link #applyHitEffect} / {@link #applyPenetration()}。
     */
    protected boolean doHurt(Entity target) {
        if (TEUtils.projectileCanHurtEntityTest.test(this, target)) {
            float damage = getDamage();
            DamageSource damageSource = damageSource();

            if (IAttackableProjectile.tryHit(target, damageSource)) {
                return true;
            }

            LivingEntity hurter;
            if (LibEntityUtils.tryFindBeImpacted(target) instanceof LivingEntity living) {
                hurter = living;
            } else {
                return false;
            }

            if (getOwner() instanceof LivingEntity owner) {
                applyHitEffect(owner, hurter);
            }

            if (target.hurt(damageSource, damage)) {
                float attackKnockBack = getBaseKnockBack() + knockBack;
                LibEntityUtils.knockBackA2B(this, hurter, attackKnockBack * 0.5, 0.2);
                applyPenetration();
            }
            return true;
        }
        return false;
    }

    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.SPEAR_PROJECTILE, this, getOwner());
    }

    // ===== 发射 =====

    /**
     * 发射弹射物：设置方向、速度，并立即同步 deltaMovement 和实体数据，
     * 确保首帧即可正确移动（无卡顿）。
     *
     * @param direction 发射方向（应为归一化向量）
     * @param speed     速度大小
     * @param knockBack 击退距离
     */
    // ===== 渲染元数据（子类覆写以实现一类一物品）=====

    /** 弹射物模型纹理，默认 null（无模型） */
    @Nullable
    public ResourceLocation getProjTexture() {return null;}

    /**
     * 弹射物模型层，默认null
     */
    @Nullable
    public ModelLayerLocation getModelLayer() {return null;}

    /**
     * 飞行轴自旋角度，默认 0
     */
    public float getSpinRotation(float partialTick) {
        return Mth.lerp(partialTick, rotate.old, rotate.neo);
    }

    /**
     * 自旋轴，默认是Z 轴。子类可覆写此方法以使用不同的旋转轴
     */
    public com.mojang.math.Axis getSpinAxis() {
        return com.mojang.math.Axis.ZP;
    }

    public void fire(Vec3 direction, float speed, float knockBack) {
        this.direction = direction;
        Vec3 initialVelocity = initVelocity(null, direction, speed);
        this.velocity = initialVelocity;
        this.initSpeed = initialVelocity;
        this.addKnockBack(knockBack);

        // 立即设置 deltaMovement 并同步到客户端，避免首帧卡顿
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

    // ===== 属性辅助方法 =====

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

    // ===== 不可攻击/不可拾取/免疫火焰 =====

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
    public double getDefaultGravity() {
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
