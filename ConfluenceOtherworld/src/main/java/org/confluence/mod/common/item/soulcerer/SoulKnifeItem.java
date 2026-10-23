package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.item.IWeaponTooltip;

import javax.annotation.ParametersAreNonnullByDefault;

/// 祈灵之刃：战士侧副武器，基础伤害走近战属性，灵魂只作为附条件增伤与额外收魂的来源。
///
/// 它没有耐久（走 {@code ModTiers.UNBREAKABLE} 的攻击属性），原版默认的
/// {@link Item#isEnchantable} 会因缺少最大耐久而判定为不可附魔，因此这里显式放开附魔台。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulKnifeItem extends SoulSecondaryWeaponItem implements IWeaponTooltip {
    public SoulKnifeItem(Item.Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }
}
