package org.confluence.mod.common.entity.projectile.sword;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.api.entity.IAttackableProjectile;
import org.confluence.terraentity.api.entity.ICollisionAttackEntity;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;

/**
 * 基础属性如伤害、击退、初始位置由弹幕容器设置，弹幕实体只定义运动、伤害公式、碰撞检测
 */
public abstract class SwordProjectile extends AbstractHurtingProjectile implements ICollisionAttackEntity {

    // 可调参数
    public int lifetime = 40;
    public int hitCount = 1;
    protected float attackDamageFactor = 1F;
    protected float baseAttackDamage = 0;
    protected float criticalChance = 0.0F;
    protected float knockBack = 0.0F;
    protected float baseKnockBack = 0.0F;
    protected boolean canPenalize = false;
    protected CollisionProperties collisionProperties = new CollisionProperties(1, 1, 0.5F);
    protected SwordProjectileComponent projComponent;

    protected ItemStack firedFromWeapon;

    public SwordProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        if (!level().isClientSide()) {
            direction = new Vec3(this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f);
//            direction = new Vec3(1,0,0);
            this.entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
    }

    // 数据同步
    float gravity = 0;
    Vec3 initSpeed = new Vec3(0, 0, 0);
    public Vec3 direction = new Vec3(0, 0, 0);
    public static final EntityDataAccessor<Vector3f> DATA_DIRECTION = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> DATA_INIT_SPEED = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Float> DATA_INIT_GRAVITY = SynchedEntityData.defineId(SwordProjectile.class, EntityDataSerializers.FLOAT);

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (level().isClientSide) {
            if (data == DATA_INIT_SPEED) {
                this.initSpeed = new Vec3(this.entityData.get(DATA_INIT_SPEED));
                this.setDeltaMovement(initSpeed);
            } else if (data == DATA_INIT_GRAVITY) {
                this.gravity = this.entityData.get(DATA_INIT_GRAVITY);
            } else if (DATA_DIRECTION.equals(data)) {
                direction = new Vec3(this.entityData.get(DATA_DIRECTION));
                float yaw = (float) Math.atan2(direction.x, direction.z) * (180F / (float) Math.PI);
                this.setYRot(yaw);
                yRotO = yaw;
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_INIT_SPEED, new Vector3f(0, 0, 0));
        builder.define(DATA_INIT_GRAVITY, 0.0F);
        builder.define(DATA_DIRECTION, new Vector3f());
    }

    @Override
    @Nullable
    public ItemStack getWeaponItem() {
        return firedFromWeapon;
    }

    public void setWeapon(ItemStack weapon) {
        firedFromWeapon = weapon;
    }

    protected float getBaseDamage() {
        return baseAttackDamage;
    }

    protected float getBaseKnockBack() {
        return baseKnockBack;
    }

    public SwordProjectile addAttackDamage(float attackDamage) {
        this.baseAttackDamage += attackDamage;
        return this;
    }

    public SwordProjectile addKnockBack(float knockBack) {
        this.baseKnockBack += knockBack;
        return this;
    }

    public void setProjComponent(SwordProjectileComponent projComponent) {
        this.projComponent = projComponent;
        this.gravity = projComponent.gravity();
        this.lifetime = projComponent.existTicks();
        this.entityData.set(DATA_INIT_GRAVITY, gravity);
    }

    LivingEntity target;

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        var owner1 = getOwner();
        if (owner1 instanceof LivingEntity owner) {
            AttributeInstance attributeInstance = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);

            if (attributeInstance != null) {

                this.knockBack += (float) attributeInstance.getValue();
            }
            if (TCAttributes.hasCustomAttribute(TCAttributes.CRIT_CHANCE)) return;
            attributeInstance = owner.getAttribute(TCAttributes.CRIT_CHANCE);
            if (attributeInstance != null) {
                this.criticalChance = (float) attributeInstance.getValue();
            }

            var entities = level().getEntities(this, getBoundingBox().inflate(50), e -> e instanceof LivingEntity living && living.isAlive() && e != owner1);
            entities.sort(Comparator.comparingDouble(a -> a.distanceToSqr(this)));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    target = living;
                    break;
                }
            }
        }

    }

    @Override
    protected double getDefaultGravity() {
        return gravity;
    }

    @Override
    public void tick() {
        super.tick();
        if (projComponent != null) {
            if (!level().isClientSide() && tickCount >= projComponent.existTicks())
                discard();
            this.applyGravity();
            if (target != null && target.isAlive()) {

                Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(this.position()).normalize();
                Vec3 motion = getDeltaMovement();
                double angle = TEUtils.angleBetween(motion, dir);

                if (projComponent.trackType().isPresent()) {
                    this.setDeltaMovement(projComponent.trackType().get().calDeltaMovement(motion, dir, angle));
                }
            }
        }
        if (!level().isClientSide && tickCount >= lifetime)
            discard();
//        this.applyGravity();
        doCollisionAttack(this::canHitEntity,
                this::doHurt);

    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (hitCount <= 0) {
            return false;
        }

        return TEUtils.projectileCanHitEntityTest.test(this, target);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
//        Entity entity = entityHitResult.getEntity();
//        if (!level().isClientSide && entity instanceof LivingEntity living && getOwner() instanceof LivingEntity owner && owner != entity) {
        // 事件统一暴击判定 org.confluence.mod.common.event.game.entity.LivingEntityEvents.livingDamage$Pre
//            if (random.nextFloat() < criticalChance) damage *= 1.5F;
//            doHurt(living);
//        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!canPenalize && !level().isClientSide) discard();
    }

    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, getOwner());
    }

    @Override
    public CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    protected boolean doHurt(Entity target) {
        if (TEUtils.projectileCanHurtEntityTest.test(this, target)) {
            float damage = getBaseDamage() * (attackDamageFactor);
            DamageSource damageSource = damageSource();

            if (IAttackableProjectile.tryHit(target, damageSource)) {
                return true;
            }

            Entity victim = LibUtils.getOwner(target);
            LivingEntity hurter;
            if (victim instanceof LivingEntity living) {
                hurter = living;
            } else {
                return false;
            }

            if (getOwner() instanceof LivingEntity owner && projComponent != null)
                projComponent.hitEffect().ifPresent(effect -> {
                    effect.applyAll(owner, hurter);
                });

            if (target.hurt(damageSource, damage)) {
                float attackKnockBack = getBaseKnockBack() + knockBack;
                VectorUtils.knockBackA2B(this, hurter, attackKnockBack * 0.5, 0.2);

                if (--hitCount <= 0 && !level().isClientSide) {
                    discard();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3 = shooter.getKnownMovement().scale(0.25f);
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
        this.entityData.set(DATA_INIT_SPEED, getDeltaMovement().toVector3f());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }


    @Override//火焰效果
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override//空气阻力
    protected float getInertia() {
        return 1;
    }

    @Override
    @Nullable
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    public SwordProjectile setExistTime(int time) {
        lifetime = time;
        return this;
    }

    @Override
    public boolean shouldDoCollision() {
        return true;
    }
}
