package org.confluence.terraentity.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


/**
 * 慎用！！！！
 * 用于超大boss在渲染距离之外的保活接口。
 * 可能有未知的性能问题
 * 慎重！！！！
 */
public interface IExtendedTracking {
    /**
     * @return 保持追踪的水平最大距离(不计算y)（方块数）。
     * 默认返回 1024.0。
     */
    default double getExtendedTrackingRange() {
        return  1024.0D;
    }

    /**
     * 检查玩家是否在扩展追踪范围内
     *
     * @param entity 实现了 IExtendedTracking 的实体
     * @param player 目标玩家
     * @return 如果玩家在追踪范围内返回 true
     */
    static boolean isPlayerInTrackingRange(Entity entity, Player player) {
        if (!(entity instanceof IExtendedTracking tracker)) {
            return false;
        }
        double distance = calculateHorizontalDistance(entity, player);
        return distance < tracker.getExtendedTrackingRange();
    }

    /**
     * 计算实体与玩家之间的水平距离（不计算 Y 轴）
     *
     * @param entity 要计算距离的实体
     * @param player 目标玩家
     * @return 水平距离（方块数）
     */
    static double calculateHorizontalDistance(Entity entity, Player player) {
        double deltaX = entity.getX() - player.getX();
        double deltaZ = entity.getZ() - player.getZ();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }


}
