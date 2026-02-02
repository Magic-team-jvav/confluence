package org.confluence.terraentity.registries.mappeddata;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 键类型
 *
 * <p>一个键只可以绑定一个MappedDataType，以便从codec序列化得到唯一的数据</p>
 *
 * @param <V> 指示值的类型，便于编译器检查，可以是任意类型的值
 * @param <Y> type类型，不同的键不能混用
 */
public class MappedKey<Y extends MappedDataType<Y, ?>, V> implements IAutoReloadable<V> {
    // 唯一标识
    final String key;

    // 唯一实例的额外数据
    Consumer<V> onReload;
    Supplier<V> defaultValue;
    Codec<V> valueCodec;
    Y dataType;

    static Codec<MappedKey<?, ?>> ID_CODEC = Codec.STRING.xmap(MappedKey::new, MappedKey::getKey);

    MappedKey(String key) {
        this.key = key;
    }

    MappedKey(ResourceLocation key) {
        this.key = key.toString();
    }

    public MappedKey<Y, V> withOnReload(Consumer<V> onReload) {
        this.onReload = onReload;
        return this;
    }

    public MappedKey<Y, V> withDefaultValue(Supplier<V> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Codec<MappedKey<Y, ?>> getKeyCodec() {
        if (dataType == null) {
            throw new IllegalStateException("dataType is null");
        }
        return dataType.keyCodec;
    }

    public Codec<V> getValueCodec() {
        return valueCodec;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        MappedKey<?, ?> other = (MappedKey<?, ?>) obj;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public void onReload(Object value) {
        if (onReload != null) {
            onReload.accept((V) value);
        }
    }

    @Override
    public Supplier<V> defaultValue() {
        if (defaultValue == null) {
            return () -> null;
        }
        return defaultValue;
    }


}
