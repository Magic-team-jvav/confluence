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

import java.util.ArrayList;
import java.util.List;

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
        int radius = config.radius;
        int width = config.radius * 2 + 1;
        float chance = config.chance;
        int maxLength = config.maxLength;
        int maxCheck = config.maxCheck;
        List<BlockPos> posList = new ArrayList<>();
        boolean placeSuccess = false;
        for (int x = -radius; x < width; x++){
            for (int z = -radius; z < width; z++){
                if (chance > random.nextFloat()) posList.add(baseBlockPos.offset(x, 0, z));
            }
        }

        for (BlockPos blockPos : posList) {
            int minY = level.getMinBuildHeight();
            boolean placed = level.getBlockState(blockPos).canBeReplaced() && blockPos.getY() > minY;
            int checkY = blockPos.getY();
            int endY = blockPos.getY();

            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(blockPos.getX(), 0, blockPos.getZ());

            if (placed) {
                int searchY = 1;
                int checkCount = 0;
                while (checkY >= minY && placed && (checkCount < maxCheck)) {
                    placed = checkY != minY;
                    if (level.getBlockState(mutable.setY(checkY)).is(Blocks.WATER)) {
                        searchY++;
                    } else {
                        searchY = 0;
                    }
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
                    if (searchY > maxLength) {
                        placed = false;
                        break;
                    }
                    checkY--;
                    checkCount++;
                }
            }

            if (placed) {
                boolean than = true;
                checkY = endY;
                int checkCount = 0;
                while (than && (checkCount < maxCheck)) {
                    mutable.setY(checkY);
                    if (!level.getBlockState(mutable.setY(checkY + 1)).canBeReplaced()) {
                        level.setBlock(mutable.setY(checkY), cattailsHeadBlockState.trySetValue(WATERLOGGED, true), 3);
                        than = false;
                    } else if ((((!level.getBlockState(mutable.setY(checkY + 1)).is(Blocks.WATER)) && level.getBlockState(mutable.setY(checkY + 1)).canBeReplaced()) && level.getBlockState(mutable.setY(checkY)).is(Blocks.WATER))) {
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
                    checkCount++;
                }
                placeSuccess = true;
            }
        }

        return placeSuccess;
    }

    public record Config(BlockStateProvider cattailsHead, BlockStateProvider cattailsBody, int radius, float chance, int maxLength, int maxCheck) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("cattails_head").forGetter(Config::cattailsHead),
                BlockStateProvider.CODEC.fieldOf("cattails_body").forGetter(Config::cattailsBody),
                Codec.INT.fieldOf("radius").forGetter(CattailsFeature.Config::radius),
                Codec.FLOAT.fieldOf("chance").forGetter(CattailsFeature.Config::chance),
                Codec.INT.fieldOf("max_length").forGetter(CattailsFeature.Config::maxLength),
                Codec.INT.fieldOf("max_check").forGetter(CattailsFeature.Config::maxCheck)
        ).apply(instance, Config::new));
    }
}
