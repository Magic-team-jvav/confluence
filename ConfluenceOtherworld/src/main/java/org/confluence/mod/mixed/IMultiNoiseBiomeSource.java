package org.confluence.mod.mixed;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.confluence.lib.mixed.SelfGetter;
import org.jetbrains.annotations.Nullable;

public interface IMultiNoiseBiomeSource extends SelfGetter<MultiNoiseBiomeSource> {
    @Nullable Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair();

    static IMultiNoiseBiomeSource of(MultiNoiseBiomeSource source) {
        return (IMultiNoiseBiomeSource) source;
    }
}
