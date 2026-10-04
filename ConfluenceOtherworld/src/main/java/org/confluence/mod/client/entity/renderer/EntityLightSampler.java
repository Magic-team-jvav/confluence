package org.confluence.mod.client.entity.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.function.ToIntFunction;

/// 在相邻光照格点之间插值，保留光照贴图坐标的小数级精度，避免跨格时整块跳亮。
public final class EntityLightSampler {
    private EntityLightSampler() {}

    public static int sample(Vec3 position, ToIntFunction<BlockPos> blockLight, ToIntFunction<BlockPos> skyLight) {
        double x = position.x - 0.5, y = position.y - 0.5, z = position.z - 0.5;
        int bx = Mth.floor(x), by = Mth.floor(y), bz = Mth.floor(z);
        double fx = x - bx, fy = y - by, fz = z - bz;
        double block = 0.0, sky = 0.0;
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    double weight = (dx == 0 ? 1.0 - fx : fx) * (dy == 0 ? 1.0 - fy : fy) * (dz == 0 ? 1.0 - fz : fz);
                    if (weight == 0.0) continue;
                    probe.set(bx + dx, by + dy, bz + dz);
                    block += blockLight.applyAsInt(probe) * weight;
                    sky += skyLight.applyAsInt(probe) * weight;
                }
            }
        }
        // UV2 的两路坐标每级占十六单位，先乘再取整才能保留级间过渡。
        return Mth.clamp((int) Math.round(block * 16.0), 0, 240) | Mth.clamp((int) Math.round(sky * 16.0), 0, 240) << 16;
    }
}
