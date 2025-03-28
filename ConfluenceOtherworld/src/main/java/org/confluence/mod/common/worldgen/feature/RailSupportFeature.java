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

public class RailSupportFeature extends Feature<RailSupportFeature.Config> {
    public RailSupportFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos centerPos = pContext.origin();
        BlockState checkBlockState = config.check().getState(pContext.random(), centerPos);
        BlockState upBlockState0 = Blocks.CHAIN.defaultBlockState();
        BlockState downBlockState0 = Blocks.MUD_BRICK_WALL.defaultBlockState();
        BlockState upBlockState1 = Blocks.BAMBOO_FENCE.defaultBlockState();
        BlockState downBlockState1 = Blocks.PACKED_MUD.defaultBlockState();
        boolean place = level.getBlockState(centerPos.offset(0, 1, 0)).isAir() && level.getBlockState(centerPos.offset(0, -1, 0)).isAir() && (level.getBlockState(centerPos.offset(0, 0, 0)) == checkBlockState);
        int upLength = 0;
        int downLength = 0;
        int maxLength = 100;
        boolean upPlace = true;
        boolean downPlace = true;

        for (int i = 1; i <= maxLength; i++) {
            if (level.getBlockState(centerPos.offset(0, i, 0)).isAir() || level.getBlockState(centerPos.offset(0, i, 0)).canBeReplaced()) {
                upLength = i;
            } else {
                break;
            }
            if (i == maxLength) {
                upPlace = false;
            }
        }
        for (int i = 1; i <= maxLength; i++) {
            if (level.getBlockState(centerPos.offset(0, -i, 0)).isAir() || level.getBlockState(centerPos.offset(0, -i, 0)).canBeReplaced()) {
                downLength = i;
            } else {
                break;
            }
            if (i == maxLength) {
                downPlace = false;
            }
        }
        place = place && (upPlace || downPlace);

        if (place) {
            if (upPlace && downPlace) {
                if (upLength > downLength) {
                    for (int i = 1; i <= downLength; i++) {
                        level.setBlock(centerPos.offset(0, -i, 0), downBlockState0, 3);
                        if (i == downLength) {
                            level.setBlock(centerPos.offset(0, -i - 1, 0), downBlockState1, 3);
                        }
                    }
                } else {
                    for (int i = 1; i <= upLength; i++) {
                        level.setBlock(centerPos.offset(0, i, 0), (i == 1) ? upBlockState1 : upBlockState0, 3);
                    }
                }
            } else if (upPlace) {
                for (int i = 1; i <= upLength; i++) {
                    level.setBlock(centerPos.offset(0, i, 0), (i == 1) ? upBlockState1 : upBlockState0, 3);
                }
            } else {
                for (int i = 1; i <= downLength; i++) {
                    level.setBlock(centerPos.offset(0, -i, 0), downBlockState0, 3);
                    if (i == downLength) {
                        level.setBlock(centerPos.offset(0, -i - 1, 0), downBlockState1, 3);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider check) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("check").forGetter(Config::check)
        ).apply(instance, Config::new));
    }
}
