package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.permanent.PermanentUpgradeItem;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.PermanentUpgrades;

/// 魔力水晶通过自定义 levelAccess 接入 MagicLib 永久升级 API，ManaStorage 仍是魔力容量的权威数据源。
public class ManaCrystalItem extends PermanentUpgradeItem {
    public ManaCrystalItem() {
        super(new Properties().stacksTo(16).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.YELLOW),
                PermanentUpgrades.MANA_CRYSTAL,
                ModSoundEvents.MANA_STAR_USE,
                TooltipItem.getTooltipsFromString("mana_crystal", 1, ChatFormatting.GREEN));
    }
}
