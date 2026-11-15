package org.confluence.mod.common.item.bow;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.mod.api.item.ISoulBow;

import java.util.List;

/// 灵魂弓：满蓄力三倍伤害的常规弓体，与灵魂短弓共用「击杀额外收魂 + 灵魂高于阈值增伤」的魂师弓机制。
/// 不可破坏：没有耐久限制，不消耗耐久。
public class SoulBowItem extends BaseTerraBowItem implements ISoulBow {
    public SoulBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ISoulBow.appendSoulBowTooltips(stack, tooltip);
    }
}
