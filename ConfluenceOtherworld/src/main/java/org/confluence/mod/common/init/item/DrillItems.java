package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseDrillItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class DrillItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseDrillItem> COBALT_DRILL = ITEMS.registerItem("cobalt_drill", properties -> new BaseDrillItem(ModTiers.COBALT, 15, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> PALLADIUM_DRILL = ITEMS.registerItem("palladium_drill", properties -> new BaseDrillItem(ModTiers.PALLADIUM, 16, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> MYTHRIL_DRILL = ITEMS.registerItem("mythril_drill", properties -> new BaseDrillItem(ModTiers.MYTHRIL, 17, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> ORICHALCUM_DRILL = ITEMS.registerItem("orichalcum_drill", properties -> new BaseDrillItem(ModTiers.ORICHALCUM, 18, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> ADAMANTITE_DRILL = ITEMS.registerItem("adamantite_drill", properties -> new BaseDrillItem(ModTiers.ADAMANTITE, 19, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> TITANIUM_DRILL = ITEMS.registerItem("titanium_drill", properties -> new BaseDrillItem(ModTiers.TITANIUM, 20, 2.5F, unbreakable(), attributes(-1, 0.05), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> CHLOROPHYTE_DRILL = ITEMS.registerItem("chlorophyte_drill", properties -> new BaseDrillItem(ModTiers.CHLOROPHYTE, 24, 2.7F, unbreakable(), attributes(0, 0.1), ModRarity.LIME));
    public static final PortDeferredItem<BaseDrillItem> DRAX = ITEMS.registerItem("drax", properties -> new BaseDrillItem(ModTiers.HALLOWED, 28, 2.5F, unbreakable(), attributes(-1, 0.475), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseDrillItem> SOLAR_FLARE_DRILL = ITEMS.registerItem("solar_flare_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final PortDeferredItem<BaseDrillItem> VORTEX_DRILL = ITEMS.registerItem("vortex_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final PortDeferredItem<BaseDrillItem> NEBULA_DRILL = ITEMS.registerItem("nebula_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    public static final PortDeferredItem<BaseDrillItem> STARDUST_DRILL = ITEMS.registerItem("stardust_drill", properties -> new BaseDrillItem(ModTiers.LUMINITE, 34, 2.9F, unbreakable(), attributes(2, 0.05), ModRarity.RED));
    // todo 激光钻头
}
