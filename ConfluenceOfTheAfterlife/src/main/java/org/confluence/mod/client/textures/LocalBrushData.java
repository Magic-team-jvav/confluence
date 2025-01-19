package org.confluence.mod.client.textures;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class LocalBrushData {
    private static final Hashtable<BlockPos, Object2IntOpenHashMap<Direction>> DATA = new Hashtable<>();

    public static void putData(BlockPos pos, Direction facing, int color) {
        DATA.computeIfAbsent(pos, pos1 -> new Object2IntOpenHashMap<>()).put(facing, color);
    }

    public static @Nullable Object2IntMap<Direction> getDirs(BlockPos pos) {
        return DATA.get(pos);
    }

    public static int getColor(BlockPos pos, Direction facing) {
        Object2IntOpenHashMap<Direction> map = DATA.get(pos);
        return map == null ? BrushData.EMPTY_COLOR : map.getOrDefault(facing, BrushData.EMPTY_COLOR);
    }

    public static boolean hasColor(BlockPos pos) {
        return DATA.get(pos) != null;
    }

    public static void removeData(BlockPos pos) {
        DATA.remove(pos);
    }

    public static void removeData(BlockPos pos, Direction facing) {
        Object2IntOpenHashMap<Direction> map = DATA.get(pos);
        if (map != null) {
            map.removeInt(facing);
            if (map.isEmpty()) DATA.remove(pos);
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
