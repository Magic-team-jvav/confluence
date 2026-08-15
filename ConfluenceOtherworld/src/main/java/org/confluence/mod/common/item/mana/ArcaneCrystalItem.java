package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.permanent.PermanentUpgradeItem;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.PermanentUpgrades;

/**
 * 奥术水晶不再自建使用逻辑；一次性状态、失败反馈和消耗均由公共永久升级物品处理。
 */
public class ArcaneCrystalItem extends PermanentUpgradeItem {
    public ArcaneCrystalItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_PURPLE),
                PermanentUpgrades.ARCANE_CRYSTAL,
                ModSoundEvents.TRANSMUTATION_USE,
                TooltipItem.getTooltipsFromString("arcane_crystal", 1, ChatFormatting.GREEN));
    }
}
