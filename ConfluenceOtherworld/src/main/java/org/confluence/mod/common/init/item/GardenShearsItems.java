package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.GardenShearsItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class GardenShearsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<GardenShearsItem> COBALT_GARDEN_SHEARS = registerItem("cobalt_garden_shears", "钴园艺剪", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));
    public static final DeferredItem<GardenShearsItem> PALLADIUM_GARDEN_SHEARS = registerItem("palladium_garden_shears", "钯金园艺剪", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
