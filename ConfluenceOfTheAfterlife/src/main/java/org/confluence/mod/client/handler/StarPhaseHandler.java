package org.confluence.mod.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.StarPhase;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = Confluence.MODID)
public final class StarPhaseHandler {
    private static final ResourceLocation[] TEXTURES = Util.make(new ResourceLocation[STAR_PHASES_SIZE], textures -> {
        Arrays.fill(textures, Confluence.asResource("textures/gui/icon/exit.png"));
    });
    private static final List<StarPhase> STAR_PHASES = Util.make(new ArrayList<>(), map -> {
        for (int i = 0; i < STAR_PHASES_SIZE; i++) {
            map.add(StarPhase.DEFAULT);
        }
    });

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null || level.dimension() != Level.OVERWORLD) return;

            Tesselator instance = Tesselator.getInstance();
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(event.getModelViewMatrix());
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
            long gameTime = level.getGameTime(); // 游戏时间
            for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                StarPhase phase = STAR_PHASES.get(i); // 星象
                float size = 20.0F; // 你要算的大小
                float alpha = 1.0F - level.getRainLevel(partialTick);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                poseStack.pushPose();

                // poseStack变换在这

                Matrix4f matrix4f = poseStack.last().pose();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, TEXTURES[i]);
                BufferBuilder bufferBuilder = instance.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.addVertex(matrix4f, -size, 100.0F, -size).setUv(0.0F, 0.0F);
                bufferBuilder.addVertex(matrix4f, size, 100.0F, -size).setUv(1.0F, 0.0F);
                bufferBuilder.addVertex(matrix4f, size, 100.0F, size).setUv(1.0F, 1.0F);
                bufferBuilder.addVertex(matrix4f, -size, 100.0F, size).setUv(0.0F, 1.0F);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                poseStack.popPose();
            }
        }
    }

    public static void handleStarPhases(Either<Int2ObjectMap<StarPhase>, Int2ObjectMap.Entry<StarPhase>> packet) {
        packet.ifLeft(map -> {
            STAR_PHASES.clear();
            for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                STAR_PHASES.add(map.getOrDefault(i, StarPhase.DEFAULT));
            }
        });
        packet.ifRight(triple -> STAR_PHASES.set(triple.getIntKey(), triple.getValue()));
    }
}
