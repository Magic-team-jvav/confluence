package org.confluence.mod.common.entity.projectile;

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
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.jetbrains.annotations.Nullable;

/**
 * 基础属性如伤害、击退、初始位置由弹幕容器设置，弹幕实体只定义运动、伤害公式、碰撞检测
 */
public abstract class SwordProjectile extends AbstractHurtingProjectile {
    protected static final int TIME_EXISTENCE = 40;
    protected float attackDamage = 0.0F;
    protected float baseAttackDamage = 0;
    protected float criticalChance = 0.0F;
    protected float knockBack = 0.0F;
    protected float baseKnockBack = 0.0F;
    protected ItemStack firedFromWeapon;

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
        checkCollision();
    }

    protected void checkCollision() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> entity != getOwner());
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitResult);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (!level().isClientSide) {
            float damage = getBaseDamage() * (attackDamage);
             if (random.nextFloat() < criticalChance) damage *= 1.5F;
            if (entity.hurt(damageSources().mobProjectile(this, (LivingEntity) getOwner()), damage)) {
                float attackKnockBack = getBaseKnockBack() + knockBack;
                ModUtils.knockBackA2B(this, entity, attackKnockBack * 0.5, 0.2);
            }
        }
        if (entity.isPickable()) discard();
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
}
