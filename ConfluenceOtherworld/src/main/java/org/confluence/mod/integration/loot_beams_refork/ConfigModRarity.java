package org.confluence.mod.integration.loot_beams_refork;

import me.clefal.lootbeams.config.impl.ModifyingConfigHandler;
import me.clefal.lootbeams.data.lbitementity.LBItemEntity;
import me.clefal.lootbeams.data.lbitementity.rarity.LBColor;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;

public class ConfigModRarity extends ModifyingConfigHandler {
    @Override
    public LBItemEntity modify(LBItemEntity lbItemEntity) {
        ModRarity rarity = lbItemEntity.item().getItem().get(ConfluenceMagicLib.MOD_RARITY);
        if (rarity == null) return lbItemEntity;
        return lbItemEntity.to(lbItemEntity.rarity().modifyColor(LBColor.of(rarity.color())));
    }
}
