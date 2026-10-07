package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WormholeHandlerClient;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.Team;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/// 将服务端确认的队友、所在维度和位置发送给虫洞药水界面。
public record WormholePlayerDataSyncPacketS2C(Map<UUID, Data> data) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("wormhole_player_data_sync");
    public static final PortStreamCodec<FriendlyByteBuf, WormholePlayerDataSyncPacketS2C> STREAM_CODEC =
            LibStreamCodecUtils.map(HashMap::new, LibStreamCodecUtils.UUID, Data.STREAM_CODEC)
                    .map(WormholePlayerDataSyncPacketS2C::new, WormholePlayerDataSyncPacketS2C::data);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        WormholeHandlerClient.work(this);
    }

    public static <T extends Player> void sendToClient(ServerPlayer player, List<T> players) {
        Map<UUID, Data> data = players.stream().map(target -> Map.entry(target.getUUID(), Data.of(target)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        PortPacketDistributor.sendToPlayer(player, new WormholePlayerDataSyncPacketS2C(data));
    }

    public record Data(UUID uuid, Team team, ResourceKey<Level> levelResourceKey, Vec3i pos) {
        public static final PortStreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = new PortStreamCodec<>() {
            @Override
            public Data decode(FriendlyByteBuf buffer) {
                return new Data(buffer.readUUID(), Team.STREAM_CODEC.decode(buffer),
                        ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation()), buffer.readBlockPos());
            }

            @Override
            public void encode(FriendlyByteBuf buffer, Data value) {
                buffer.writeUUID(value.uuid());
                Team.STREAM_CODEC.encode(buffer, value.team());
                buffer.writeResourceLocation(value.levelResourceKey().location());
                buffer.writeBlockPos(new BlockPos(value.pos()));
            }
        };

        public static Data of(Player player) {
            return new Data(player.getUUID(), PlayerSpecialData.of(player).getTeam(),
                    player.level().dimension(), player.blockPosition());
        }
    }
}
