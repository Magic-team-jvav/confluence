package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;

import java.util.Map;

public record AchievementOffsetSyncPacketS2C(Object2BooleanMap<ResourceLocation> value) implements IPacketS2C {
    public static final Type<AchievementOffsetSyncPacketS2C> TYPE = Confluence.createType("achievement_offset_sync");
    public static final StreamCodec<ByteBuf, AchievementOffsetSyncPacketS2C> STREAM_CODEC = LibStreamCodecUtils.object2BooleanMap(ResourceLocation.STREAM_CODEC)
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
        Object2BooleanMap<ResourceLocation> map = new Object2BooleanOpenHashMap<>();
        for (Map.Entry<ResourceLocation, AchievementOffset> entry : AchievementOffsetLoader.getDisplayOffset().entrySet()) {
            map.put(entry.getKey(), entry.getValue().hideLink());
        }
        PacketDistributor.sendToPlayer(player, new AchievementOffsetSyncPacketS2C(map));
    }
}
