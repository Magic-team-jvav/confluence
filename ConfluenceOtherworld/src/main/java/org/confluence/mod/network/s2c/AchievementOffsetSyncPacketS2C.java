package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.network.IPacket;

import java.util.HashMap;
import java.util.Map;

public record AchievementOffsetSyncPacketS2C(Map<ResourceLocation, AchievementOffset> value) implements IPacketS2C {
    public static final Type<AchievementOffsetSyncPacketS2C> TYPE = IPacket.createType("achievement_offset_sync");
    public static final StreamCodec<ByteBuf, AchievementOffsetSyncPacketS2C> STREAM_CODEC = LibStreamCodecUtils
            .map(HashMap::new, ResourceLocation.STREAM_CODEC, AchievementOffset.STREAM_CODEC)
            .map(AchievementOffsetSyncPacketS2C::new, AchievementOffsetSyncPacketS2C::value);

    @Override
    public Type<AchievementOffsetSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        AchievementOffsetLoader.handle(value);
    }

    public static void sendToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new AchievementOffsetSyncPacketS2C(AchievementOffsetLoader.getInstance().getRegisteredAchievements()));
    }
}
