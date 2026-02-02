package org.confluence.lib.mixin.client;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.block.ISimulatorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BlockModelShaper.class, priority = 900)
public abstract class BlockModelShaperMixin {
    @ModifyVariable(method = "getBlockModel", at = @At("HEAD"), argsOnly = true)
    private BlockState simulator(BlockState state) {
        if (state.getBlock() instanceof ISimulatorBlock simulatorBlock) {
            state = simulatorBlock.getSimulatedBlock(true);
        }
        return state;
    }
}
