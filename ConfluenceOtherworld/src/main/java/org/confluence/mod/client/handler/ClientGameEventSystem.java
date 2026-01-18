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
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.gameevent.GameEventAfterRenderSkyRegisterEvent;
import org.confluence.mod.api.event.gameevent.GameEventSyncCallbackRegisterEvent;
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
        map.put(BloodMoonGameEvent.KEY, GameEventSyncCallback::handleBloodMoon);
        map.put(GoblinArmyGameEvent.KEY, GameEventSyncCallback::handleGoblinArmy);
        ModLoader.postEvent(new GameEventSyncCallbackRegisterEvent(map));
    });
    private static final Map<ResourceKey<? extends GameEvent>, AfterRenderSky> RENDERERS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(SlimeRainGameEvent.KEY, AfterRenderSky::renderSlimeRain);
        map.put(MeteorShowerGameEvent.KEY, AfterRenderSky::renderMeteorShower);
        ModLoader.postEvent(new GameEventAfterRenderSkyRegisterEvent(map));
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
                reset();
            }
        }
    }

    public static void reset() {
        moonTexture = null;
        lightTextureColor = null;
        afterRenderSky = null;
        SlimeRainSprite.SLIME_RAIN_SPRITES.clear();
    }

    @FunctionalInterface
    public interface GameEventSyncCallback {
        void call(Player player, boolean start);

        static void handleAllEvent(Player player, boolean start) {
            for (GameEventSyncCallback callback : CALLBACKS.values()) {
                callback.call(player, start);
            }
        }

        static void handleBloodMoon(Player player, boolean start) {
            if (start) {
                moonTexture = BLOOD_MOON_TEXTURE;
                lightTextureColor = new Vector3f(1, 0, 0);
            }
        }

        static void handleGoblinArmy(Player player, boolean start) {

        }
    }

    @FunctionalInterface
    public interface AfterRenderSky {
        void render(LocalPlayer player, RenderLevelStageEvent event);

        static void renderSlimeRain(LocalPlayer player, RenderLevelStageEvent event) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            for (SlimeRainSprite sprite : SlimeRainSprite.SLIME_RAIN_SPRITES) {
                float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
                poseStack.pushPose();
                poseStack.mulPose(event.getModelViewMatrix());
                poseStack.mulPose(Axis.YP.rotation(sprite.yaw).rotateX(Mth.lerp(partialTick, sprite.pitchO, sprite.pitch)));
                RenderSystem.setShaderTexture(0, sprite.texture);
                RenderSystem.setShaderColor(1, 1, 1, sprite.alpha);
                Matrix4f matrix4f = poseStack.last().pose();
                BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                float radius = sprite.radius;
                float y = sprite.y;
                bufferBuilder.addVertex(matrix4f, -radius, y, -radius).setUv(0.0F, sprite.v1);
                bufferBuilder.addVertex(matrix4f, radius, y, -radius).setUv(1.0F, sprite.v1);
                bufferBuilder.addVertex(matrix4f, radius, y, radius).setUv(1.0F, sprite.v0);
                bufferBuilder.addVertex(matrix4f, -radius, y, radius).setUv(0.0F, sprite.v0);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                poseStack.popPose();
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        static void renderMeteorShower(LocalPlayer player, RenderLevelStageEvent event) {
            // todo
        }
    }

    public static class SlimeRainSprite {
        private static final ResourceLocation[] SLIME_RAIN_TEXTURES = new ResourceLocation[]{
                Confluence.asResource("textures/environment/slime_rain_blue.png"),
                Confluence.asResource("textures/environment/slime_rain_green.png"),
                Confluence.asResource("textures/environment/slime_rain_purple.png")
        };
        private static final Queue<SlimeRainSprite> SLIME_RAIN_SPRITES = EvictingQueue.create(32);
        private final float initialRatio;
        private final float initialPitch;
        private final float yaw;
        private final ResourceLocation texture;
        private final float y;
        private final float alpha;
        private final float radius;
        private float v0;
        private float v1 = 0.25F;
        private int tick;
        private float pitch;
        private float pitchO;

        private SlimeRainSprite() {
            float v = -Mth.PI * 0.1F;
            this.initialRatio = v - 0.05F * (float) Math.random();
            this.initialPitch = v * (float) Math.random() + initialRatio;
            this.yaw = Mth.TWO_PI * (float) Math.random();
            this.texture = SLIME_RAIN_TEXTURES[(int) (Math.random() * 3)];
            int i = (int) (Math.random() * 75);
            this.y = i + 25;
            float j = Mth.clamp(i / 75.0F + 0.25F, 0, 1); // 0 -> 1
            this.alpha = 1 - j; // 1 -> 0
            this.radius = 0.5F + j;
            this.pitch = this.pitchO = initialRatio;
        }

        public static void tick(long gameTime) {
            if (gameTime % 10 == 0) {
                SlimeRainSprite.SLIME_RAIN_SPRITES.add(new SlimeRainSprite());
            }
            for (SlimeRainSprite sprite : SLIME_RAIN_SPRITES) {
                ++sprite.tick;
                sprite.v0 = 0.25F * ((sprite.tick >> 1) % 4);
                sprite.v1 = sprite.v0 + 0.25F;
                sprite.pitchO = sprite.pitch;
                sprite.pitch = sprite.initialPitch * sprite.tick / 75 + sprite.initialRatio;
            }
        }
    }
}
