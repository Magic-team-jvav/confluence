package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.item.IWeaponTooltip;

import javax.annotation.ParametersAreNonnullByDefault;

/// 祈灵之刃：战士侧副武器，基础伤害走近战属性，灵魂只作为附条件增伤与额外收魂的来源。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulKnifeItem extends SoulSecondaryWeaponItem implements IWeaponTooltip {
    public SoulKnifeItem(Item.Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }
}
