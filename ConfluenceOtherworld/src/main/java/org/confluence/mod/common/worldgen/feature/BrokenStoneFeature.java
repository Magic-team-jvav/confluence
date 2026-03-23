package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.VectorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class BrokenStoneFeature extends Feature<BrokenStoneFeature.Config> {
    public BrokenStoneFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        long seed = random.nextLong();
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(seed));
        BlockState air = Blocks.AIR.defaultBlockState();
        TagKey<Block> moveTag = config.moveTag;

        int height = config.height + random.nextInt(config.heightMore + 1);
        int radius = config.radius + random.nextInt(config.radiusMore + 1);
        float residueProbability = config.residueProbability;
        ResidueType residueType = config.residueType;
        List<Vector3d> posList = VectorUtils.ballPos(radius, basePos, 0.006F, worldgenRandom);
        List<BlockPos> movePos = VectorUtils.getBlocksInConvexHull(posList);
        List<BlockPos> placePos = new ArrayList<>();

        if (movePos.isEmpty()) return false;

        Object2IntOpenHashMap<Block> counts = new Object2IntOpenHashMap<>();
        counts.defaultReturnValue(0);

        Block maxBlock = null;
        int maxCount = 0;

        for (BlockPos p : movePos) {
            BlockState state = level.getBlockState(p);
            if (state.isAir()) continue;
            if ((moveTag == null) || state.is(moveTag)) {
                Block block = state.getBlock();

                level.setBlock(p, air, 3);
                level.setBlock(p.offset(0, height, 0), state, 3);

                int newCount = counts.addTo(block, 1);

                if (newCount > maxCount) {
                    maxCount = newCount;
                    maxBlock = block;
                }
            }
        }

        if (maxBlock == null) return false;

        if (radius > 5) {
            radius -= 4;
            for (int i = 0; i < height - 10; i++) {
                if (random.nextFloat() < 0.05F) {
                    double smallRadius = radius * ((double) i / height);
                    int intSmallRadius = (int) smallRadius;
                    placePos.addAll(VectorUtils.getBlocksInConvexHull(VectorUtils.ballPos(smallRadius, basePos.offset(random.nextInt(-intSmallRadius, intSmallRadius + 1), i, random.nextInt(-intSmallRadius, intSmallRadius + 1)), 0.06F, worldgenRandom)));
                }
            }
        }

        boolean residueBoolean = false;

        switch (residueType) {
            case ALL -> residueBoolean = random.nextBoolean();
            case SPIRAL -> residueBoolean = true;
        }

        if (config.residue != null) {
            BlockState residue = config.residue.getState(random, basePos);
            if ( random.nextFloat() < residueProbability) {
                if (residueBoolean) spiral(height, random, radius, basePos, level, residue);
                else discrete(height, random, basePos, level, residue);
            }
        }

        BlockState placedBlock = maxBlock.defaultBlockState();
        placePos.forEach(pos -> level.setBlock(pos, placedBlock, 3));

        return true;
    }

    private static void spiral(int height, RandomSource random, int radius, BlockPos basePos, WorldGenLevel level, BlockState residue) {
        for (float i = 0; i < height; i += 0.5F) {
            double smallRadius = radius * 0.5 * ((double) i / height);
            if (random.nextFloat() < ( i / height)) {
                float rotate = i * Mth.PI * 0.2F;
                BlockPos pos = basePos.offset((int) (Mth.sin(rotate) * smallRadius), (int) i, (int) (Mth.cos(rotate) * smallRadius));
                if (level.getBlockState(pos).canBeReplaced()) level.setBlock(pos, residue, 3);
            }
        }
    }

    private static void discrete(int height, RandomSource random, BlockPos basePos, WorldGenLevel level, BlockState residue) {
        for (float i = 0; i < height; i += 0.5F) {
            if (random.nextFloat() < ( i / height)) {
                BlockPos pos = basePos.offset(random.nextInt(-1, 2), (int) i,random.nextInt(-1, 2));
                if (level.getBlockState(pos).canBeReplaced()) level.setBlock(pos, residue, 3);
            }
        }
    }

    public record Config(int height,
                         int heightMore,
                         int radius,
                         int radiusMore,
                         @Nullable BlockStateProvider residue,
                         float residueProbability,
                         ResidueType residueType,
                         @Nullable TagKey<Block> moveTag
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("height_more").forGetter(Config::heightMore),
                Codec.INT.fieldOf("radius").forGetter(Config::radius),
                Codec.INT.fieldOf("radius_more").forGetter(Config::radiusMore),
                BlockStateProvider.CODEC.optionalFieldOf("residue", null).forGetter(Config::residue),
                Codec.FLOAT.optionalFieldOf("residue_probability", 1.0F).forGetter(Config::residueProbability),
                StringRepresentable.fromEnum(ResidueType::values).optionalFieldOf("residue_type", ResidueType.ALL).forGetter(Config::residueType),
                TagKey.codec(BuiltInRegistries.BLOCK.key()).optionalFieldOf("move_tag", null).forGetter(Config::moveTag)
        ).apply(instance, Config::new));
    }

    public enum ResidueType implements StringRepresentable {
        SPIRAL("spiral"),
        DISCRETE("discrete"),
        ALL("all");

        private final String name;

        ResidueType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}

