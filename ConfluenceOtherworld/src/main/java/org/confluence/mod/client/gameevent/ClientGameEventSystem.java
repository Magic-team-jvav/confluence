package org.confluence.mod.client.gameevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.gameevent.GameEventAfterRenderSkyRegisterEvent;
import org.confluence.mod.api.event.gameevent.GameEventSyncCallbackRegisterEvent;
import org.confluence.mod.common.data.saved.SpecificMoonVariant;
import org.confluence.mod.common.gameevent.*;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ClientGameEventSystem {
    static final Map<ResourceKey<? extends GameEvent>, GameEventSyncCallback> CALLBACKS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(GameEventSystem.ALL_EVENT_KEY, ClientGameEventSystem::handleAllEvent);
        map.put(SlimeRainGameEvent.KEY, SlimeRainSprite::handle);
        map.put(MeteorShowerGameEvent.KEY, MeteorShowerSprite::handle);
        map.put(LanternNightGameEvent.KEY, LanternNightSprite::handle);
        map.put(BloodMoonGameEvent.KEY, ClientGameEventSystem::handleBloodMoon);
        map.put(GoblinArmyGameEvent.KEY, ClientGameEventSystem::handleGoblinArmy);
        ModLoader.postEvent(new GameEventSyncCallbackRegisterEvent(map));
    });
    static final Map<ResourceKey<? extends GameEvent>, AfterRenderSky> RENDERERS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(SlimeRainGameEvent.KEY, SlimeRainSprite::renderSlimeRain);
        map.put(MeteorShowerGameEvent.KEY, MeteorShowerSprite::renderMeteorShower);
        map.put(LanternNightGameEvent.KEY, LanternNightSprite::renderLanternNight);
        ModLoader.postEvent(new GameEventAfterRenderSkyRegisterEvent(map));
    });
    public static final ResourceLocation NO_MOON_TEXTURE = Confluence.asResource("textures/environment/no_moon.png");
    static final PoseStack poseStack = new PoseStack();
    public static @Nullable ResourceLocation moonTexture;
    public static @Nullable Vector3f lightTextureColor;
    static Map<ResourceKey<? extends GameEvent>, AfterRenderSky> afterRenderSky = Map.of();

    public static void handle(LocalPlayer player) {
        if (!Minecraft.getInstance().isPaused() && player.clientLevel.tickRateManager().runsNormally()) {
            long gameTime = player.level().getGameTime();
            SlimeRainSprite.tick(gameTime);
            MeteorShowerSprite.tick(gameTime);
            LanternNightSprite.tick();
        }
    }

    public static void handlePacket(Player player, List<ResourceKey<? extends GameEvent>> keys, boolean start) {
        Map<ResourceKey<? extends GameEvent>, AfterRenderSky> map = new IdentityHashMap<>(afterRenderSky);
        for (ResourceKey<? extends GameEvent> key : keys) {
            GameEventSyncCallback callback = CALLBACKS.get(key);
            if (callback != null) {
                callback.call(player, start);
            }
            if (start) {
                AfterRenderSky renderSky = RENDERERS.get(key);
                if (renderSky != null) {
                    map.put(key, renderSky);
                }
            } else {
                map.remove(key);
            }
        }
        afterRenderSky = map;
    }

    public static void reset() {
        moonTexture = null;
        lightTextureColor = null;
        afterRenderSky = Map.of();
        SlimeRainSprite.reset();
        MeteorShowerSprite.reset();
        LanternNightSprite.reset();
    }

    public static void afterRenderSky(RenderLevelStageEvent event, Minecraft minecraft) {
        if (afterRenderSky.isEmpty()) return;
        for (AfterRenderSky renderSky : afterRenderSky.values()) {
            renderSky.render(minecraft.player, event);
        }
    }

    public static void handleAllEvent(Player player, boolean start) {
        for (GameEventSyncCallback callback : CALLBACKS.values()) {
            callback.call(player, start);
        }
    }

    public static void handleBloodMoon(Player player, boolean start) {
        if (start && player.level().dimension() == OverworldUtils.dimension()) {
            moonTexture = SpecificMoonVariant.TR_BLOOD_FULL_MOON.texture;
            lightTextureColor = new Vector3f(1, 0, 0);
        } else {
            moonTexture = null;
            lightTextureColor = null;
        }
    }

    public static void handleGoblinArmy(Player player, boolean start) {
        // todo 进度条
    }

    static float random() {
        return (float) Math.random();
    }
}
