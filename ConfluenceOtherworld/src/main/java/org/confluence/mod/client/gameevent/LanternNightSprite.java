package org.confluence.mod.client.gameevent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.data.molang.compiler.value.Variable;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import static org.confluence.mod.client.gameevent.ClientGameEventSystem.poseStack;
import static org.confluence.mod.client.gameevent.ClientGameEventSystem.random;

final class LanternNightSprite {
    static final ResourceLocation TEXTURE = Confluence.asResource("textures/environment/lantern_night.png");
    static final int MAX = 100;
    static final Queue<LanternNightSprite> SPRITES = new ArrayDeque<>(MAX);
    static boolean started;
    static float globalAlpha;
    static float globalY;
    static float globalYO;
    static @Nullable ParticleEmitter emitter;
    final float radius;
    final float dist;
    final float alpha;
    final float dYaw;
    int tick;
    float step;
    float y;
    float yo;
    float yaw;
    float yawO;
    byte k;

    LanternNightSprite() {
        this.radius = 1 + random() * 0.5F;
        float v = random();
        this.dist = 75 + v * 25;
        this.alpha = 1 - v * 0.5F;
        this.dYaw = Mth.TWO_PI / (random() * 1200 + 1800);
        this.tick = (int) (random() * 2400);
        this.y = 90 - random() * 100; // [90, -10]
        cal();
    }

    void cal() {
        byte kO = k;
        float v = tick * dYaw;
        this.k = (byte) Mth.sign(Mth.cos(v));
        if (kO != k) {
            this.step = (random() * 7 + 3) * dYaw;
        }
        this.yo = y;
        this.yawO = yaw;
        this.yaw = v;
        this.y += k * step;
    }

    void render(BufferBuilder builder, float partialTick, float a) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation(Mth.lerp(partialTick, yawO, yaw)).rotateX(-Mth.HALF_PI));
        poseStack.translate(0, 0, Mth.lerp(partialTick, yo, y));
        Matrix4f matrix4f = poseStack.last().pose();
        builder.addVertex(matrix4f, -radius, dist, -radius).setUv(0, 1).setColor(255, 255, 255, (int) (alpha * a * 255));
        builder.addVertex(matrix4f, radius, dist, -radius).setUv(1, 1).setColor(255, 255, 255, (int) (alpha * a * 255));
        builder.addVertex(matrix4f, radius, dist, radius).setUv(1, 0).setColor(255, 255, 255, (int) (alpha * a * 255));
        builder.addVertex(matrix4f, -radius, dist, radius).setUv(0, 0).setColor(255, 255, 255, (int) (alpha * a * 255));
        poseStack.popPose();
    }

    static void renderLanternNight(LocalPlayer player, RenderLevelStageEvent event) {
        if (player.level().dimension() != OverworldUtils.dimension()) return;
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float rainLevel = player.level().getRainLevel(partialTick);
        if (rainLevel > 1 - Mth.EPSILON) return;
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, globalAlpha);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float a = 1.0F - rainLevel;
        poseStack.pushPose();
        poseStack.mulPose(event.getModelViewMatrix());
        poseStack.translate(0, Mth.lerp(partialTick, globalYO, globalY), 0);
        for (LanternNightSprite sprite : SPRITES) {
            sprite.render(builder, partialTick, a);
        }
        poseStack.popPose();
        MeshData data = builder.build();
        if (data != null) {
            BufferUploader.drawWithShader(data);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    static void tick(LocalPlayer player) {
        if (!started) return;
        if (globalAlpha < 1) {
            globalAlpha = Math.min(globalAlpha + 0.005F, 1);
        }
        if (globalY < 0) {
            globalYO = globalY;
            globalY += 0.1F;
        } else {
            globalY = globalYO = 0;
        }
        if (emitter == null || emitter.isRemoved()) {
            createEmitter(player);
        }
        int spawn = MAX - SPRITES.size();
        if (spawn > 0) {
            for (int i = 0; i < spawn; i++) {
                SPRITES.add(new LanternNightSprite());
            }
        }
        Iterator<LanternNightSprite> iterator = SPRITES.iterator();
        while (iterator.hasNext()) {
            LanternNightSprite sprite = iterator.next();
            ++sprite.tick;
            sprite.cal();
            if (sprite.y > 100 || sprite.y < -20) {
                iterator.remove();
            }
        }
    }

    static void handle(Player player, boolean start) {
        if (start) {
            started = true;
            createEmitter(player);
        } else {
            reset();
        }
    }

    static void createEmitter(Player player) {
        emitter = new ParticleEmitter(player.level(), player.position(), Confluence.asResource("lantern_night")) {
            @Override
            protected void createVars() {
                super.createVars();
                Variable windX = new Variable("variable.wind_x", p -> WeatherHandler.getWindSpeedX());
                Variable windZ = new Variable("variable.wind_z", p -> WeatherHandler.getWindSpeedZ());
                vars.table.put(windX.name(), windX);
                vars.table.put(windZ.name(), windZ);
            }
        };
        emitter.attachEntity(player);
        PSGameClient.LOADER.addEmitter(emitter, false);
    }

    static void reset() {
        SPRITES.clear();
        started = false;
        globalAlpha = 0;
        globalY = -90;
        globalYO = -90;
        if (emitter != null) {
            emitter.remove();
            emitter = null;
        }
    }
}
