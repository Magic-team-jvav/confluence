package org.confluence.mod.common.item.bow;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.mod.api.item.ISoulBow;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// 灵魂短弓：短弓手感，与灵魂弓共用「击杀额外收魂 + 灵魂高于阈值增伤」的魂师弓机制。
/// 不可破坏：没有耐久限制，不消耗耐久。
public class SoulShortBowItem extends ShortBowItem implements ISoulBow {
    public SoulShortBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ISoulBow.appendSoulBowTooltips(stack, tooltip);
    }
}
