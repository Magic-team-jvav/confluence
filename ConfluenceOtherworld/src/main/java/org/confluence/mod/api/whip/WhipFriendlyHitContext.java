package org.confluence.mod.api.whip;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// 鞭子命中同一玩家拥有的友方召唤物时提供的只读上下文。
///
/// @param owner     鞭子的使用者，也是召唤物的主人
/// @param summon    被鞭子命中的友方召唤物
/// @param whipStack 本次挥鞭开始时保存的物品快照
public record WhipFriendlyHitContext(
        Player owner,
        LivingEntity summon,
        ItemStack whipStack
) {
}
