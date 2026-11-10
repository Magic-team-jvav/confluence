package org.confluence.mod.common.data.saved;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.worldgen.feature.MeteoriteFeature;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum MeteoriteTracker implements IGlobalData {
    INSTANCE;
    public static final ResourceKey<ConfiguredFeature<?, ?>> METEORITE = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "meteorite");

    private transient boolean shouldGenerate = true;
    public boolean spawnAtNextNight = false;
    private BlockPos location = BlockPos.ZERO;
    private int tickUntilLanding = 0;

    private final LongSet unavailableChunks = new LongOpenHashSet();

    public void tick(ServerLevel level) {
        if (!CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        if (spawnAtNextNight && LibDateUtils.getDayTime(level) == LibDateUtils._00$00) {
            this.spawnAtNextNight = false;
            generateLandingDetail(level, Mth.randomBetweenInclusive(level.random, 200, 400));
            Component message = Component.translatable("event.confluence.meteorite.ready").withColor(GlobalColors.EVENT.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
        if (tickUntilLanding == 0) {
            // this.location = BlockPos.ZERO; 注释掉这行，阻止重置
            this.shouldGenerate = true;
        } else if (tickUntilLanding > 0) {
            tickUntilLanding--;
            if (tickUntilLanding == 0) {
                ChunkPos chunkPos = new ChunkPos(location);
                place(level, chunkPos.x, chunkPos.z, new BlockPos(location));
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
        } while (isUnAvailableChunk(chunkMap, x, z));
        // 获取能放陨石的区块
        BlockPos.MutableBlockPos landingPos = new BlockPos.MutableBlockPos();
        LongSet forcedChunks = level.getForcedChunks();
        boolean liquidSurface;
        do {
            while (isUnAvailableChunk(chunkMap, x, z)) {
                if (level.random.nextBoolean()) x += xStep;
                else z += zStep;
            }
            boolean requires = !forcedChunks.contains(ChunkPos.asLong(x, z));
            if (requires) level.setChunkForced(x, z, true);
            int bx = SectionPos.sectionToBlockCoord(x, 7);
            int bz = SectionPos.sectionToBlockCoord(z, 7);
            landingPos.set(bx, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz), bz);
            liquidSurface = !level.getBlockState(landingPos).getFluidState().isEmpty();

            if (requires) level.setChunkForced(x, z, false);
            if (liquidSurface) {
                x += xStep;
                z += zStep;
            }
        } while (liquidSurface);

        this.shouldGenerate = true;
        this.location = landingPos;
        this.tickUntilLanding = landingTime;
        markUnavailable(ChunkPos.asLong(x, z));
        MeteoriteLocationPacketS2C.sendToAll(location, landingTime);
    }

    private boolean isUnAvailableChunk(ChunkMap chunkMap, int x, int z) {
        long pos = ChunkPos.asLong(x, z);
        ChunkHolder chunkHolder;
        return unavailableChunks.contains(pos)
                && (chunkHolder = chunkMap.getVisibleChunkIfPresent(pos)) != null
                && chunkHolder.getTicketLevel() >= ChunkLevel.byStatus(ChunkStatus.FEATURES);
    }

    /// 陨石落地时覆盖的区块半径：1 表示 3×3 区块（与 [MeteoriteFeature.Config#radius()] 对应）。
    public static final int LANDING_CHUNK_RADIUS = 1;

    /// 落地时把整片 3×3 区域强制加载再放方块。
    ///
    /// 必须这样：陨石现在横跨 3 个区块（半径 23，坑心对齐到区块内 local (7,7)），
    /// 隔着未加载的区块写方块轻则写入丢失，重则在 tick 里触发级联加载。
    /// 只强制/释放**本来没被强制**的区块，不动别人的 ticket。
    private void place(ServerLevel level, int chunkX, int chunkZ, BlockPos origin) {
        LongList forced = new LongArrayList();
        for (int dx = -LANDING_CHUNK_RADIUS; dx <= LANDING_CHUNK_RADIUS; dx++) {
            for (int dz = -LANDING_CHUNK_RADIUS; dz <= LANDING_CHUNK_RADIUS; dz++) {
                int cx = chunkX + dx;
                int cz = chunkZ + dz;
                long key = ChunkPos.asLong(cx, cz);
                if (!level.getForcedChunks().contains(key) && level.setChunkForced(cx, cz, true)) {
                    forced.add(key);
                }
            }
        }
        boolean placed = false;
        try {
            level.registryAccess().holderOrThrow(METEORITE).value().place(level, level.getChunkSource().getGenerator(), level.random, origin);
            placed = true;
        } catch (Exception ignored) {}
        for (long key : forced) {
            level.setChunkForced(ChunkPos.getX(key), ChunkPos.getZ(key), false);
        }

        if (placed) {
            Component message = Component.translatable("event.confluence.meteorite").withColor(GlobalColors.MESSAGE.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
            Confluence.LOGGER.debug("A meteorite has been landed, which at [{}]", origin.toShortString());
        }
    }

    public static void spawnMeteor(ServerLevel level) {
        if (KillBoard.INSTANCE.isEvilDefeated() && level.random.nextInt(50) == 0) {
            INSTANCE.spawnAtNextNight = true;
        }
    }

    public void setMeteorite(BlockPos location, int tickUntilLanding) {
        this.location = location;
        this.tickUntilLanding = tickUntilLanding;
        MeteoriteLocationPacketS2C.sendToAll(location, tickUntilLanding);
    }

    public BlockPos getLocation() {
        return location;
    }

    public void markUnavailable(long pos) {
        unavailableChunks.add(pos);
    }

    @Override
    public void decode(CompoundTag tag) {
        this.spawnAtNextNight = tag.getBoolean("spawnAtNextNight");
        this.location = NbtUtils.readBlockPos(tag, "meteoriteLocation").orElse(BlockPos.ZERO);
        this.tickUntilLanding = tag.getInt("tickUtilMeteoriteLanding");
        unavailableChunks.clear();
        for (long pos : tag.getLongArray("unavailableChunks")) {
            markUnavailable(pos);
        }
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("spawnAtNextNight", spawnAtNextNight);
        tag.put("meteoriteLocation", NbtUtils.writeBlockPos(location));
        tag.putInt("tickUtilMeteoriteLanding", tickUntilLanding);
        tag.putLongArray("unavailableChunks", unavailableChunks.longStream().toArray());
    }

    @Override
    public String serializeKey() {
        return "confluence:meteorite_tracker";
    }

    @Override
    public void clear() {
        this.shouldGenerate = true;
        this.spawnAtNextNight = false;
        this.location = BlockPos.ZERO;
        this.tickUntilLanding = 0;
        unavailableChunks.clear();
    }
}
