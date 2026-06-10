package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.GardenShearsItem;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class GardenShearsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<GardenShearsItem> COBALT_GARDEN_SHEARS = ITEMS.register("cobalt_garden_shears", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));
    public static final RegistryObject<GardenShearsItem> PALLADIUM_GARDEN_SHEARS = ITEMS.register("palladium_garden_shears", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));

}
