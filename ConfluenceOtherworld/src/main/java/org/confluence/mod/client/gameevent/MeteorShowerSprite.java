package org.confluence.mod.client.gameevent;

import com.google.common.collect.EvictingQueue;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.OverworldUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

import java.util.Iterator;
import java.util.Queue;

import static org.confluence.mod.client.gameevent.ClientGameEventSystem.poseStack;
import static org.confluence.mod.client.gameevent.ClientGameEventSystem.random;

final class MeteorShowerSprite {
    static final ResourceLocation TEXTURE = Confluence.asResource("textures/environment/meteor_shower.png");
    static final Queue<MeteorShowerSprite> SPRITES = EvictingQueue.create(32);
    static boolean started;
    static final float frame = 1 / 7.0F;
    final float radius;
    final float alpha;
    final Quaternionf quaternionf;
    int tick;
    float v0;
    float v1 = frame;
    boolean remove;

    MeteorShowerSprite() {
        this.radius = 4 + random() * 2;
        this.alpha = 0.5F + random() * 0.5F;
        this.quaternionf = new Quaternionf().rotationXYZ(
                random() * -Mth.HALF_PI - Mth.PI * 0.25F,
                random() * Mth.HALF_PI * 0.95F,
                random() * Mth.TWO_PI
        );
    }

    void render(BufferBuilder builder, float a) {
        poseStack.pushPose();
        poseStack.mulPose(quaternionf);
        RenderSystem.setShaderColor(1, 1, 1, alpha * a);
        Matrix4f matrix4f = poseStack.last().pose();
        builder.vertex(matrix4f, -radius, 100, -radius).uv(0.0F, v1);
        builder.vertex(matrix4f, radius, 100, -radius).uv(1.0F, v1);
        builder.vertex(matrix4f, radius, 100, radius).uv(1.0F, v0);
        builder.vertex(matrix4f, -radius, 100, radius).uv(0.0F, v0);
        poseStack.popPose();
    }

    static void renderMeteorShower(LocalPlayer player, PortRenderLevelStageEvent event) {
        if (player.level().dimension() != OverworldUtils.dimension()) return;
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float a = Math.max(1.0F - player.level().getRainLevel(partialTick), 0.2F);
        poseStack.pushPose();
        poseStack.mulPose(event.getModelViewMatrix());
        for (MeteorShowerSprite sprite : SPRITES) {
            sprite.render(builder, a);
        }
        poseStack.popPose();
        MeshData data = builder.build();
        if (data != null) {
            BufferUploader.drawWithShader(data);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    static void tick(long gameTime) {
        if (!started) return;
        if (gameTime % 2 == 0) {
            SPRITES.add(new MeteorShowerSprite());
        }
        Iterator<MeteorShowerSprite> iterator = SPRITES.iterator();
        while (iterator.hasNext()) {
            MeteorShowerSprite sprite = iterator.next();
            ++sprite.tick;
            int i = (sprite.tick >> 1) % 7;
            sprite.v0 = frame * i;
            sprite.v1 = sprite.v0 + frame;
            if (sprite.remove) {
                iterator.remove();
            }
            if (i == 6) {
                sprite.remove = true;
            }
        }
    }

    static void handle(Player player, boolean start) {
        started = start;
        if (!start) {
            SPRITES.clear();
        }
    }

    static void reset() {
        started = false;
        SPRITES.clear();
    }
}
