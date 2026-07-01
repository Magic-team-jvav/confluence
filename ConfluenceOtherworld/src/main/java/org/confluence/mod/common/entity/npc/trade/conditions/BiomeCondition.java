package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

import java.util.List;

public record BiomeCondition(List<ResourceKey<Biome>> values,
                             List<TagKey<Biome>> tags) implements TradeCondition {
    public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    ResourceKey.codec(Registries.BIOME).listOf()
                            .optionalFieldOf("values", List.of()).forGetter(BiomeCondition::values),
                    TagKey.codec(Registries.BIOME).listOf()
                            .optionalFieldOf("tags", List.of()).forGetter(BiomeCondition::tags)
            ).apply(b, BiomeCondition::new));

    @SafeVarargs
    public static BiomeCondition of(TagKey<Biome>... tags) {
        return new BiomeCondition(List.of(), List.of(tags));
    }

    @Override public boolean test(ServerPlayer player, BaseNPC npc) {
        var biome = player.serverLevel().getBiome(npc.blockPosition());
        return (values.isEmpty() || values.stream().anyMatch(biome::is))
                && (tags.isEmpty() || tags.stream().anyMatch(biome::is));
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.BIOME.get(); }
}
