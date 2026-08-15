package org.confluence.mod.client.handler;

/**
 * 把连续的放置速度倍率换算为整数游戏刻冷却。
 *
 * <p>Minecraft 的右键冷却只能保存整数，直接把四刻冷却减一会变成约 25% 提速。
 * 此处累计每次放置产生的小数刻收益，累计满一刻时才真正缩短冷却，因此长期平均速度
 * 与属性声明的倍率一致，同时不会改变打开容器等其他右键交互。</p>
 */
public final class PlacementSpeedHandler {
    private double savedTicks;

    /**
     * 返回本次成功放置后应使用的冷却。
     *
     * @param vanillaDelay    原版为本次操作设置的冷却
     * @param speedMultiplier 玩家当前放置速度倍率
     */
    public int apply(int vanillaDelay, double speedMultiplier) {
        if (vanillaDelay <= 0) return vanillaDelay;
        if (speedMultiplier <= 1.0) {
            savedTicks = 0.0;
            return vanillaDelay;
        }

        savedTicks += vanillaDelay - vanillaDelay / speedMultiplier;
        int reduction = (int) Math.floor(savedTicks + 1.0E-9);
        savedTicks -= reduction;
        return Math.max(0, vanillaDelay - reduction);
    }
}
