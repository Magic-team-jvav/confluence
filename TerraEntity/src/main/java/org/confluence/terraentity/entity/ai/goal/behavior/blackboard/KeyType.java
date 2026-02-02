package org.confluence.terraentity.entity.ai.goal.behavior.blackboard;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>黑板变量键类型</p>
 * <p>已定义常见的黑板变量键类型，也可以自定义新的键类型</p>
 * @param key 唯一标识
 * @param codec 编解码器
 * @param <V> 值类型
 */
public record KeyType<V>(String key, @Nullable Codec<V> codec) {

    private static final Map<String, KeyType<?>> ALL_KEYS = new HashMap<>();

    public static final KeyType<Integer> STAGE = KeyType.of("stage", Codec.INT);
    public static final KeyType<Integer> TIME = KeyType.of("time", Codec.INT);
    public static final KeyType<Boolean> IS_RUNNING = KeyType.of("is_running", Codec.BOOL);


    public KeyType(String key, Codec<V> codec) {
        this.key = key;
        this.codec = codec;
        ALL_KEYS.put(key, this);
    }

    public static <V> KeyType<V> of(String key) {
        return new KeyType<>(key, null);
    }

    public static <V> KeyType<V> of(String key, Codec<V> codec) {
        return new KeyType<>(key, codec);
    }

    public static <V> KeyType<V> getById(String key) {
        return (KeyType<V>) ALL_KEYS.get(key);
    }

    @Override
    public @NotNull String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        KeyType<?> keyType = (KeyType<?>) o;
        return Objects.equals(key, keyType.key);
    }

}
