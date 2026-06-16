package org.confluence.mod.common.data.saved;

import PortLib.extensions.net.minecraft.core.HolderLookup.PortHolderLookupExtension;
import PortLib.extensions.net.minecraft.network.chat.MutableComponent.PortMutableComponentExtension;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum MeteoriteTracker {
    INSTANCE;
    public static final ResourceKey<ConfiguredFeature<?, ?>> METEORITE = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "meteorite");

    private transient boolean shouldGenerate = true;
    public boolean spawnAtNextNight = false;
    @NotNull BlockPos location = BlockPos.ZERO;
    int tickUntilLanding = 0;

    public void tick(ServerLevel level) {
        if (!CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        if (spawnAtNextNight && LibDateUtils.getDayTime(level) == LibDateUtils._00$00) {
            this.spawnAtNextNight = false;
            generateLandingDetail(level, Mth.randomBetweenInclusive(level.random, 200, 400));
            Component message = PortMutableComponentExtension.withColor(Component.translatable("event.confluence.meteorite.ready"), GlobalColors.EVENT.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
        if (tickUntilLanding == 0) {
            // this.location = BlockPos.ZERO; 注释掉这行，阻止重置
            this.shouldGenerate = true;
        } else if (tickUntilLanding > 0) {
            tickUntilLanding--;
            if (tickUntilLanding == 0) {
                ChunkPos chunkPos = new ChunkPos(location);
                place(level, chunkPos.x, chunkPos.z, !level.getForcedChunks().contains(chunkPos.toLong()), new BlockPos(location));
                ConfluenceData.get(level).setDirty();
            }
        }
    }

    public void generateLandingDetail(ServerLevel level, int landingTime) {
        if (!shouldGenerate || !CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        this.shouldGenerate = false;

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
        int dist = level.getChunkSource().chunkMap.getDistanceManager().playerTicketManager.viewDistance;
        do {
            if (!players.isEmpty()) {
                Iterator<ServerPlayer> iterator = players.iterator();
                while (iterator.hasNext()) {
                    ServerPlayer player = iterator.next();
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
        LongSet forcedChunks = level.getForcedChunks();
        do {
            while ((chunkHolder = chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(x, z))) != null && chunkHolder.getTicketLevel() >= 34) {
                if (level.random.nextBoolean()) x += xStep;
                else z += zStep;
            }
            boolean requires = !forcedChunks.contains(ChunkPos.asLong(x, z));
            if (requires) level.setChunkForced(x, z, true);
            int bx = SectionPos.sectionToBlockCoord(x, 7);
            int bz = SectionPos.sectionToBlockCoord(z, 7);
            landingPos.set(bx, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz), bz);
            if (requires) level.setChunkForced(x, z, false);
            x += xStep;
            z += zStep;
        } while (!level.getBlockState(landingPos).getFluidState().isEmpty());

        this.shouldGenerate = true;
        this.location = landingPos;
        this.tickUntilLanding = landingTime;
        MeteoriteLocationPacketS2C.sendToAll(location, landingTime);
        ConfluenceData.get(level).setDirty();
    }

    private void place(ServerLevel level, int chunkX, int chunkZ, boolean withForceChunk, BlockPos origin) {
        boolean placed = false;
        if (withForceChunk) level.setChunkForced(chunkX, chunkZ, true);
        try {
            PortHolderLookupExtension.Provider.holderOrThrow(level.registryAccess(), METEORITE)
                    .value().place(level, level.getChunkSource().getGenerator(), level.random, origin);
            placed = true;
        } catch (Exception ignored) {}
        if (withForceChunk) level.setChunkForced(chunkX, chunkZ, false);

        if (placed) {
            Component message = PortMutableComponentExtension.withColor(Component.translatable("event.confluence.meteorite"), GlobalColors.MESSAGE.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
            Confluence.LOGGER.debug("A meteorite has been landed, which at [{}]", origin.toShortString());
        }
    }

    public void deserialize(CompoundTag nbt) {
        this.spawnAtNextNight = nbt.getBoolean("spawnAtNextNight");
        this.location = NbtUtils.readBlockPos(nbt, "meteoriteLocation").orElse(BlockPos.ZERO);
        this.tickUntilLanding = nbt.getInt("tickUtilMeteoriteLanding");
    }

    public void serialize(CompoundTag nbt) {
        nbt.putBoolean("spawnAtNextNight", spawnAtNextNight);
        nbt.put("meteoriteLocation", NbtUtils.writeBlockPos(location));
        nbt.putInt("tickUtilMeteoriteLanding", tickUntilLanding);
    }

    public static void spawnMeteor(ServerLevel level) {
        if (KillBoard.INSTANCE.isAnyDefeated(TEBossEntities.EATER_OF_WORLDS.get(), TEBossEntities.BRAIN_OF_CTHULHU.get()) && level.random.nextFloat() < 0.02F) {
            INSTANCE.spawnAtNextNight = true;
        }
    }
}
