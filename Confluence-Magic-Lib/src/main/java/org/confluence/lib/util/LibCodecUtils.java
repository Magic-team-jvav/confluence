package org.confluence.lib.util;

import com.google.common.collect.ImmutableListMultimap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.booleans.BooleanObjectMutablePair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class LibCodecUtils {
    public static final Codec<Vec2> VEC_2 = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(vec2 -> vec2.x),
            Codec.FLOAT.fieldOf("y").forGetter(vec2 -> vec2.y)
    ).apply(instance, Vec2::new));

    public static <A, B> Codec<Tuple<A, B>> tuple(Codec<A> aCodec, Codec<B> bCodec) {
        return tuple("a", aCodec, "b", bCodec);
    }

    public static <A, B> Codec<Tuple<A, B>> tuple(String aName, Codec<A> aCodec, String bName, Codec<B> bCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                aCodec.fieldOf(aName).forGetter(Tuple::getA),
                bCodec.fieldOf(bName).forGetter(Tuple::getB)
        ).apply(instance, Tuple::new));
    }

    public static <L, M, R> Codec<Triple<L, M, R>> triple(Codec<L> lCodec, Codec<M> mCodec, Codec<R> rCodec) {
        return triple("l", lCodec, "m", mCodec, "r", rCodec);
    }

    public static <L, M, R> Codec<Triple<L, M, R>> triple(String lName, Codec<L> lCodec, String mName, Codec<M> mCodec, String rName, Codec<R> rCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                lCodec.fieldOf(lName).forGetter(Triple::getLeft),
                mCodec.fieldOf(mName).forGetter(Triple::getMiddle),
                rCodec.fieldOf(rName).forGetter(Triple::getRight)
        ).apply(instance, ImmutableTriple::new));
    }

    public static <A> Codec<List<A>> homogenousList(Codec<A> codec, boolean disallowInline) {
        Codec<List<A>> listCodec = codec.listOf();
        return disallowInline ? listCodec : Codec.either(listCodec, codec).xmap(
                either -> either.map(Function.identity(), List::of),
                list -> list.size() == 1 ? Either.right(list.getFirst()) : Either.left(list)
        );
    }

    @Deprecated(forRemoval = true)
    public static <K, V> Codec<ImmutableListMultimap<K, V>> multimapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return multimap(keyCodec, valueCodec);
    }

    public static <K, V> Codec<ImmutableListMultimap<K, V>> multimap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.unboundedMap(keyCodec, LibCodecUtils.homogenousList(valueCodec, false)).xmap(map -> {
            ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
            for (Map.Entry<K, List<V>> entry : map.entrySet()) {
                builder.putAll(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }, multimap -> {
            Map<K, List<V>> map = new Hashtable<>();
            for (K holder : multimap.keySet()) {
                map.put(holder, multimap.get(holder));
            }
            return map;
        });
    }

    public static Codec<Float> floatRange(float min, float max) {
        return Codec.FLOAT.validate(value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0
                ? DataResult.success(value)
                : DataResult.error(() -> "Value must be within range [" + min + ";" + max + "]: " + value)
        );
    }

    public static <O> Codec<BooleanObjectPair<O>> booleanObjectPair(String boolKey, String objKey, Codec<O> objCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf(boolKey).forGetter(BooleanObjectPair::leftBoolean),
                objCodec.fieldOf(objKey).forGetter(BooleanObjectPair::right)
        ).apply(instance, BooleanObjectMutablePair::new));
    }

    public static <K, V> Codec<Map<K, V>> notStringKeyMap(String kName, Codec<K> kCodec, String vName, Codec<V> vCodec) {
        return tuple(kName, kCodec, vName, vCodec).listOf().xmap(LibUtils::convertTupleListToMap, LibUtils::convertMapToTupleList);
    }
}
