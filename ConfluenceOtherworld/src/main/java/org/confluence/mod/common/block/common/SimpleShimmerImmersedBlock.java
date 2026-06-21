package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.ModFluids;

import javax.annotation.Nullable;
import java.util.Optional;

public interface SimpleShimmerImmersedBlock extends BucketPickup, LiquidBlockContainer {

    @Override
    default boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == ModFluids.SHIMMER.fluid().get();
    }

    @Override
    default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(StateProperties.SHIMMER_IMMERSED) && fluidState.getType() == ModFluids.SHIMMER.fluid().get()) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(StateProperties.SHIMMER_IMMERSED, true), 3);
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }
            return true;
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}
