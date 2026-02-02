package org.confluence.terraentity.registries.mappeddata;

import com.mojang.serialization.Codec;
import org.confluence.terraentity.registries.TERegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

/**
 * 类似于DataMap，但不同的是这个类可以存储值类型不相同的map，且不需要对每个类型都注册到注册表，只需要注册MappedDataType即可。
 *
 * @param <Y> type 类型
 */
public abstract class MappedData<Y extends MappedDataType<Y, ?>> implements Iterable<Map.Entry<MappedKey<Y, ?>, Object>> {
    Map<MappedKey<Y, ?>, Object> data;

    /**
     * 只是为了生成数据提供信息，游戏中不需要使用
     */
    String comment;

    public static Codec<MappedData<?>> CODEC = TERegistries.MAPPED_DATAS.byNameCodec()
            .dispatch(MappedData::getType, MappedDataType::getCodec);

    protected MappedData(Map<MappedKey<Y, ?>, Object> data, @Nullable String comment) {
        this.data = data;
        this.comment = comment;
    }

    public <V> V getData(MappedKey<Y, V> key) {
        return (V) this.data.get(key);
    }

    public abstract Y getType();

    @Override
    public @NotNull Iterator<Map.Entry<MappedKey<Y, ?>, Object>> iterator() {
        return data.entrySet().iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public Object put(MappedKey<Y, ?> key, Object value) {
        return data.put(key, value);
    }

    public boolean containsKey(MappedKey<?, Y> key) {
        return data.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    public String getComment() {
        return comment;
    }

}
