package org.confluence.mod.network.s2c;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WormholeHandlerClient;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record WormholePlayerDataSyncPacketS2C(
        Map<UUID, Data> data) implements IPacketS2C {
    public static final CustomPacketPayload.Type<WormholePlayerDataSyncPacketS2C> TYPE = Confluence.createType("wormhole_player_data_sync");
    public static final StreamCodec<FriendlyByteBuf, WormholePlayerDataSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, Data.STREAM_CODEC), WormholePlayerDataSyncPacketS2C::data,
            WormholePlayerDataSyncPacketS2C::new
    );

    @Override
    public CustomPacketPayload.Type<WormholePlayerDataSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        WormholeHandlerClient.work(this);
    }

    public static <T extends Player> void sendToClient(ServerPlayer serverPlayer, List<T> player) {
        WormholePlayerDataSyncPacketS2C payload = new WormholePlayerDataSyncPacketS2C(player.stream()
                .map(player1 -> Map.entry(player1.getUUID(), Data.of(player1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }

    public record Data(
            UUID uuid,
            Team team,
            ResourceKey<Level> levelResourceKey,
            Vec3i pos
    ) {
        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, Data::uuid,
                Team.STREAM_CODEC, Data::team,
                ByteBufCodecs.fromCodec(Level.RESOURCE_KEY_CODEC), Data::levelResourceKey,
                ByteBufCodecs.fromCodec(Vec3i.CODEC), Data::pos,
                Data::new
        );

        public static Data of(Player player) {
            return new Data(
                    player.getUUID(),
                    PlayerSpecialData.of(player).getTeam(),
                    player.level().dimension(),
                    player.blockPosition()
            );
        }
    }
}
