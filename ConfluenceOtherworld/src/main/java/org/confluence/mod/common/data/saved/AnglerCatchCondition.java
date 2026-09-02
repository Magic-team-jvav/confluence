package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Optional;

/// 渔夫任务鱼的捕获环境。条件以浮标位置为准，不复用以 NPC 位置为准的商店条件。
public record AnglerCatchCondition(List<TagKey<Biome>> biomeTags,
                                   List<TagKey<Biome>> excludedBiomeTags, int minY, int maxY,
                                   Optional<TagKey<Fluid>> fluid) {
    public static final Codec<AnglerCatchCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("biome_tags", List.of()).forGetter(AnglerCatchCondition::biomeTags),
            TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("excluded_biome_tags", List.of()).forGetter(AnglerCatchCondition::excludedBiomeTags),
            Codec.INT.optionalFieldOf("min_y", Integer.MIN_VALUE).forGetter(AnglerCatchCondition::minY),
            Codec.INT.optionalFieldOf("max_y", Integer.MAX_VALUE).forGetter(AnglerCatchCondition::maxY),
            TagKey.codec(Registries.FLUID).optionalFieldOf("fluid").forGetter(AnglerCatchCondition::fluid)
    ).apply(instance, AnglerCatchCondition::new));

    public AnglerCatchCondition {
        biomeTags = List.copyOf(biomeTags);
        excludedBiomeTags = List.copyOf(excludedBiomeTags);
        if (minY > maxY)
            throw new IllegalArgumentException("Angler quest minimum Y cannot exceed maximum Y");
    }

    public boolean matches(FishingHook hook) {
        var biome = hook.level().getBiome(hook.blockPosition());
        if (!biomeTags.isEmpty() && biomeTags.stream().noneMatch(biome::is)) return false;
        if (excludedBiomeTags.stream().anyMatch(biome::is)) return false;
        int y = hook.blockPosition().getY();
        if (y < minY || y > maxY) return false;
        return fluid.isEmpty() || hook.getInBlockState().getFluidState().is(fluid.get());
    }
}
