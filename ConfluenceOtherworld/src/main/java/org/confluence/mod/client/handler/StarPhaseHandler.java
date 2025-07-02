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
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.StarPhase;
import org.confluence.mod.util.ModUtils;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

public final class StarPhaseHandler {
    private static final ResourceLocation[] TEXTURES = Util.make(new ResourceLocation[STAR_PHASES_SIZE], textures -> {
        textures[0] = Confluence.asResource("textures/stars/star_0_half_life.png");
        textures[1] = Confluence.asResource("textures/stars/star_1_warcraft.png");
        textures[2] = Confluence.asResource("textures/stars/star_2_pvz.png");
        textures[3] = Confluence.asResource("textures/stars/star_3_ut.png");
        textures[4] = Confluence.asResource("textures/stars/star_4_tetris.png");
        textures[5] = Confluence.asResource("textures/stars/star_5_stardew_valley.png");
        textures[6] = Confluence.asResource("textures/stars/star_6_dont_starve.png");
        textures[7] = Confluence.asResource("textures/stars/star_7_isaac.png");
        textures[8] = Confluence.asResource("textures/stars/star_8_super_mario.png");
        textures[9] = Confluence.asResource("textures/stars/star_9_pokemon.png");
    });
    private static final List<StarPhase> STAR_PHASES = Util.make(new ArrayList<>(), map -> {
        for (int i = 0; i < STAR_PHASES_SIZE; i++) {
            map.add(StarPhase.DEFAULT);
        }
    });
    public static boolean enabled = false;

    public static void render(RenderLevelStageEvent event) {
        if (!enabled) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || level.dimension() != ModUtils.dimension()) return;
        Tesselator tesselator = Tesselator.getInstance();
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());

        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        float gameTime = (level.getGameTime() % 24000L) + partialTick;
        float dayTime = level.getTimeOfDay(partialTick);
        float rDay = dayTime * Mth.TWO_PI + Mth.HALF_PI; // (dayTime * 360 + 90) * Mth.DEG_TO_RAD
        float alpha = 1.0F - level.getRainLevel(partialTick);
        float v1 = gameTime * 0.001F; // gameTime / 1000
        float v2 = gameTime * 0.004166667F; // gameTime / 240
        float earthRa = 100.0F;
        float earthT = 400.0F;
        float invEarthT = 1.0F / earthT;
        float invSqrt = Mth.invSqrt(earthRa * earthRa * earthRa * invEarthT) * Mth.DEG_TO_RAD;

        float time = 1.0F;
        float sizeSet = 1.0F;

        float alphaX = dayTime * Mth.PI / 1200;
        float alphaSet = (float) Math.pow(Mth.abs(Mth.sin(alphaX)), 0.1) * (Mth.sin(alphaX) / Mth.abs(Mth.sin(alphaX) * 2)) + 0.5F;
        alpha *= alphaSet;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        for (int i = 0; i < STAR_PHASES_SIZE; i++) {
            StarPhase phase = STAR_PHASES.get(i);
            float timeOffset = phase.timeOffset();
            float radius = phase.radius();
            float size = sizeSet * (radius * 0.001F + timeOffset * 0.0001F + 0.07F / radius);
            poseStack.pushPose();

            // poseStack变换在这
            float angle = phase.angle() * Mth.DEG_TO_RAD;
            float v = time * v2;
            float earthR = v * invSqrt;

            float starR = (v / Mth.sqrt(radius * radius * radius * invEarthT) + timeOffset) * Mth.DEG_TO_RAD - earthR;
            float sin = Mth.sin(starR + earthR);
            float cos = radius * Mth.cos(sin * angle);

            float starX = cos * Mth.sin(starR);
            float starY = cos * Mth.cos(starR) - earthRa;
            float starZ = radius * sin * Mth.sin(angle);

            float dis = Mth.sqrt(starX * starX + starY * starY);
            float disMax = radius + earthRa;
            float disMin = Mth.abs(radius - earthRa);
            float trueDis = (dis - disMin) / (disMax - disMin) * 20 + 600;
            float trueSize = size / dis * trueDis;

            poseStack.mulPose(Axis.ZP
                    .rotation((float) Mth.atan2(starY, starX))
                    .rotateZ(rDay)
                    .rotateX((float) Mth.atan2(starZ, dis))
                    .rotateY(v1 + timeOffset));

            Matrix4f matrix4f = poseStack.last().pose();
            RenderSystem.setShaderTexture(0, TEXTURES[i]);
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.addVertex(matrix4f, -trueSize, trueDis, -trueSize).setUv(0.0F, 1.0F);
            bufferBuilder.addVertex(matrix4f, trueSize, trueDis, -trueSize).setUv(1.0F, 1.0F);
            bufferBuilder.addVertex(matrix4f, trueSize, trueDis, trueSize).setUv(1.0F, 0.0F);
            bufferBuilder.addVertex(matrix4f, -trueSize, trueDis, trueSize).setUv(0.0F, 0.0F);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            poseStack.popPose();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void handleStarPhases(Either<Int2ObjectMap<StarPhase>, Int2ObjectMap.Entry<StarPhase>> packet) {
        packet.ifLeft(map -> {
            STAR_PHASES.clear();
            for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                STAR_PHASES.add(map.getOrDefault(i, StarPhase.DEFAULT));
            }
        }).ifRight(triple -> STAR_PHASES.set(triple.getIntKey(), triple.getValue()));
    }
}
