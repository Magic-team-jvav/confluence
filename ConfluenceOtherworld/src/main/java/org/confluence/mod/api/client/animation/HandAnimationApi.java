package org.confluence.mod.api.client.animation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.util.ModGunUtils;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.Objects;

public final class HandAnimationApi {
    private HandAnimationApi() {
    }

    public static boolean play(GeoItem animatable, ItemStack itemStack, ServerPlayer serverPlayer, HandAnimationProfile profile, HandAnimationAction action) {
        requireArguments(animatable, itemStack, serverPlayer, profile, action);
        boolean played = false;
        for (HandAnimationChannel channel : profile.channels()) {
            if (channel.clip(action).isEmpty()) {
                continue;
            }
            ModGunUtils.stopAndPlayAnim(animatable, itemStack, serverPlayer, channel.name(), action.id());
            played = true;
        }
        return played;
    }

    /// Stop a triggered action on every channel that declares it. Actions are
    /// channel-local in GeckoLib, so stopping one controller is not enough for
    /// profiles that split hand/camera and weapon animations.
    public static boolean stop(GeoItem animatable, ItemStack itemStack, ServerPlayer serverPlayer, HandAnimationProfile profile, HandAnimationAction action) {
        requireArguments(animatable, itemStack, serverPlayer, profile, action);
        long instanceId = GeoItem.getOrAssignId(itemStack, serverPlayer.serverLevel());
        boolean stopped = false;
        for (HandAnimationChannel channel : profile.channels()) {
            if (channel.clip(action).isEmpty()) {
                continue;
            }
            animatable.stopTriggeredAnim(serverPlayer, instanceId, channel.name(), action.id());
            stopped = true;
        }
        return stopped;
    }

    private static void requireArguments(GeoItem animatable, ItemStack itemStack, ServerPlayer serverPlayer, HandAnimationProfile profile, HandAnimationAction action) {
        Objects.requireNonNull(animatable, "Animatable must not be null");
        Objects.requireNonNull(itemStack, "Item stack must not be null");
        Objects.requireNonNull(serverPlayer, "Server player must not be null");
        Objects.requireNonNull(profile, "Animation profile must not be null");
        Objects.requireNonNull(action, "Animation action must not be null");
    }
}
