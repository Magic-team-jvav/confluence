package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulSecondaryWeaponItem extends CustomRarityItem {
    /// 保留具体武器的职业属性，灵魂效果由子类复用公共资源入口。
    public SoulSecondaryWeaponItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }
}
