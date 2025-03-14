package org.confluence.mod.common.worldgen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.util.ModUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GridPiece extends StructurePiece {
    public static final Codec<List<Tuple<Integer, LongArrayList>>> BLOCK_MAP_CODEC = ModUtils.tupleCodec(Codec.INT, Codec.LONG.listOf().xmap(LongArrayList::new, Function.identity())).listOf();

    private final ChunkPos startPos;
    private final int y;
    private final List<Tuple<Integer, LongArrayList>> blockMap;
    public List<BlockState> blockList;

    public GridPiece(ChunkPos startPos, int y, Object2IntMap<BlockPos> blockMap) {
        super(ModStructures.GRID_PIECE.get(), 0, new BoundingBox(
                startPos.getMinBlockX() - 24, y - 12, startPos.getMinBlockZ() - 24,
                startPos.getMaxBlockX() + 23, y + 11, startPos.getMaxBlockZ() + 23
        ));
        this.startPos = startPos;
        this.y = y;
        this.blockMap = new ArrayList<>();
        Map<Integer, LongArrayList> map = new HashMap<>();
        for (Object2IntMap.Entry<BlockPos> posEntry : blockMap.object2IntEntrySet()) {
            map.computeIfAbsent(posEntry.getIntValue(), index -> new LongArrayList()).add(posEntry.getKey().asLong());
        }
        for (Map.Entry<Integer, LongArrayList> entry : map.entrySet()) {
            this.blockMap.add(new Tuple<>(entry.getKey(), entry.getValue()));
        }
    }

    public GridPiece(CompoundTag tag) {
        super(ModStructures.GRID_PIECE.get(), tag);
        this.startPos = new ChunkPos(tag.getLong("StartPos"));
        this.y = tag.getInt("Y");
        this.blockMap = BLOCK_MAP_CODEC.parse(NbtOps.INSTANCE, tag.get("BlockMap")).getOrThrow();
        this.blockList = BlockState.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("BlockList")).getOrThrow();
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putLong("StartPos", startPos.toLong());
        tag.putInt("Y", y);
        tag.put("BlockMap", BLOCK_MAP_CODEC.encodeStart(NbtOps.INSTANCE, blockMap).getOrThrow());
        tag.put("BlockList", BlockState.CODEC.listOf().encodeStart(NbtOps.INSTANCE, blockList).getOrThrow());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        if (blockList == null || !chunkPos.equals(startPos)) return;
        for (Tuple<Integer, LongArrayList> pair : blockMap) {
            BlockState blockState = blockList.get(pair.getA());
            pair.getB().removeIf(blockPos -> level.setBlock(BlockPos.of(blockPos), blockState, 2));
        }
    }

    public static Map<ChunkPos, Object2IntMap<BlockPos>> sliceChunks(Object2IntMap<BlockPos> blockMap, ChunkPos startChunk) {
        Map<ChunkPos, Object2IntMap<BlockPos>> gridMap = new HashMap<>();
        for (Object2IntMap.Entry<BlockPos> posEntry : blockMap.object2IntEntrySet()) {
            BlockPos blockPos = posEntry.getKey();
            int cx = SectionPos.blockToSectionCoord(blockPos.getX());
            int cz = SectionPos.blockToSectionCoord(blockPos.getZ());
            int regionX = (cx - startChunk.x + 1) / 3;
            int regionZ = (cz - startChunk.z + 1) / 3;

            gridMap.computeIfAbsent(new ChunkPos(cx + regionX, cz + regionZ), map -> new Object2IntOpenHashMap<>()).put(blockPos, posEntry.getIntValue());
        }
        return gridMap;
    }
}
