package org.confluence.mod.client.handler;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientBossBarTracker {
    private static final Map<UUID, BossBarData> ACTIVE_BARS = new HashMap<>();

    private ClientBossBarTracker() {}

    public static void synchronize(UUID eventId, ResourceLocation entityType, float health, float maximumHealth) {
        ACTIVE_BARS.put(eventId, new BossBarData(entityType, health, maximumHealth));
    }

    public static @Nullable BossBarData get(UUID eventId) {
        return ACTIVE_BARS.get(eventId);
    }

    public static void remove(UUID eventId) {
        ACTIVE_BARS.remove(eventId);
    }

    public static void clear() {
        ACTIVE_BARS.clear();
    }

    public record BossBarData(ResourceLocation entityType, float health, float maximumHealth) {}
}
