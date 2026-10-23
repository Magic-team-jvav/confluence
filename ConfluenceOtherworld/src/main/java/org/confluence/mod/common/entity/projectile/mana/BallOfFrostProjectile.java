package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.function.UnaryOperator;

public class BallOfFrostProjectile extends AbstractManaProjectile {
    /// 火之花弹幕的重力，寒霜之花按比例取用。
    private static final double FIRE_FLOWER_GRAVITY = 0.04;
    /// 寒霜之花只受火之花约 10% 的重力影响。
    private static final double GRAVITY_RATIO_OF_FIRE_FLOWER = 0.1;

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
        doBouncyMove(true, () -> doCollisionCheck(8), UnaryOperator.identity());
        doAgeCheck(1200);
    }

    /// 和火之花一样受重力影响，但只有火之花约 10% 的重力（0.04 -> 0.004）。
    @Override
    protected double getDefaultGravity() {
        return FIRE_FLOWER_GRAVITY * GRAVITY_RATIO_OF_FIRE_FLOWER;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        LivingEntity living = LibEntityUtils.effectTarget(entity);
        if (doHurtAndKnockback(entity, 0.65, 0.2) && living != null) {
            living.addEffect(new MobEffectInstance(ModEffects.FROSTBITE, Mth.randomBetweenInclusive(living.getRandom(), 100, 280)));
        }
    }
}
