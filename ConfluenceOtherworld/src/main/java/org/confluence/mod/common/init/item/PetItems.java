package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class PetItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> CHESTER_STAFF = ITEMS.registerSimpleItem("chester_staff", new Item.Properties());
    public static final PortDeferredItem<Item> WALLET = ITEMS.registerSimpleItem("wallet", new Item.Properties());
}
