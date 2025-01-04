package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

public record BrushData(Map<BlockPos, IntArrayList> colors) {
    public static final Function<BlockPos, IntArrayList> COMPUTE = pos -> createColor(-1);
    public static final MapCodec<BrushData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(
                    Codec.STRING.xmap(str -> {
                        String[] split = str.split(", ");
                        int[] pos = new int[3];
                        for (int i = 0; i < 3; i++) {
                            if (i < split.length) {
                                pos[i] = Integer.parseInt(split[i]);
                            }
                        }
                        return new BlockPos(pos[0], pos[1], pos[2]);
                    }, Vec3i::toShortString),
                    Codec.INT.listOf().xmap(IntArrayList::new, Function.identity())
            ).fieldOf("entries").forGetter(BrushData::colors)
    ).apply(instance, map -> new BrushData(new Hashtable<>(map))));
    public static final Codec<BrushData> CODEC = MAP_CODEC.codec();

    public BrushData(BlockPos pos, @Nullable Direction facing, int color) {
        this(Util.make(new Hashtable<>(), map -> {
            IntArrayList colors;
            if (facing == null) {
                colors = createColor(color);
            } else {
                colors = createColor(-1);
                colors.set(facing.get3DDataValue(), color);
            }
            map.put(pos, colors);
        }));
    }

    public int get(BlockPos pos, Direction facing) {
        IntArrayList list = colors.get(pos);
        return list == null ? -1 : list.getInt(facing.get3DDataValue());
    }

    public void put(BlockPos pos, IntArrayList color) {
        IntArrayList list = colors.get(pos);
        if (list == null) {
            colors.put(pos, color);
        } else {
            for (int i = 0; i < 6; i++) {
                list.set(i, color.getInt(i));
            }
        }
    }

    public void put(BlockPos pos, Direction facing, int color) {
        colors.computeIfAbsent(pos, COMPUTE).set(facing.get3DDataValue(), color);
    }

    public void merge(BrushData data) {
        for (Map.Entry<BlockPos, IntArrayList> entry : data.colors.entrySet()) {
            BlockPos pos = entry.getKey();
            IntArrayList list = colors.get(pos);
            IntArrayList color = entry.getValue();
            if (list == null) {
                colors.put(pos, color);
            } else {
                for (int i = 0; i < 6; i++) {
                    int c = color.getInt(i);
                    if (c != -1) list.set(i, c);
                }
            }
        }
    }

    public boolean remove(BlockPos pos) {
        return colors.remove(pos) != null;
    }

    public boolean remove(BlockPos pos, Direction facing) {
        IntArrayList list = colors.get(pos);
        if (list == null) return false;
        int index = facing.get3DDataValue();
        boolean b = list.getInt(index) != -1;
        list.set(index, -1);
        if (list.intStream().allMatch(c -> c == -1)) {
            colors.remove(pos);
        }
        return b;
    }

    public void ensureValid(ServerLevel serverLevel) {
        colors.entrySet().removeIf(entry -> serverLevel.isLoaded(entry.getKey()) && serverLevel.getBlockState(entry.getKey()).isEmpty());
    }

    public static @NotNull IntArrayList createColor(int color) {
        IntArrayList colors = new IntArrayList();
        for (int i = 0; i < 6; i++) {
            colors.add(i, color);
        }
        return colors;
    }
}
