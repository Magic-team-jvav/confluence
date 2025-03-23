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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.util.VectorUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.entity.ai.ICollisionAttackEntity;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * 基础属性如伤害、击退、初始位置由弹幕容器设置，弹幕实体只定义运动、伤害公式、碰撞检测
 */
public abstract class SwordProjectile extends AbstractHurtingProjectile implements ICollisionAttackEntity<SwordProjectile> {

    // 可调参数
    public int TIME_EXISTENCE = 40;
    public int hitCount = 1;
    protected float attackDamage = 0.0F;
    protected float baseAttackDamage = 0;
    protected float criticalChance = 0.0F;
    protected float knockBack = 0.0F;
    protected float baseKnockBack = 0.0F;
    protected boolean canPenalize = false;
    protected CollisionProperties collisionProperties = new CollisionProperties(1, 1, 0);
    protected SwordProjectileComponent projComponent;

    protected ItemStack firedFromWeapon;

    public SwordProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        if(!level().isClientSide()){
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

    public void onSyncedDataUpdated(EntityDataAccessor<?> data){
        super.onSyncedDataUpdated(data);
        if(level().isClientSide) {
            if (data == DATA_INIT_SPEED) {
                this.initSpeed = new Vec3(this.entityData.get(DATA_INIT_SPEED));
                this.setDeltaMovement(initSpeed);
            }else if (data == DATA_INIT_GRAVITY) {
                this.gravity = this.entityData.get(DATA_INIT_GRAVITY);
            }else if (DATA_DIRECTION.equals(data)) {
                direction =new Vec3(this.entityData.get(DATA_DIRECTION));
                float yaw = (float) Math.atan2(direction.x, direction.z) * (180F / (float) Math.PI);
                this.setYRot(yaw);
                yRotO = yaw;
            }
        }
    }

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
        this.TIME_EXISTENCE = projComponent.existTicks();
        this.entityData.set(DATA_INIT_GRAVITY, gravity);
    }

    LivingEntity target;

    public void onAddedToLevel(){
        super.onAddedToLevel();
        var owner1 = getOwner();
        if(owner1 instanceof LivingEntity owner){
            AttributeInstance attributeInstance = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);

            if (attributeInstance != null) {

                this.knockBack += (float) attributeInstance.getValue();
            }
            attributeInstance = owner.getAttribute(TCAttributes.getRangedDamage());
            if (attributeInstance != null) {
                this.attackDamage += (float) attributeInstance.getValue();
            }
            if (TCAttributes.hasCustomAttribute(TCAttributes.CRIT_CHANCE)) return;
            attributeInstance = owner.getAttribute(TCAttributes.CRIT_CHANCE);
            if (attributeInstance != null) {
                this.criticalChance = (float) attributeInstance.getValue();
            }

            var entities = level().getEntities(this, getBoundingBox().inflate(50), e-> e instanceof LivingEntity living && living.isAlive() && e != owner1);
            entities.sort((a, b) -> (int) (a.distanceToSqr(this) - b.distanceToSqr(this)));
            for (Entity entity : entities) {
                if(entity instanceof LivingEntity living){
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
        if(projComponent != null){
            if(!level().isClientSide() && tickCount >= projComponent.existTicks())
                discard();
            this.applyGravity();
            if(target != null && target.isAlive()) {

                Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(this.position()).normalize();
                Vec3 motion = getDeltaMovement();
                double angle = TEUtils.angleBetween(motion, dir);

                if (projComponent.trackType().isPresent()) {
                    this.setDeltaMovement(projComponent.trackType().get().calDeltaMovement(motion, dir, angle));
                }
            }
        }
        if (!level().isClientSide &&tickCount >= TIME_EXISTENCE)
            discard();
//        this.applyGravity();
        doCollisionAttack(this::canHitEntity,
                this::doHurt);
        if (target != null) {
//            this.setDeltaMovement(
//                    VectorUtils.interpolateSimple(this.getDeltaMovement(), target.position().subtract(this.position()),
//                            0.5f, 0.1f, getDeltaMovement().length() * 0.5f,1, getDeltaMovement()));



//            this.setDeltaMovement(
//                        VectorUtils.interpolateBasis(motion, dir,
//                                d -> d * 0.1, d -> 0));

        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.canBeHitByProjectile() || target instanceof Villager) {
            return false;
        }
        Entity entity = this.getOwner();
        // 防止击中仆从
        if(
                entity != null && (
                        target instanceof TamableAnimal animal &&
                        entity instanceof LivingEntity living &&
                        animal.isOwnedBy(living)
                )
        ){
            return false;
        }

        if(entity == null || !entity.isPassengerOfSameVehicle(target)) {
            return true;
        }
        return target != entity;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (!level().isClientSide && entity instanceof LivingEntity living && getOwner() instanceof LivingEntity owner && owner != entity) {
            // 事件统一暴击判定 org.confluence.mod.common.event.subscriber.game.entity.LivingEntityEvents.livingDamage$Pre
//            if (random.nextFloat() < criticalChance) damage *= 1.5F;
//            doHurt(living);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if(!canPenalize && !level().isClientSide) discard();
    }


    public DamageSource damageSource(){
        if(getOwner() instanceof LivingEntity living)
            return damageSources().mobProjectile(this, living);
        else return damageSources().magic();
    }

    public CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    protected boolean doHurt(Entity living){
        if(living instanceof LivingEntity hurter) {
            float damage = getBaseDamage() * (attackDamage);
            if (getOwner() instanceof LivingEntity owner && projComponent != null)
                projComponent.hitEffect().ifPresent(effect -> {
                    effect.applyAll(owner, hurter);
                });
            if (hurter.hurt(damageSource(), damage)) {
                float attackKnockBack = getBaseKnockBack() + knockBack;
                VectorUtils.knockBackA2B(this, hurter, attackKnockBack * 0.5, 0.2);
                if (--hitCount == 0) {
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

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    public SwordProjectile setExistTime(int time){
        TIME_EXISTENCE = time;
        return this;
    }

    @Override
    public boolean shouldDoCollision() {
        return true;
    }
}
