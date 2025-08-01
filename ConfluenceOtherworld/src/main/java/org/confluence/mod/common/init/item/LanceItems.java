package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.lance.*;

public class LanceItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);


    public static final DeferredItem<SpearItem> SPEAR = ITEMS.register("spear", SpearItem::new);
    public static final DeferredItem<TheRottedForkItem> THE_ROTTED_FORK = ITEMS.register("the_rotted_fork", TheRottedForkItem::new);
    public static final DeferredItem<DarkLanceItem> DARK_LANCE = ITEMS.register("dark_lance", DarkLanceItem::new);
    public static final DeferredItem<CobaltNaginataItem> COBALT_NAGINATA = ITEMS.register("cobalt_naginata", CobaltNaginataItem::new);
    public static final DeferredItem<CobaltNaginataItem> PALLADIUM_PIKE = ITEMS.register("palladium_pike", CobaltNaginataItem::new);
    public static final DeferredItem<OrichalcumHalberdItem> ORICHALCUM_HALBERD = ITEMS.register("orichalcum_halberd", OrichalcumHalberdItem::new);
}
