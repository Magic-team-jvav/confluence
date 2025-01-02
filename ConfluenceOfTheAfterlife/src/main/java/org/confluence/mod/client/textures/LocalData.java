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
public final class LocalData {
    private static final Hashtable<BlockPos, Object2IntOpenHashMap<Integer>> DATA = new Hashtable<>();

    public static void putData(BlockPos pos, Integer tint, int color) {
        Object2IntOpenHashMap<Integer> map = DATA.computeIfAbsent(pos, pos1 -> new Object2IntOpenHashMap<>());
        map.put(tint, color);
    }

    public static @Nullable Object2IntOpenHashMap<Integer> get(BlockPos pos) {
        return DATA.get(pos);
    }

    public static int getColor(BlockPos pos, Integer tint) {
        Object2IntOpenHashMap<Integer> map = DATA.get(pos);
        if (map == null) return -1;
        return map.getOrDefault(tint, 0xFFFFFF);
    }

    public static boolean hasColor(BlockPos pos) {
        return DATA.get(pos) != null;
    }

    public static void removeData(BlockPos pos) {
        DATA.remove(pos);
    }

    public static void removeData(BlockPos pos, Integer tint) {
        Object2IntOpenHashMap<Integer> map = DATA.get(pos);
        if (map != null) map.removeInt(tint);
    }

    public static void clear() {
        DATA.clear();
    }

    public static void handlePacket(BrushingColorPacketS2C packet) {
        Set<SectionPos> sectionPoses = new HashSet<>();
        for (BrushData.Entry entry : packet.data().colors()) {
            BlockPos pos = entry.pos();
            if (entry.map().isEmpty()) {
                removeData(pos);
            } else {
                for (Map.Entry<String, Integer> integerEntry : entry.map().entrySet()) {
                    int tint = Integer.parseInt(integerEntry.getKey());
                    int color = integerEntry.getValue();
                    if (color == -1) {
                        removeData(pos, tint);
                    } else {
                        putData(pos, tint, color);
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
