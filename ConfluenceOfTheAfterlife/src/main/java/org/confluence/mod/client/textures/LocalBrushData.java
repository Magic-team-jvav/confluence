package org.confluence.mod.client.textures;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class LocalBrushData {
    private static final Long2ObjectMap<EnumMap<Direction, Integer>> DATA = new Long2ObjectOpenHashMap<>();

    public static void putData(BlockPos pos, Direction facing, int color) {
        DATA.computeIfAbsent(pos.asLong(), pos1 -> new EnumMap<>(Direction.class)).put(facing, color);
    }

    public static @Nullable EnumMap<Direction, Integer> getDirs(BlockPos pos) {
        return DATA.get(pos.asLong());
    }

    public static int getColor(BlockPos pos, Direction facing) {
        EnumMap<Direction, Integer> map = DATA.get(pos.asLong());
        return map == null ? BrushData.EMPTY_COLOR : map.getOrDefault(facing, BrushData.EMPTY_COLOR);
    }

    public static boolean hasColor(BlockPos pos) {
        return DATA.get(pos.asLong()) != null;
    }

    public static void removeData(BlockPos pos) {
        DATA.remove(pos.asLong());
    }

    public static void removeData(BlockPos pos, Direction facing) {
        EnumMap<Direction, Integer> map = DATA.get(pos.asLong());
        if (map != null) {
            map.remove(facing);
            if (map.isEmpty()) DATA.remove(pos.asLong());
        }
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
                Direction facing = ModUtils.DIRECTIONS[i];
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
