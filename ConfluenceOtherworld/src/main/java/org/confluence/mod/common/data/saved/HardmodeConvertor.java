package org.confluence.mod.common.data.saved;

import com.google.common.collect.AbstractIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.mutable.MutableInt;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.EnterHardmodeEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.mixed.IDedicatedServer;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.SecretFlagSyncPacketS2C;
import org.confluence.mod.util.AchievementUtils;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/%E5%9B%B0%E9%9A%BE%E6%A8%A1%E5%BC%8F%E8%BD%AC%E6%8D%A2">困难模式转换</a>
 */
public class HardmodeConvertor implements IGlobalData {
    public static final HardmodeConvertor INSTANCE = new HardmodeConvertor();
    public static final Codec<List<Tuple<ChunkPos, BlockPosColumn[][]>>> SANCTIFICATION_CODEC = Codec.lazyInitialized(() -> {
        Codec<BlockPosColumn[][]> codec = new Codec<>() {
            @Override
            public <T> DataResult<Pair<BlockPosColumn[][], T>> decode(DynamicOps<T> ops, T input) {
                BlockPosColumn[][] columns = new BlockPosColumn[16][16];
                MutableInt counter = new MutableInt();
                ops.getLongStream(input).getOrThrow().forEach(l -> {
                    int i = counter.getAndIncrement();
                    int x = i / 16;
                    int z = i % 16;
                    columns[x][z] = BlockPosColumn.of(l);
                });
                return DataResult.success(new Pair<>(columns, input), Lifecycle.stable());
            }

            @Override
            public <T> DataResult<T> encode(BlockPosColumn[][] input, DynamicOps<T> ops, T prefix) {
                T longList = ops.createLongList(Arrays.stream(input).flatMapToLong(columns -> Arrays.stream(columns).mapToLong(BlockPosColumn::asLong)));
                return DataResult.success(longList, Lifecycle.stable());
            }
        };
        return LibUtils.tupleCodec(Codec.LONG.xmap(ChunkPos::new, ChunkPos::toLong), codec).listOf().xmap(LinkedList::new, Function.identity());
    });

    private volatile boolean started = false;
    private volatile List<Tuple<ChunkPos, BlockPosColumn[][]>> sanctification = new LinkedList<>();
    private volatile boolean completed = false;
    private transient volatile boolean shouldContinue = true;
    private transient final TheHallowConversionTable theHallowConversionTable = new TheHallowConversionTable();

    public boolean isStarted() {
        return started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void start(MinecraftServer server, boolean debug) {
        if (started || completed) return;
        if (server instanceof IDedicatedServer dedicatedServer) {
            dedicatedServer.confluence$setOnHardmodeConversation(true);
        }
        this.shouldContinue = false;
        this.started = true;
        print(server, Component.translatable("event.confluence.hardmode_conversion.starting"), debug);
        CompletableFuture.supplyAsync(() -> {
            ServerLevel overworld = server.overworld();
            BlockPos startPos = server.getWorldData().overworldData().getSpawnPos().atY(overworld.getMinBuildHeight());
            int height = overworld.getMaxBuildHeight() - overworld.getMinBuildHeight(), startRadius = 32, thickness = 64;
            return generateConicalCylinder(startPos, height, startRadius, startRadius + height, thickness);
        }, Util.backgroundExecutor()).thenAccept(list -> {
            this.sanctification = list;
            this.shouldContinue = true;
            print(server, Component.translatable("event.confluence.hardmode_conversion.generate_data.sanctification", sanctification.size(), sanctification.size() / 4), debug);
            print(server, Component.translatable("event.confluence.hardmode_conversion.started"), debug);
        });
    }

    public void scheduleRefill(ServerLevel serverLevel) {
        if (completed || !shouldContinue) return;
        if (sanctification.isEmpty()) {
            if (started) {
                this.completed = true;
                MinecraftServer server = serverLevel.getServer();
                if (server instanceof IDedicatedServer dedicatedServer) {
                    dedicatedServer.confluence$setOnHardmodeConversation(false);
                }
                SecretFlagSyncPacketS2C.sendToAll(((IMinecraftServer) server).confluence$getSecretFlag());
                for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                    AchievementUtils.awardAchievement(serverPlayer, "its_hard");
                }
                ((IMinecraftServer) server).confluence$updateSecretFlag(IWorldOptions.HARDMODE);
                print(server, Component.translatable("event.confluence.hardmode_conversion.hardmode"), !FMLEnvironment.production);
                print(server, Component.translatable("event.confluence.hardmode_conversion.finished").withColor(GlobalColors.MESSAGE.get()), true);
                print(server, Component.translatable("event.confluence.hardmode_conversion.welcome").withColor(GlobalColors.EVENT.get()), true);
                NeoForge.EVENT_BUS.post(new EnterHardmodeEvent(server));
            }
            this.started = false;
            theHallowConversionTable.clear();
        } else {
            if (serverLevel.getServer() instanceof IDedicatedServer dedicatedServer && !dedicatedServer.confluence$isOnHardmodeConversation()) {
                dedicatedServer.confluence$setOnHardmodeConversation(true);
            }
            if (CommonConfigs.INSTANTLY_HARDMODE_CONVERSION.get()) {
                sanctification.removeIf(entry -> {
                    ChunkPos chunkPos = entry.getA();

                    boolean noForceBefore = !serverLevel.getForcedChunks().contains(chunkPos.toLong());
                    if (noForceBefore) serverLevel.setChunkForced(chunkPos.x, chunkPos.z, true);
                    boolean refilled = refill(serverLevel, chunkPos, entry.getB());
                    if (noForceBefore) serverLevel.setChunkForced(chunkPos.x, chunkPos.z, false);

                    return refilled;
                });
            } else if (serverLevel.getGameTime() % 5 == 0) {
                Tuple<ChunkPos, BlockPosColumn[][]> entry = sanctification.getFirst();
                ChunkPos chunkPos = entry.getA();

                boolean noForceBefore = !serverLevel.getForcedChunks().contains(chunkPos.toLong());
                if (noForceBefore) serverLevel.setChunkForced(chunkPos.x, chunkPos.z, true);
                boolean refilled = refill(serverLevel, chunkPos, entry.getB());
                if (noForceBefore) serverLevel.setChunkForced(chunkPos.x, chunkPos.z, false);

                if (refilled) sanctification.removeFirst();
            }
        }
    }

    private boolean refill(ServerLevel overworld, ChunkPos chunkPos, BlockPosColumn[][] set) {
        ChunkAccess chunkAccess = overworld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
        if (chunkAccess == null) return false;
        int cx = SectionPos.sectionToBlockCoord(chunkPos.x);
        int cz = SectionPos.sectionToBlockCoord(chunkPos.z);
        for (int x = 0; x < 16; x++) {
            BlockPosColumn[] columns = set[x];
            for (int z = 0; z < 16; z++) {
                BlockPosColumn column = columns[z];
                if (column == null || column == BlockPosColumn.ZERO) continue;
                for (BlockPos blockPos : column.iterable(cx + x, cz + z)) {
                    BlockState sourceState = chunkAccess.getBlockState(blockPos);
                    if (sourceState.isAir()) continue;
                    BlockState targetState = theHallowConversionTable.get(sourceState);
                    if (targetState != null) {
                        chunkAccess.setBlockState(blockPos, targetState, false);
                    }
                }
            }
        }
        ChunkMap chunkMap = overworld.getChunkSource().chunkMap;
        for (ServerPlayer player : overworld.players()) {
            if (player.getChunkTrackingView().contains(chunkPos)) {
                chunkMap.markChunkPendingToSend(player, chunkPos);
            }
        }
        return true;
    }

    private static List<Tuple<ChunkPos, BlockPosColumn[][]>> generateConicalCylinder(BlockPos startPos, int height, int startRadius, int endRadius, int thickness) {
        float deltaRadius = (endRadius - startRadius) / (float) height;
        float currentRadius = startRadius;
        Map<ChunkPos, BlockPosColumn[][]> map = new HashMap<>();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int y = 0; y < height; y++) {
            mutable.setY(y + startPos.getY());
            float startDiameter = (currentRadius + thickness) * 2 + 1;
            float currentOuterSqr = currentRadius * currentRadius;
            float currentInnerSqr = Mth.square(currentRadius - thickness);
            for (int x = 0; x < startDiameter; x++) {
                mutable.setX((int) (x - currentRadius + startPos.getX()));
                int cacheXSqr = (int) Mth.square(x - currentRadius);
                for (int z = 0; z < startDiameter; z++) {
                    int cacheSqr = (int) (Mth.square(z - currentRadius) + cacheXSqr);
                    if (cacheSqr < currentOuterSqr && cacheSqr > currentInnerSqr) {
                        mutable.setZ((int) (z - currentRadius + startPos.getZ()));
                        ChunkPos chunkPos = new ChunkPos(mutable);
                        BlockPosColumn[][] columns = map.computeIfAbsent(chunkPos, pos -> {
                            BlockPosColumn[][] posColumns = new BlockPosColumn[16][16];
                            for (int i = 0; i < 16; i++) {
                                BlockPosColumn[] columnss = new BlockPosColumn[16];
                                Arrays.fill(columnss, BlockPosColumn.ZERO);
                                posColumns[i] = columnss;
                            }
                            return posColumns;
                        });
                        int i = mutable.getX() - chunkPos.getMinBlockX();
                        int j = mutable.getZ() - chunkPos.getMinBlockZ();
                        BlockPosColumn column = columns[i][j];
                        if (column == null || column == BlockPosColumn.ZERO) column = new BlockPosColumn(startPos.getY(), height);
                        columns[i][j] = column.updateY(mutable.getY());
                    }
                }
            }
            currentRadius += deltaRadius;
        }

        List<Tuple<ChunkPos, BlockPosColumn[][]>> list = new LinkedList<>();
        for (Map.Entry<ChunkPos, BlockPosColumn[][]> entry : map.entrySet()) {
            list.add(new Tuple<>(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    private static void print(MinecraftServer server, Component component, boolean debug) {
        if (debug) server.getPlayerList().broadcastSystemMessage(component, false);
    }

    @Override
    public <T> void decode(Dynamic<T> tag) {
        this.shouldContinue = false;
        tag.get("sanctification").orElseEmptyList().read(SANCTIFICATION_CODEC).ifSuccess(result -> this.sanctification = result);
        this.started = tag.get("started").asBoolean(false);
        this.completed = tag.get("completed").asBoolean(false);
        this.shouldContinue = true;
    }

    @Override
    public void encode(CompoundTag tag) {
        this.shouldContinue = false;
        tag.put("sanctification", SANCTIFICATION_CODEC.encodeStart(NbtOps.INSTANCE, sanctification).getOrThrow());
        tag.putBoolean("started", started);
        tag.putBoolean("completed", completed);
        this.shouldContinue = true;
    }

    @Override
    public String serializeKey() {
        return "confluence:hardmode_convertor";
    }

    @Override
    public void clear() {
        this.started = false;
        this.completed = false;
        sanctification.clear();
        this.shouldContinue = true;
        theHallowConversionTable.clear();
    }

    public static class BlockPosColumn {
        public static final BlockPosColumn ZERO = new BlockPosColumn(0, 0);
        public static final Codec<BlockPosColumn> CODEC = Codec.LONG.xmap(BlockPosColumn::of, BlockPosColumn::asLong);
        private int minY;
        private int maxY;

        public BlockPosColumn(int minY, int height) {
            this.minY = minY + height; // 反过来，用于更新
            this.maxY = minY;
        }

        public BlockPosColumn updateY(int y) {
            if (y < minY) this.minY = y;
            if (y > maxY) this.maxY = y;
            return this;
        }

        public long asLong() {
            return ChunkPos.asLong(minY, maxY);
        }

        public Iterable<BlockPos> iterable(int x, int z) {
            int min = Math.min(minY, maxY);
            int max = Math.max(minY, maxY);
            return () -> new AbstractIterator<>() {
                private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, 0, z);
                private int index = min;
                private final int endIndex = max + 1;

                @CheckForNull
                @Override
                protected BlockPos computeNext() {
                    if (index == endIndex) return endOfData();
                    return cursor.setY(this.index++);
                }
            };
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BlockPosColumn column && column.minY == minY && column.maxY == maxY;
        }

        public static BlockPosColumn of(long packedPos) {
            return new BlockPosColumn(ChunkPos.getX(packedPos), ChunkPos.getZ(packedPos));
        }
    }

    public static class TheHallowConversionTable {
        private final Map<BlockState, BlockState> cache = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        public <T extends Comparable<T>, V extends T> BlockState get(BlockState blockState) {
            return cache.computeIfAbsent(blockState, source -> {
                Block target = null;

                if (source.is(BlockTags.LOGS)) {
                    target = NatureBlocks.PEARL_LOG_BLOCKS.getLog().get();
                } else if (source.is(BlockTags.LEAVES)) {
                    target = NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().get();
                } else if (source.is(BlockTags.BASE_STONE_OVERWORLD)) {
                    target = NatureBlocks.PEARL_STONE.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_GRASS_BLOCK)) {
                    target = NatureBlocks.HALLOW_GRASS_BLOCK.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_JUNGLE_GRASS_BLOCK)) {
                    target = NatureBlocks.JUNGLE_GRASS_BLOCK.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SHORT_GRASS)) {
                    target = NatureBlocks.HALLOW_GRASS.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_PACKED_ICE)) {
                    target = NatureBlocks.PINK_PACKED_ICE.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_ICE)) {
                    target = NatureBlocks.PINK_ICE.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SAND)) {
                    target = NatureBlocks.PEARL_SAND.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE)) {
                    target = NatureBlocks.PEARL_SANDSTONE.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_HARDENED_SAND_BLOCK)) {
                    target = NatureBlocks.PEARL_HARDENED_SAND_BLOCK.get();
                } else if (source.is(ModTags.Blocks.HALLOW_CONVERSION_MOIST_SAND_BLOCK)) {
                    target = NatureBlocks.PEARL_MOIST_SAND_BLOCK.get();
                }

                if (target == null) return null;
                BlockState targetState = target.defaultBlockState();
                for (Map.Entry<Property<?>, Comparable<?>> entry1 : source.getValues().entrySet()) {
                    if (targetState.hasProperty(entry1.getKey())) {
                        targetState = targetState.setValue((Property<T>) entry1.getKey(), (V) entry1.getValue());
                    }
                }
                return targetState;
            });
        }

        public void clear() {
            cache.clear();
        }
    }
}
