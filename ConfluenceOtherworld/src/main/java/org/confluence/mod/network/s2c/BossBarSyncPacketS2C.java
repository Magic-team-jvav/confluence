package org.confluence.mod.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientBossBarTracker;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 所有 Boss 共用有序的血量段；普通 Boss 一段，双子依次为激光眼与魔焰眼。
public record BossBarSyncPacketS2C(UUID eventId, ResourceLocation styleId, List<Health> health,
                                   boolean visible)
        implements IPortPacket.S2C {
    private static final int MAX_HEALTH_SEGMENTS = 16;
    public static final ResourceLocation ID = Confluence.asResource("boss_bar_sync");
    public static final PortStreamCodec<FriendlyByteBuf, BossBarSyncPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public BossBarSyncPacketS2C decode(FriendlyByteBuf buffer) {
            UUID eventId = buffer.readUUID();
            ResourceLocation styleId = buffer.readResourceLocation();
            boolean visible = buffer.readBoolean();
            int count = buffer.readVarInt();
            if (count < 0 || count > MAX_HEALTH_SEGMENTS)
                throw new IllegalArgumentException("Invalid boss bar segment count: " + count);
            List<Health> health = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
                health.add(new Health(buffer.readFloat(), buffer.readFloat()));
            return new BossBarSyncPacketS2C(eventId, styleId, health, visible);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, BossBarSyncPacketS2C value) {
            buffer.writeUUID(value.eventId);
            buffer.writeResourceLocation(value.styleId);
            buffer.writeBoolean(value.visible);
            buffer.writeVarInt(value.health.size());
            for (Health health : value.health) {
                buffer.writeFloat(health.current);
                buffer.writeFloat(health.maximum);
            }
        }
    };

    // 固定快照，避免发送后列表被修改，导致客户端各段血量不一致。
    public BossBarSyncPacketS2C {
        if (health.size() > MAX_HEALTH_SEGMENTS)
            throw new IllegalArgumentException("Too many boss bar segments: " + health.size());
        health = List.copyOf(health);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        if (visible) ClientBossBarTracker.synchronize(eventId, styleId, health);
        else ClientBossBarTracker.remove(eventId);
    }

    public record Health(float current, float maximum) {}
}
