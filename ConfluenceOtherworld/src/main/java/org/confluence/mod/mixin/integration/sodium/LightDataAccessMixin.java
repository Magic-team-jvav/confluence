package org.confluence.mod.mixin.integration.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.client.textures.LocalBrushData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @see org.confluence.mod.mixin.client.renderer.ModelBlockRendererMixin.AmbientOcclusionFaceMixin
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess", remap = false)
public class LightDataAccessMixin {
    @WrapOperation(method = "compute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState replace(BlockAndTintGetter instance, BlockPos pos, Operation<BlockState> original) {
        if (!ClientPacketHandler.hasEchoVisible() && LocalBrushData.hasEcho(pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return original.call(instance, pos);
    }
}
