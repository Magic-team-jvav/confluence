package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.GardenShearsItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class GardenShearsItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<GardenShearsItem> COBALT_GARDEN_SHEARS = ITEMS.register("cobalt_garden_shears", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<GardenShearsItem> PALLADIUM_GARDEN_SHEARS = ITEMS.register("palladium_garden_shears", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));
}
