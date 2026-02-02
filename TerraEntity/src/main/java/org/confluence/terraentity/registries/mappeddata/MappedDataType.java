package org.confluence.terraentity.registries.mappeddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;

/**
 * MapCodec的类型，也是codecProvider，为方便类型推断，应该继承本类使用
 *
 * @param <T> map 类型
 * @param <Y> type 类型
 */
public abstract class MappedDataType<Y extends MappedDataType<Y, T>, T extends MappedData<Y>> {

    MapCodec<T> codec;
    T defaultData;
    MappedDataConstructor<Y, T> constructor;
    Set<MappedKey<Y, ?>> keySet;
    Codec<MappedKey<Y, ?>> keyCodec;

    protected MappedDataType(MapCodec<T> codec,
                             T defaultData,
                             MappedDataConstructor<Y, T> constructor,
                             Set<MappedKey<Y, ?>> keySet,
                             Codec<MappedKey<Y, ?>> keyCodec) {
        this.codec = codec;
        this.defaultData = defaultData;
        this.constructor = constructor;
        this.keySet = keySet;
        this.keyCodec = keyCodec;

        keySet.forEach(key -> key.dataType = (Y) this);
    }


    /**
     * codec provider
     */
    public MapCodec<T> getCodec() {
        return codec;
    }

    /**
     * 获取值，这是MappedData的快捷访问方式
     *
     * @param key 绑定的键
     * @param <V> 值类型
     * @return 值
     */
    public <V> V getData(MappedKey<Y, V> key) {
        return defaultData.getData(key);
    }

    /**
     * 把neoData的数据添加到或覆盖原始data
     *
     * @param neoData 新的数据
     */
    public void updateData(T neoData) {
        for (Map.Entry<MappedKey<Y, ?>, Object> entry : neoData.data.entrySet()) {
            defaultData.put(entry.getKey(), entry.getValue());
            entry.getKey().onReload(entry.getValue());
        }
    }

    /**
     * 获取默认的MappedData，用于一键数据生成默认的数据
     */
    public T getDefaultValue() {
        return generateDefaultValue(constructor, keySet, defaultData.comment);
    }

    private static <T extends MappedData<Y>, Y extends MappedDataType<Y, T>> T generateDefaultValue(MappedDataConstructor<Y, T> constructor,
                                                                                                    Collection<MappedKey<Y, ?>> codecSet,
                                                                                                    String comment) {
        T data = constructor.createMap(new HashMap<>(), comment);
        for (MappedKey<Y, ?> key : codecSet) {
            Object defaultValue = key.defaultValue().get();
            if (defaultValue != null) {
                data.put(key, defaultValue);
            }
        }
        return data;
    }

    public static <Y extends MappedDataType<Y, T>, T extends MappedData<Y>> MappedDataType.Builder<Y, T> builder(MappedDataTypeConstructor<Y, T> mappedDataTypeConstructor) {
        return new MappedDataType.Builder<>(mappedDataTypeConstructor);
    }

    /**
     * 统一使用builder创建新的mappedDataType
     * @param <Y> mappedDataType 类型
     * @param <T> mappedData 类型
     */
    public static class Builder<Y extends MappedDataType<Y, T>, T extends MappedData<Y>> {

        Map<String, MappedKey<Y, ?>> keyMap;
        Set<MappedKey<Y, ?>> keySet;
        String comment;
        MappedDataTypeConstructor<Y, T> mappedDataTypeConstructor;

        /**
         *
         * @param mappedDataTypeConstructor mappedType构造器，方便自动推断类型
         */
        public Builder(MappedDataTypeConstructor<Y, T> mappedDataTypeConstructor) {
            keySet = new HashSet<>();
            keyMap = new HashMap<>();
            this.mappedDataTypeConstructor = mappedDataTypeConstructor;
        }

        public Builder<Y, T> setComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * 注册key对应的值的codec
         */
        public <V> MappedKey<Y, V> registerCodec(String key, Codec<V> codec) {
            MappedKey<Y, V> mappedKey = new MappedKey<>(key);
            if (keySet.contains(mappedKey)) {
                throw new IllegalArgumentException("Codec already registered for key: " + key);
            }
            keyMap.put(mappedKey.toString(), mappedKey);
            keySet.add(mappedKey);
            mappedKey.valueCodec = codec;
            return mappedKey;
        }

        /**
         * 注册key对应的值的codec
         */
        public <V> MappedKey<Y, V> registerCodec(ResourceLocation key, Codec<V> codec) {
            return registerCodec(key.toString(), codec);
        }

        /**
         * 绑定codec
         *
         * @param constructor MappedData的构造函数
         * @return 绑定后的MappedDataType
         */
        public Y build(MappedDataConstructor<Y, T> constructor) {
            // 为了获取唯一实例
            Codec<MappedKey<Y, ?>> keyCodec = MappedKey.ID_CODEC.xmap(i -> keyMap.get(i.toString()), Function.identity());
            Codec<Map<MappedKey<Y, ?>, Object>> dataCodec = Codec.dispatchedMap(keyCodec, MappedKey::getValueCodec);
            MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    dataCodec.fieldOf("data").forGetter(i -> i.data),
                    Codec.STRING.optionalFieldOf("comment").forGetter(i -> Optional.ofNullable(i.comment))
            ).apply(instance, (data, comment) -> constructor.createMap(data, comment.orElse(null))));

            T data = generateDefaultValue(constructor, keySet, comment);
            return this.mappedDataTypeConstructor.createType(codec, data, constructor, keySet, keyCodec);
        }
    }

    @FunctionalInterface
    public interface MappedDataConstructor<Y extends MappedDataType<Y, T>, T extends MappedData<Y>> {
        T createMap(Map<MappedKey<Y, ?>, Object> data, String comment);
    }

    @FunctionalInterface
    public interface MappedDataTypeConstructor<Y extends MappedDataType<Y, T>, T extends MappedData<Y>> {
        Y createType(MapCodec<T> codec,
                     T defaultData,
                     MappedDataConstructor<Y, T> constructor,
                     Set<MappedKey<Y, ?>> keySet,
                     Codec<MappedKey<Y, ?>> keyCodec);
    }

}
