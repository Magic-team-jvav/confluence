package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BilayerOreFeature extends Feature<BilayerOreFeature.Config> {
    public BilayerOreFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        BlockState innerOre = config.innerOre.getState(random, basePos);
        BlockState outerOre = config.outerOre.getState(random, basePos);
        int innerCount = config.innerCount + random.nextInt(config.innerCountMore + 1);
        Optional<TagKey<Block>> replaceTag = config.replaceTag;
        Set<BlockPos> innerPos = new HashSet<>();
        Set<BlockPos> innerPosDebug = new HashSet<>();

        if (replaceTag.isPresent() && !level.getBlockState(basePos).is(replaceTag.get())) {
            return false;
        }
        for (Direction dir : LibUtils.DIRECTIONS) {
            if (replaceTag.isPresent() && !level.getBlockState(basePos.relative(dir)).is(replaceTag.get())) {
                return false;
            }
        }
        innerPos.add(basePos);

        int i = 0;
        while ((innerPos.size() < innerCount) && (i < innerCount + 100)) {
            innerPosDebug.clear();
            innerPosDebug.addAll(innerPos);
            for (BlockPos pos : innerPosDebug) {
                BlockPos nextPos = pos.relative(Direction.getRandom(random));
                boolean input = true;
                for (Direction dir : LibUtils.DIRECTIONS) {
                    if (replaceTag.isPresent() && !level.getBlockState(nextPos.relative(dir)).is(replaceTag.get())) {
                        input = false;
                        break;
                    }
                }
                if (input) innerPos.add(nextPos);
                if (innerPos.size() >= innerCount) break;
            }
            i++;
        }

        innerPos.forEach(p -> {
            level.setBlock(p, innerOre, 3);
            for (Direction dir : LibUtils.DIRECTIONS) {
                BlockPos place = p.relative(dir);
                level.setBlock(place, outerOre, 3);
            }
        });
        innerPos.forEach(p -> level.setBlock(p, innerOre, 3));

        return true;
    }

    public record Config(
            int innerCount,
            int innerCountMore,
            BlockStateProvider innerOre,
            BlockStateProvider outerOre,
            Optional<TagKey<Block>> replaceTag
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("inner_count").forGetter(Config::innerCount),
                Codec.INT.fieldOf("inner_count_more").forGetter(Config::innerCountMore),
                BlockStateProvider.CODEC.fieldOf("inner_ore").forGetter(Config::innerOre),
                BlockStateProvider.CODEC.fieldOf("outer_ore").forGetter(Config::outerOre),
                TagKey.codec(Registries.BLOCK).optionalFieldOf("replace_tag").forGetter(BilayerOreFeature.Config::replaceTag)
        ).apply(instance, Config::new));

        public Config(
                int innerCount,
                int innerCountMore,
                BlockStateProvider innerOre,
                BlockStateProvider outerOre,
                TagKey<Block> replaceTag
        ) {
            this(innerCount, innerCountMore, innerOre, innerOre, Optional.of(replaceTag));
        }

        public Config(
                int innerCount,
                int innerCountMore,
                BlockStateProvider innerOre,
                BlockStateProvider outerOre
        ) {
            this(innerCount, innerCountMore, innerOre, innerOre, Optional.empty());
        }
    }
}
