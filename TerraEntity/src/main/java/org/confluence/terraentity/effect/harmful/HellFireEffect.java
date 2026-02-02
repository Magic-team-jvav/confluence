package org.confluence.terraentity.effect.harmful;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.EffectCure;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.TETags;

import java.util.Set;

/**
 * 狱炎: 缓慢损失生命 每秒损失15点生命 停止生命再生
 */
public class HellFireEffect extends MobEffect {
    public HellFireEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(TETags.DamageTypes.of(living.level(), DamageTypes.LAVA), 2.0F * (amplifier + 1));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);
        livingEntity.setRemainingFireTicks(1);
        livingEntity.level().explode(
                livingEntity,
                Explosion.getDefaultDamageSource(livingEntity.level(), null),
                new ExplosionDamageCalculator() {
                    @Override
                    public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
                        return false;
                    }

                    @Override
                    public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                        if (!(entity instanceof Enemy || entity instanceof AbstractTerraBossBase)) return 0;
                        return 3 + amplifier * 3;
                    }

                    @Override
                    public float getKnockbackMultiplier(Entity entity) {
                        if(!entity.isPickable()){
                            return 0.0F;
                        }
                        return 1.0F;
                    }
                },
                livingEntity.getX(), livingEntity.getY(0.0625), livingEntity.getZ(),
                1, true, Level.ExplosionInteraction.MOB
        );

    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        //cures.add(LibUtils.DENY_HEAL);
    }
}
