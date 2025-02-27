package org.confluence.mod.common.effect.neutral;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.ModBlocks;

public class ShimmerEffect extends MobEffect {
    public ShimmerEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF96FF);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int pAmplifier) {
        Level level = living.level();
        if (level.isClientSide) return true;
        if (living.position().y < level.getMinBuildHeight() + 5) return false;
        if (level.getFluidState(living.getOnPos()).getType().getFluidType() != ModFluids.SHIMMER.type().get()) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 1, false, false, false));
        }
        return !level.getBlockState(living.getOnPos()).is(Blocks.BEDROCK) &&
                !level.getBlockStates(living.getBoundingBox().inflate(-0.1)).allMatch(blockState ->
                        (blockState.liquid() && !blockState.is(ModBlocks.SHIMMER.get())) || blockState.isAir());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
