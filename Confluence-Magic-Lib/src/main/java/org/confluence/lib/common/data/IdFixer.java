package org.confluence.lib.common.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.StateHolder;
import net.neoforged.fml.ModLoader;
import org.apache.commons.lang3.mutable.MutableObject;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.event.NameFixRegisterEvent;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.lib.util.LibUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class IdFixer {
    public static final Codec<List<Tuple<Integer, IntArrayList>>> FIXED_BLOCK_MAP_CODEC = Codec.lazyInitialized(() -> {
        Codec<List<Tuple<Integer, LongArrayList>>> codec = LibCodecUtils.tuple(Codec.INT, Codec.LONG.listOf().xmap(LongArrayList::new, Function.identity())).listOf();
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<List<Tuple<Integer, IntArrayList>>, T>> decode(DynamicOps<T> ops, T input) {
                Optional<List<Tuple<Integer, LongArrayList>>> result = codec.parse(ops, input).result();
                if (result.isEmpty()) return DataResult.error(() -> "Error decoding!");
                List<Tuple<Integer, LongArrayList>> list = result.get();
                List<Tuple<Integer, IntArrayList>> ret = new ArrayList<>(5 + list.size() + (list.size() / 10));
                for (Tuple<Integer, LongArrayList> tuple : list) {
                    LongArrayList longs = tuple.getB();
                    IntArrayList integers = new IntArrayList(5 + longs.size() + (longs.size() / 10));
                    for (long l : longs) {
                        integers.add(((BlockPos.getX(l) & 0xF) << 16) | ((BlockPos.getY(l) + 2048) << 4) | (BlockPos.getZ(l) & 0xF));
                    }
                    ret.add(new Tuple<>(tuple.getA(), integers));
                }
                return DataResult.success(new Pair<>(ret, input), Lifecycle.stable());
            }

            @Override
            public <T> DataResult<T> encode(List<Tuple<Integer, IntArrayList>> input, DynamicOps<T> ops, T prefix) {
                throw new UnsupportedOperationException("Cannot encode with decode-only codec! Decoder:" + codec);
            }
        };
    });
    public static final ResourceLocation STP = ResourceLocation.fromNamespaceAndPath(ConfluenceMagicLib.CONFLUENCE_ID, "simple_template_piece");
    public static final ResourceLocation FIXED_STP = ConfluenceMagicLib.asResource("simple_template_piece");
    public static final ResourceLocation GP = ResourceLocation.fromNamespaceAndPath(ConfluenceMagicLib.CONFLUENCE_ID, "grid_piece");
    public static final ResourceLocation FIXED_GP = ConfluenceMagicLib.asResource("grid_piece");
    private static Map<String, String> BLOCK_NAME_FIX_MAP;
    private static Map<String, String> ITEM_NAME_FIX_MAP;
    private static Map<String, String> BIOME_NAME_FIX_MAP;
    public static final Codec.ResultFunction<ResourceKey<Biome>> FIX_BIOME_KEY_FUNC = new Codec.ResultFunction<>() {
        @Override
        public <T> DataResult<Pair<ResourceKey<Biome>, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<ResourceKey<Biome>, T>> a) {
            return ops.getStringValue(input).result().map(s -> {
                String s1 = BIOME_NAME_FIX_MAP.get(s);
                try {
                    return s1 == null ? a : DataResult.success(new Pair<>(ResourceKey.create(Registries.BIOME, ResourceLocation.parse(s1)), input), Lifecycle.stable());
                } catch (Exception e) {
                    return a;
                }
            }).orElse(a);
        }

        @Override
        public <T> DataResult<T> coApply(DynamicOps<T> ops, ResourceKey<Biome> input, DataResult<T> t) {
            return t;
        }
    };

    public static ResourceLocation fixPieceNamespace(ResourceLocation original) {
        if (original.equals(STP)) return FIXED_STP;
        if (original.equals(GP)) return FIXED_GP;
        return original;
    }

    public static <A> Codec.ResultFunction<A> fixBlockName(Codec<A> codec) {
        return new Codec.ResultFunction<>() {
            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> a) {
                if (a.isSuccess()) return a;
                MutableObject<DataResult<Pair<A, T>>> mutableObject = new MutableObject<>(a);
                ops.getMap(input).ifSuccess(map -> {
                    T t = map.get(StateHolder.NAME_TAG);
                    if (t != null) ops.getStringValue(t).ifSuccess(s -> {
                        String s1 = s;
                        while ((s = BLOCK_NAME_FIX_MAP.get(s1)) != null) s1 = s;
                        ops.mergeToMap(input, ops.createString(StateHolder.NAME_TAG), ops.createString(s1))
                                .ifSuccess(t1 -> mutableObject.setValue(codec.decode(ops, t1)));
                    });
                });
                return mutableObject.getValue();
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> t) {
                return t;
            }
        };
    }

    public static void fixPersistentData(Player player) {
        CompoundTag persistedData = LibUtils.getOrCreatePersistedData(player);
        if (persistedData.getBoolean("confluence:fixed_persistent_data")) return;
        CompoundTag persistentData = player.getPersistentData();
        persistentData.getAllKeys().removeIf(key -> {
            if (key.startsWith("confluence:") || key.startsWith("terra_curio:")) {
                Tag value = persistentData.get(key);
                if (value != null) persistedData.put(key, value);
                return true;
            }
            return false;
        });
        persistedData.putBoolean("confluence:fixed_persistent_data", true);
    }

    public static ResourceLocation fixPatchKeyNamespace(ResourceLocation original) {
        if (original.getNamespace().equals("terra_curio")) {
            String path = original.getPath();
            if (path.equals("nbt") || path.equals("tool_mode") || path.equals("mod_rarity")) {
                return ConfluenceMagicLib.asResource(path);
            }
        }
        return original;
    }

    public static Codec<Holder<Item>> fixItemName(Codec<Holder<Item>> codec) {
        return codec.mapResult(new Codec.ResultFunction<>() {
            @Override
            public <T> DataResult<Pair<Holder<Item>, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<Holder<Item>, T>> a) {
                if (a.isSuccess()) return a;
                return ops.getStringValue(input).result().map(s -> {
                    String s1 = s;
                    while ((s = ITEM_NAME_FIX_MAP.get(s1)) != null) s1 = s;
                    return codec.decode(ops, ops.createString(s1));
                }).orElse(a);
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, Holder<Item> input, DataResult<T> t) {
                return t;
            }
        });
    }

    public static Codec.ResultFunction<Holder<Biome>> fixBiomeName(Codec<Holder<Biome>> codec) {
        return new Codec.ResultFunction<>() {
            @Override
            public <T> DataResult<Pair<Holder<Biome>, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<Holder<Biome>, T>> a) {
                if (a.isSuccess()) return a;
                return ops.getStringValue(input).result().map(s -> {
                    String s1 = s;
                    while ((s = BIOME_NAME_FIX_MAP.get(s1)) != null) s1 = s;
                    return codec.decode(ops, ops.createString(s1));
                }).orElse(a);
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, Holder<Biome> input, DataResult<T> t) {
                return t;
            }
        };
    }

    public static void postRegisterEvents() {
        ImmutableMap.Builder<String, String> blockWithItem = ImmutableMap.builder();
        ModLoader.postEvent(new NameFixRegisterEvent.BlockWithItem(blockWithItem));
        ImmutableMap<String, String> map = blockWithItem.build();

        ImmutableMap.Builder<String, String> block = ImmutableMap.builder();
        ModLoader.postEvent(new NameFixRegisterEvent.Block(block));
        block.putAll(map);
        BLOCK_NAME_FIX_MAP = block.build();

        ImmutableMap.Builder<String, String> item = ImmutableMap.builder();
        ModLoader.postEvent(new NameFixRegisterEvent.Item(item));
        item.putAll(map);
        ITEM_NAME_FIX_MAP = item.build();

        ImmutableMap.Builder<String, String> biome = ImmutableMap.builder();
        ModLoader.postEvent(new NameFixRegisterEvent.Biome(biome));
        BIOME_NAME_FIX_MAP = biome.build();
    }
}
