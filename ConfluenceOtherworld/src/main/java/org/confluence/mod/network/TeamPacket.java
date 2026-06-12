package org.confluence.mod.network;

import PortLib.extensions.net.minecraft.network.chat.MutableComponent.PortMutableComponentExtension;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record TeamPacket(int playerId, Team team, boolean pvp) implements IPortPacket {
    public static final byte TEAM_MASK = 0b0001_1111;
    public static final byte PVP_MASK = 0b0100_0000;
    public static final ResourceLocation ID = Confluence.asResource("team");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, TeamPacket> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public TeamPacket decode(PortRegistryFriendlyByteBuf buffer) {
            byte b = buffer.readByte();
            return new TeamPacket(buffer.readVarInt(), Team.TEAMS[b & TEAM_MASK], (b & PVP_MASK) != 0);
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, TeamPacket value) {
            buffer.writeByte((byte) (value.team.ordinal() | (value.pvp ? PVP_MASK : 0)));
            buffer.writeVarInt(value.playerId);
        }
    };

    @Override
    public void handle(Context context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            c2s(serverPlayer);
        } else if (context.player() != null) {
            s2c(context.player());
        }
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public void c2s(ServerPlayer player) {
        if (player.level().getEntity(playerId) instanceof Player target) {
            PlayerSpecialData data = PlayerSpecialData.of(target);
            PlayerList playerList = player.server.getPlayerList();
            int textColor = team.getColor().getTextColor();
            if (data.getTeam() != team) {
                Component msg;
                if (team == Team.WHITE) {
                    msg = PortMutableComponentExtension.withColor(Component.translatable(
                            "message.confluence.leave_team", target.getName()
                    ), textColor);
                } else {
                    msg = PortMutableComponentExtension.withColor(Component.translatable(
                            "message.confluence.join_team", target.getName(), team.getLowerCaseName()
                    ), textColor);
                }
                playerList.broadcastSystemMessage(msg, false);
                data.setTeam(team);
            }
            if (data.isPvP() != pvp) {
                Component msg = PortMutableComponentExtension.withColor(Component.translatable(
                        pvp ? "message.confluence.enable_pvp" : "message.confluence.disable_pvp", target.getName()
                ), textColor);
                playerList.broadcastSystemMessage(msg, false);
                data.setPvP(pvp);
            }
        }
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntity(player, this);
    }

    public void s2c(Player player) {
        if (player.level().getEntity(playerId) instanceof Player target) {
            PlayerSpecialData data = PlayerSpecialData.of(target);
            data.setTeam(team);
            data.setPvP(pvp);
        }
    }

    /// local设置时调用，然后由server广播到remote
    public static void sendToServer(Player player) {
        Confluence.NETWORK_HANDLER.sendToServer(makePacket(player));
    }

    /// 自动同步
    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target) {
        Confluence.NETWORK_HANDLER.sendToPlayer(sendTo, makePacket(target));
    }

    /// server设置时调用
    public static void broadcast(ServerPlayer player) {
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntityAndSelf(player, makePacket(player));
    }

    private static TeamPacket makePacket(Player player) {
        PlayerSpecialData data = PlayerSpecialData.of(player);
        return new TeamPacket(player.getId(), data.getTeam(), data.isPvP());
    }
}
