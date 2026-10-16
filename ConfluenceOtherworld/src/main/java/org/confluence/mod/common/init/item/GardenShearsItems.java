package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.GardenShearsItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class GardenShearsItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<GardenShearsItem> COBALT_GARDEN_SHEARS = registerItem("cobalt_garden_shears", "钴园艺剪", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<GardenShearsItem> PALLADIUM_GARDEN_SHEARS = registerItem("palladium_garden_shears", "钯金园艺剪", () -> new GardenShearsItem(unbreakable(), ModRarity.LIGHT_RED));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
