package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.block.natural.DragonsBreathPepperBlock;
import org.confluence.mod.common.block.natural.VoidTreeRootBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3d;
import java.util.ArrayList;
import java.util.List;

import static org.confluence.lib.util.FeatureUtils.updateLeavesOptimized;
import static org.confluence.mod.common.block.natural.VoidTreeRootBlock.CONNECTION_PROPERTIES;

public class VoidTreeFeature extends Feature<VoidTreeFeature.Config> {
    public VoidTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }
    private static final TagKey<Block> VOID_TREE_ROOT_CAN_CONNECT = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "void_tree_root_can_connect"));
    private static final TagKey<Block> END_PLANT_CAN_SURVIVE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "end_plant_can_survive"));

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();

        if (!level.getBlockState(basePos.below()).is(END_PLANT_CAN_SURVIVE)) return false;

        BlockState trunkBlock = config.trunk().getState(random, basePos);
        BlockState rootBlock = config.root().getState(random, basePos);
        BlockState leavesBlock = config.leaves().getState(random, basePos);
        LongOpenHashSet trunkSet = new LongOpenHashSet();
        LongOpenHashSet leavesSet = new LongOpenHashSet();
        LongOpenHashSet rootSet = new LongOpenHashSet();

        Vector3d startPos = new Vector3d(basePos.getX(), basePos.getY(), basePos.getZ());
        Vector3d middlePos = new Vector3d(basePos.getX() + random.nextInt(-2, 3), basePos.getY() + 5, basePos.getZ() + random.nextInt(-2, 3));
        Vector3d endPos = new Vector3d(basePos.getX() + random.nextInt(-3, 4), basePos.getY() + 10 + random.nextInt(7), basePos.getZ() + random.nextInt(-3, 4));
        List<Vector3d> trunk = new ArrayList<>();
        trunk.add(middlePos);
        trunk.add(endPos);

        List<List<Vector3d>> trunks = VectorUtils.lightningPathList(trunk, 0.3, 0.3F, random, 3, 0.5F);

        trunk.clear();
        trunk.add(startPos);
        trunk.add(middlePos);
        VectorUtils.lightningPathList(trunk, 0.3, 0.3F, random);
        trunks.add(trunk);

        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.from2DDataValue(i);
            LongOpenHashSet rootDebugSet = new LongOpenHashSet();
            int length = random.nextInt(1,4);
            int height = random.nextInt(2);
            BlockPos rootPos = basePos.offset(0, height, 0);
            BlockPos rootMiddlePos = basePos.offset(0, height, 0).relative(direction, length);
            boolean end = false;
            for (int j = 0; j <= length; j++) {
                BlockPos debugPos = rootPos.relative(direction, j);
                if (level.getBlockState(debugPos).canBeReplaced()) rootDebugSet.add(debugPos.asLong());
                else {
                    rootSet.addAll(rootDebugSet);
                    end = true;
                    break;
                }
            }
            if (end) continue;
            for (int j = 0; j <= 15; j++) {
                BlockPos debugPos = rootMiddlePos.offset(0, -j, 0);
                if (!level.getBlockState(debugPos.below()).canBeReplaced()) {
                    rootDebugSet.add(debugPos.asLong());
                    rootSet.addAll(rootDebugSet);
                    break;
                } else rootDebugSet.add(debugPos.asLong());
            }
        }

        for (List<Vector3d> trunkList : trunks) {
            for (Vector3d trunkPos : trunkList) {
                BlockPos posPlace = VectorUtils.fromVector3d(trunkPos);
                if (!level.getBlockState(posPlace).canBeReplaced()) return false;
                if (trunkPos == trunkList.getLast()) {
                    int mainSide = random.nextInt(3, 8);
                    int side;
                    for (int y = -1; y < 3; y++) {
                        side = mainSide - Mth.abs(y);
                        for (int x = -side; x <= side; x++) {
                            for (int z = -side; z <= side; z++) {
                                if (Mth.abs(x) + Mth.abs(z) <= side) {
                                    BlockPos lPos = posPlace.offset(x, y, z);
                                    if (level.getBlockState(lPos).canBeReplaced() && random.nextFloat() >= 0.5F * (1 + (float) (Mth.abs(x) + Mth.abs(z) - side) / side)) {
                                        long lPosLong = lPos.asLong();
                                        leavesSet.add(lPosLong);
                                        if (random.nextFloat() > 0.67F) {
                                            int vLen = random.nextInt(1, 3);
                                            for (int l = 1; l <= vLen; l++) {
                                                BlockPos vPos = lPos.below(l);
                                                if (level.getBlockState(vPos).canBeReplaced())
                                                    leavesSet.add(vPos.asLong());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                trunkSet.add(posPlace.asLong());
            }
        }

        leavesSet.forEach(p -> level.setBlock(BlockPos.of(p), leavesBlock, 3));
        rootSet.forEach(p -> level.setBlock(BlockPos.of(p), rootBlock, 3));
        rootSet.forEach(p -> {
            BlockState rootPlace = rootBlock;
            BlockPos placePos = BlockPos.of(p);
            for (int i = 0; i < 6; i++) {
                Direction direction = Direction.from3DDataValue(i);
                BlockState ns = level.getBlockState(placePos.relative(direction));
                rootPlace = rootPlace.setValue(CONNECTION_PROPERTIES.get(direction), (ns.is(rootBlock.getBlock()) || ns.is(VOID_TREE_ROOT_CAN_CONNECT)) ? VoidTreeRootBlock.ConnectType.CONNECT : VoidTreeRootBlock.ConnectType.DIS_CONNECT);
            }
            level.setBlock(placePos, rootPlace, 3);
        });
        trunkSet.forEach(p -> level.setBlock(BlockPos.of(p), trunkBlock, 3));
        placeDragonFruits(level, trunkSet, random);
        updateLeavesOptimized(level, trunkSet, leavesSet, true, false);

        return true;
    }

    private void placeDragonFruits(WorldGenLevel level, LongOpenHashSet trunkSet, RandomSource random) {
        if (random.nextFloat() > 0.4F) return;
        int count = random.nextInt(1, 4);
        Direction[] horizontalDirections = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int i = 0; i < count; i++) {
            BlockPos trunkPos = BlockPos.of(trunkSet.toLongArray()[random.nextInt(2, trunkSet.size())]);
            Direction direction = horizontalDirections[random.nextInt(4)];
            BlockPos fruitPos = trunkPos.relative(direction);
            if (level.getBlockState(fruitPos).canBeReplaced()) {
                int age = random.nextInt(DragonsBreathPepperBlock.MAX_AGE + 1);
                level.setBlock(fruitPos, NatureBlocks.DRAGONS_BREATH_PEPPER.get().defaultBlockState()
                        .setValue(DragonsBreathPepperBlock.FACING, direction)
                        .setValue(DragonsBreathPepperBlock.AGE, age), 3);
            }
        }
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider root,
                         BlockStateProvider leaves) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("root_block").forGetter(Config::root),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves)
        ).apply(instance, Config::new));
    }
}
