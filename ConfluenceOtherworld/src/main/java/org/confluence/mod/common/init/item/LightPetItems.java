package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

// todo
public class LightPetItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> SHADOW_ORB = ITEMS.register("shadow_orb", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final PortDeferredItem<Item> CRIMSON_HEART = ITEMS.register("crimson_heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final PortDeferredItem<Item> MAGIC_LANTERN = ITEMS.register("magic_lantern", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
}
