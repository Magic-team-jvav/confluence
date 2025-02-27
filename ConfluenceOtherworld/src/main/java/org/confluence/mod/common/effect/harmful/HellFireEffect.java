package org.confluence.mod.common.effect.harmful;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;

/**
 * 狱炎: 缓慢损失生命 每秒损失15点生命 停止生命再生
 */
public class HellFireEffect extends MobEffect {
    public HellFireEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(ModDamageTypes.of(living.level(), DamageTypes.LAVA), 2.0F * (amplifier + 1));
        return true;
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);
        livingEntity.level().explode(
                livingEntity,
                Explosion.getDefaultDamageSource(livingEntity.level(), livingEntity),
                new ExplosionDamageCalculator() {
                    @Override
                    public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
                        return false;
                    }
                    @Override
                    public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                        if(!( entity instanceof Enemy || entity instanceof AbstractTerraBossBase<?>)) return 0;
                        return 3 + amplifier*3;
                    }
                } ,
                livingEntity.getX(), livingEntity.getY(0.0625),livingEntity.getZ(),
                1, true, Level.ExplosionInteraction.TRIGGER);

    }
}
