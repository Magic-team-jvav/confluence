package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.common.block.natural.CattailsHeadBlock;

import static org.confluence.mod.common.block.common.BaseRopeBlock.WATERLOGGED;

public class CattailsFeature extends Feature<CattailsFeature.Config> {
    public CattailsFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        RandomSource random = context.random();
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos baseBlockPos = context.origin();
        BlockState cattailsHeadBlockState = config.cattailsHead.getState(random, baseBlockPos);
        BlockState cattailsBodyBlockState = config.cattailsBody.getState(random, baseBlockPos);
        int minY = level.getMinBuildHeight();
        boolean placed = level.getBlockState(baseBlockPos).canBeReplaced() && baseBlockPos.getY() > minY;
        int checkY = baseBlockPos.getY();
        int endY = baseBlockPos.getY();


        final int MAX_SEARCH_DEPTH = 5;
        int searchDepth = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(baseBlockPos.getX(), 0, baseBlockPos.getZ());

        if (placed) {
            while (checkY >= minY && placed && searchDepth <= MAX_SEARCH_DEPTH) {
                placed = checkY != minY;
                if (!level.getBlockState(mutable.setY(checkY)).canBeReplaced()) {
                    placed = false;
                    break;
                }
                if (level.getBlockState(mutable.setY(checkY)).is(Blocks.WATER) &&
                        level.getBlockState(mutable.setY(checkY - 1)).isFaceSturdy(level, mutable.setY(checkY - 1), Direction.UP)) {
                    endY = checkY;
                    break;
                } else if (level.getBlockState(mutable.setY(checkY)).canBeReplaced() &&
                        !level.getBlockState(mutable.setY(checkY - 1)).canBeReplaced()) {
                    placed = false;
                    break;
                }
                checkY--;
                searchDepth++;
            }

            if (searchDepth > MAX_SEARCH_DEPTH) {
                placed = false;
            }
        }

        if (placed) {
            boolean than = true;
            checkY = endY;
            while (than) {
                mutable.setY(checkY);
                if (!level.getBlockState(mutable.setY(checkY + 1)).canBeReplaced()) {
                    level.setBlock(mutable.setY(checkY), cattailsHeadBlockState.trySetValue(WATERLOGGED, true), 3);
                    than = false;
                } else if ((level.getBlockState(mutable.setY(checkY + 1)).isAir() && level.getBlockState(mutable.setY(checkY)).is(Blocks.WATER))) {
                    int length = random.nextInt(1, 5);
                    level.setBlock(mutable.setY(checkY), cattailsBodyBlockState.trySetValue(WATERLOGGED, true), 3);
                    for (int i = 1; i <= length; i++) {
                        BlockPos checkThan = mutable.setY(checkY + i);
                        if ((!level.getBlockState(checkThan.offset(0, 1, 0)).canBeReplaced()) || (i == length)) {
                            level.setBlock(checkThan, cattailsHeadBlockState.trySetValue(CattailsHeadBlock.AGE, 3), 3);
                            break;
                        } else {
                            level.setBlock(checkThan, cattailsBodyBlockState, 3);
                        }
                    }
                    than = false;
                } else {
                    level.setBlock(mutable.setY(checkY), cattailsBodyBlockState.trySetValue(WATERLOGGED, true), 3);
                }
                checkY++;
            }
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider cattailsHead, BlockStateProvider cattailsBody) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("cattails_head").forGetter(Config::cattailsHead),
                BlockStateProvider.CODEC.fieldOf("cattails_body").forGetter(Config::cattailsBody)
        ).apply(instance, Config::new));
    }
}
