package org.confluence.mod.client.graphics;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;

import java.util.List;

/// 画质里要单独处理的点光源（目前是灯笼夜的灯笼）：每帧挑出离相机最近的 4 个，
/// 给水面的灯光倒影（WaterSurface）和屏幕空间的灯光光束（TerraGraphics，terra_point_rays）用。
final class PointLights {
    static final int MAX = 4;
    static final float RANGE = 48.0F;
    /// 灯笼的暖橙色（与 LanternNightSprite 的动态光一致）
    static final float RED = 1.0F, GREEN = 0.69F, BLUE = 0.38F;

    static final float[] x = new float[MAX], y = new float[MAX], z = new float[MAX], distance = new float[MAX];
    static int count;

    private PointLights() {}

    /// 相对相机的位置，按距离从近到远
    static void select(Vec3 camera) {
        count = 0;
        List<Vec3> lights = ClientGameEventSystem.lanternLights();
        for (Vec3 light : lights) {
            float dx = (float) (light.x - camera.x), dy = (float) (light.y - camera.y), dz = (float) (light.z - camera.z);
            float d = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            if (d > RANGE) continue;
            int slot = count < MAX ? count++ : -1;
            if (slot < 0) {
                // 已满：替换最远的那个
                int farthest = 0;
                for (int i = 1; i < MAX; i++) if (distance[i] > distance[farthest]) farthest = i;
                if (d >= distance[farthest]) continue;
                slot = farthest;
            }
            x[slot] = dx;
            y[slot] = dy;
            z[slot] = dz;
            distance[slot] = d;
        }
    }

    /// 远处渐隐
    static float fade(int index) {
        return 1.0F - Mth.clamp((distance[index] - 32.0F) / (RANGE - 32.0F), 0.0F, 1.0F);
    }

    /// 天越黑，灯光越显眼：白天几乎看不出光束和倒影
    static float darkness(ClientLevel level, float partialTick) {
        return Mth.clamp((1.0F - level.getSkyDarken(partialTick)) * 1.6F, 0.12F, 1.0F);
    }
}
