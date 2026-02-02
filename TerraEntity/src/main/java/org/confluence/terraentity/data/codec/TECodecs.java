package org.confluence.terraentity.data.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class TECodecs {

    public static Codec<List<Integer>> INT_LIST_CODEC = Codec.INT.listOf();
    public static Codec<List<Float>> FLOAT_LIST_CODEC = Codec.FLOAT.listOf();

    /**
     * 将int作为字符串序列化，可以作为Map的key
     */
    public static final Codec<Integer> INT_KEY = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Integer> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).map(Integer::parseInt);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Integer value) {
            return ops.createString(value.toString());
        }
    };

    /**
     * 快速创建枚举的codec
     */
    public static<T extends Enum<T>> Codec<T> createEnumCodec(Class<T> enumClass) {
        return Codec.STRING.xmap(
                name->Enum.valueOf(enumClass, name.toUpperCase()),
                baker-> baker.name().toLowerCase(Locale.ROOT)
        );
    }

    /**
     * 修复Encode总是选择left的codec
     *
     * <p>{@link Codec#withAlternative(Codec, Codec)} 在Encode时总是选择left，如果需要数据驱动，这是不对的</p>
     * @param primary 默认的codec
     * @param alternative 备用codec
     * @param chooser 根据情况选择left或者right
     * @param <T> 抽象类型
     * @param <S> 具体类型
     */
    public static <T, S extends T> Codec<T> alternativeCodec(final Codec<T> primary, final Codec<S> alternative, Function<T, Either<T, S>> chooser) {
        return Codec.either(primary, alternative).xmap(Either::unwrap, chooser);
    }

}
