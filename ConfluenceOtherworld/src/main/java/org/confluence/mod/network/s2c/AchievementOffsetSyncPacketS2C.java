package org.confluence.mod.network.s2c;

import PortLib.extensions.net.minecraft.resources.ResourceLocation.PortResourceLocationExtension;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Map;

public record AchievementOffsetSyncPacketS2C(
        Object2BooleanMap<ResourceLocation> value) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("achievement_offset_sync");
    public static final PortStreamCodec<ByteBuf, AchievementOffsetSyncPacketS2C> STREAM_CODEC = LibStreamCodecUtils.object2BooleanMap(PortResourceLocationExtension.streamCodec())
            .map(AchievementOffsetSyncPacketS2C::new, AchievementOffsetSyncPacketS2C::value);

    @Override
    public ResourceLocation identifier() {
        return ID;
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
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new AchievementOffsetSyncPacketS2C(map));
    }
}
