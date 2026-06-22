package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.terraentity.init.TEEffects;

public class BallOfFireProjectile extends AbstractManaProjectile {
    public BallOfFireProjectile(EntityType<BallOfFireProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("ball_of_fire"));
    }

    public BallOfFireProjectile(LivingEntity living) {
        this(ModEntities.BALL_OF_FIRE.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doBouncyMove(true, () -> doCollisionCheck(6), vec3 -> vec3.scale(0.99));
        doFluidCheck(fluidState -> !fluidState.isEmpty());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (random.nextBoolean()) {
            if (ModSecretSeeds.DONT_DIG_UP.match() && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(TEEffects.HELLFIRE, 100));
            } else {
                entity.setRemainingFireTicks(100);
            }
        }
        doHurtAndKnockback(entity, 0.6, 0.2);
    }

    @Override
    public float getCalculatedDamage() {
        float damage = super.getCalculatedDamage();
        return ModSecretSeeds.DONT_DIG_UP.match() ? damage * 1.5F : damage;
    }
}
