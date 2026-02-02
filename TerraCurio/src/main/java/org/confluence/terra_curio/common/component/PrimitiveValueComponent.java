package org.confluence.terra_curio.common.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unchecked")
public record PrimitiveValueComponent(Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> types) implements DataComponentType<PrimitiveValueComponent> {
    public static final Codec<PrimitiveValueComponent> CODEC = Codec.<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>>dispatchedMap(
            ResourceLocation.CODEC.xmap(ValueType.TYPES::get, ValueType::key), ValueType.VALUE_CODECS::get
    ).xmap(PrimitiveValueComponent::new, PrimitiveValueComponent::types);

    public static final StreamCodec<RegistryFriendlyByteBuf, PrimitiveValueComponent> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static <T, V extends PrimitiveValue<T>> PrimitiveValueComponent entry(ValueType<T, V> type, V value) {
        Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> map = new Hashtable<>();
        map.put(type, value);
        return new PrimitiveValueComponent(map);
    }

    public static <T, V extends PrimitiveValue<T>> PrimitiveValueComponent of(ValueType<T, V> type, T value) {
        Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> map = new Hashtable<>();
        map.put(type, type.newInstance(value));
        return new PrimitiveValueComponent(map);
    }

    public static PrimitiveValueComponent units(ValueType<Unit, ? extends UnitValue> type, ValueType<Unit, ? extends UnitValue>... types) {
        Hashtable<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> table = new Hashtable<>(Map.of(type, UnitValue.INSTANCE));
        for (ValueType<Unit, ? extends UnitValue> type1 : types) {
            table.put(type1, UnitValue.INSTANCE);
        }
        return new PrimitiveValueComponent(table);
    }

    public <T, V extends PrimitiveValue<T>> boolean contains(ValueType<T, V> type) {
        return types.containsKey(type);
    }

    public <T, V extends PrimitiveValue<T>> @Nullable V get(ValueType<T, V> type) {
        return (V) types.get(type);
    }

    public <T, V extends PrimitiveValue<T>> void put(ValueType<T, V> type, V value) {
        if (value instanceof PrimitiveValue<?> value1) {
            types.put(type, value1);
        }
    }

    @Override
    public @Nullable Codec<PrimitiveValueComponent> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PrimitiveValueComponent> streamCodec() {
        return STREAM_CODEC;
    }

    public record Remover(List<ValueType<?, ? extends PrimitiveValue<?>>> types) implements DataMapValueRemover<Item, PrimitiveValueComponent> {
        public static final Codec<Remover> CODEC = ValueType.CODEC.listOf().xmap(Remover::new, Remover::types);

        @Override
        public Optional<PrimitiveValueComponent> remove(PrimitiveValueComponent component, Registry<Item> registry, Either<TagKey<Item>, ResourceKey<Item>> source, Item item) {
            HashMap<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> map = new HashMap<>(component.types());
            for (ValueType<?, ? extends PrimitiveValue<?>> type : types) {
                map.remove(type);
            }
            return Optional.of(new PrimitiveValueComponent(map));
        }
    }

    public static class Merger implements DataMapValueMerger<Item, PrimitiveValueComponent> {
        public static final Merger INSTANCE = new Merger();

        private Merger() {}

        @Override
        public PrimitiveValueComponent merge(Registry<Item> registry, Either<TagKey<Item>, ResourceKey<Item>> neoSource, PrimitiveValueComponent neoValue, Either<TagKey<Item>, ResourceKey<Item>> oldSource, PrimitiveValueComponent oldValue) {
            Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> map = new Hashtable<>(oldValue.types());
            map.putAll(neoValue.types());
            return new PrimitiveValueComponent(map);
        }
    }
}
