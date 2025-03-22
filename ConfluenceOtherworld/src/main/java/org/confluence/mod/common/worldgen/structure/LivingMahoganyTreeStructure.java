package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.LeavesBlock.PERSISTENT;
import static org.confluence.mod.util.StructureUtils.*;
import static org.confluence.mod.util.VectorUtils.lightningPathList;

public class LivingMahoganyTreeStructure extends Structure {
    public static final MapCodec<LivingMahoganyTreeStructure> CODEC = simpleCodec(LivingMahoganyTreeStructure::new);

    protected LivingMahoganyTreeStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int lowestY = getLowestY(context, 16, 16);
        if (lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            ChunkPos startChunk = context.chunkPos();
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            BaseStructures.livingTree(
                    centerPos,
                    blockMap,
                    random,
                    random.nextInt(55, 65),
                    4,
                    5.9D,
                    1.0D,
                    1,
                    20,
                    15,
                    -20,
                    2,
                    2,
                    1,
                    1,
                    5,
                    5,
                    10,
                    7,
                    -10,
                    2,
                    2,
                    1,
                    1,
                    5,
                    5,
                    true,
                    random.nextInt(65, 95),
                    0,
                    0,
                    0,
                    0,
                    false,
                    20,
                    10,
                    30,
                    15,
                    4,
                    2,
                    0.01F,
                    0.75F,
                    2
            );

            GridPiece.addPieces(blockMap, startChunk, lowestY, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.LIVING_LOG_BLOCKS.getWood().get().defaultBlockState(),
                    NatureBlocks.LIVING_LOG_BLOCKS.getLeaves().get().defaultBlockState().setValue(PERSISTENT, Boolean.TRUE)
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LIVING_MAHOGANY_TREE.get();
    }
}