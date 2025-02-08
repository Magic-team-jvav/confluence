package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.jetbrains.annotations.Nullable;

/**
 * 基础属性如伤害、击退、初始位置由弹幕容器设置，弹幕实体只定义运动、伤害公式、碰撞检测
 */
public abstract class SwordProjectile extends AbstractHurtingProjectile {
    public int TIME_EXISTENCE = 40;
    protected float attackDamage = 0.0F;
    protected float baseAttackDamage = 0;
    protected float criticalChance = 0.0F;
    protected float knockBack = 0.0F;
    protected float baseKnockBack = 0.0F;
    protected ItemStack firedFromWeapon;
    public int hitCount = 1;

    public SwordProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    @Override
    @Nullable
    public ItemStack getWeaponItem(){
        return firedFromWeapon;
    }

    public void setWeapon(ItemStack weapon){
        firedFromWeapon = weapon;
    }

    protected float getBaseDamage(){
        return baseAttackDamage;
    }

    protected float getBaseKnockBack(){
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
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide &&tickCount >= TIME_EXISTENCE) discard();
        tickAttack();
    }

    protected void tickAttack() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> entity != getOwner());
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitResult);
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.canBeHitByProjectile()) {
            return false;
        }
        Entity entity = this.getOwner();
        if(entity == null || !entity.isPassengerOfSameVehicle(target))
            return true;
        return target != entity;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (!level().isClientSide && entity instanceof LivingEntity living && getOwner() instanceof LivingEntity owner && owner != entity) {
            // 事件统一暴击判定 org.confluence.mod.common.event.game.entity.LivingEntityEvents.livingDamage$Pre
//            if (random.nextFloat() < criticalChance) damage *= 1.5F;
            doHurt(living);
        }
    }

    public DamageSource damageSource(){
        if(getOwner() instanceof LivingEntity living)
            return damageSources().mobProjectile(this, living);
        else return damageSources().magic();
    }

    protected void doHurt(LivingEntity living){
        float damage = getBaseDamage() * (attackDamage);
        if (living.hurt(damageSource(), damage)) {
            float attackKnockBack = getBaseKnockBack() + knockBack;
            ModUtils.knockBackA2B(this, living, attackKnockBack * 0.5, 0.2);
            if(--hitCount == 0){
                discard();
            }
        }
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3 = shooter.getKnownMovement().scale(0.5f);
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
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
}
