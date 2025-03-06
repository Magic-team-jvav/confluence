package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.VectorUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.confluence.mod.util.StructureUtils.boll;
import static org.confluence.mod.util.StructureUtils.lineSet;

public class QueenBeeHiveStructure extends Structure {
    public static final MapCodec<QueenBeeHiveStructure> CODEC = simpleCodec(QueenBeeHiveStructure::new);

    protected QueenBeeHiveStructure(StructureSettings settings) {
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
            int radius = random.nextInt(12, 15);
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY).offset(0, 20, 0);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            double r1 = ((double) random.nextInt(360) / 180) * Math.PI;
            double r2 = ((double) random.nextInt(30) + 165) / 180 * Math.PI + r1;

            BlockPos hight1 = new BlockPos((int) (radius * Math.sin(r1)), centerPos.getY(), (int) (radius * Math.cos(r1)));
            BlockPos hight2 = new BlockPos((int) (radius * Math.sin(r2)), centerPos.getY(), (int) (radius * Math.cos(r2)));

            boll(radius, 3, centerPos, blockMap, 0.02F, random, 1, 0);

            for (int xPos = -radius * 2 - 1; xPos <= radius * 2; xPos++) {
                for (int zPos = -radius * 2 - 1; zPos <= radius * 2; zPos++) {
                    if ((xPos + zPos * 4) % 15 == 0) {
                        double dis1 = 2 * (radius - Math.sqrt(Math.pow(xPos - hight1.getX(), 2) + Math.pow(zPos - hight1.getZ(), 2))) / radius - 1;
                        double dis2 = 2 * (radius - Math.sqrt(Math.pow(xPos - hight2.getX(), 2) + Math.pow(zPos - hight2.getZ(), 2))) / radius - 1;
                        double uDis1 = (Math.sqrt(Math.abs(dis1)) * dis1 / Math.abs(dis1) + 1) * radius * 0.4;
                        double uDis2 = (Math.sqrt(Math.abs(dis2)) * dis2 / Math.abs(dis2) + 1) * radius * 0.4;
                        BlockPos newPos = new BlockPos(xPos + centerPos.getX(), centerPos.getY() - radius + (int) uDis1 + (int) uDis2 + random.nextInt(2), zPos + centerPos.getZ());
                        if (blockMap.containsKey(newPos)) {
                            blockMap.put(newPos, 2);
                        }
                    }
                }
            }

            List<BlockState> blockList = Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.JUNGLE_HIVE_BLOCK.get().defaultBlockState(),
                    Blocks.RED_CONCRETE.defaultBlockState()
            );
            Map<ChunkPos, Object2IntMap<BlockPos>> gridMap = GridPiece.sliceChunks(blockMap, startChunk);
            for (Map.Entry<ChunkPos, Object2IntMap<BlockPos>> entry : gridMap.entrySet()) {
                GridPiece piece = new GridPiece(entry.getKey(), lowestY, entry.getValue());
                piece.blockList = blockList;
                builder.addPiece(piece);
            }
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.QUEEN_BEE_HIVE.get();
    }
}
