package org.confluence.terraentity.registries.npc_trade_lock.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;

import java.util.Arrays;
import java.util.List;

public record BiomeLock(List<ResourceKey<Biome>> values, List<TagKey<Biome>> tags) implements ITradeLock {
    public static final MapCodec<BiomeLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.BIOME).listOf().lenientOptionalFieldOf("values", List.of()).forGetter(BiomeLock::values),
            TagKey.codec(Registries.BIOME).listOf().lenientOptionalFieldOf("tags", List.of()).forGetter(BiomeLock::tags)
    ).apply(instance, BiomeLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        Holder<Biome> biome = npc.level().getBiome(npc.blockPosition());
        return (values.isEmpty() || values.stream().anyMatch(biome::is)) && (tags.isEmpty() || tags.stream().anyMatch(biome::is));
    }

    @Override
    public TradeLockProvider getCodec() {
        return TradeLockProviderTypes.BIOME_LOCK.get();
    }

    @SafeVarargs
    public static BiomeLock of(ResourceKey<Biome>... values) {
        return new BiomeLock(Arrays.stream(values).toList(), List.of());
    }

    @SafeVarargs
    public static BiomeLock of(TagKey<Biome>... tags) {
        return new BiomeLock(List.of(), Arrays.stream(tags).toList());
    }
}
