package org.confluence.mod.common.entity.ai.bt.leaf;

/// 以世界游戏刻为时间源的轻量冷却器。
///
/// 行为树节点可能被停止并重新启动，若只在节点运行时递减计数会造成冷却漂移。
/// 保存绝对就绪时间可以让冷却跨节点生命周期继续流逝，并自然适配服务器暂停后的游戏时间。
final class GameTickCooldown {
    private long readyAtGameTick;

    boolean isReady(long gameTime) {
        return gameTime >= readyAtGameTick;
    }

    void restart(long gameTime, int durationTicks) {
        if (durationTicks < 0) {
            throw new IllegalArgumentException("durationTicks must not be negative");
        }
        readyAtGameTick = gameTime + durationTicks;
    }

    long readyAtGameTick() {
        return readyAtGameTick;
    }
}
