package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class CrystalChargeProjectile extends AbstractManaProjectile {
    public CrystalChargeProjectile(EntityType<? extends CrystalChargeProjectile> entityType, Level level) {
        super(entityType, level);
//        withParticle(getType() == ModEntities.CRYSTAL_CHARGE_1_PROJECTILE.get() ? big : small);
    }

    public CrystalChargeProjectile(LivingEntity living) {
        this(ModEntities.CRYSTAL_CHARGE_1_PROJECTILE.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (getType() == ModEntities.CRYSTAL_CHARGE_1_PROJECTILE.get()) {
            doSimpleMove();
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.96));
            doBouncyMove(true, this::doNothing, vec3 -> vec3.scale(0.98));
            doAgeCheck(20);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (getType() == ModEntities.CRYSTAL_CHARGE_1_PROJECTILE.get()) {
            doSplit();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (doHurtAndKnockback(result.getEntity(), 0.44, 0.2)) {
            if (getType() == ModEntities.CRYSTAL_CHARGE_1_PROJECTILE.get()) {
                doSplit();
            }
        }
    }

    @Override
    protected boolean doHurtAndKnockback(Entity target, double knockbackStrength, double knockbackMotionY) {
        if (getType() == ModEntities.CRYSTAL_CHARGE_2_PROJECTILE.get()) {
            knockbackStrength *= 0.8;
            knockbackMotionY *= 0.8;
        }
        return super.doHurtAndKnockback(target, knockbackStrength, knockbackMotionY);
    }

    private void doSplit() {
        if (level().isClientSide) return;
        int amount = random.nextIntBetweenInclusive(3, 5);
        float damage = this.damage * 0.8F;
        float velocity = getDefaultVelocity();
        Entity owner = getOwner();
        Vec3 vec3 = getDeltaMovement();
        for (int i = 0; i < amount; i++) {
            CrystalChargeProjectile projectile = new CrystalChargeProjectile(ModEntities.CRYSTAL_CHARGE_2_PROJECTILE.get(), level());
            projectile.setPos(getX(), getY(), getZ());
            projectile.setDamage(damage);
            projectile.setDefaultVelocity(velocity);
            projectile.setOwner(owner);
            projectile.shoot(vec3.x, vec3.y, vec3.z, velocity, 10);
            level().addFreshEntity(projectile);
        }
        discard();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }
}
