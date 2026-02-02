package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeamAttachmentSyncPayload(int entityId, TeamAttachment attachment) implements CustomPacketPayload {
    public static final Type<TeamAttachmentSyncPayload> TYPE = new Type<>(MineTeam.asResource("team_sync"));
    public static final StreamCodec<FriendlyByteBuf, TeamAttachmentSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TeamAttachmentSyncPayload::entityId,
            TeamAttachment.STREAM_CODEC, TeamAttachmentSyncPayload::attachment,
            TeamAttachmentSyncPayload::new
    );

    @Override
    public Type<TeamAttachmentSyncPayload> type() {
        return TYPE;
    }

    public void clientHandle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(entityId);
            if (entity != null) {
                TeamAttachment.of(entity).sync(attachment);
            }
        });
    }

    public void serverHandle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(entityId);
            if (entity != null) {
                TeamAttachment.of(entity).sync(attachment);
            }
        });
    }

    public static void sendToClient(ServerPlayer player, Entity entity) {
        PacketDistributor.sendToPlayer(player, new TeamAttachmentSyncPayload(entity.getId(), TeamAttachment.of(entity)));
    }

    public static void sendToServer(Entity entity) {
        PacketDistributor.sendToServer(new TeamAttachmentSyncPayload(entity.getId(), TeamAttachment.of(entity)));
    }
}
