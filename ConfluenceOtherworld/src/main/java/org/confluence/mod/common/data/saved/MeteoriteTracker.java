package org.confluence.mod.common.data.saved;

import PortLib.extensions.net.minecraft.core.HolderLookup.PortHolderLookupExtension;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum MeteoriteTracker {
    INSTANCE;
    public static final ResourceKey<ConfiguredFeature<?, ?>> METEORITE = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "meteorite");
    private static final String RUNTIME_TAG = "ConfluenceMeteoriteRuntime";
    private static final int RUNTIME_VERSION = 1;

    private transient boolean shouldGenerate = true;
    private boolean spawnAtNextNight = false;
    @NotNull BlockPos location = BlockPos.ZERO;
    int tickUntilLanding = 0;

    /// 清除上一世界可能遗留在枚举单例中的运行状态。
    ///
    /// <p>集成服务器会在同一 JVM 内连续打开多个世界，而枚举不会随世界卸载。新世界创建、
    /// 或旧世界反序列化前都必须先回到安全默认值。</p>
    void reset() {
        this.shouldGenerate = true;
        this.spawnAtNextNight = false;
        this.location = BlockPos.ZERO;
        this.tickUntilLanding = 0;
    }

    public void tick(ServerLevel level) {
        if (!CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        if (spawnAtNextNight && LibDateUtils.getDayTime(level) == LibDateUtils._00$00) {
            setSpawnAtNextNight(level, false);
            generateLandingDetail(level, Mth.randomBetweenInclusive(level.random, 200, 400));
            Component message = Component.translatable("event.confluence.meteorite.ready").withColor(GlobalColors.EVENT.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
        if (tickUntilLanding == 0) {
            // this.location = BlockPos.ZERO; 注释掉这行，阻止重置
            this.shouldGenerate = true;
        } else if (tickUntilLanding > 0) {
            tickUntilLanding--;
            // 倒计时本身就是世界存档状态；任意一次正常保存都应记录当时剩余时间。
            ConfluenceData.get(level).setDirty();
            if (tickUntilLanding == 0) {
                if (!isValidLandingPosition(level, location)) {
                    this.location = BlockPos.ZERO;
                    this.shouldGenerate = true;
                    ConfluenceData.get(level).setDirty();
                    return;
                }
                ChunkPos chunkPos = new ChunkPos(location);
                place(level, chunkPos.x, chunkPos.z, !level.getForcedChunks().contains(chunkPos.toLong()), new BlockPos(location));
                ConfluenceData.get(level).setDirty();
            }
        }
    }

    public void generateLandingDetail(ServerLevel level, int landingTime) {
        if (!shouldGenerate || !CommonConfigs.DO_METEORITE_SPAWNING.get()) return;
        if (landingTime < 1) {
            throw new IllegalArgumentException("Meteorite landing delay must be positive");
        }
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
        ChunkHolder chunkHolder;
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
        } while ((chunkHolder = chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(x, z))) != null
                && chunkHolder.getTicketLevel() >= 34);

        BlockPos.MutableBlockPos landingPos = new BlockPos.MutableBlockPos();
        LongSet forcedChunks = level.getForcedChunks();
        while (true) {
            while ((chunkHolder = chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(x, z))) != null
                    && chunkHolder.getTicketLevel() >= 34) {
                if (level.random.nextBoolean()) x += xStep;
                else z += zStep;
            }

            int bx = SectionPos.sectionToBlockCoord(x, 7);
            int bz = SectionPos.sectionToBlockCoord(z, 7);
            BlockPos borderProbe = new BlockPos(bx, level.getMinBuildHeight(), bz);
            if (!isValidLandingPosition(level, borderProbe)) {
                resetLandingSearch(level, "Meteorite landing search reached an invalid world position");
                return;
            }

            boolean requires = !forcedChunks.contains(ChunkPos.asLong(x, z));
            try {
                if (requires) level.setChunkForced(x, z, true);
                int by = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
                landingPos.set(bx, by, bz);
            } finally {
                if (requires) level.setChunkForced(x, z, false);
            }
            if (level.getBlockState(landingPos).getFluidState().isEmpty()) {
                break;
            }
            x += xStep;
            z += zStep;
        }

        this.location = landingPos;
        this.tickUntilLanding = landingTime;
        MeteoriteLocationPacketS2C.sendToAll(location, landingTime);
        ConfluenceData.get(level).setDirty();
    }

    private void place(ServerLevel level, int chunkX, int chunkZ, boolean withForceChunk, BlockPos origin) {
        boolean placed = false;
        try {
            if (withForceChunk) level.setChunkForced(chunkX, chunkZ, true);
            PortHolderLookupExtension.Provider.holderOrThrow(level.registryAccess(), METEORITE)
                    .value().place(level, level.getChunkSource().getGenerator(), level.random, origin);
            placed = true;
        } catch (Exception ignored) {} finally {
            if (withForceChunk) level.setChunkForced(chunkX, chunkZ, false);
        }

        if (placed) {
            Component message = Component.translatable("event.confluence.meteorite").withColor(GlobalColors.MESSAGE.get());
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
            Confluence.LOGGER.debug("A meteorite has been landed, which at [{}]", origin.toShortString());
        }
    }

    public void deserialize(CompoundTag nbt) {
        reset();
        if (!nbt.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) return;
        CompoundTag runtime = nbt.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Scheduled", Tag.TAG_BYTE)
                || !runtime.contains("LandingDelay", Tag.TAG_INT)) {
            return;
        }

        boolean scheduled = runtime.getBoolean("Scheduled");
        int delay = runtime.getInt("LandingDelay");
        if (delay < 0) return;
        if (delay == 0) {
            this.spawnAtNextNight = scheduled;
            return;
        }
        if (!runtime.contains("Location", Tag.TAG_COMPOUND)) return;
        BlockPos savedLocation = NbtUtils.readBlockPos(runtime.getCompound("Location"));
        if (savedLocation == null) return;

        this.spawnAtNextNight = scheduled;
        this.location = savedLocation;
        this.tickUntilLanding = delay;
        this.shouldGenerate = false;
    }

    public void serialize(CompoundTag nbt) {
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putBoolean("Scheduled", spawnAtNextNight);
        runtime.putInt("LandingDelay", tickUntilLanding);
        if (tickUntilLanding > 0) {
            runtime.put("Location", NbtUtils.writeBlockPos(location));
        }
        nbt.put(RUNTIME_TAG, runtime);
    }

    public boolean isSpawnAtNextNight() {
        return spawnAtNextNight;
    }

    /// 更新下一夜陨石调度，并同步标记主世界 SavedData。
    public void setSpawnAtNextNight(ServerLevel level, boolean scheduled) {
        ConfluenceData data = ConfluenceData.get(level);
        if (this.spawnAtNextNight == scheduled) return;
        this.spawnAtNextNight = scheduled;
        data.setDirty();
    }

    /// 所有坐标检查都发生在请求区块前，损坏存档不能借此加载世界边缘或边界外区块。
    private static boolean isValidLandingPosition(ServerLevel level, BlockPos position) {
        return level.isInWorldBounds(position) && level.getWorldBorder().isWithinBounds(position);
    }

    /// 搜索失败时恢复调度状态，避免本次失败永久阻止后续陨石事件。
    private void resetLandingSearch(ServerLevel level, String warning) {
        this.shouldGenerate = true;
        this.location = BlockPos.ZERO;
        this.tickUntilLanding = 0;
        ConfluenceData.get(level).setDirty();
        Confluence.LOGGER.warn(warning);
    }

    public static void spawnMeteor(ServerLevel level) {
        if (KillBoard.INSTANCE.isAnyDefeated(BossEntities.EATER_OF_WORLDS.get(), BossEntities.BRAIN_OF_CTHULHU.get()) && level.random.nextFloat() < 0.02F) {
            INSTANCE.setSpawnAtNextNight(level, true);
        }
    }
}
