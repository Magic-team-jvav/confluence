package org.confluence.mod.api.whip;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// 鞭子直接命中生物时提供给扩展效果的只读上下文。
///
/// @param owner    攻击者
/// @param target   被鞭子直接命中的生物
/// @param weapon   本次攻击开始时保存的武器快照
/// @param damage   本次直接命中的最终伤害
/// @param hitIndex 本次挥动中合法敌人的命中序号
public record WhipDirectHitContext(Player owner, LivingEntity target, ItemStack weapon,
                                   float damage, int hitIndex) {
}
