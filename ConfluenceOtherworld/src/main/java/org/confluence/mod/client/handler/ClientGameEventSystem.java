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
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

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

    public static void handle(LocalPlayer player) {
        long gameTime = player.level().getGameTime();
        SlimeRainSprite.tick(gameTime);
        MeteorShowerSprite.tick(gameTime);
    }

    public static void handlePacket(Player player, List<ResourceKey<? extends GameEvent>> keys, boolean start) {
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
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            float a = 1.0F - player.level().getRainLevel(partialTick);
            for (SlimeRainSprite sprite : SlimeRainSprite.SLIME_RAIN_SPRITES) {
                poseStack.pushPose();
                poseStack.mulPose(event.getModelViewMatrix());
                sprite.render(partialTick, a);
                poseStack.popPose();
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }

        static void renderMeteorShower(LocalPlayer player, RenderLevelStageEvent event) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, MeteorShowerSprite.TEXTURE);
            BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            float a = 1.0F - player.level().getRainLevel(partialTick);
            for (MeteorShowerSprite sprite : MeteorShowerSprite.METEOR_SHOWER_SPRITES) {
                poseStack.pushPose();
                poseStack.mulPose(event.getModelViewMatrix());
                sprite.render(builder, a);
                poseStack.popPose();
            }
            MeshData data = builder.build();
            if (data != null) {
                BufferUploader.drawWithShader(data);
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    private static float random() {
        return (float) Math.random();
    }

    private static class SlimeRainSprite {
        private static final ResourceLocation[] SLIME_RAIN_TEXTURES = new ResourceLocation[]{
                Confluence.asResource("textures/environment/slime_rain_blue.png"),
                Confluence.asResource("textures/environment/slime_rain_green.png"),
                Confluence.asResource("textures/environment/slime_rain_purple.png")
        };
        private static final Queue<SlimeRainSprite> SLIME_RAIN_SPRITES = EvictingQueue.create(32);
        private final float initialY;
        private final float yaw;
        private final ResourceLocation texture;
        private final float dist;
        private final float alpha;
        private final float radius;
        private float v0;
        private float v1 = 0.25F;
        private int tick;
        private float y;
        private float yo;

        private SlimeRainSprite() {
            float v = 1;
            this.initialY = 90 - v + v * random();
            this.yaw = Mth.TWO_PI * random();
            this.texture = SLIME_RAIN_TEXTURES[(int) (random() * 3)];
            int i = (int) (random() * 75);
            this.dist = i + 25;
            float j = Mth.clamp(i / 75.0F - 0.25F, 0, 1); // 0 -> 1
            this.alpha = 1 - j; // 1 -> 0
            this.radius = 1 + j;
            this.y = this.yo = initialY - 90;
        }

        private void render(float partialTick, float a) {
            poseStack.mulPose(Axis.YP.rotation(yaw).rotateX(-Mth.HALF_PI));
            poseStack.translate(0, 0, Mth.lerp(partialTick, yo, y));
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.setShaderColor(1, 1, 1, alpha * a);
            Matrix4f matrix4f = poseStack.last().pose();
            BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            builder.addVertex(matrix4f, -radius, dist, -radius).setUv(0.0F, v1);
            builder.addVertex(matrix4f, radius, dist, -radius).setUv(1.0F, v1);
            builder.addVertex(matrix4f, radius, dist, radius).setUv(1.0F, v0);
            builder.addVertex(matrix4f, -radius, dist, radius).setUv(0.0F, v0);
            BufferUploader.drawWithShader(builder.buildOrThrow());
        }

        private static void tick(long gameTime) {
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

    private static class MeteorShowerSprite {
        private static final ResourceLocation TEXTURE = Confluence.asResource("textures/environment/meteor_shower.png");
        private static final Vector2f vec = new Vector2f();
        private static final Queue<MeteorShowerSprite> METEOR_SHOWER_SPRITES = EvictingQueue.create(32);
        private static final float frame = 1 / 7.0F;
        private final float radius;
        private final float alpha;
        private final Quaternionf quaternionf;
        private int tick;
        private float v0;
        private float v1 = frame;
        private boolean remove;

        private MeteorShowerSprite() {
            this.radius = 4 + random() * 4;
            this.alpha = 0.5F + random() * 0.5F;
            this.quaternionf = new Quaternionf().rotationXYZ(
                    random() * -Mth.HALF_PI - Mth.PI * 0.25F,
                    random() * Mth.HALF_PI,
                    random() * Mth.TWO_PI
            );
        }

        private void render(BufferBuilder builder, float a) {
            poseStack.mulPose(quaternionf);
            RenderSystem.setShaderColor(1, 1, 1, alpha * a);
            Matrix4f matrix4f = poseStack.last().pose();
            builder.addVertex(matrix4f, -radius, 100, -radius).setUv(0.0F, v1);
            builder.addVertex(matrix4f, radius, 100, -radius).setUv(1.0F, v1);
            builder.addVertex(matrix4f, radius, 100, radius).setUv(1.0F, v0);
            builder.addVertex(matrix4f, -radius, 100, radius).setUv(0.0F, v0);
        }

        private static void tick(long gameTime) {
            if (gameTime % 2 == 0) {
                MeteorShowerSprite.METEOR_SHOWER_SPRITES.add(new MeteorShowerSprite());
            }
            Iterator<MeteorShowerSprite> iterator = MeteorShowerSprite.METEOR_SHOWER_SPRITES.iterator();
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
    }
}
