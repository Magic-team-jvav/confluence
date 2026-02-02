package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.confluence.terra_curio.TerraCurio;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class ValueType<T, V extends PrimitiveValue<T>> {
    public static final Map<ValueType<?, ? extends PrimitiveValue<?>>, Codec<PrimitiveValue<?>>> VALUE_CODECS = new Hashtable<>();
    public static final Map<ResourceLocation, ValueType<?, ? extends PrimitiveValue<?>>> TYPES = new Hashtable<>();
    public static final Codec<ValueType<?, ? extends PrimitiveValue<?>>> CODEC = ResourceLocation.CODEC.xmap(TYPES::get, ValueType::key);
    public static final StreamCodec<ByteBuf, ValueType<?, ?>> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(TYPES::get, ValueType::key);

    private final ResourceLocation key;
    private final CombineRule<T, V> combineRule;
    private final T defaultValue;
    private final Function<T, V> factory;

    // 不允许从外部创建，这样是不安全的
    private ValueType(ResourceLocation key, CombineRule<T, V> combineRule, T defaultValue, Function<T, V> factory) {
        this.key = key;
        this.combineRule = combineRule;
        this.defaultValue = defaultValue;
        this.factory = factory;
    }

    private static <T, V extends PrimitiveValue<T>> void registerCodec(ValueType<?, ? extends PrimitiveValue<?>> type, Codec<V> codec) {
        if (codec instanceof Codec<? extends PrimitiveValue<?>> codec1) {
            VALUE_CODECS.put(type, (Codec<PrimitiveValue<?>>) codec1);
        }
    }

    private static void registerType(ResourceLocation id, ValueType<?, ? extends PrimitiveValue<?>> type) {
        if (TYPES.put(id, type) != null) {
            throw new IllegalArgumentException("Duplicated value type with id '" + id + "'");
        }
    }

    public static <T, V extends PrimitiveValue<T>> ValueType<T, V> create(String path, CombineRule<T, V> combineRule, Codec<V> codec, T defaultValue, Function<T, V> factory) {
        return create(TerraCurio.asResource(path), combineRule, codec, defaultValue, factory);
    }

    public static <T, V extends PrimitiveValue<T>> ValueType<T, V> create(ResourceLocation id, CombineRule<T, V> combineRule, Codec<V> codec, T defaultValue, Function<T, V> factory) {
        ValueType<T, V> type = new ValueType<>(id, combineRule, defaultValue, factory);
        registerType(id, type);
        registerCodec(type, codec);
        return type;
    }

    public static ValueType<Unit, UnitValue> ofUnit(String path) {
        return UnitType.of(TerraCurio.asResource(path));
    }

    public static ValueType<Integer, IntegerValue> ofInteger(String path, CombineRule<Integer, IntegerValue> combineRule, int defaultValue) {
        return IntegerType.of(TerraCurio.asResource(path), combineRule, defaultValue);
    }

    public static ValueType<Float, FloatValue> ofFloat(String path, CombineRule<Float, FloatValue> combineRule, float defaultValue) {
        return FloatType.of(TerraCurio.asResource(path), combineRule, defaultValue);
    }

    public ResourceLocation key() {
        return key;
    }

    public CombineRule<T, V> combineRule() {
        return combineRule;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public V newInstance(T t) {
        return factory.apply(t);
    }

    @Override
    public String toString() {
        return "Type{" +
                "key=" + key +
                ", combineRule=" + combineRule +
                ", defaultValue=" + defaultValue +
                ", factory=" + factory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ValueType<?, ?> valueType && valueType.key.equals(key));
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public static class IntegerType extends ValueType<Integer, IntegerValue> {
        private IntegerType(ResourceLocation key, CombineRule<Integer, IntegerValue> combineRule, Integer defaultValue) {
            super(key, combineRule, defaultValue, IntegerValue::new);
        }

        public static IntegerType of(ResourceLocation id, CombineRule<Integer, IntegerValue> combineRule, int defaultValue) {
            IntegerType type = new IntegerType(id, combineRule, defaultValue);
            registerType(id, type);
            registerCodec(type, IntegerValue.CODEC);
            return type;
        }
    }

    public static class FloatType extends ValueType<Float, FloatValue> {
        private FloatType(ResourceLocation key, CombineRule<Float, FloatValue> combineRule, float defaultValue) {
            super(key, combineRule, defaultValue, FloatValue::new);
        }

        public static FloatType of(ResourceLocation id, CombineRule<Float, FloatValue> combineRule, float defaultValue) {
            FloatType type = new FloatType(id, combineRule, defaultValue);
            registerType(id, type);
            registerCodec(type, FloatValue.CODEC);
            return type;
        }
    }

    public static class UnitType extends ValueType<Unit, UnitValue> {
        private UnitType(ResourceLocation key) {
            super(key, UnitValue.GET_SELF, Unit.INSTANCE, UnitValue.UNIT_2_VALUE);
        }

        public static UnitType of(ResourceLocation id) {
            UnitType type = new UnitType(id);
            registerType(id, type);
            registerCodec(type, UnitValue.CODEC);
            return type;
        }
    }
}
