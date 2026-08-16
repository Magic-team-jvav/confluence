package org.confluence.mod.api.whip;

/// 鞭子命中同一玩家的友方召唤物时执行的效果。
///
/// <p>该入口用于保留 1.21 皮鞭一类“强化友方召唤物”的行为。它与敌对目标的直接命中效果、
/// 敌对目标身上的召唤标记分别处理，避免为了一个增益复制整套挥鞭逻辑。</p>
@FunctionalInterface
public interface WhipFriendlyHitEffect {
    void apply(WhipFriendlyHitContext context);
}
