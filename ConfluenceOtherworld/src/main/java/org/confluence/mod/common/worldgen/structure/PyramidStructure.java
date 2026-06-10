package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.mod.common.init.ModStructures;
import org.joml.Vector3d;

import java.util.Optional;

import static org.confluence.lib.util.LibStructureUtils.*;

public class PyramidStructure extends Structure {
    public static final MapCodec<PyramidStructure> CODEC = simpleCodec(PyramidStructure::new);

    protected PyramidStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int lowestY = getHeight(x, z, context);
        if (x * x + z * z <= 400 * 400 || lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY).offset(0, 1, 0);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            pyramidSet(centerPos, 1, 120, blockMap);
            for (int i = 0; i < 8; i++) {
                mazeSet(centerPos.offset(0, 1 + (10 * i), 0), 10, 10 - i, 0, 3, 8, random, 1.0F, blockMap);
            }
            frustumSet(new Vector3d(centerPos.getX(), centerPos.getY() + 1, centerPos.getZ()), new Vector3d(centerPos.getX(), centerPos.getY() + 82.5, centerPos.getZ()), 23.5, 23.5, 0, blockMap);
            frustumSet(new Vector3d(centerPos.getX(), centerPos.getY() + 81, centerPos.getZ()), new Vector3d(centerPos.getX(), centerPos.getY() + 92.5, centerPos.getZ()), 4.5, 4.5, 0, blockMap);
            pyramidSet(centerPos.offset(0, 90, 0), 0, 23, blockMap);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    Blocks.SANDSTONE.defaultBlockState()
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.PYRAMID.get();
    }
}
