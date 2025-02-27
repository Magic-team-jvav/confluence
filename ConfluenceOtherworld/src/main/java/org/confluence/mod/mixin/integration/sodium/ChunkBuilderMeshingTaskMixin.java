package org.confluence.mod.mixin.integration.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.client.textures.LocalBrushData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @see org.confluence.mod.mixin.client.renderer.SectionCompilerMixin
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask", remap = false)
public abstract class ChunkBuilderMeshingTaskMixin {
    @WrapOperation(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",at= @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState replace(LevelSlice instance, int blockX, int blockY, int blockZ, Operation<BlockState> original) {
        if (!ClientPacketHandler.hasEchoVisible() && LocalBrushData.hasEcho(BlockPos.asLong(blockX, blockY, blockZ))) {
            return Blocks.AIR.defaultBlockState();
        }
        return original.call(instance, blockX, blockY, blockZ);
    }
}
