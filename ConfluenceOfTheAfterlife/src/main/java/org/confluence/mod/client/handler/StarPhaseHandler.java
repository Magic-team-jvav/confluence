package org.confluence.mod.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.StarPhase;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = Confluence.MODID)
public final class StarPhaseHandler {
    private static final ResourceLocation[] TEXTURES = Util.make(new ResourceLocation[STAR_PHASES_SIZE], textures -> {
        Arrays.fill(textures, Confluence.asResource("textures/stars/star_0_half_life.png"));
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
            float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
            long gameTime = level.getGameTime(); // 游戏时间
            float dayTime = level.getTimeOfDay(partialTick); // 游戏时间
            float earthRa = 100.0F;
            float earthT = 400.0F;
            for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                StarPhase phase = STAR_PHASES.get(i); // 星象
                float sizeSet = 1.0F;
                float size = sizeSet * (phase.radius() * 0.001F + phase.timeOffset() * 0.0001F + 0.07F / phase.radius()); // 你要算的大小
                float alpha = 1.0F - level.getRainLevel(partialTick);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                poseStack.pushPose();

                // poseStack变换在这
                TEXTURES[1] = Confluence.asResource("textures/stars/star_1_warcraft.png");
                TEXTURES[2] = Confluence.asResource("textures/stars/star_2_pvz.png");
                TEXTURES[3] = Confluence.asResource("textures/stars/star_3_ut.png");
                TEXTURES[4] = Confluence.asResource("textures/stars/star_4_tetris.png");
                TEXTURES[5] = Confluence.asResource("textures/stars/star_5_stardew_valley.png");
                TEXTURES[6] = Confluence.asResource("textures/stars/star_6_dont_starve.png");
                TEXTURES[7] = Confluence.asResource("textures/stars/star_7_isaac.png");
                TEXTURES[8] = Confluence.asResource("textures/stars/star_8_super_mario.png");
                TEXTURES[9] = Confluence.asResource("textures/stars/star_9_pokemon.png");
                double time = 1.0D;
                double angle = phase.angle() * Mth.DEG_TO_RAD;
                float R = phase.radius();
                double earthR = time * gameTime / 240.0D / Math.sqrt(Math.pow(earthRa, 3) / earthT) * Mth.DEG_TO_RAD;
                Vector3f earthPos = new Vector3f(0.0F, 0.0F, earthRa);
                double starR = (time * gameTime / 240.0D / Math.sqrt(Math.pow(R, 3) / earthT) + phase.timeOffset()) * Mth.DEG_TO_RAD - earthR;
                double a = Math.sin(starR + earthR) * angle;
                double cos = Math.cos(a);
                Vector3f starPos = new Vector3f(R * (float) cos * (float) Math.sin(starR), R * (float) (Math.sin(starR + earthR) * Math.sin(angle)), R * (float) Math.cos(a) * (float) Math.cos(starR));

                float starX = starPos.x - earthPos.x;
                float starY = starPos.z - earthPos.z;
                float starZ = starPos.y - earthPos.y;

                float dis = (float) Math.sqrt(starX * starX + starY * starY);

                float rDay = (dayTime * 360 + 90) * Mth.DEG_TO_RAD;

                poseStack.mulPose(Axis.ZP.rotation((float) (Math.atan2(starY, starX) - 0 * Mth.DEG_TO_RAD)));
                poseStack.mulPose(Axis.ZP.rotation(rDay));
                poseStack.mulPose(Axis.XP.rotation((float) (Math.atan2(starZ, dis) - 0 * Mth.DEG_TO_RAD)));
                poseStack.mulPose(Axis.YP.rotation((float) gameTime / 1000 + (float) phase.timeOffset()));

                Matrix4f matrix4f = poseStack.last().pose();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, TEXTURES[i]);
                BufferBuilder bufferBuilder = instance.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.addVertex(matrix4f, -size, dis, -size).setUv(0.0F, 1.0F);
                bufferBuilder.addVertex(matrix4f, size, dis, -size).setUv(1.0F, 1.0F);
                bufferBuilder.addVertex(matrix4f, size, dis, size).setUv(1.0F, 0.0F);
                bufferBuilder.addVertex(matrix4f, -size, dis, size).setUv(0.0F, 0.0F);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                poseStack.popPose();
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
