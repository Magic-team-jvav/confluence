package org.confluence.mod.mixin.client;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.ISimulatorBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public abstract class BlockModelShaperMixin {
    @Shadow
    private Map<BlockState, BakedModel> modelByStateCache;

    @Shadow
    @Final
    private ModelManager modelManager;

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    private void simulator(BlockState blockState, CallbackInfoReturnable<BakedModel> cir) {
        if (blockState.getBlock() instanceof ISimulatorBlock simulatorBlock) {
            cir.setReturnValue(modelByStateCache.getOrDefault(simulatorBlock.getSimulatedBlock(true), modelManager.getMissingModel()));
        }
    }
//
//    @Inject(method = "getBlockModel", at = @At("RETURN"), cancellable = true)
//    private void wrap(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
//        cir.setReturnValue(new GrayBlockModelSwapper(cir.getReturnValue()));
//    }
}
