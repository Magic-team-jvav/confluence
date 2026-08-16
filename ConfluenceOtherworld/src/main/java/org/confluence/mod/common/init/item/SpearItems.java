package org.confluence.mod.common.init.item;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.spear.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class SpearItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    // 矛参考数值为 泰拉wiki中的伤害÷2为基础值 击退值为一半
    public static final PortDeferredItem<SpearItem> SPEAR = ITEMS.register("spear", SpearItem::new);
    public static final PortDeferredItem<StormSpearItem> STORM_SPEAR = ITEMS.register("storm_spear", StormSpearItem::new);
    public static final PortDeferredItem<TheRottedForkItem> THE_ROTTED_FORK = ITEMS.register("the_rotted_fork", TheRottedForkItem::new);
    public static final PortDeferredItem<StreamstrikeHalberdItem> STREAMSTRIKE_HALBERD = ITEMS.register("streamstrike_halberd", StreamstrikeHalberdItem::new);
    public static final PortDeferredItem<DarkLanceItem> DARK_LANCE = ITEMS.register("dark_lance", DarkLanceItem::new);
    public static final PortDeferredItem<CobaltNaginataItem> COBALT_NAGINATA = ITEMS.register("cobalt_naginata", CobaltNaginataItem::new);
    public static final PortDeferredItem<CobaltNaginataItem> PALLADIUM_PIKE = ITEMS.register("palladium_pike", CobaltNaginataItem::new);
    public static final PortDeferredItem<OrichalcumHalberdItem> ORICHALCUM_HALBERD = ITEMS.register("orichalcum_halberd", OrichalcumHalberdItem::new);
    public static final PortDeferredItem<OrichalcumHalberdItem> MYTHRIL_HALBERD = ITEMS.register("mythril_halberd", OrichalcumHalberdItem::new);
    public static final PortDeferredItem<AdamantiteGlaiveItem> ADAMANTITE_GLAIVE = ITEMS.register("adamantite_glaive", AdamantiteGlaiveItem::new);
    public static final PortDeferredItem<AdamantiteGlaiveItem> TITANIUM_TRIDENT = ITEMS.register("titanium_trident", AdamantiteGlaiveItem::new);
    public static final PortDeferredItem<GungnirItem> GUNGNIR = ITEMS.register("gungnir", GungnirItem::new);
    public static final PortDeferredItem<ChlorophytePartisanItem> CHLOROPHYTE_PARTISAN = ITEMS.register("chlorophyte_partisan", ChlorophytePartisanItem::new);
    public static final PortDeferredItem<NorthPoleItem> NORTH_POLE = ITEMS.register("north_pole", NorthPoleItem::new);
    public static final PortDeferredItem<MushroomSpearItem> MUSHROOM_SPEAR = ITEMS.register("mushroom_spear", MushroomSpearItem::new);
    public static final PortDeferredItem<GhastlyglaiveItem> GHASTLY_GLAIVE = ITEMS.register("ghastly_glaive", GhastlyglaiveItem::new);
}
