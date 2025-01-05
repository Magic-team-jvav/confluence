package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

public record BrushData(Map<BlockPos, int[]> colors) {
    public static final Function<BlockPos, int[]> COMPUTE = pos -> createColor(-1);
    public static final String POS_SPLIT = ", ";
    public static final MapCodec<BrushData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(
                    Codec.STRING.xmap(str -> {
                        String[] split = str.split(POS_SPLIT);
                        int[] pos = new int[3];
                        for (int i = 0; i < 3; i++) {
                            if (i < split.length) {
                                pos[i] = Integer.parseInt(split[i]);
                            }
                        }
                        return new BlockPos(pos[0], pos[1], pos[2]);
                    }, pos -> pos.getX() + POS_SPLIT + pos.getY() + POS_SPLIT + pos.getZ()),
                    Codec.INT.listOf().xmap(list -> new IntArrayList(list).elements(), IntArrayList::new)
            ).fieldOf("entries").forGetter(BrushData::colors)
    ).apply(instance, map -> new BrushData(new Hashtable<>(map))));
    public static final Codec<BrushData> CODEC = MAP_CODEC.codec();

    public BrushData(BlockPos pos, @Nullable Direction facing, int color) {
        this(Util.make(new Hashtable<>(), map -> {
            int[] colors;
            if (facing == null) {
                colors = createColor(color);
            } else {
                colors = createColor(-1);
                colors[facing.get3DDataValue()] = color;
            }
            map.put(pos, colors);
        }));
    }

    public int get(BlockPos pos, Direction facing) {
        int[] list = colors.get(pos);
        return list == null ? -1 : list[facing.get3DDataValue()];
    }

    public void put(BlockPos pos, int[] color) {
        int[] list = colors.get(pos);
        if (list == null) {
            colors.put(pos, color);
        } else {
            System.arraycopy(color, 0, list, 0, 6);
        }
    }

    public void put(BlockPos pos, Direction facing, int color) {
        colors.computeIfAbsent(pos, COMPUTE)[facing.get3DDataValue()] = color;
    }

    public void merge(BrushData data) {
        for (Map.Entry<BlockPos, int[]> entry : data.colors.entrySet()) {
            BlockPos pos = entry.getKey();
            int[] list = colors.get(pos);
            int[] color = entry.getValue();
            if (list == null) {
                colors.put(pos, color);
            } else {
                for (int i = 0; i < 6; i++) {
                    int c = color[i];
                    if (c != -1) list[i] = c;
                }
            }
        }
    }

    public boolean remove(BlockPos pos) {
        return colors.remove(pos) != null;
    }

    public boolean remove(BlockPos pos, Direction facing) {
        int[] list = colors.get(pos);
        if (list == null) return false;
        int index = facing.get3DDataValue();
        boolean b = list[index] != -1;
        list[index] = -1;
        for (int c : list) {
            if (c != -1) return b;
        }
        colors.remove(pos);
        return b;
    }

    public void ensureValid(ServerLevel serverLevel) {
        colors.entrySet().removeIf(entry -> serverLevel.isLoaded(entry.getKey()) && serverLevel.getBlockState(entry.getKey()).isEmpty());
    }

    public static int[] createColor(int color) {
        return new int[]{color, color, color, color, color, color};
    }
}
