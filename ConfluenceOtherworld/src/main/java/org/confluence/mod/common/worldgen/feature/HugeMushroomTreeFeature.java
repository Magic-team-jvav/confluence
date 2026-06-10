package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.lib.util.LibVectorUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.confluence.lib.util.LibFeatureUtils.ball;
import static org.confluence.lib.util.LibFeatureUtils.ellipsoid;

public class HugeMushroomTreeFeature extends Feature<HugeMushroomTreeFeature.Config> {
    public HugeMushroomTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, baseBlockPos.getX(), baseBlockPos.getZ());
        baseBlockPos = new BlockPos(baseBlockPos.getX(), surfaceY, baseBlockPos.getZ());
        BlockPos headPos = baseBlockPos.offset(
                random.nextInt(-5, 6),
                random.nextInt(10, 15),
                random.nextInt(-5, 6)
        );
        BlockState stemBlockState = config.stem().getState(random, baseBlockPos);
        BlockState pileusBlockState = config.pileus().getState(random, baseBlockPos);
        BlockState indusiumBlockState = config.indusium().getState(random, baseBlockPos);
        BlockState mycelium = config.mycelium().getState(random, baseBlockPos);
        BlockState mycelialDirt = config.mycelialDirt().getState(random, baseBlockPos);
        BlockState hangingMycelium = config.hangingMycelium().getState(random, baseBlockPos);
        BlockState mushroom1 = config.mushroom1().getState(random, baseBlockPos);
        BlockState mushroom2 = config.mushroom2().getState(random, baseBlockPos);
        int maxY = baseBlockPos.getY() - level.getMinBuildHeight() - 3;
        int length = 0;
        boolean placed = true;
        for (int i = 1; i < maxY; i++) {
            BlockPos pos = baseBlockPos.offset(0, -i, 0);
            BlockState checkBlock = level.getBlockState(pos);
            if (checkBlock.canBeReplaced()) {
                placed = false;
                break;
            } else if (level.getBlockState(pos.offset(0, -1, 0)).canBeReplaced() && checkBlock.is(Blocks.MUD)) {
                length = i;
                break;
            }
        }
        if (placed) {
            List<Vector3d> trunkList = new ArrayList<>();
            trunkList.add(LibVectorUtils.toVector3d(baseBlockPos.offset(0, 1, 0)));
            trunkList.add(LibVectorUtils.toVector3d(headPos));
            LibGeometryUtils.lightningPathList(trunkList, 1, 0.3F, random);
            double radius = 2.5;
            double step = 1.0 / trunkList.size();

            Vector3d indusiumVct = trunkList.get((int) (trunkList.size() * 0.5));
            BlockPos indusiumPos = BlockPos.containing(indusiumVct.x, indusiumVct.y, indusiumVct.z);
            ellipsoid(5.5, 1, 5.5, indusiumPos, indusiumBlockState, true, level);
            for (Vector3d truckVct : trunkList) {
                BlockPos trunkPos = BlockPos.containing(truckVct.x, truckVct.y, truckVct.z);
                ball(radius, trunkPos, stemBlockState, true, level);
                radius -= step;
            }

            ball(1.5, baseBlockPos.offset(0, 1, 0), Blocks.AIR.defaultBlockState(), true, level);

            long seed = random.nextLong();
            RandomSource worldgenRandom = new WorldgenRandom(RandomSource.create(seed));

            List<Vector3d> pileusList = LibGeometryUtils.ellipsoidPos(
                    random.nextDouble() * 5 + 7,
                    1,
                    random.nextDouble() * 5 + 7,
                    headPos,
                    0.8F,
                    worldgenRandom);
            pileusList.addAll(LibGeometryUtils.ellipsoidPos(
                    random.nextDouble() * 2 + 3,
                    1,
                    random.nextDouble() * 2 + 3,
                    headPos.offset(0, 4, 0),
                    0.8F,
                    worldgenRandom)
            );
            for (Vector3d pileusVct : pileusList) {
                double pileusRadius = random.nextDouble() * 2.5 + 1;
                BlockPos pileusPos = BlockPos.containing(pileusVct.x, pileusVct.y, pileusVct.z);
                ball(pileusRadius, pileusPos, pileusBlockState, true, level);
                pileusCheck((int) pileusRadius + 1, pileusPos, pileusBlockState, level);
            }

            TagKey<Block> DIRT_TAG = TagKey.create(Registries.BLOCK, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:dirt")));

            for (int x = -10; x < 11; x++) {
                int x2 = x * x;
                for (int z = -10; z < 11; z++) {
                    int z2 = z * z;
                    double dis = Mth.sqrt(x2 + z2);
                    if (random.nextDouble() > (dis - 5) / 5) {
                        int offset = 0;
                        for (int i = 0; i < 10; i++) {
                            if (level.getBlockState(baseBlockPos.offset(x, offset, z)).is(DIRT_TAG) && level.getBlockState(baseBlockPos.offset(x, offset + 1, z)).canBeReplaced()) {
                                level.setBlock(baseBlockPos.offset(x, offset, z), mycelium, 3);
                                if (random.nextFloat() > 0.7)
                                    level.setBlock(baseBlockPos.offset(x, offset + 1, z), random.nextBoolean() ? mushroom1 : mushroom2, 3);
                                break;
                            } else if (level.getBlockState(baseBlockPos.offset(x, offset, z)).canBeReplaced()) {
                                offset--;
                            } else if (!level.getBlockState(baseBlockPos.offset(x, offset, z)).canBeReplaced() && !level.getBlockState(baseBlockPos.offset(x, offset + 1, z)).canBeReplaced()) {
                                offset++;
                            }
                        }
                    }
                }
            }
            for (int i = 1; i <= length; i++) {
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        if (random.nextFloat() > 0.67) {
                            level.setBlock(baseBlockPos.offset(x, -i, z), mycelialDirt, 3);
                            if ((i == length) && (level.getBlockState(baseBlockPos.offset(x, -i - 1, z)).canBeReplaced())) {
                                level.setBlock(baseBlockPos.offset(x, -i - 1, z), hangingMycelium, 3);
                            }
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }

    public record Config(
            BlockStateProvider stem,
            BlockStateProvider pileus,
            BlockStateProvider indusium,
            BlockStateProvider hangingMycelium,
            BlockStateProvider mycelialDirt,
            BlockStateProvider mushroom1,
            BlockStateProvider mushroom2,
            BlockStateProvider mycelium
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("stem_block").forGetter(Config::stem),
                BlockStateProvider.CODEC.fieldOf("pileus_block").forGetter(Config::pileus),
                BlockStateProvider.CODEC.fieldOf("indusium_block").forGetter(Config::indusium),
                BlockStateProvider.CODEC.fieldOf("hanging_mycelium").forGetter(Config::hangingMycelium),
                BlockStateProvider.CODEC.fieldOf("mycelial_dirt").forGetter(Config::mycelialDirt),
                BlockStateProvider.CODEC.fieldOf("mushroom_1").forGetter(Config::mushroom1),
                BlockStateProvider.CODEC.fieldOf("mushroom_2").forGetter(Config::mushroom2),
                BlockStateProvider.CODEC.fieldOf("mycelium").forGetter(Config::mycelium)
        ).apply(instance, Config::new));
    }

    private static void pileusCheck(int side, BlockPos baseBlockPos, BlockState pileusBlockState, WorldGenLevel level) {
        BlockPos checkPos;
        for (int x = -side; x <= side; x++) {
            for (int y = -side; y <= side; y++) {
                for (int z = -side; z <= side; z++) {
                    checkPos = baseBlockPos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(pileusBlockState.getBlock())) {
                        if (level.getBlockState(checkPos.offset(0, 1, 0)).is(pileusBlockState.getBlock())) {
                            level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.UP, true), 3);
                        }
                        if (level.getBlockState(checkPos.offset(0, -1, 0)).is(pileusBlockState.getBlock())) {
                            level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.DOWN, true), 3);
                        }
                        if (level.getBlockState(checkPos.offset(1, 0, 0)).is(pileusBlockState.getBlock())) {
                            level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.EAST, true), 3);
                        }
                        if (level.getBlockState(checkPos.offset(-1, 0, 0)).is(pileusBlockState.getBlock())) {
                            level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.WEST, true), 3);
                        }
                        if (level.getBlockState(checkPos.offset(0, 0, 1)).is(pileusBlockState.getBlock())) {
                            level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.SOUTH, true), 3);
                        }
                        if (level.getBlockState(checkPos.offset(0, 0, -1)).is(pileusBlockState.getBlock())) {
                            level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.NORTH, true), 3);
                        }
                    }
                }
            }
        }
    }
}
