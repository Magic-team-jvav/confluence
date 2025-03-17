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
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Optional;

import static org.confluence.mod.util.StructureUtils.ball;
import static org.confluence.mod.util.StructureUtils.rectangular;

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
            int radius2 = 2 * radius;
            int block;
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            centerPos = new BlockPos(centerPos.getX(), random.nextInt(-10, 20), centerPos.getZ());
            //todo ---------------------------------------------------↑↑↑↑↑↑↑↑↑↑ y轴绝对范围
            BlockPos.MutableBlockPos pillarEndPos = centerPos.mutable();
            BlockPos.MutableBlockPos pillarStartPos = centerPos.mutable();
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            double r1 = ((double) random.nextInt(360) / 180) * Math.PI;
            double r2 = ((double) random.nextInt(30) + 165) / 180 * Math.PI + r1;
            float chance;

            BlockPos hight1 = new BlockPos((int) (radius * Math.sin(r1)), centerPos.getY(), (int) (radius * Math.cos(r1)));
            BlockPos hight2 = new BlockPos((int) (radius * Math.sin(r2)), centerPos.getY(), (int) (radius * Math.cos(r2)));

            ball(radius, 3, centerPos, blockMap, 0.02F, random, 1, 0, 3, centerPos.getY() - radius + 1);

            for (int xPos = -radius2 - 1; xPos <= radius2; xPos++) {
                for (int zPos = -radius2 - 1; zPos <= radius2; zPos++) {
                    if ((xPos + zPos * 4) % 15 == 0) {
                        double dis1 = 2 * (radius - Math.sqrt(Math.pow(xPos - hight1.getX(), 2) + Math.pow(zPos - hight1.getZ(), 2))) / radius - 1;
                        double dis2 = 2 * (radius - Math.sqrt(Math.pow(xPos - hight2.getX(), 2) + Math.pow(zPos - hight2.getZ(), 2))) / radius - 1;
                        double uDis1 = (Math.sqrt(Math.abs(dis1)) * dis1 / Math.abs(dis1) + 1) * radius * 0.4;
                        double uDis2 = (Math.sqrt(Math.abs(dis2)) * dis2 / Math.abs(dis2) + 1) * radius * 0.4;
                        pillarEndPos.set(xPos + centerPos.getX(), centerPos.getY() - radius + (int) uDis1 + (int) uDis2 + random.nextInt(2), zPos + centerPos.getZ());
                        pillarStartPos.set(pillarEndPos.immutable());
                        pillarStartPos.setY(centerPos.getY() - radius2);
                        block = blockMap.getInt(pillarEndPos.immutable());
                        if (block == 0 || block == 3) {
                            rectangular(pillarStartPos.offset(-2, 0, -1).immutable(), pillarEndPos.offset(2, 0, 1), 1, blockMap, 1);
                            rectangular(pillarStartPos.offset(-1, 0, -2).immutable(), pillarEndPos.offset(1, 0, -1), 1, blockMap, 1);
                            rectangular(pillarStartPos.offset(-1, 0, 1).immutable(), pillarEndPos.offset(1, 0, 2), 1, blockMap, 1);
                            chance = random.nextFloat();
                            if (chance <= 0.5F) {
                                rectangular(pillarEndPos.offset(-1, 0, -1).immutable(), pillarEndPos.offset(1, 0, 1).immutable(), 0, blockMap, 1);
                            } else if (chance <= 0.75F) {
                                rectangular(pillarEndPos.offset(-1, 0, -1).immutable(), pillarEndPos.offset(1, 0, 1).immutable(), 2, blockMap, 1);
                            } else {
                                rectangular(pillarEndPos.offset(-1, 0, -1).immutable(), pillarEndPos.offset(1, 0, 1).immutable(), 3, blockMap, 1);
                            }
                        }
                    }
                }
            }

            GridPiece.addPieces(blockMap, startChunk, lowestY, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.JUNGLE_HIVE_BLOCK.get().defaultBlockState(),
                    Blocks.HONEY_BLOCK.defaultBlockState(),
                    ModBlocks.HONEY.get().defaultBlockState()
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.QUEEN_BEE_HIVE.get();
    }
}
