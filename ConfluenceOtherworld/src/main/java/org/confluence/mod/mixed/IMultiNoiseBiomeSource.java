package org.confluence.mod.mixed;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public interface IMultiNoiseBiomeSource {
    @Nullable Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair();
}
