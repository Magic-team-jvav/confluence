package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHoeItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseHoeItem> COPPER_HOE = ITEMS.register("copper_hoe", () -> new BaseHoeItem(ModTiers.COPPER, 1, 1f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> TIN_HOE = ITEMS.register("tin_hoe", () -> new BaseHoeItem(ModTiers.TIN, 1, 1f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> LEAD_HOE = ITEMS.register("lead_hoe", () -> new BaseHoeItem(ModTiers.LEAD, 1, 2f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> SILVER_HOE = ITEMS.register("silver_hoe", () -> new BaseHoeItem(ModTiers.SILVER, 1, 3, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> TUNGSTEN_HOE = ITEMS.register("tungsten_hoe", () -> new BaseHoeItem(ModTiers.TUNGSTEN, 1, 3f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> GOLDEN_HOE = ITEMS.register("golden_hoe", () -> new BaseHoeItem(ModTiers.GOLD, 1, 4f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> PLATINUM_HOE = ITEMS.register("platinum_hoe", () -> new BaseHoeItem(ModTiers.PLATINUM, 1, 4f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> SHADOW_HOE = ITEMS.register("shadow_hoe", () -> new BaseHoeItem(ModTiers.DEMONITE, 2, 4f, unbreakable(), ModRarity.BLUE));
    public static final PortDeferredItem<BaseHoeItem> CULTIVATOR = ITEMS.register("cultivator", () -> new BaseHoeItem(ModTiers.CRIMTANE, 2, 4f, unbreakable(), ModRarity.BLUE));
}
