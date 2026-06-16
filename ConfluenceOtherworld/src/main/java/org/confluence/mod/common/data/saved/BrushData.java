package org.confluence.mod.common.data.saved;

import PortLib.extensions.net.minecraft.world.level.block.state.BlockState.PortBlockStateExtension;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

public record BrushData(Map<BlockPos, int[]> colors) {
    public static final int EMPTY_COLOR = -1;
    public static final int CLEAR_COLOR = -2;
    public static final int NEGATIVE_COLOR = -3;
    public static final int ILLUMINANT_COLOR = -4;
    public static final int ECHO_COLOR = -5;
    public static final Function<BlockPos, int[]> COMPUTE = pos -> createColor(EMPTY_COLOR);

    public BrushData(BlockPos pos, @Nullable Direction facing, int color) {
        this(Util.make(new Hashtable<>(), map -> {
            int[] colors;
            if (facing == null) {
                colors = createColor(color);
            } else {
                colors = createColor(EMPTY_COLOR);
                colors[facing.get3DDataValue()] = color;
            }
            map.put(pos, colors);
        }));
    }

    public int @Nullable [] get(BlockPos pos) {
        return colors.get(pos);
    }

    public int get(BlockPos pos, Direction facing) {
        int[] list = colors.get(pos);
        return list == null ? EMPTY_COLOR : list[facing.get3DDataValue()];
    }

    public int getOrDefault(BlockPos pos, Direction facing, int defaultColor) {
        int[] list = colors.get(pos);
        return list == null ? defaultColor : list[facing.get3DDataValue()];
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
        int[] colors = this.colors.computeIfAbsent(pos, COMPUTE);
        if (color == ECHO_COLOR) {
            Arrays.fill(colors, ECHO_COLOR);
        } else {
            if (colors[0] == ECHO_COLOR) {
                Arrays.fill(colors, EMPTY_COLOR);
            }
            colors[facing.get3DDataValue()] = color;
        }
    }

    public void merge(BrushData data) {
        for (Map.Entry<BlockPos, int[]> entry : data.colors.entrySet()) {
            BlockPos pos = entry.getKey();
            int[] list = colors.get(pos);
            int[] color = entry.getValue();
            if (list == null) {
                colors.put(pos, color);
            } else if (Arrays.stream(color).anyMatch(c -> c != EMPTY_COLOR)) {
                if (list[0] == ECHO_COLOR) {
                    Arrays.fill(list, EMPTY_COLOR);
                }
                for (int i = 0; i < 6; i++) {
                    int c = color[i];
                    if (c != EMPTY_COLOR) list[i] = c;
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
        boolean b = list[index] != EMPTY_COLOR;
        if (list[index] != ECHO_COLOR) {
            list[index] = EMPTY_COLOR;
            for (int c : list) {
                if (c != EMPTY_COLOR) return b;
            }
        }
        colors.remove(pos);
        return b;
    }

    public void ensureValid(ServerLevel level) {
        colors.entrySet().removeIf(entry -> level.isLoaded(entry.getKey()) && PortBlockStateExtension.isEmpty(level.getBlockState(entry.getKey())));
    }

    public static int[] createColor(int color) {
        return new int[]{color, color, color, color, color, color};
    }

    public static int[] rotateColor(int[] colors, Rotation rotation) {
        if (rotation == Rotation.NONE) return colors;
        int[] rotated = new int[6];
        for (int i = 0; i < 6; i++) {
            int j = rotation.rotate(Direction.from3DDataValue(i)).get3DDataValue();
            rotated[j] = colors[i];
        }
        return rotated;
    }
}
