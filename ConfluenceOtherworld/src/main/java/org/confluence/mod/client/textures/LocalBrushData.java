package org.confluence.mod.client.textures;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LocalBrushData {
    private static final Long2ObjectMap<int[]> DATA = new Long2ObjectOpenHashMap<>();

    public static void putData(BlockPos pos, Direction facing, int color) {
        try {
            DATA.computeIfAbsent(pos.asLong(), l -> BrushData.createColor(BrushData.EMPTY_COLOR))[facing.get3DDataValue()] = color;
        } catch (Exception ignored) {}
    }

    public static int @Nullable [] getColors(BlockPos pos) {
        try {
            return DATA.get(pos.asLong());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasEcho(BlockPos pos) {
        return hasEcho(pos.asLong());
    }

    public static boolean hasEcho(long pos) {
        try {
            int[] colors = DATA.get(pos);
            return colors != null && colors[0] == BrushData.ECHO_COLOR;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getColor(BlockPos pos, @Nullable Direction facing) {
        if (facing == null) return BrushData.EMPTY_COLOR;
        try {
            int[] colors = DATA.get(pos.asLong());
            return colors == null ? BrushData.EMPTY_COLOR : colors[facing.get3DDataValue()];
        } catch (Exception e) {
            return BrushData.EMPTY_COLOR;
        }
    }

    public static void removeData(BlockPos pos, Direction facing) {
        try {
            int[] colors = DATA.get(pos.asLong());
            if (colors != null) {
                colors[facing.get3DDataValue()] = BrushData.EMPTY_COLOR;
                if (Arrays.stream(colors).allMatch(i -> i == BrushData.EMPTY_COLOR)) {
                    DATA.remove(pos.asLong());
                }
            }
        } catch (Exception ignored) {}
    }

    public static void clear() {
        DATA.clear();
    }

    public static void handlePacket(BrushingColorPacketS2C packet) {
        Set<SectionPos> sectionPoses = new HashSet<>();
        for (Map.Entry<BlockPos, int[]> entry : packet.data().colors().entrySet()) {
            BlockPos pos = entry.getKey();
            int[] colors = entry.getValue();
            for (int i = 0; i < 6; i++) {
                int color = colors[i];
                Direction facing = LibUtils.DIRECTIONS[i];
                if (color == BrushData.CLEAR_COLOR) {
                    removeData(pos, facing);
                } else if (color != BrushData.EMPTY_COLOR) {
                    putData(pos, facing, color);
                }
            }
            sectionPoses.add(SectionPos.of(pos));
        }
        for (SectionPos sectionPos : sectionPoses) {
            Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
        }
    }
}
