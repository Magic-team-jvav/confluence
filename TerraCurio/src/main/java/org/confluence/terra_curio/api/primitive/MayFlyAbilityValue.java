package org.confluence.terra_curio.api.primitive;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import org.confluence.terra_curio.TerraCurio;

import java.util.Map;

public record MayFlyAbilityValue(Map<ResourceKey<Item>, FlyStack> flyStacks) implements PrimitiveValue<Map<ResourceKey<Item>, MayFlyAbilityValue.FlyStack>> {
    public static final Codec<MayFlyAbilityValue> CODEC = Codec.unboundedMap(ResourceKey.codec(Registries.ITEM), FlyStack.CODEC).xmap(MayFlyAbilityValue::new, MayFlyAbilityValue::get);
    public static final CombineRule<Map<ResourceKey<Item>, FlyStack>, MayFlyAbilityValue> COMBINE_RULE = CombineRule.register((a, b) -> {
        Map<ResourceKey<Item>, FlyStack> map = Maps.newHashMap(a);
        map.putAll(b);
        return ImmutableMap.copyOf(map);
    }, "may_fly_ability");

    @Override
    public Map<ResourceKey<Item>, FlyStack> get() {
        return flyStacks;
    }

    @Override
    public Codec<MayFlyAbilityValue> codec() {
        return CODEC;
    }

    public static Map<ResourceKey<Item>, FlyStack> of(String path, int order, float flySpeed, int flyTicks, boolean couldGlide, boolean horizontalFlight) {
        return Map.of(ResourceKey.create(Registries.ITEM, TerraCurio.asResource(path)), new FlyStack(order, flySpeed, flyTicks, couldGlide, horizontalFlight));
    }

    public static Map<ResourceKey<Item>, FlyStack> of(String path, float flySpeed, int flyTicks, boolean couldGlide, boolean horizontalFlight) {
        return Map.of(ResourceKey.create(Registries.ITEM, TerraCurio.asResource(path)), new FlyStack(1000, flySpeed, flyTicks, couldGlide, horizontalFlight));
    }

    public record FlyStack(int older, float flySpeed, int flyTicks, boolean couldGlide, boolean horizontalFlight) {
        public static final Codec<FlyStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.lenientOptionalFieldOf("order", 1000).forGetter(FlyStack::older),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("fly_speed").forGetter(FlyStack::flySpeed),
                ExtraCodecs.POSITIVE_INT.fieldOf("fly_ticks").forGetter(FlyStack::flyTicks),
                Codec.BOOL.fieldOf("could_glide").orElse(false).forGetter(FlyStack::couldGlide),
                Codec.BOOL.fieldOf("horizontal_flight").orElse(false).forGetter(FlyStack::horizontalFlight)
        ).apply(instance, FlyStack::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, FlyStack> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, FlyStack::older,
                ByteBufCodecs.FLOAT, FlyStack::flySpeed,
                ByteBufCodecs.VAR_INT, FlyStack::flyTicks,
                ByteBufCodecs.BOOL, FlyStack::couldGlide,
                ByteBufCodecs.BOOL, FlyStack::horizontalFlight,
                FlyStack::new
        );
    }
}
