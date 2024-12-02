package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.mixed.IWorldGenRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueenBeeHiveFeature extends Feature<QueenBeeHiveFeature.Config> {

    private static void placeBlock(IWorldGenRegion worldGenRegion, BlockPos hivePos, int yLow, BlockState hiveBlockState, BlockState honeyState, BlockState honeyBlockState, WorldGenLevel level, FeaturePlaceContext<Config> pContext) {
        for (int y = hivePos.getY(); y > yLow; y--) {
            List<Integer> locateX1 = new ArrayList<>(Arrays.asList(2, 2, 2, -2, -2, -2, 1, 1, 0, 0, -1, -1));
            List<Integer> locateZ1 = new ArrayList<>(Arrays.asList(1, 0, -1, 1, 0, -1, 2, -2, 2, -2, 2, -2));
            List<Integer> locateX2 = new ArrayList<>(Arrays.asList(2, 2, 2, -2, -2, -2, 1, 1, 0, 0, -1, -1, 1, 1, 1, 0, 0, 0, -1, -1, -1));
            List<Integer> locateZ2 = new ArrayList<>(Arrays.asList(1, 0, -1, 1, 0, -1, 2, -2, 2, -2, 2, -2, 1, 0, -1, 1, 0, -1, 1, 0, -1));
            List<Integer> locateX3 = new ArrayList<>(Arrays.asList(1, 1, 1, 0, 0, 0, -1, -1, -1));
            List<Integer> locateZ3 = new ArrayList<>(Arrays.asList(1, 0, -1, 1, 0, -1, 1, 0, -1));
            if (y == hivePos.getY()) {
                int type = pContext.random().nextInt(30);
                for (int i = 0; i < 12; i++) {
                    BlockPos newPos = new BlockPos(hivePos.getX() + locateX1.get(i), y, hivePos.getZ() + locateZ1.get(i));
                    if ((level.getBlockState(newPos) == Blocks.STRUCTURE_VOID.defaultBlockState() || level.getBlockState(newPos) == honeyState)) {
                        worldGenRegion.confluence$setBlock(newPos, hiveBlockState, 2);
                    }
                }
                if (type == 1) {
                    for (int i = 0; i < 9; i++) {
                        BlockPos newPos = new BlockPos(hivePos.getX() + locateX3.get(i), y, hivePos.getZ() + locateZ3.get(i));
                        if ((level.getBlockState(newPos) == Blocks.STRUCTURE_VOID.defaultBlockState() || level.getBlockState(newPos) == honeyState)) {
                            worldGenRegion.confluence$setBlock(newPos, honeyBlockState, 2);
                        }
                    }
                } else if (type == 2) {
                    for (int i = 0; i < 9; i++) {
                        BlockPos newPos = new BlockPos(hivePos.getX() + locateX3.get(i), y, hivePos.getZ() + locateZ3.get(i));
                        if ((level.getBlockState(newPos) == Blocks.STRUCTURE_VOID.defaultBlockState() || level.getBlockState(newPos) == honeyState)) {
                            worldGenRegion.confluence$setBlock(newPos, honeyState, 2);
                        }
                    }
                }
            } else {
                for (int i = 0; i < 21; i++) {
                    BlockPos newPos = new BlockPos(hivePos.getX() + locateX2.get(i), y, hivePos.getZ() + locateZ2.get(i));
                    if ((level.getBlockState(newPos) == Blocks.STRUCTURE_VOID.defaultBlockState() || level.getBlockState(newPos) == honeyState)) {
                        worldGenRegion.confluence$setBlock(newPos, hiveBlockState, 2);
                    }
                }
            }
        }
    }

    public QueenBeeHiveFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        IWorldGenRegion worldGenRegion = (IWorldGenRegion) level;
        BlockPos hiveBlockPos = pContext.origin();
        BlockState honeyState = config.honey().getState(pContext.random(), hiveBlockPos);
        BlockState honeyBlockState = config.honey_block().getState(pContext.random(), hiveBlockPos);
        BlockState hiveBlockState = config.hive_block().getState(pContext.random(), hiveBlockPos);

        List<BlockPos> listOfS = new ArrayList<>();

        int radius = config.radius;
        int y_to_honey = config.y_to_honey;
        int interRadius = radius / 2;
        double r1 = ((double) pContext.random().nextInt(360) / 180) * Math.PI;
        double r2 = ((double) pContext.random().nextInt(30) + 165) / 180 * Math.PI + r1;

        BlockPos hight1 = new BlockPos((int) (radius * Math.sin(r1)), hiveBlockPos.getY(), (int) (radius * Math.cos(r1)));
        BlockPos hight2 = new BlockPos((int) (radius * Math.sin(r2)), hiveBlockPos.getY(), (int) (radius * Math.cos(r2)));

        for (int xPos = -interRadius - 1; xPos <= interRadius; xPos++) {
            for (int yPos = -interRadius - 1; yPos <= interRadius; yPos++) {
                for (int zPos = -interRadius - 1; zPos <= interRadius; zPos++) {
                    float distance = (float) Math.sqrt(xPos * xPos + yPos * yPos + zPos * zPos);
                    if (distance <= interRadius && pContext.random().nextInt(100) <= 1) {
                        BlockPos newPos = new BlockPos(xPos + hiveBlockPos.getX(), yPos + hiveBlockPos.getY(), zPos + hiveBlockPos.getZ());
                        listOfS.add(newPos);
                    }
                }
            }
        }
        for (int list = 0; list < listOfS.size(); list++) {
            for (int xPos = -interRadius - 1; xPos <= interRadius; xPos++) {
                for (int yPos = -interRadius - 1; yPos <= interRadius; yPos++) {
                    for (int zPos = -interRadius - 1; zPos <= interRadius; zPos++) {
                        float distance = (float) Math.sqrt(xPos * xPos + yPos * yPos + zPos * zPos);
                        if (distance <= interRadius) {
                            BlockPos newPos = new BlockPos(xPos + listOfS.get(list).getX(), yPos + listOfS.get(list).getY(), zPos + listOfS.get(list).getZ());
                            if (distance >= interRadius - 3) {
                                worldGenRegion.confluence$setBlock(newPos, hiveBlockState, 2);
                            }
                        }
                    }
                }
            }
        }
        for (int list = 0; list < listOfS.size(); list++) {
            for (int xPos = -interRadius - 1; xPos <= interRadius; xPos++) {
                for (int yPos = -interRadius - 1; yPos <= interRadius; yPos++) {
                    for (int zPos = -interRadius - 1; zPos <= interRadius; zPos++) {
                        float distance = (float) Math.sqrt(xPos * xPos + yPos * yPos + zPos * zPos);
                        if (distance <= interRadius) {
                            BlockPos newPos = new BlockPos(xPos + listOfS.get(list).getX(), yPos + listOfS.get(list).getY(), zPos + listOfS.get(list).getZ());
                            if (distance < interRadius - 3 && newPos.getY() <= hiveBlockPos.getY() + y_to_honey) {
                                worldGenRegion.confluence$setBlock(newPos, honeyState, 2);
                            }
                            if (distance < interRadius - 3 && newPos.getY() > hiveBlockPos.getY() + y_to_honey) {
                                worldGenRegion.confluence$setBlock(newPos, Blocks.STRUCTURE_VOID.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }
        for (int xPos = -radius - 1; xPos <= radius; xPos++) {
            for (int zPos = -radius - 1; zPos <= radius; zPos++) {
                if ((xPos + zPos * 4) % 15 == 0) {
                    double dis1 = 2 * (radius - Math.sqrt(Math.pow(xPos - hight1.getX(), 2) + Math.pow(zPos - hight1.getZ(), 2))) / radius - 1;
                    double dis2 = 2 * (radius - Math.sqrt(Math.pow(xPos - hight2.getX(), 2) + Math.pow(zPos - hight2.getZ(), 2))) / radius - 1;
                    double uDis1 = (Math.sqrt(Math.abs(dis1)) * dis1 / Math.abs(dis1) + 1) * radius * 0.4;
                    double uDis2 = (Math.sqrt(Math.abs(dis2)) * dis2 / Math.abs(dis2) + 1) * radius * 0.4;
                    BlockPos newPos = new BlockPos(xPos + hiveBlockPos.getX(), hiveBlockPos.getY() - interRadius + (int) uDis1 + (int) uDis2 + pContext.random().nextInt(2), zPos + hiveBlockPos.getZ());
                    placeBlock(worldGenRegion, newPos, hiveBlockPos.getY() - radius, hiveBlockState, honeyState, honeyBlockState, level, pContext);
                }
            }
        }
        for (int xPos = -radius - 1; xPos <= radius; xPos++) {
            for (int yPos = -radius - 1; yPos <= radius; yPos++) {
                for (int zPos = -radius - 1; zPos <= radius; zPos++) {
                    BlockPos newPos = new BlockPos(xPos + hiveBlockPos.getX(), yPos + hiveBlockPos.getY(), zPos + hiveBlockPos.getZ());
                    if (level.getBlockState(newPos) == Blocks.STRUCTURE_VOID.defaultBlockState()) {
                        worldGenRegion.confluence$setBlock(newPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        return true;
    }

    public record Config(BlockStateProvider hive_block, BlockStateProvider honey, BlockStateProvider honey_block, int radius, int y_to_honey) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("hive_block").forGetter(Config::hive_block),
                BlockStateProvider.CODEC.fieldOf("honey").forGetter(Config::honey),
                BlockStateProvider.CODEC.fieldOf("honey_block").forGetter(Config::honey_block),
                Codec.INT.fieldOf("radius").forGetter(Config::radius),
                Codec.INT.fieldOf("y_to_honey").forGetter(Config::y_to_honey)
        ).apply(instance, Config::new));
    }
}
