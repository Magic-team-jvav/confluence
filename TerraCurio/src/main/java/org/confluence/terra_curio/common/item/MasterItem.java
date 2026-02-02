package org.confluence.terra_curio.common.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;

public class MasterItem extends Item {
    public MasterItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.MASTER));
    }
}
