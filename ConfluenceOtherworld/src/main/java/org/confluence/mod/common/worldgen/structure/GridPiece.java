package org.confluence.mod.common.worldgen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GridPiece extends StructurePiece {
    public static final Codec<Object2IntMap<BlockPos>> BLOCK_MAP_CODEC = Codec.unboundedMap(ModUtils.BLOCK_POS_CODEC, Codec.INT).xmap(map -> {
        Object2IntMap<BlockPos> ret = new Object2IntOpenHashMap<>();
        ret.putAll(map);
        return ret;
    }, Function.identity());
    private final ChunkPos startPos;
    private final int y;
    private final Object2IntMap<BlockPos> blockMap;
    public List<BlockState> blockList;

    public GridPiece(ChunkPos startPos, int y, Object2IntMap<BlockPos> blockMap) {
        super(ModStructures.GRID_PIECE.get(), 0, new BoundingBox(
                startPos.getMinBlockX() - 24, y - 12, startPos.getMinBlockZ() - 24,
                startPos.getMaxBlockX() + 23, y + 11, startPos.getMaxBlockZ() + 23
        ));
        this.startPos = startPos;
        this.y = y;
        this.blockMap = blockMap;
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
        if (!chunkPos.equals(startPos)) return;
        ObjectIterator<Object2IntMap.Entry<BlockPos>> iterator = blockMap.object2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            Object2IntMap.Entry<BlockPos> posEntry = iterator.next();
            level.setBlock(posEntry.getKey(), blockList.get(posEntry.getIntValue()), 2);
            iterator.remove();
        }
    }

    public static @NotNull Map<ChunkPos, Object2IntMap<BlockPos>> sliceChunks(Object2IntMap<BlockPos> blockMap, ChunkPos startChunk) {
        Map<ChunkPos, Object2IntMap<BlockPos>> gridMap = new HashMap<>();
        ObjectIterator<Object2IntMap.Entry<BlockPos>> iterator = blockMap.object2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            Object2IntMap.Entry<BlockPos> posEntry = iterator.next();
            BlockPos blockPos = posEntry.getKey();
            int cx = SectionPos.blockToSectionCoord(blockPos.getX());
            int cz = SectionPos.blockToSectionCoord(blockPos.getZ());
            int regionX = (cx - startChunk.x + 1) / 3;
            int regionZ = (cz - startChunk.z + 1) / 3;

            gridMap.computeIfAbsent(new ChunkPos(cx + regionX, cz + regionZ), map -> new Object2IntOpenHashMap<>()).put(blockPos, posEntry.getIntValue());
            iterator.remove();
        }
        return gridMap;
    }
}
