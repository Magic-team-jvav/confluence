package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.util.ModUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class LivingTreeFeature extends Feature<LivingTreeFeature.Config> {
    public LivingTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static void placeBlock(List<Vector3d> posListToPlace, double rMax, double rMin, BlockState blockStateToPlace, WorldGenLevel level, double rPer, double placePer, boolean repAir, FeaturePlaceContext<Config> context) {
        RandomSource random = context.random();
        int rCheck = (int) (rMax + 2.0);

        for (int posList = 0; posList < posListToPlace.size(); posList++) {
            double r = rMax - (rMax - rMin) / posListToPlace.size() * posList;
            for (int setY = -rCheck; setY <= rCheck; setY++) {
                double rPlaceSq = r * r - setY * setY;
                if (rPlaceSq >= 0) {
                    double rPlace = Math.sqrt(rPlaceSq);
                    for (int setZ = -rCheck; setZ <= rCheck; setZ++) {
                        double xSq = rPlace * rPlace - setZ * setZ;
                        if (xSq >= 0) {
                            int trunkBlockPosX1 = (int) posListToPlace.get(posList).x + (int) Math.sqrt(xSq);
                            int trunkBlockPosX2 = (int) posListToPlace.get(posList).x - (int) Math.sqrt(xSq);
                            int trunkBlockPosY = (int) (posListToPlace.get(posList).y + setY * rPer);
                            int trunkBlockPosZ = (int) posListToPlace.get(posList).z + setZ;
                            if (trunkBlockPosX1 != trunkBlockPosX2) {
                                for (int trunkBlockPosXli = trunkBlockPosX2; trunkBlockPosXli <= trunkBlockPosX1; trunkBlockPosXli++) {
                                    BlockPos trunkBlockPosPlace = new BlockPos(trunkBlockPosXli, trunkBlockPosY, trunkBlockPosZ);
                                    if (((double) random.nextInt(10001) / 10000) <= placePer && (level.getBlockState(trunkBlockPosPlace).isAir() || repAir) && !(level.getBlockState(trunkBlockPosPlace) == blockStateToPlace)) {
                                        level.setBlock(trunkBlockPosPlace, blockStateToPlace, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<Vector3d> placeList(List<Vector3d> posListToPlace, double rMax, double rMin, double rPer, double placePer, FeaturePlaceContext<Config> context) {
        RandomSource random = context.random();
        int rCheck = (int) (rMax + 2.0);
        List<Vector3d> listToReturn = new ArrayList<>();

        for (int posList = 0; posList < posListToPlace.size(); posList++) {
            double r = rMax - (rMax - rMin) / posListToPlace.size() * posList;
            for (int setY = -rCheck; setY <= rCheck; setY++) {
                double rPlaceSq = r * r - setY * setY;
                if (rPlaceSq >= 0) {
                    double rPlace = Math.sqrt(rPlaceSq);
                    for (int setZ = -rCheck; setZ <= rCheck; setZ++) {
                        double xSq = rPlace * rPlace - setZ * setZ;
                        if (xSq >= 0) {
                            int trunkBlockPosX1 = (int) posListToPlace.get(posList).x + (int) Math.sqrt(xSq);
                            int trunkBlockPosX2 = (int) posListToPlace.get(posList).x - (int) Math.sqrt(xSq);
                            int trunkBlockPosY = (int) (posListToPlace.get(posList).y + setY * rPer);
                            int trunkBlockPosZ = (int) posListToPlace.get(posList).z + setZ;
                            if (trunkBlockPosX1 != trunkBlockPosX2) {
                                for (int trunkBlockPosXli = trunkBlockPosX2; trunkBlockPosXli <= trunkBlockPosX1; trunkBlockPosXli++) {
                                    BlockPos trunkBlockPosPlace = new BlockPos(trunkBlockPosXli, trunkBlockPosY, trunkBlockPosZ);
                                    if (((double) random.nextInt(10001) / 10000) <= placePer) {
                                        Vector3d posS = new Vector3d();
                                        posS.x = trunkBlockPosPlace.getX();
                                        posS.y = trunkBlockPosPlace.getY();
                                        posS.z = trunkBlockPosPlace.getZ();
                                        listToReturn.add(posS);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return listToReturn;
    }

    private static void placeRoot(BlockPos blockPos, FeaturePlaceContext<Config> context, double rWoodMax, double rWoodMin, double rAirMax, double rAirMin, BlockState blockStateForWood, BlockState blockStateForAir, WorldGenLevel level) {
        int rCheck = (int) rWoodMax + 2;
        int lengthWood = 40 + context.random().nextInt(20);
        int lengthAir = 5 + lengthWood;
        for (int yToPlace = 0; yToPlace < lengthWood; yToPlace++) {
            double rToPlace = rWoodMax - (rWoodMax - rWoodMin) / lengthWood * yToPlace;
            for (int zToPlace = -rCheck; zToPlace <= rCheck; zToPlace++) {
                double xSq = rToPlace * rToPlace - zToPlace * zToPlace;
                if (xSq >= 0) {
                    int xToPlace = (int) Math.sqrt(xSq);
                    int xToPlace2 = -xToPlace;
                    if (xToPlace != xToPlace2) {
                        for (int xPlaceLi = xToPlace2; xPlaceLi <= xToPlace; xPlaceLi++) {
                            BlockPos posToPlaceWood = new BlockPos(blockPos.getX() + xPlaceLi, blockPos.getY() - 1 - yToPlace, blockPos.getZ() + zToPlace);
                            level.setBlock(posToPlaceWood, blockStateForWood, 2);
                        }
                    } else {
                        BlockPos posToPlaceAir = new BlockPos(blockPos.getX() + xToPlace, blockPos.getY() - 1 - yToPlace, blockPos.getZ() + zToPlace);
                        level.setBlock(posToPlaceAir, blockStateForWood, 2);
                    }
                }
            }
        }
        for (int yToPlace = 0; yToPlace < lengthAir; yToPlace++) {
            double rToPlace = rAirMax - (rAirMax - rAirMin) / lengthAir * yToPlace;
            for (int zToPlace = -rCheck; zToPlace <= rCheck; zToPlace++) {
                double xSq = rToPlace * rToPlace - zToPlace * zToPlace;
                if (xSq >= 0) {
                    int xToPlace = (int) Math.sqrt(xSq);
                    int xToPlace2 = -xToPlace;
                    if (xToPlace != xToPlace2) {
                        for (int xPlaceLi = xToPlace2; xPlaceLi <= xToPlace; xPlaceLi++) {
                            BlockPos posToPlaceAir = new BlockPos(blockPos.getX() + xPlaceLi, blockPos.getY() - 1 - yToPlace, blockPos.getZ() + zToPlace);
                            level.setBlock(posToPlaceAir, blockStateForAir, 2);
                        }
                    } else {
                        BlockPos posToPlaceAir = new BlockPos(blockPos.getX() + xToPlace, blockPos.getY() - 1 - yToPlace, blockPos.getZ() + zToPlace);
                        level.setBlock(posToPlaceAir, blockStateForAir, 2);
                    }
                }
            }
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        int bl = random.nextBoolean() ? 1 : -1;
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos trunkBlockPos = pContext.origin();
        BlockPos leavesBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, trunkBlockPos);
        BlockState leavesBlockState = config.leaves().getState(random, leavesBlockPos);
        double dis = Math.sqrt(Math.pow(trunkBlockPos.getZ(), 2) + Math.pow(trunkBlockPos.getX(), 2));

        if (dis >= 700) {

            List<Vector3d> locationList1 = new ArrayList<>();
            Vector3d locationStart = new Vector3d();
            Vector3d locationEnd = new Vector3d();

            locationStart.x = (trunkBlockPos.getX());
            locationStart.y = (trunkBlockPos.getY());
            locationStart.z = (trunkBlockPos.getZ());
            locationEnd.x = (trunkBlockPos.getX() + bl * random.nextInt(5));
            locationEnd.y = (trunkBlockPos.getY() + 55 + random.nextInt(10));
            locationEnd.z = (trunkBlockPos.getZ() + bl * random.nextInt(5));

            locationList1.add(locationStart);
            locationList1.add(locationEnd);
            List<Vector3d> leavesListFirst = new ArrayList<>();
            leavesListFirst.add(locationEnd);
            placeBlock(placeList(leavesListFirst, 30.0, 30.0, 0.5, 0.005, pContext), 4.0, 4.0, leavesBlockState, level, 0.5, 0.5, false, pContext);

            ModUtils.lightningPathList(locationList1, 1.0, 8, pContext.random());
            placeBlock(locationList1, 6.0, 1.0, trunkBlockState, level, 1.0, 1.0, true, pContext);

            int stickCount = 5 + random.nextInt(5);
            for (int stickPlace = 0; stickPlace < stickCount; stickPlace++) {
                double anCs = 360.0 / stickCount;
                double everyA = anCs * stickPlace / 180 * Math.PI;
                double everyB = ((((double) random.nextInt(110) - 20) * Math.pow((double) random.nextInt(101) / 100, 3)) / 180 * Math.PI);
                int len = 20 + random.nextInt(15);
                double endX = len * Math.cos(everyA) * Math.cos(everyB);
                double endY = len * Math.sin(everyB);
                double endZ = len * Math.sin(everyA) * Math.cos(everyB);
                Vector3d stickStart = locationList1.get(locationList1.size() - (locationList1.size() / 11 * 7) - random.nextInt(10));
                Vector3d stickEnd = new Vector3d();
                stickEnd.x = (locationEnd.x + endX);
                stickEnd.y = (locationEnd.y + endY - 20);
                stickEnd.z = (locationEnd.z + endZ);
                List<Vector3d> stickList = new ArrayList<>();
                List<Vector3d> leavesList = new ArrayList<>();
                stickList.add(stickStart);
                stickList.add(stickEnd);
                ModUtils.lightningPathList(stickList, 1.0, 8, pContext.random());
                placeBlock(stickList, 2.0 + random.nextInt(2), 1.0, trunkBlockState, level, 1.0, 1.0, true, pContext);
                leavesList.add(stickEnd);
                placeBlock(placeList(leavesList, 20.0, 20.0, 0.5, 0.005, pContext), 4.0, 4.0, leavesBlockState, level, 0.5, 0.5, false, pContext);
            }

            for (int stickPlace = 0; stickPlace < stickCount; stickPlace++) {
                double anCs = 360.0 / stickCount;
                double everyA = anCs * stickPlace / 180 * Math.PI;
                double everyB = ((double) random.nextInt(50) / 180 * Math.PI);
                int len = 20 + random.nextInt(15);
                double endX = len * Math.cos(everyA) * Math.cos(everyB);
                double endY = len * Math.sin(everyB);
                double endZ = len * Math.sin(everyA) * Math.cos(everyB);
                Vector3d stickStart = locationList1.get(random.nextInt(10));
                Vector3d stickEnd = new Vector3d();
                BlockPos stickEndPos = new BlockPos((int) (locationStart.get(0) + endX), (int) (locationStart.get(1) - endY), (int) (locationStart.get(2) + endZ));
                if (!level.getBlockState(stickEndPos).isAir()) {
                    stickEnd.x = (locationStart.x + endX);
                    stickEnd.y = (locationStart.y - endY);
                    stickEnd.z = (locationStart.z + endZ);
                    List<Vector3d> stickList = new ArrayList<>();
                    stickList.add(stickStart);
                    stickList.add(stickEnd);
                    ModUtils.lightningPathList(stickList, 1.0, 8, pContext.random());
                    placeBlock(stickList, 2.0 + random.nextInt(2), 1.0, trunkBlockState, level, 1.0, 1.0, true, pContext);
                }
            }

            for (int setY = -10; setY <= 10; setY++) {
                double rPlaceSq = 25 - setY * setY;
                if (rPlaceSq >= 0) {
                    double rPlace = Math.sqrt(rPlaceSq);
                    for (int setZ = -10; setZ <= 10; setZ++) {
                        double xSq = rPlace * rPlace - setZ * setZ;
                        if (xSq >= 0) {
                            int trunkBlockPosX;
                            int trunkBlockPosX1 = trunkBlockPos.getX() + (int) Math.sqrt(xSq);
                            int trunkBlockPosX2 = trunkBlockPos.getX() - (int) Math.sqrt(xSq);
                            int trunkBlockPosY = trunkBlockPos.getY() + setY;
                            int trunkBlockPosZ = trunkBlockPos.getZ() + setZ;
                            if (trunkBlockPosX1 == trunkBlockPosX2) {
                                trunkBlockPosX = trunkBlockPosX1;
                                BlockPos trunkBlockPosPlace = new BlockPos(trunkBlockPosX, trunkBlockPosY, trunkBlockPosZ);
                                level.setBlock(trunkBlockPosPlace, trunkBlockState, 2);
                            } else {
                                for (int trunkBlockPosXli = trunkBlockPosX2; trunkBlockPosXli <= trunkBlockPosX1; trunkBlockPosXli++) {
                                    BlockPos trunkBlockPosPlace = new BlockPos(trunkBlockPosXli, trunkBlockPosY, trunkBlockPosZ);
                                    level.setBlock(trunkBlockPosPlace, Blocks.AIR.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
            }

            placeRoot(trunkBlockPos, pContext, 6.0, 2.0, 1.5, 1.5, trunkBlockState, Blocks.AIR.defaultBlockState(), level);
            //BlockPos trunkBlockPosPlace1 = new BlockPos(trunkBlockPos.getX() + 4, trunkBlockPos.getY() + 3, trunkBlockPos.getZ());
            //BlockPos trunkBlockPosPlace2 = new BlockPos(trunkBlockPos.getX() - 4, trunkBlockPos.getY() + 3, trunkBlockPos.getZ());
            //BlockPos trunkBlockPosPlace3 = new BlockPos(trunkBlockPos.getX(), trunkBlockPos.getY() + 3, trunkBlockPos.getZ() + 3);
            //BlockPos trunkBlockPosPlace4 = new BlockPos(trunkBlockPos.getX(), trunkBlockPos.getY() + 3, trunkBlockPos.getZ() - 3);

            //level.setBlock(trunkBlockPosPlace1, Blocks.WALL_TORCH.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.EAST), 2);
            //level.setBlock(trunkBlockPosPlace2, Blocks.WALL_TORCH.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.WEST), 2);
            //level.setBlock(trunkBlockPosPlace3, Blocks.WALL_TORCH.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.SOUTH), 2);
            //level.setBlock(trunkBlockPosPlace4, Blocks.WALL_TORCH.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.NORTH), 2);

            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider leaves) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves)
        ).apply(instance, Config::new));
    }
}
