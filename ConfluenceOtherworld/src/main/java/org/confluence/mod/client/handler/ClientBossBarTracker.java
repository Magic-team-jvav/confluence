package org.confluence.mod.client.handler;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.network.s2c.BossBarSyncPacketS2C.Health;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ClientBossBarTracker {
    private static final Map<UUID, BossBarData> ACTIVE_BARS = new HashMap<>();

    private ClientBossBarTracker() {}

    public static void synchronize(UUID eventId, ResourceLocation styleId, List<Health> health) {
        BossBarData previous = ACTIVE_BARS.get(eventId);
        // 同一样式下收到新血量时沿用起播时间，切换样式才从首帧播放。
        long start = previous != null && previous.styleId().equals(styleId) ? previous.styleSince() : Util.getMillis();
        ACTIVE_BARS.put(eventId, new BossBarData(styleId, List.copyOf(health), start));
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

    public record BossBarData(ResourceLocation styleId, List<Health> health, long styleSince) {}
}
