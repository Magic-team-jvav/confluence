package org.confluence.mod.common.data.saved;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import org.confluence.lib.color.GlobalColors;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class MeteoriteTracker {
    public static final MeteoriteTracker INSTANCE = new MeteoriteTracker();

    private transient volatile boolean shouldGenerate = true;
    public volatile boolean spawnAtNextNight = false;
    volatile BlockPos location = BlockPos.ZERO;
    volatile AtomicInteger tickUntilLanding = new AtomicInteger();

    public void tick(ServerLevel level) {
        if (!CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        if (spawnAtNextNight && level.getDayTime() % 24000L == 18000L) { // midnight
            this.spawnAtNextNight = false;
            generateLandingDetail(level, level.random.nextInt(200, 401));
            Component message = Component.translatable("event.confluence.meteorite.ready").withColor(GlobalColors.EVENT.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
        if (tickUntilLanding.get() == 0) {
            this.location = BlockPos.ZERO;
            this.shouldGenerate = true;
        } else if (tickUntilLanding.get() > 0) {
            tickUntilLanding.decrementAndGet();
            if (tickUntilLanding.get() == 0) {
                ChunkPos chunkPos = new ChunkPos(location);
                place(level, chunkPos.x, chunkPos.z, !level.getForcedChunks().contains(chunkPos.toLong()), new BlockPos(location));
                ConfluenceData.get(level).setDirty();
            }
        }
    }

    public void generateLandingDetail(ServerLevel level, int landingTime) {
        if (!shouldGenerate || !CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        this.shouldGenerate = false;
        CompletableFuture.supplyAsync(() -> {
            // 获取玩家数量最小的象限
            int[][] quadrant = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            int[] counts = new int[4];
            for (int i = 0; i < 4; i++) {
                int x = quadrant[i][0];
                int z = quadrant[i][1];
                for (ServerPlayer player : level.players()) {
                    if (Mth.sign(player.getX()) == x && Mth.sign(player.getZ()) == z) counts[i]++;
                }
            }
            int min = 0;
            for (int i = 0; i < 4; i++) if (counts[i] < counts[min]) min = i;
            int xStep = quadrant[min][0];
            int zStep = quadrant[min][1];
            // 获取未被加载的区块
            int x = 0, z = 0;
            List<ServerPlayer> players = new ArrayList<>(level.players());
            ChunkHolder chunkHolder;
            ChunkMap chunkMap = level.getChunkSource().chunkMap;
            do {
                if (!players.isEmpty()) {
                    Iterator<ServerPlayer> iterator = players.iterator();
                    while (iterator.hasNext()) {
                        ServerPlayer player = iterator.next();
                        int dist = player.requestedViewDistance();
                        int cx = SectionPos.blockToSectionCoord(player.getX());
                        int cz = SectionPos.blockToSectionCoord(player.getZ());
                        boolean removal = false;
                        if (x > cx - dist && x < cx + dist) {
                            x += xStep * dist;
                            removal = true;
                        }
                        if (z > cz - dist && z < cz + dist) {
                            z += zStep * dist;
                            removal = true;
                        }
                        if (removal || Math.abs(x) > Math.abs(cx) || Math.abs(z) > Math.abs(cz)) {
                            iterator.remove();
                        }
                    }
                }
                x += xStep;
                z += zStep;
            } while ((chunkHolder = chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(x, z))) != null && chunkHolder.getTicketLevel() >= 34);
            // 获取能放陨石的区块
            BlockPos.MutableBlockPos landingPos = new BlockPos.MutableBlockPos();
            int maxBuildHeight = level.getMaxBuildHeight();
            do {
                while ((chunkHolder = chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(x, z))) != null && chunkHolder.getTicketLevel() >= 34) {
                    if (level.random.nextBoolean()) x += xStep;
                    else z += zStep;
                }
                level.setChunkForced(x, z, true);
                landingPos.set(new ChunkPos(x, z).getBlockAt(7, maxBuildHeight, 7).mutable());
                while (level.getBlockState(landingPos).isAir()) {
                    landingPos.move(0, -1, 0);
                }
                level.setChunkForced(x, z, false);
                x += xStep;
                z += zStep;
            } while (!level.getBlockState(landingPos).getFluidState().isEmpty());

            return landingPos.immutable();
        }, Util.backgroundExecutor()).thenAccept(blockPos -> {
            this.shouldGenerate = true;
            this.location = blockPos;
            this.tickUntilLanding.set(landingTime);
            MeteoriteLocationPacketS2C.sendToAll(location, landingTime);
            ConfluenceData.get(level).setDirty();
        });
    }

    private void place(ServerLevel level, int chunkX, int chunkZ, boolean withForceChunk, BlockPos origin) {
        CompletableFuture.supplyAsync(() -> {
            boolean placed = false;
            if (withForceChunk) level.setChunkForced(chunkX, chunkZ, true);
            try {
                level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                        .getHolder(Confluence.asResource("normal_meteorite")).orElseThrow().value()
                        .place(level, level.getChunkSource().getGenerator(), level.random, origin);
                placed = true;
            } catch (Exception ignored) {}
            if (withForceChunk) level.setChunkForced(chunkX, chunkZ, false);
            return placed;
        }, Util.backgroundExecutor()).thenAccept(success -> {
            if (success) {
                Component message = Component.translatable("event.confluence.meteorite").withColor(GlobalColors.MESSAGE.get());
                level.getServer().getPlayerList().broadcastSystemMessage(message, false);
                Confluence.LOGGER.debug("A meteorite has been landed, which at [{}]", origin.toShortString());
            }
        });
    }

    public void deserialize(CompoundTag nbt) {
        this.spawnAtNextNight = nbt.getBoolean("spawnAtNextNight");
        this.location = NbtUtils.readBlockPos(nbt, "meteoriteLocation").orElse(BlockPos.ZERO);
        this.tickUntilLanding.set(nbt.getInt("tickUtilMeteoriteLanding"));
    }

    public void serialize(CompoundTag nbt) {
        nbt.putBoolean("spawnAtNextNight", spawnAtNextNight);
        nbt.put("meteoriteLocation", NbtUtils.writeBlockPos(location));
        nbt.putInt("tickUtilMeteoriteLanding", tickUntilLanding.get());
    }
}
