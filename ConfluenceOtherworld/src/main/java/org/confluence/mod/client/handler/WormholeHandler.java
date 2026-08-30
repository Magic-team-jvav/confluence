package org.confluence.mod.client.handler;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.network.c2s.WormholeRequestPlayerDataPacketC2S;
import org.confluence.mod.network.s2c.WormholePlayerDataSyncPacketS2C;
import org.jetbrains.annotations.NotNull;

public final class WormholeHandler {
    public static int distanceToSqr(Player player, WormholePlayerDataSyncPacketS2C.Data data, boolean isSqr) {
        Vec3i pos = data.pos();
        float sqrt;
        if (isSqr) {
            sqrt = (float) player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
        } else {
            sqrt = Mth.sqrt((float) player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()));
        }
        return Mth.floor(sqrt);
    }

    public static void work(WormholeRequestPlayerDataPacketC2S packet, ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        Team team = getTeam(player);
        WormholePlayerDataSyncPacketS2C.sendToClient(player, level.getServer().getPlayerList().getPlayers().stream()
                .filter(v ->
                        v.getUUID() != player.getUUID() &&
                                judgment(team, getTeam(v))
                ).toList());
    }

    public static boolean judgment(Player sourcePlayer, Player targetPlayer) {
        if (!sourcePlayer.isAlive() || !targetPlayer.isAlive()) {
            return false;
        }

        if (sourcePlayer.isSpectator() || targetPlayer.isSpectator()) {
            return false;
        }

        return judgment(getTeam(sourcePlayer),
                getTeam(targetPlayer));
    }

    public static boolean judgment(Team sourcePlayer, Team targetPlayer) {
        if (sourcePlayer == Team.WHITE || targetPlayer == Team.WHITE) {
            return false;
        }

        return sourcePlayer == targetPlayer;
    }

    private static @NotNull Team getTeam(Player sourcePlayer) {
        return PlayerSpecialData.of(sourcePlayer).getTeam();
    }
}
