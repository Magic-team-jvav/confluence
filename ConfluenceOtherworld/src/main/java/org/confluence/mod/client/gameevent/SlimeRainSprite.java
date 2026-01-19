package org.confluence.mod.client.gameevent;

import com.google.common.collect.EvictingQueue;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.OverworldUtils;
import org.joml.Matrix4f;

import java.util.Queue;

final class SlimeRainSprite {
    static final ResourceLocation[] SLIME_RAIN_TEXTURES = new ResourceLocation[]{
            Confluence.asResource("textures/environment/slime_rain_blue.png"),
            Confluence.asResource("textures/environment/slime_rain_green.png"),
            Confluence.asResource("textures/environment/slime_rain_purple.png")
    };
    static final Queue<SlimeRainSprite> SLIME_RAIN_SPRITES = EvictingQueue.create(32);
    final float initialY;
    final float yaw;
    final ResourceLocation texture;
    final float dist;
    final float alpha;
    final float radius;
    float v0;
    float v1 = 0.25F;
    int tick;
    float y;
    float yo;

    SlimeRainSprite() {
        this.initialY = 90 - 2 + 2 * ClientGameEventSystem.random();
        this.yaw = Mth.TWO_PI * ClientGameEventSystem.random();
        this.texture = SLIME_RAIN_TEXTURES[(int) (ClientGameEventSystem.random() * 3)];
        int i = (int) (ClientGameEventSystem.random() * 75);
        this.dist = i + 25;
        float j = Mth.clamp(i / 75.0F - 0.25F, 0, 1); // 0 -> 1
        this.alpha = 1 - j; // 1 -> 0
        this.radius = 1 + j;
        this.y = this.yo = initialY;
    }

    void render(float partialTick, float a) {
        ClientGameEventSystem.poseStack.mulPose(Axis.YP.rotation(yaw).rotateX(-Mth.HALF_PI));
        ClientGameEventSystem.poseStack.translate(0, 0, Mth.lerp(partialTick, yo, y));
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1, 1, 1, alpha * a);
        Matrix4f matrix4f = ClientGameEventSystem.poseStack.last().pose();
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(matrix4f, -radius, dist, -radius).setUv(0.0F, v1);
        builder.addVertex(matrix4f, radius, dist, -radius).setUv(1.0F, v1);
        builder.addVertex(matrix4f, radius, dist, radius).setUv(1.0F, v0);
        builder.addVertex(matrix4f, -radius, dist, radius).setUv(0.0F, v0);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    static void renderSlimeRain(LocalPlayer player, RenderLevelStageEvent event) {
        if (player.level().dimension() != OverworldUtils.dimension()) return;
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float a = (1.0F - player.level().getRainLevel(partialTick)) * 0.5F;
        for (SlimeRainSprite sprite : SLIME_RAIN_SPRITES) {
            ClientGameEventSystem.poseStack.pushPose();
            ClientGameEventSystem.poseStack.mulPose(event.getModelViewMatrix());
            sprite.render(partialTick, a);
            ClientGameEventSystem.poseStack.popPose();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    static void tick(long gameTime) {
        if (gameTime % 10 == 0) {
            SlimeRainSprite.SLIME_RAIN_SPRITES.add(new SlimeRainSprite());
        }
        for (SlimeRainSprite sprite : SLIME_RAIN_SPRITES) {
            ++sprite.tick;
            sprite.v0 = 0.25F * ((sprite.tick >> 1) % 4);
            sprite.v1 = sprite.v0 + 0.25F;
            sprite.yo = sprite.y;
            sprite.y = sprite.initialY - 180.0F * sprite.tick / 320;
        }
    }
}
