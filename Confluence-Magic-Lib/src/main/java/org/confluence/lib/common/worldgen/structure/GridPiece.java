package org.confluence.lib.common.worldgen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.data.IdFixer;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GridPiece extends StructurePiece {
    public static final Codec<List<Tuple<Integer, IntArrayList>>> BLOCK_MAP_CODEC = LibCodecUtils.tuple(Codec.INT, Codec.INT.listOf().xmap(IntArrayList::new, Function.identity())).listOf();
    public static final Codec<List<Tuple<BlockPos, ResourceLocation>>> FEATURES_CODEC = LibCodecUtils.tuple(BlockPos.CODEC, ResourceLocation.CODEC).listOf();

    private final ChunkPos startPos;
    private final List<Tuple<Integer, IntArrayList>> blockMap;
    public List<BlockState> blockList;
    private final List<Tuple<BlockPos, ResourceLocation>> features;

    public GridPiece(ChunkPos startPos, int minY, int maxY, Object2IntMap<BlockPos> blockMap, Map<BlockPos, ResourceLocation> features) {
        super(ConfluenceMagicLib.GRID_PIECE.get(), 0, new BoundingBox(
                startPos.getMinBlockX(), minY, startPos.getMinBlockZ(),
                startPos.getMaxBlockX(), maxY, startPos.getMaxBlockZ()
        ));
        this.startPos = startPos;
        this.blockMap = new ArrayList<>();
        this.features = new ArrayList<>();
        convertToList(blockMap, features);
    }

    private void convertToList(Object2IntMap<BlockPos> blockMap, Map<BlockPos, ResourceLocation> features) {
        Map<Integer, IntArrayList> map = new HashMap<>();
        for (Object2IntMap.Entry<BlockPos> posEntry : blockMap.object2IntEntrySet()) {
            map.computeIfAbsent(posEntry.getIntValue(), index -> new IntArrayList()).add(LibUtils.compressRelativePos(posEntry.getKey()));
        }
        for (Map.Entry<Integer, IntArrayList> entry : map.entrySet()) {
            this.blockMap.add(new Tuple<>(entry.getKey(), entry.getValue()));
        }
        features.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> this.features.add(new Tuple<>(entry.getKey(), entry.getValue())));
    }

    public GridPiece(CompoundTag tag) {
        super(ConfluenceMagicLib.GRID_PIECE.get(), tag);
        this.startPos = new ChunkPos(tag.getLong("StartPos"));
        if (tag.contains("BlockMap")) {
            this.blockMap = decodeBlockMap(tag);
            this.features = FEATURES_CODEC.parse(NbtOps.INSTANCE, tag.get("Features")).getOrThrow();
        } else {
            this.blockMap = List.of();
            this.features = List.of();
        }
    }

    @Deprecated(since = "1.1.3")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    private List<Tuple<Integer, IntArrayList>> decodeBlockMap(CompoundTag tag) {
        final List<Tuple<Integer, IntArrayList>> blockMap;
        if (tag.getBoolean("confluence:fixed_relative_pos")) {
            blockMap = BLOCK_MAP_CODEC.parse(NbtOps.INSTANCE, tag.get("BlockMap")).getOrThrow();
            this.blockList = BlockState.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("BlockList")).getOrThrow();
        } else {
            blockMap = IdFixer.FIXED_BLOCK_MAP_CODEC.parse(NbtOps.INSTANCE, tag.get("BlockMap")).getOrThrow();
            this.blockList = BlockState.CODEC.mapResult(IdFixer.fixBlockName(BlockState.CODEC)).listOf().parse(NbtOps.INSTANCE, tag.get("BlockList")).getOrThrow();
        }
        return blockMap;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putLong("StartPos", startPos.toLong());
        if (blockMap.stream().anyMatch(tuple -> !tuple.getB().isEmpty())) {
            tag.put("BlockMap", BLOCK_MAP_CODEC.encodeStart(NbtOps.INSTANCE, blockMap).getOrThrow());
            tag.put("BlockList", BlockState.CODEC.listOf().encodeStart(NbtOps.INSTANCE, blockList).getOrThrow());
            tag.put("Features", FEATURES_CODEC.encodeStart(NbtOps.INSTANCE, features).getOrThrow());
        }
        tag.putBoolean("confluence:fixed_relative_pos", true);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        if (blockList == null || !chunkPos.equals(startPos)) return;
        for (Tuple<Integer, IntArrayList> pair : blockMap) {
            BlockState blockState = blockList.get(pair.getA());
            for (int blockPos : pair.getB()) {
                level.setBlock(LibUtils.decompressRelativePos(startPos, blockPos), blockState, 2);
            }
            pair.getB().clear();
        }
        Registry<ConfiguredFeature<?, ?>> configuredFeatures = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        for (Tuple<BlockPos, ResourceLocation> pair : features) {
            configuredFeatures.getHolder(pair.getB()).ifPresent(feature -> {
                feature.value().place(level, generator, random, pair.getA());
            });
        }
    }

    public static void addPieces(Object2IntMap<BlockPos> blockMap, List<BlockState> blockList, StructurePiecesBuilder builder) {
        addPieces(blockMap, blockList, Map.of(), builder);
    }

    public static void addPieces(Object2IntMap<BlockPos> blockMap, List<BlockState> blockList, Map<BlockPos, ResourceLocation> featureMap, StructurePiecesBuilder builder) {
        Map<ChunkPos, Map<BlockPos, ResourceLocation>> sliceMap = new HashMap<>();
        for (Map.Entry<BlockPos, ResourceLocation> posEntry : featureMap.entrySet()) {
            BlockPos blockPos = posEntry.getKey();
            sliceMap.computeIfAbsent(new ChunkPos(blockPos), map -> new HashMap<>()).put(blockPos, posEntry.getValue());
        }

        Map<ChunkPos, Object2IntMap<BlockPos>> gridMap = new HashMap<>();
        int minY = 2048, maxY = -2048;
        for (Object2IntMap.Entry<BlockPos> posEntry : blockMap.object2IntEntrySet()) {
            BlockPos blockPos = posEntry.getKey();
            int y = blockPos.getY();
            if (y > maxY) maxY = y;
            if (y < minY) minY = y;
            gridMap.computeIfAbsent(new ChunkPos(blockPos), map -> new Object2IntOpenHashMap<>()).put(blockPos, posEntry.getIntValue());
        }

        for (Map.Entry<ChunkPos, Object2IntMap<BlockPos>> entry : gridMap.entrySet()) {
            GridPiece piece = new GridPiece(entry.getKey(), minY, maxY, entry.getValue(), sliceMap.getOrDefault(entry.getKey(), Map.of()));
            piece.blockList = blockList;
            builder.addPiece(piece);
        }
    }
}
