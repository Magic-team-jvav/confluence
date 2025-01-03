package org.confluence.mod.client.textures;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class LocalBrushData {
    private static final Hashtable<BlockPos, Object2IntOpenHashMap<BrushData.Facing>> DATA = new Hashtable<>();

    public static void putData(BlockPos pos, BrushData.Facing facing, int color) {
        DATA.computeIfAbsent(pos, pos1 -> new Object2IntOpenHashMap<>()).put(facing, color);
    }

    public static @Nullable Object2IntOpenHashMap<BrushData.Facing> get(BlockPos pos) {
        return DATA.get(pos);
    }

    public static int getColor(BlockPos pos, Integer tint) {
        Object2IntOpenHashMap<BrushData.Facing> map = DATA.get(pos);
        if (map == null) return -1;
        return map.getOrDefault(tint, -1);
    }

    public static int getColor(BlockPos pos) {
        Object2IntOpenHashMap<BrushData.Facing> map = DATA.get(pos);
        if (map == null) return -1;
        return map.getOrDefault(BrushData.Facing.ALL, -2);
    }

    public static boolean hasColor(BlockPos pos) {
        return DATA.get(pos) != null;
    }

    public static void removeData(BlockPos pos) {
        DATA.remove(pos);
    }

    public static void removeData(BlockPos pos, BrushData.Facing facing) {
        Object2IntOpenHashMap<BrushData.Facing> map = DATA.get(pos);
        if (map != null) map.removeInt(facing);
    }

    public static void clear() {
        DATA.clear();
    }

    public static void handlePacket(BrushingColorPacketS2C packet) {
        Set<SectionPos> sectionPoses = new HashSet<>();
        for (Map.Entry<BlockPos, BrushData.Entry> entry : packet.data().colors().entrySet()) {
            BlockPos pos = entry.getKey();
            if (entry.getValue().map().isEmpty()) {
                removeData(pos);
            } else {
                for (Map.Entry<BrushData.Facing, Integer> integerEntry : entry.getValue().map().entrySet()) {
                    BrushData.Facing facing = integerEntry.getKey();
                    int color = integerEntry.getValue();
                    if (color == -1) {
                        removeData(pos, facing);
                    } else {
                        putData(pos, facing, color);
                    }
                }
            }
            sectionPoses.add(SectionPos.of(pos));
        }
        for (SectionPos sectionPos : sectionPoses) {
            Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
        }
    }
}
