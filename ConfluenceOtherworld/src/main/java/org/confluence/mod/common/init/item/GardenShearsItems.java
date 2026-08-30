package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.GardenShearsItem;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class GardenShearsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<GardenShearsItem> COBALT_GARDEN_SHEARS = ITEMS.register("cobalt_garden_shears", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));
    public static final DeferredItem<GardenShearsItem> PALLADIUM_GARDEN_SHEARS = ITEMS.register("palladium_garden_shears", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));

}
