package org.confluence.mod.client.effect.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record BiomeSkyEffect(
        Predicate<Holder<Biome>> predicate,
        @Nullable ResourceLocation skyTexture,
        @Nullable BiomeSkyRenderer renderer
) {
    public boolean matches(Holder<Biome> holder) {
        return predicate.test(holder);
    }
}
