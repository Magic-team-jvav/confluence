package org.confluence.mod.client.handler;

import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.gameevent.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ClientGameEventSystem {
    private static final Map<ResourceKey<? extends GameEvent>, GameEventSyncCallback> CALLBACKS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(GameEventSystem.ALL_EVENT_KEY, ClientGameEventSystem::handleAllEvent);
        map.put(SlimeRainGameEvent.KEY, ClientGameEventSystem::handleSlimeRain);
        map.put(BloodMoonGameEvent.KEY, ClientGameEventSystem::handleBloodMoon);
        map.put(GoblinArmyGameEvent.KEY, ClientGameEventSystem::handleGoblinArmy);
        // todo 事件注册
    });
    public static final ResourceLocation NO_MOON_TEXTURE = Confluence.asResource("textures/environment/no_moon.png");
    private static final ResourceLocation BLOOD_MOON_TEXTURE = Confluence.asResource("textures/environment/specific_moon_tr_blood_full_moon.png");
    public static @Nullable ResourceLocation moonTexture;
    public static @Nullable Vector3f lightTextureColor;

    public static void handle(Player player, List<ResourceKey<? extends GameEvent>> keys, boolean start) {
        for (ResourceKey<? extends GameEvent> key : keys) {
            GameEventSyncCallback callback = CALLBACKS.get(key);
            if (callback != null) {
                callback.call(player, start);
            }
        }
    }

    public static void handleAllEvent(Player player, boolean start) {
        for (GameEventSyncCallback callback : CALLBACKS.values()) {
            callback.call(player, start);
        }
    }

    public static void handleSlimeRain(Player player, boolean start) {

    }

    public static void handleBloodMoon(Player player, boolean start) {
        if (start) {
            moonTexture = BLOOD_MOON_TEXTURE;
            lightTextureColor = new Vector3f(1, 0, 0);
        } else {
            moonTexture = null;
            lightTextureColor = null;
        }
    }

    public static void handleGoblinArmy(Player player, boolean start) {

    }

    @FunctionalInterface
    public interface GameEventSyncCallback {
        void call(Player player, boolean start);
    }
}
