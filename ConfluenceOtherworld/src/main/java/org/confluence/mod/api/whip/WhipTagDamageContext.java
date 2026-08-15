package org.confluence.mod.api.whip;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.summon.OwnedSummon;

/**
 * 主人的召唤物攻击鞭子标记目标时提供的只读上下文。
 *
 * @param owner     召唤物主人
 * @param summon    发起伤害的召唤物
 * @param target    被攻击的标记目标
 * @param whipStack 施加当前标记时保存的鞭子快照
 */
public record WhipTagDamageContext(
        Player owner,
        OwnedSummon summon,
        LivingEntity target,
        ItemStack whipStack
) {
}
