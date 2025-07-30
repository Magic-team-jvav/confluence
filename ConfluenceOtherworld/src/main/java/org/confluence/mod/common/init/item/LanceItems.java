package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.lance.DarkLanceItem;
import org.confluence.mod.common.item.lance.SpearItem;

public class LanceItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<DarkLanceItem> DARK_LANCE = ITEMS.register("dark_lance", DarkLanceItem::new);
    public static final DeferredItem<SpearItem> SPEAR = ITEMS.register("spear", SpearItem::new);
}
