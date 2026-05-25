package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.block.natural.herbs.BaseHerbBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.mixed.ILevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class LevelRendererMixin implements ILevelRenderer {
    @Shadow
    @Nullable
    private ViewArea viewArea;

    @Shadow
    public abstract void needsUpdate();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void confluence$rebuildAllChunks() {
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        if (IS_SODIUM_LOADED) {
            confluence$rebuildAllChunksSodium(player);
        } else if (viewArea != null) {
            for (SectionRenderDispatcher.RenderSection chunk : viewArea.sections) {
                chunk.setDirty(true);
            }
            needsUpdate();
        }
    }

    @Unique
    private void confluence$rebuildAllChunksSodium(LocalPlayer player) {
        ChunkPos chunkPos = player.chunkPosition();
        int viewDistance = minecraft.options.renderDistance().get();
        Level level = player.level();
        int startY = level.getMinSection();
        int endY = level.getMaxSection();
        int cx, cz;
        for (int x = -viewDistance; x < viewDistance; ++x) {
            cx = chunkPos.x + x;
            for (int z = -viewDistance; z < viewDistance; ++z) {
                cz = chunkPos.z + z;
                for (int y = startY; y <= endY; ++y) {
                    ILevelRenderer.scheduleRebuildForChunk(cx, y, cz);
                }
            }
        }
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0))
    private ResourceLocation renderSun(ResourceLocation textureId) {
        return ClientPacketHandler.sunTexture == null ? textureId : ClientPacketHandler.sunTexture;
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1))
    private ResourceLocation renderMoon(ResourceLocation textureId) {
        return ClientGameEventSystem.moonTexture == null ? textureId : ClientGameEventSystem.moonTexture;
    }

    @Inject(method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I", at = @At("RETURN"), cancellable = true)
    private static void makeHerbEmissive(BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (state.is(ModBlocks.SHIVERTHORN) && state.getValue(BaseHerbBlock.AGE) == 2) {
            int packed = cir.getReturnValue();
            int block = (packed >> 4) & 0xf;
            if (block < 6) {
                packed &= ~0xF0;
                packed |= (6 << 4);
                cir.setReturnValue(packed);
            }
        }
    }
}
