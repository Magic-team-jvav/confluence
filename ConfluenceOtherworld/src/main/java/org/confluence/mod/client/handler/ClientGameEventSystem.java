package org.confluence.mod.client.handler;

import com.google.common.collect.EvictingQueue;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.gameevent.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class ClientGameEventSystem {
    private static final Map<ResourceKey<? extends GameEvent>, GameEventSyncCallback> CALLBACKS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(GameEventSystem.ALL_EVENT_KEY, GameEventSyncCallback::handleAllEvent);
        map.put(SlimeRainGameEvent.KEY, GameEventSyncCallback::handleSlimeRain);
        map.put(BloodMoonGameEvent.KEY, GameEventSyncCallback::handleBloodMoon);
        map.put(GoblinArmyGameEvent.KEY, GameEventSyncCallback::handleGoblinArmy);
        // todo 事件注册
    });
    private static final Map<ResourceKey<? extends GameEvent>, AfterRenderSky> RENDERERS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(SlimeRainGameEvent.KEY, AfterRenderSky::renderSlimeRain);
        // todo 事件注册
    });
    public static final ResourceLocation NO_MOON_TEXTURE = Confluence.asResource("textures/environment/no_moon.png");
    private static final ResourceLocation BLOOD_MOON_TEXTURE = Confluence.asResource("textures/environment/specific_moon_tr_blood_full_moon.png");
    private static final PoseStack poseStack = new PoseStack();
    public static @Nullable ResourceLocation moonTexture;
    public static @Nullable Vector3f lightTextureColor;
    public static @Nullable AfterRenderSky afterRenderSky;

    public static void handle(Player player, List<ResourceKey<? extends GameEvent>> keys, boolean start) {
        for (ResourceKey<? extends GameEvent> key : keys) {
            GameEventSyncCallback callback = CALLBACKS.get(key);
            if (callback != null) {
                callback.call(player, start);
            }
            if (start) {
                afterRenderSky = RENDERERS.get(key);
            } else {
                afterRenderSky = null;
            }
        }
    }

    @FunctionalInterface
    public interface GameEventSyncCallback {
        void call(Player player, boolean start);

        static void handleAllEvent(Player player, boolean start) {
            for (GameEventSyncCallback callback : CALLBACKS.values()) {
                callback.call(player, start);
            }
        }

        static void handleSlimeRain(Player player, boolean start) {
            SlimeRainSprite.SLIME_RAIN_SPRITES.clear();
        }

        static void handleBloodMoon(Player player, boolean start) {
            if (start) {
                moonTexture = BLOOD_MOON_TEXTURE;
                lightTextureColor = new Vector3f(1, 0, 0);
            } else {
                moonTexture = null;
                lightTextureColor = null;
            }
        }

        static void handleGoblinArmy(Player player, boolean start) {

        }
    }

    @FunctionalInterface
    public interface AfterRenderSky {
        void render(LocalPlayer player, RenderLevelStageEvent event);

        static void renderSlimeRain(LocalPlayer player, RenderLevelStageEvent event) {
            for (SlimeRainSprite sprite : SlimeRainSprite.SLIME_RAIN_SPRITES) {
                float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
                poseStack.pushPose();
                poseStack.mulPose(event.getModelViewMatrix());
                poseStack.mulPose(Axis.YP
                        .rotation(Mth.lerp(partialTick, sprite.yawO, sprite.yaw))
                        .rotateX(Mth.lerp(partialTick, sprite.pitchO, sprite.pitch)));

                RenderSystem.enableBlend();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, sprite.texture);
                Matrix4f matrix4f = poseStack.last().pose().rotate(LibClientUtils.ANGLE_45);
                BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                float radius = 2;
                int y = 100;
                bufferBuilder.addVertex(matrix4f, -radius, y, -radius).setUv(0.0F, sprite.v1);
                bufferBuilder.addVertex(matrix4f, radius, y, -radius).setUv(1.0F, sprite.v1);
                bufferBuilder.addVertex(matrix4f, radius, y, radius).setUv(1.0F, sprite.v0);
                bufferBuilder.addVertex(matrix4f, -radius, y, radius).setUv(0.0F, sprite.v0);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                poseStack.popPose();
            }
        }
    }

    public static class SlimeRainSprite {
        private static final ResourceLocation[] SLIME_RAIN_TEXTURES = new ResourceLocation[]{
                Confluence.asResource("textures/environment/slime_rain_blue.png"),
                Confluence.asResource("textures/environment/slime_rain_green.png"),
                Confluence.asResource("textures/environment/slime_rain_purple.png")
        };
        private static final Queue<SlimeRainSprite> SLIME_RAIN_SPRITES = EvictingQueue.create(16);
        private final float initialRatio;
        private final float initialPitch;
        private float pitch;
        private float pitchO;
        private final float initialYaw;
        private float yaw;
        private float yawO;
        private float v0;
        private float v1;
        private int tick;
        private final ResourceLocation texture;

        private SlimeRainSprite() {
            this.initialRatio = 0.5F * (float) Math.random() + 0.5F;
            this.initialPitch = -Mth.PI * 0.25F * (float) Math.random();
            this.initialYaw = Mth.HALF_PI * (float) Math.random();
            this.texture = SLIME_RAIN_TEXTURES[(int) (Math.random() * 3)];
            this.pitch = this.pitchO = initialPitch * initialRatio;
            this.yaw = this.yawO = initialYaw * initialRatio;
        }

        public static void tick(long gameTime) {
            if (gameTime % 20 == 0) {
                SlimeRainSprite.SLIME_RAIN_SPRITES.add(new SlimeRainSprite());
            }
            for (SlimeRainSprite sprite : SLIME_RAIN_SPRITES) {
                ++sprite.tick;
                int i = 3 - (sprite.tick >> 1) % 4;
                sprite.v0 = 0.25F * i;
                sprite.v1 = sprite.v0 + 0.25F;
                sprite.yawO = sprite.yaw;
                sprite.pitchO = sprite.pitch;
                float ratio = sprite.tick / 100.0F + sprite.initialRatio;
                sprite.pitch = sprite.initialPitch * ratio;
                sprite.yaw = sprite.initialYaw * ratio;
            }
        }
    }
}
