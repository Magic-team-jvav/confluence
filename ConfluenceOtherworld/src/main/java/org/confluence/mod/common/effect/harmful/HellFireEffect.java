package org.confluence.mod.common.effect.harmful;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.api.entity.Boss;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

public class HellFireEffect extends PortMobEffect {
    public HellFireEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(living.damageSources().lava(), 2.0F * (amplifier + 1));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void onEffectStarted(LivingEntity living, int amplifier) {
        super.onEffectStarted(living, amplifier);
        living.setRemainingFireTicks(1);
        living.level().explode(
                living,
                living.damageSources().explosion(living, null),
                new ExplosionDamageCalculator() {
                    @Override
                    public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader,
                                                      BlockPos pos, BlockState state, float power) {
                        return false;
                    }

                    @Override
                    public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                        if (!(entity instanceof Enemy || entity instanceof Boss)) return 0;
                        return 3 + amplifier * 3;
                    }
                },
                living.getX(), living.getY(0.0625), living.getZ(),
                1, true, Level.ExplosionInteraction.MOB);
    }
}
