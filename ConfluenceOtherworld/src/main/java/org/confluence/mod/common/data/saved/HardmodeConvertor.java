package org.confluence.mod.common.data.saved;

import com.google.common.collect.AbstractIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.apache.commons.lang3.mutable.MutableInt;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HardmodeConvertor {
    public static final HardmodeConvertor INSTANCE = new HardmodeConvertor();
    public static final Codec<List<Tuple<ChunkPos, BlockPosColumn[][]>>> LIST_CODEC = Codec.lazyInitialized(() -> {
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
    private volatile List<Tuple<ChunkPos, BlockPosColumn[][]>> list = new LinkedList<>();
    private volatile boolean shouldContinue = true;

    public boolean isStarted() {
        return started;
    }

    public void start(MinecraftServer server) {
        CompletableFuture.supplyAsync(() -> {
            ServerLevel overworld = server.overworld();
            BlockPos startPos = server.getWorldData().overworldData().getSpawnPos().atY(overworld.getMinBuildHeight());
            int height = overworld.getMaxBuildHeight() - overworld.getMinBuildHeight(), startRadius = 32, thickness = 64;
            return generateConicalCylinder(startPos, height, startRadius, startRadius + height, thickness);
        }, Util.backgroundExecutor()).thenAccept(value -> {
            started = true;
            list = value;
        });
    }

    public void scheduleRefill(ServerLevel serverLevel) {
        if (!shouldContinue) return;
        if (list.isEmpty()) {
            started = false;
        } else if (serverLevel.getGameTime() % 5 == 0) {
            Tuple<ChunkPos, BlockPosColumn[][]> entry = list.getFirst();
            ChunkPos chunkPos = entry.getA();

            boolean noForceBefore = !serverLevel.getForcedChunks().contains(chunkPos.toLong());
            if (noForceBefore) serverLevel.setChunkForced(chunkPos.x, chunkPos.z, true);
            boolean refilled = refill(serverLevel, chunkPos, entry.getB());
            if (noForceBefore) serverLevel.setChunkForced(chunkPos.x, chunkPos.z, false);

            if (refilled) list.removeFirst();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>, V extends T> boolean refill(ServerLevel overworld, ChunkPos chunkPos, BlockPosColumn[][] set) {
        ChunkAccess chunkAccess = overworld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
        if (chunkAccess == null) return false;
        Map<Block, Block> blockMap = ISpreadable.Type.HALLOW.getBlockMap();
        for (int x = 0; x < 16; x++) {
            BlockPosColumn[] columns = set[x];
            for (int z = 0; z < 16; z++) {
                BlockPosColumn column = columns[z];
                if (column == null || column == BlockPosColumn.ZERO) continue;
                for (BlockPos blockPos : column.iterable(chunkPos.getBlockX(x), chunkPos.getBlockZ(z))) {
                    BlockState sourceState = chunkAccess.getBlockState(blockPos);
                    if (sourceState.isAir()) continue;
                    Block block = blockMap.get(sourceState.getBlock());
                    if (block == null) continue;
                    BlockState targetState = block.defaultBlockState();
                    for (Map.Entry<Property<?>, Comparable<?>> entry1 : sourceState.getValues().entrySet()) {
                        if (targetState.hasProperty(entry1.getKey())) {
                            targetState = targetState.setValue((Property<T>) entry1.getKey(), (V) entry1.getValue());
                        }
                    }
                    chunkAccess.setBlockState(blockPos, targetState, false);
                }
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

    public <T> void decode(Dynamic<T> tag) {
        this.shouldContinue = false;
        Dynamic<T> dynamic = tag.get("confluence:hardmode_convertor").orElseEmptyMap();
        dynamic.get("sanctification").orElseEmptyList().read(LIST_CODEC).ifSuccess(result -> this.list = result);
        this.started = dynamic.get("started").asBoolean(false);
        this.shouldContinue = true;
    }

    public void encode(CompoundTag nbt) {
        this.shouldContinue = false;
        CompoundTag tag = new CompoundTag();
        tag.put("sanctification", LIST_CODEC.encodeStart(NbtOps.INSTANCE, list).getOrThrow());
        tag.putBoolean("started", started);
        nbt.put("confluence:hardmode_convertor", tag);
        this.shouldContinue = true;
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
}
