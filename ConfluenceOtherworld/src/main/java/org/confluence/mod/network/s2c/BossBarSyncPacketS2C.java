package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientBossBarTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 所有 Boss 共用有序的血量段；普通 Boss 一段，双子依次为激光眼与魔焰眼。
public record BossBarSyncPacketS2C(UUID eventId, ResourceLocation styleId, List<Health> health,
                                   boolean visible)
        implements IPacketS2C {
    private static final int MAX_HEALTH_SEGMENTS = 16;
    public static final Type<BossBarSyncPacketS2C> TYPE = Confluence.createType("boss_bar_sync");
    public static final StreamCodec<ByteBuf, BossBarSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public BossBarSyncPacketS2C decode(ByteBuf buffer) {
            UUID eventId = UUIDUtil.STREAM_CODEC.decode(buffer);
            ResourceLocation styleId = ResourceLocation.STREAM_CODEC.decode(buffer);
            boolean visible = buffer.readBoolean();
            int count = ByteBufCodecs.VAR_INT.decode(buffer);
            if (count < 0 || count > MAX_HEALTH_SEGMENTS)
                throw new IllegalArgumentException("Invalid boss bar segment count: " + count);
            List<Health> health = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
                health.add(new Health(buffer.readFloat(), buffer.readFloat()));
            return new BossBarSyncPacketS2C(eventId, styleId, health, visible);
        }

        @Override
        public void encode(ByteBuf buffer, BossBarSyncPacketS2C value) {
            UUIDUtil.STREAM_CODEC.encode(buffer, value.eventId);
            ResourceLocation.STREAM_CODEC.encode(buffer, value.styleId);
            buffer.writeBoolean(value.visible);
            ByteBufCodecs.VAR_INT.encode(buffer, value.health.size());
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
    public Type<BossBarSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        if (visible) ClientBossBarTracker.synchronize(eventId, styleId, health);
        else ClientBossBarTracker.remove(eventId);
    }

    public record Health(float current, float maximum) {}
}
