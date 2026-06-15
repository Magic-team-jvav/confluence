package org.confluence.mod.common.block.natural;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class SimpleMegaTreeGrower extends AbstractMegaTreeGrower {
    private final ResourceKey<ConfiguredFeature<?, ?>> megaFeature;
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;

    /**
     * @param megaFeature 巨树配置（2x2 树苗）
     * @param feature     普通树配置（单棵树苗）
     */
    public SimpleMegaTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> megaFeature, ResourceKey<ConfiguredFeature<?, ?>> feature) {
        this.megaFeature = megaFeature;
        this.feature = feature;
    }

    public SimpleMegaTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> megaFeature) {
        this.megaFeature = megaFeature;
        this.feature = null;
    }

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource random) {
        return megaFeature;
    }

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) {
        return feature;
    }
}
