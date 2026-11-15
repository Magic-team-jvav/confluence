package org.confluence.mod.api.whip;

/// 鞭子直接命中时立即执行的扩展效果。
///
/// 中毒、减速、点燃或给召唤物施加增益等逻辑属于这一阶段。标记伤害不能放在
/// 这里，因为标记只应在该玩家的召唤物随后攻击目标时生效。
@FunctionalInterface
public interface WhipDirectHitEffect {
    void apply(WhipDirectHitContext context);
}
