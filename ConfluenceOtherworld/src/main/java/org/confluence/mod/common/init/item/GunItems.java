package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import org.confluence.terra_guns.common.item.gun.CustomGun;

public class GunItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<BaseGun> STAR_CANNON = ITEMS.registerItem("star_cannon", properties -> new CustomGun(properties, 4, 14.8f, 1.8f, 0.15f, 0.04f, -1, 0.0f, ModRarity.GREEN, 0f)); // 桑百颗
}
