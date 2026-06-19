package org.confluence.mod.client.handler;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.container.WormholeScreen;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.network.c2s.WormholeRequestPlayerDataPacketC2S;
import org.confluence.mod.network.s2c.WormholePlayerDataSyncPacketS2C;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.GeckoLibCache;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class WormholeHandler {
    public static final String WORMHOLE_DIMENSION_PATH = "textures/gui/sprites/container/wormhole/dimension";

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

    public static boolean judgment(Player targetPlayer, Team team) {
        if (team == Team.WHITE || !targetPlayer.isAlive() || targetPlayer.isSpectator()) {
            return false;
        }
        Team team1 = PlayerSpecialData.of(targetPlayer).getTeam();
        if (team1 == Team.WHITE) {
            return false;
        }
        return team1 == team;
    }

    public static boolean judgment(Player targetPlayer, Player player) {
        return !player.isAlive() && player.isSpectator() && judgment(targetPlayer, PlayerSpecialData.of(player).getTeam());
    }

    public static CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        var map = new Object2ObjectOpenHashMap<ResourceLocation, ResourceLocation>();
        return CompletableFuture.runAsync(WormholeScreen.DIMENSION_TEXTURES::clear, backgroundExecutor)
                .thenRunAsync(() -> CompletableFuture.supplyAsync(() -> resourceManager.listResources(WORMHOLE_DIMENSION_PATH, rl ->
                                rl.getNamespace().equals(Confluence.MODID) && rl.getPath().endsWith(".png")), backgroundExecutor)
                        .thenApplyAsync(getMapFunction(), backgroundExecutor)
                        .thenAcceptAsync(map::putAll), backgroundExecutor)
                .thenCompose(stage::wait).thenAcceptAsync(empty -> {
                    WormholeScreen.DIMENSION_TEXTURES.putAll(map);
                });
    }

    private static @NotNull Function<Map<ResourceLocation, Resource>, Object2ObjectOpenHashMap<ResourceLocation, ResourceLocation>> getMapFunction() {
        return resources -> {
            var tasks = new Object2ObjectOpenHashMap<ResourceLocation, ResourceLocation>();
            for (ResourceLocation resource : resources.keySet()) {
                String[] split = resource.getPath().substring(WORMHOLE_DIMENSION_PATH.length() + 1).split("/", 2);
                if (split.length != 2) continue;
                String substring = split[1].substring(0, split[1].length() - 4);
                ResourceLocation k = ResourceLocation.fromNamespaceAndPath(split[0], substring);
                ResourceLocation v = ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), "container/wormhole/dimension/" + split[0] + "/" + substring);
                tasks.put(k, v);
            }
            return tasks;
        };
    }
}
