package org.confluence.mod.client.handler;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.confluence.mod.client.gui.container.WormholeScreen;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.network.c2s.WormholeRequestPlayerDataPacketC2S;
import org.confluence.mod.network.s2c.WormholePlayerDataSyncPacketS2C;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public final class WormholeHandler {
    public static void work(WormholePlayerDataSyncPacketS2C packet) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ClientPacketListener connection = localPlayer.connection;

        WormholeScreen.setPlayerList(packet.data().entrySet().stream()
                .map(data -> Pair.of(data.getValue(), connection.getPlayerInfo(data.getKey())))
                .sorted(Comparator.comparingInt(calculateDistance(localPlayer))
                        .thenComparing(data -> data.getFirst().levelResourceKey())
                        .thenComparingInt(data -> data.getSecond().getTeam() == null ? -1 : 0)
                        .thenComparing(data -> Optionull.mapOrDefault(data.getSecond().getTeam(), PlayerTeam::getName, ""))
                        .thenComparing(data -> data.getSecond().getProfile().getName(), String::compareToIgnoreCase))
                .toList());
    }

    private static @NotNull ToIntFunction<Pair<WormholePlayerDataSyncPacketS2C.Data, PlayerInfo>> calculateDistance(LocalPlayer localPlayer) {
        return data -> distanceToSqr(localPlayer, data.getFirst(), true);
    }

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
        Team team = PlayerSpecialData.of(player).getTeam();
        var playerList = level.getServer().getPlayerList().getPlayers().stream()
                .filter(v -> v.getUUID() != player.getUUID() && judgment(v, team)).toList();
        WormholePlayerDataSyncPacketS2C.sendToClient(player, playerList);
    }

    public static boolean judgment(Player player, Team team) {
        if (team == Team.WHITE || !player.isAlive() || player.isSpectator()) {
            return false;
        }
        Team team1 = PlayerSpecialData.of(player).getTeam();
        if (team1 == Team.WHITE) {
            return false;
        }
        return team1 == team;
    }
}
