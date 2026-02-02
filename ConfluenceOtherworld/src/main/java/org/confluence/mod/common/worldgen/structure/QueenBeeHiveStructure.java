package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.mod.common.block.natural.JungleHiveBlock;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Optional;

import static org.confluence.lib.util.StructureUtils.*;

public class QueenBeeHiveStructure extends Structure {
    public static final MapCodec<QueenBeeHiveStructure> CODEC = simpleCodec(QueenBeeHiveStructure::new);

    protected QueenBeeHiveStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int lowestY = getHeight(x, z, context);
        if (x * x + z * z <= 400 * 400 && lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            int radius = random.nextInt(12, 15);
            int radius2 = 2 * radius;
            int block;
            BlockPos centerPos = startChunk.getMiddleBlockPosition(random.nextInt(-40, 30));
            BlockPos.MutableBlockPos pillarEndPos = centerPos.mutable();
            BlockPos.MutableBlockPos pillarStartPos = centerPos.mutable();
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            double r1 = (double) random.nextInt(360) * Mth.DEG_TO_RAD;
            double r2 = ((double) random.nextInt(30) + 165) * Mth.DEG_TO_RAD + r1;
            float chance;

            BlockPos hight1 = new BlockPos(Mth.floor(radius * Math.sin(r1)), centerPos.getY(), Mth.floor(radius * Math.cos(r1)));
            BlockPos hight2 = new BlockPos(Mth.floor(radius * Math.sin(r2)), centerPos.getY(), Mth.floor(radius * Math.cos(r2)));

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
            rectangular(centerPos.offset(-2, -radius2, -1).immutable(), centerPos.offset(2, -1, 1), 1, blockMap, 1);
            rectangular(centerPos.offset(-1, -radius2, -2).immutable(), centerPos.offset(1, -1, -1), 1, blockMap, 1);
            rectangular(centerPos.offset(-1, -radius2, 1).immutable(), centerPos.offset(1, -1, 2), 1, blockMap, 1);
            rectangular(centerPos.offset(-1, -1, -1).immutable(), centerPos.offset(1, 2, 1).immutable(), 2, blockMap, 1);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.JUNGLE_HIVE_BLOCK.get().defaultBlockState().setValue(JungleHiveBlock.NATURAL, true),
                    NatureBlocks.THIN_HONEY_BLOCK.get().defaultBlockState(),
                    ModBlocks.HONEY.get().defaultBlockState()
            ), builder);
            builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "larva", centerPos, true, true, Util.getRandom(Rotation.values(), random)));
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.QUEEN_BEE_HIVE.get();
    }
}
