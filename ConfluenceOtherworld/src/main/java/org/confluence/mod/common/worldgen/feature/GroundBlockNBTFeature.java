package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.FeatureUtils;

import java.util.function.Consumer;

public class GroundBlockNBTFeature extends Feature<GroundBlockNBTFeature.Config> {
    public GroundBlockNBTFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockState blockState = config.block().getState(random, baseBlockPos);
        int max_down = config.max_down;
        int yPlace = 0;

        boolean placed = false;

        for (int i = 0; i < max_down; i++) {
            if (!level.getBlockState(baseBlockPos.offset(0, -i - 1, 0)).canBeReplaced()) {
                placed = true;
                yPlace = -i;
                break;
            }
            if ((baseBlockPos.getY() - i) == 1) break;
        }
        if (!level.getBlockState(baseBlockPos).canBeReplaced()) placed = false;
        if (level.getBlockState(baseBlockPos.offset(0, yPlace - 1, 0)).is(blockState.getBlock())) placed = false;

        if (placed) {
            level.setBlock(baseBlockPos.offset(0, yPlace, 0), blockState, 3);
            BlockEntity blockEntity = FeatureUtils.getBlockEntity(level, baseBlockPos.offset(0, yPlace, 0));
            if (blockEntity != null) blockEntity.loadWithComponents(config.nbt, level.registryAccess());
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider block, int max_down, CompoundTag nbt) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Config::block),
                Codec.INT.fieldOf("max_down").forGetter(GroundBlockNBTFeature.Config::max_down),
                CompoundTag.CODEC.fieldOf("nbt").forGetter(GroundBlockNBTFeature.Config::nbt)
        ).apply(instance, Config::new));

        public Config(BlockStateProvider block, int max_down, Consumer<CompoundTag> consumer) {
            this(block, max_down, tag(consumer));
        }

        private static CompoundTag tag(Consumer<CompoundTag> consumer) {
            CompoundTag nbt = new CompoundTag();
            consumer.accept(nbt);
            return nbt;
        }
    }
}
