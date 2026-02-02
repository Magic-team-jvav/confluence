package org.confluence.lib.util;

import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.BooleanObjectMutablePair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.confluence.lib.common.recipe.AmountIngredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public final class LibStreamCodecUtils {
    public static final StreamCodec<ByteBuf, Vec2> VEC_2 = StreamCodec.composite(
            ByteBufCodecs.FLOAT, vec2 -> vec2.x,
            ByteBufCodecs.FLOAT, vec2 -> vec2.y,
            Vec2::new
    );
    public static final StreamCodec<FriendlyByteBuf, java.util.UUID> UUID = new StreamCodec<>() {
        @Override
        public java.util.UUID decode(FriendlyByteBuf buffer) {
            return buffer.readUUID();
        }

        @Override
        public void encode(FriendlyByteBuf buffer, java.util.UUID value) {
            buffer.writeUUID(value);
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> INGREDIENTS = new StreamCodec<>() {
        @Override
        public NonNullList<Ingredient> decode(RegistryFriendlyByteBuf buffer) {
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(buffer.readVarInt(), AmountIngredient.EMPTY);
            nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            return nonnulllist;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, NonNullList<Ingredient> value) {
            buffer.writeVarInt(value.size());
            for (Ingredient ingredient : value) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockState> BLOCK_STATE = new StreamCodec<>() {
        private final StreamCodec<RegistryFriendlyByteBuf, Block> blockCodec = ByteBufCodecs.registry(Registries.BLOCK);

        @Override
        public BlockState decode(RegistryFriendlyByteBuf buffer) {
            Block block = blockCodec.decode(buffer);
            return block.getStateDefinition().getPossibleStates().get(buffer.readVarInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, BlockState value) {
            Block block = value.getBlock();
            blockCodec.encode(buffer, block);
            buffer.writeVarInt(block.getStateDefinition().getPossibleStates().indexOf(value));
        }
    };

    public static <B extends ByteBuf, V> StreamCodec<B, V> unit(Supplier<V> expectedValue) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buffer) {
                return expectedValue.get();
            }

            @Override
            public void encode(B buffer, V value) {
                if (!value.equals(expectedValue.get())) {
                    throw new IllegalStateException("Can't encode '" + value + "', expected '" + expectedValue.get() + "'");
                }
            }
        };
    }

    public static <B extends ByteBuf, TA, TB> StreamCodec<B, Tuple<TA, TB>> tuple(StreamCodec<? super B, TA> aCodec, StreamCodec<? super B, TB> bCodec) {
        return StreamCodec.composite(aCodec, Tuple::getA, bCodec, Tuple::getB, Tuple::new);
    }

    public static <B extends ByteBuf, L, M, R> StreamCodec<B, Triple<L, M, R>> triple(StreamCodec<? super B, L> lCodec, StreamCodec<? super B, M> mCodec, StreamCodec<? super B, R> rCodec) {
        return StreamCodec.composite(lCodec, Triple::getLeft, mCodec, Triple::getMiddle, rCodec, Triple::getRight, ImmutableTriple::new);
    }

    public static <B extends ByteBuf, K, V> StreamCodec<B, Map<K, V>> map(IntFunction<Map<K, V>> factory, StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec) {
        return ByteBufCodecs.map(factory, keyCodec, valueCodec);
    }

    public static <B extends ByteBuf, V> StreamCodec<B, Object2BooleanMap<V>> object2BooleanMap(StreamCodec<? super B, V> codec) {
        return ByteBufCodecs.map(Object2BooleanOpenHashMap::new, codec, ByteBufCodecs.BOOL);
    }

    public static <B extends ByteBuf, V> StreamCodec<B, TagKey<V>> tagKey(ResourceKey<Registry<V>> resourceKey) {
        return new StreamCodec<>() {
            public TagKey<V> decode(B buffer) {
                return TagKey.create(resourceKey, ResourceLocation.STREAM_CODEC.decode(buffer));
            }

            public void encode(B buffer, TagKey<V> tagKey) {
                ResourceLocation.STREAM_CODEC.encode(buffer, tagKey.location());
            }
        };
    }

    /**
     * Use {@link ByteBufCodecs#registry(ResourceKey)} directly
     */
    @Deprecated
    public static <V> StreamCodec<RegistryFriendlyByteBuf, V> registry(Registry<V> registry) {
        return ByteBufCodecs.registry(registry.key());
    }

    public static <B extends ByteBuf, V> StreamCodec<B, V> lazyInitialized(Supplier<StreamCodec<B, V>> delegate) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buffer) {
                return delegate.get().decode(buffer);
            }

            @Override
            public void encode(B buffer, V value) {
                delegate.get().encode(buffer, value);
            }
        };
    }

    public static StreamCodec<ByteBuf, boolean[]> booleanArray(int size) {
        int length = size % 8 == 0 ? size / 8 : size / 8 + 1;
        return new StreamCodec<>() {
            @Override
            public boolean[] decode(ByteBuf buffer) {
                boolean[] result = new boolean[size];
                for (int i = 0; i < length; i++) {
                    byte b = buffer.readByte();
                    int startIndex = i * 8;

                    for (int j = 0; j < 8; j++) {
                        int index = startIndex + j;
                        if (index < size) {
                            result[index] = (b & (1 << j)) != 0;
                        }
                    }
                }
                return result;
            }

            @Override
            public void encode(ByteBuf buffer, boolean[] value) {
                if (value.length != size) {
                    throw new IllegalArgumentException("Boolean array size mismatch. Expected: " + size + ", actual: " + value.length);
                }

                for (int i = 0; i < length; i++) {
                    byte b = 0;
                    int startIndex = i * 8;

                    for (int j = 0; j < 8; j++) {
                        int index = startIndex + j;
                        if (index < value.length && value[index]) {
                            b |= (byte) (1 << j);
                        }
                    }

                    buffer.writeByte(b);
                }
            }
        };
    }

    public static <B extends ByteBuf, O> StreamCodec<B, BooleanObjectPair<O>> booleanObjectPair(StreamCodec<? super B, O> objCodec) {
        return new StreamCodec<>() {
            @Override
            public BooleanObjectPair<O> decode(B buffer) {
                return new BooleanObjectMutablePair<>(buffer.readBoolean(), objCodec.decode(buffer));
            }

            @Override
            public void encode(B buffer, BooleanObjectPair<O> value) {
                buffer.writeBoolean(value.leftBoolean());
                objCodec.encode(buffer, value.right());
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B buffer, C composite) {
                codec1.encode(buffer, getter1.apply(composite));
                codec2.encode(buffer, getter2.apply(composite));
                codec3.encode(buffer, getter3.apply(composite));
                codec4.encode(buffer, getter4.apply(composite));
                codec5.encode(buffer, getter5.apply(composite));
                codec6.encode(buffer, getter6.apply(composite));
                codec7.encode(buffer, getter7.apply(composite));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final StreamCodec<? super B, T8> codec8,
            final Function<C, T8> getter8,
            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(B buffer, C composite) {
                codec1.encode(buffer, getter1.apply(composite));
                codec2.encode(buffer, getter2.apply(composite));
                codec3.encode(buffer, getter3.apply(composite));
                codec4.encode(buffer, getter4.apply(composite));
                codec5.encode(buffer, getter5.apply(composite));
                codec6.encode(buffer, getter6.apply(composite));
                codec7.encode(buffer, getter7.apply(composite));
                codec8.encode(buffer, getter8.apply(composite));
            }
        };
    }
}
