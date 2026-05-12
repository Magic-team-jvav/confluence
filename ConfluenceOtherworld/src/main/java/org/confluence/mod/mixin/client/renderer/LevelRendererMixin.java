package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.mixed.ILevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
}
