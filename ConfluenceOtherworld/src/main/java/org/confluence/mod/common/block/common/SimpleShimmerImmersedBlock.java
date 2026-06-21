package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.common.init.ModFluids;

import java.util.Optional;

public interface SimpleShimmerImmersedBlock extends BucketPickup, LiquidBlockContainer {
    BooleanProperty SHIMMER_IMMERSED = BooleanProperty.create("shimmer_immersed");

    @Override
    default boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == ModFluids.SHIMMER.fluid().get();
    }

    @Override
    default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(SHIMMER_IMMERSED) && fluidState.getType() == ModFluids.SHIMMER.fluid().get()) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(SHIMMER_IMMERSED, true), 3);
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }
            return true;
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}
