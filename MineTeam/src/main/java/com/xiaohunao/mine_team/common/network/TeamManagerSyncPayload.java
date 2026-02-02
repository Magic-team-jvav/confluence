package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeamManagerSyncPayload(TeamManager manager) implements CustomPacketPayload {
    public static final Type<TeamManagerSyncPayload> TYPE = new Type<>(MineTeam.asResource("team_manager_sync"));
    public static final StreamCodec<FriendlyByteBuf, TeamManagerSyncPayload> STREAM_CODEC = TeamManager.STREAM_CODEC.map(TeamManagerSyncPayload::new, TeamManagerSyncPayload::manager);

    @Override
    public Type<TeamManagerSyncPayload> type() {
        return TYPE;
    }

    public void clientHandle(IPayloadContext context) {
        context.enqueueWork(() -> TeamManager.of(context.player().level()).copyFrom(manager));
    }

    public static void sendToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new TeamManagerSyncPayload(TeamManager.of(player.level())));
    }
}
