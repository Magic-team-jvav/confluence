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

import java.util.UUID;

///
public record BossBarSyncPacketS2C(UUID eventId, ResourceLocation entityType, float health,
                                   float maximumHealth, boolean visible) implements IPacketS2C {
    public static final Type<BossBarSyncPacketS2C> TYPE = Confluence.createType("boss_bar_sync");
    public static final StreamCodec<ByteBuf, BossBarSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, BossBarSyncPacketS2C::eventId,
            ResourceLocation.STREAM_CODEC, BossBarSyncPacketS2C::entityType,
            ByteBufCodecs.FLOAT, BossBarSyncPacketS2C::health,
            ByteBufCodecs.FLOAT, BossBarSyncPacketS2C::maximumHealth,
            ByteBufCodecs.BOOL, BossBarSyncPacketS2C::visible,
            BossBarSyncPacketS2C::new
    );

    @Override
    public Type<BossBarSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        if (visible) {
            ClientBossBarTracker.synchronize(eventId, entityType, health, maximumHealth);
        } else {
            ClientBossBarTracker.remove(eventId);
        }
    }
}
