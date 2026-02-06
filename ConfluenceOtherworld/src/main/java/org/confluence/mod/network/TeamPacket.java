package org.confluence.mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;

public record TeamPacket(int playerId, Team team, boolean pvp) implements IPacket {
    public static final byte TEAM_MASK = 0b0001_1111;
    public static final byte PVP_MASK = 0b0100_0000;
    public static final Type<TeamPacket> TYPE = Confluence.createType("team");
    public static final StreamCodec<FriendlyByteBuf, TeamPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TeamPacket decode(FriendlyByteBuf buffer) {
            byte b = buffer.readByte();
            return new TeamPacket(buffer.readVarInt(), Team.TEAMS[b & TEAM_MASK], (b & PVP_MASK) == PVP_MASK);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, TeamPacket value) {
            buffer.writeByte((byte) (value.team.ordinal() | (value.pvp ? PVP_MASK : 0)));
            buffer.writeVarInt(value.playerId);
        }
    };

    @Override
    public Type<TeamPacket> type() {
        return TYPE;
    }

    @Override
    public void c2s(ServerPlayer player) {
        if (player.level().getEntity(playerId) instanceof Player target) {
            PlayerSpecialData data = PlayerSpecialData.of(target);
            PlayerList playerList = player.server.getPlayerList();
            int textColor = team.getColor().getTextColor();
            if (data.getTeam() != team) {
                if (team == Team.WHITE) {
                    playerList.broadcastSystemMessage(Component.translatable(
                            "message.confluence.leave_team", target.getName()
                    ).withColor(textColor), false);
                } else {
                    playerList.broadcastSystemMessage(Component.translatable(
                            "message.confluence.join_team", target.getName(), team.getLowerCaseName()
                    ).withColor(textColor), false);
                }
            }
            if (data.isPvP() != pvp) {
                playerList.broadcastSystemMessage(Component.translatable(
                        pvp ? "message.confluence.enable_pvp" : "message.confluence.disable_pvp", target.getName()
                ).withColor(textColor), false);
            }
            data.setTeam(team);
            data.setPvP(pvp);
        }
        PacketDistributor.sendToPlayersTrackingEntity(player, this);
    }

    @Override
    public void s2c(Player player) {
        if (player.level().getEntity(playerId) instanceof Player target) {
            PlayerSpecialData data = PlayerSpecialData.of(target);
            data.setTeam(team);
            data.setPvP(pvp);
        }
    }

    /// local设置时调用，然后由server广播到remote
    public static void sendToServer(Player player) {
        PacketDistributor.sendToServer(makePacket(player));
    }

    /// 自动同步
    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target) {
        PacketDistributor.sendToPlayer(sendTo, makePacket(target));
    }

    /// server设置时调用
    public static void broadcast(ServerPlayer player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, makePacket(player));
    }

    private static TeamPacket makePacket(Player player) {
        PlayerSpecialData data = PlayerSpecialData.of(player);
        return new TeamPacket(player.getId(), data.getTeam(), data.isPvP());
    }
}
