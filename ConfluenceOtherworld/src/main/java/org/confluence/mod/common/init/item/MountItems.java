package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class MountItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> FUZZY_CARROT = ITEMS.register("fuzzy_carrot",
            () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
}
