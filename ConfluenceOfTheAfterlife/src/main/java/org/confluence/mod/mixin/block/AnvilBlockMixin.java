package org.confluence.mod.mixin.block;

import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void modify(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        Block block = state.getBlock();
        if (block == FunctionalBlocks.LEAD_ANVIL.get()) {
            cir.setReturnValue(FunctionalBlocks.CHIPPED_LEAD_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
        } else if (block == FunctionalBlocks.CHIPPED_LEAD_ANVIL.get()) {
            cir.setReturnValue(FunctionalBlocks.DAMAGED_LEAD_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
        }
    }
}
