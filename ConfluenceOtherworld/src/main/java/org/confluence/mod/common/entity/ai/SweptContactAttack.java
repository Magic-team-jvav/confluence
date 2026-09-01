package org.confluence.mod.common.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/// 连续实体接触检测。
///
/// 普通的当前帧 AABB 查询会漏掉一帧内完整穿过目标的高速实体。这里把攻击者上一帧与
/// 当前帧的包围盒转换为“中心线段 + Minkowski 扩张后的目标包围盒”，既能覆盖冲刺路径，
/// 又不会像简单合并两个包围盒那样把斜向移动形成的整个矩形区域都判定为命中。
public final class SweptContactAttack {
    private SweptContactAttack() {}

    /// 默认允许连续检测的最大单 tick 位移。更大的变化视为传送，只检查落点包围盒。
    public static final double DEFAULT_MAX_SWEEP_DISTANCE = 16.0D;

    public static List<Entity> findTargets(Entity attacker, double inflation,
                                           double maximumSweepDistance,
                                           Predicate<Entity> filter) {
        return findTargets(attacker, new Vec3(attacker.xo, attacker.yo, attacker.zo),
                inflation, maximumSweepDistance, filter);
    }

    /// 使用调用方保存的移动起点执行连续检测。由父实体直接重排位置的部件不能依赖
    /// {@code xo/yo/zo}，因为部件自身开始 tick 时原版会把这些字段刷新到当前位置。
    public static List<Entity> findTargets(Entity attacker, Vec3 previousPosition, double inflation,
                                           double maximumSweepDistance,
                                           Predicate<Entity> filter) {
        AABB currentBox = attacker.getBoundingBox();
        Vec3 displacement = attacker.position().subtract(previousPosition);
        if (!isFinite(displacement) || displacement.lengthSqr() > maximumSweepDistance * maximumSweepDistance) {
            return attacker.level().getEntities(attacker, currentBox.inflate(inflation), filter);
        }

        AABB previousBox = currentBox.move(previousPosition.subtract(attacker.position()));
        AABB searchBox = previousBox.minmax(currentBox).inflate(inflation);
        Vec3 previousCenter = previousBox.getCenter();
        Vec3 currentCenter = currentBox.getCenter();
        double horizontalExtent = currentBox.getXsize() * 0.5D + inflation;
        double verticalExtent = currentBox.getYsize() * 0.5D + inflation;

        return attacker.level().getEntities(attacker, searchBox, candidate -> {
            if (!filter.test(candidate)) return false;
            AABB expandedTarget = candidate.getBoundingBox().inflate(
                    horizontalExtent,
                    verticalExtent,
                    horizontalExtent
            );
            return expandedTarget.contains(previousCenter)
                    || expandedTarget.contains(currentCenter)
                    || expandedTarget.clip(previousCenter, currentCenter).isPresent();
        });
    }

    private static boolean isFinite(Vec3 vector) {
        return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }
}
