package org.confluence.mod.common.effect.neutral;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

public class ShimmerEffect extends PortMobEffect {
    public ShimmerEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF96FF);
    }

    @Override
    public boolean applyEffectTick1211(LivingEntity living, int amplifier) {
        Level level = living.level();
        if (level.isClientSide) return true;
        if (living.position().y < level.getMinBuildHeight() + 5) return false;
        BlockPos onPos = living.getOnPos();
        if (level.getBlockState(onPos).is(Blocks.BEDROCK)) return false;

        if (level.getFluidState(onPos).getType().getFluidType() != ModFluids.SHIMMER.type().get()) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 1, false, false, false));
        }

        boolean shouldExpire = level.getBlockStates(living.getBoundingBox().inflate(-0.1)).allMatch(blockState -> {
            if (blockState.isAir()) return true;
            return blockState.liquid() && !blockState.is(ModBlocks.SHIMMER.get());
        });
        if (amplifier > 0) {
            if (shouldExpire) {
                MobEffectInstance effect = living.getEffect(ModEffects.SHIMMER.get());
                if (effect != null) effect.amplifier = 0;
            }
            return true;
        }
        return !shouldExpire;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public static void applyShimmerEffect(LivingEntity living) {
        if (!living.level().isClientSide && living.getEyeInFluidType() == ModFluids.SHIMMER.type().get() && !living.hasEffect(ModEffects.SHIMMER.get())) {
            if (living.isCrouching() || !TCUtils.getValue(living, TCItems.EFFECT$IMMUNITIES).contains(ModEffects.SHIMMER.get())) {
                living.addEffect(new MobEffectInstance(ModEffects.SHIMMER.get(), MobEffectInstance.INFINITE_DURATION));
            }
        }
    }
}
