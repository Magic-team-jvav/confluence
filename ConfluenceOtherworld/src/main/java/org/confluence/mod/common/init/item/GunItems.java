package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.StarCannonItem;

public class GunItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<StarCannonItem> STAR_CANNON = ITEMS.registerItem("star_cannon", StarCannonItem::new); // 桑百颗
}
