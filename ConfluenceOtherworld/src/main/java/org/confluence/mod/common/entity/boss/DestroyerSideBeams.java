package org.confluence.mod.common.entity.boss;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 主激光蓄力期间逐个点亮的侧向光束；客户端和服务端使用同一组选择及姿态。
public final class DestroyerSideBeams {
    public static final int COUNT = (TheDestroyer.SEGMENT_COUNT - 2) / 8;

    private DestroyerSideBeams() {}

    private static int mix(int value) {
        value ^= value >>> 16;
        value *= 0x7feb352d;
        value ^= value >>> 15;
        value *= 0x846ca68b;
        return value ^ (value >>> 16);
    }

    public static int section(TheDestroyer boss, int ordinal) {
        return 1 + ordinal * 8 + Math.floorMod(mix(boss.getSideBeamSeed() + ordinal), 4) * 2;
    }

    public static int startTick(TheDestroyer boss, int ordinal) {
        return 100 + Math.floorMod(mix(boss.getSideBeamSeed() ^ (ordinal * 0x9e3779b9)), 120);
    }

    public static Vec3 direction(TheDestroyer boss, int ordinal, float partialTick) {
        int hash = mix(boss.getSideBeamSeed() + ordinal * 31);
        float side = (hash & 1) == 0 ? 1 : -1;
        float y = ((hash >>> 1 & 255) / 255.0F - 0.5F) * 0.7F;
        float z = ((hash >>> 9 & 255) / 255.0F - 0.5F) * 0.7F;
        Vector3f vector = boss.getBody().getRenderRotation(section(boss, ordinal), partialTick)
                .transform(new Vector3f(side, y, z).normalize());
        return new Vec3(vector.x, vector.y, vector.z).normalize();
    }

    public static Vec3 origin(TheDestroyer boss, int ordinal, float partialTick) {
        int index = section(boss, ordinal);
        float side = (mix(boss.getSideBeamSeed() + ordinal * 31) & 1) == 0 ? 1 : -1;
        Quaternionf rotation = boss.getBody().getRenderRotation(index, partialTick);
        // 模型中两侧探针定位点相对于体节中心的位置，随体节姿态旋转。
        Vector3f offset = rotation.transform(new Vector3f(side * 3.17F, 0.03F, 1.59F));
        return boss.getBody().getRenderPosition(index, partialTick).add(offset.x, TheDestroyer.BODY_HEIGHT / 2.0 + offset.y, offset.z);
    }
}
