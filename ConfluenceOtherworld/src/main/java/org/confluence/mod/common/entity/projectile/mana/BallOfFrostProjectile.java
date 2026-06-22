package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.function.UnaryOperator;

public class BallOfFrostProjectile extends AbstractManaProjectile {
    public BallOfFrostProjectile(EntityType<? extends AbstractManaProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("ball_of_frost"));
    }

    public BallOfFrostProjectile(LivingEntity living) {
        super(ModEntities.BALL_OF_FROST.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doBouncyMove(false, () -> doCollisionCheck(8), UnaryOperator.identity());
        doAgeCheck(1200);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.FROSTBITE, Mth.randomBetweenInclusive(living.getRandom(), 100, 280)));
        }
        doHurtAndKnockback(entity, 0.65, 0.2);
    }
}
