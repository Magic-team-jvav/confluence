package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

// todo boomerang
public class BoomerangItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> ENCHANTED_BOOMERANG = ITEMS.registerSimpleItem("enchanted_boomerang", new Item.Properties());
    public static final PortDeferredItem<Item> FLAMARANG = ITEMS.registerSimpleItem("flamarang", new Item.Properties());
    public static final PortDeferredItem<Item> ICE_BOOMERANG = ITEMS.registerSimpleItem("ice_boomerang", new Item.Properties());
    public static final PortDeferredItem<Item> SHROOMERANG = ITEMS.registerSimpleItem("shroomerang", new Item.Properties());
    public static final PortDeferredItem<Item> TRIMARANG = ITEMS.registerSimpleItem("trimarang", new Item.Properties());
    public static final PortDeferredItem<Item> COMBAT_WRENCH = ITEMS.registerSimpleItem("combat_wrench", new Item.Properties());
    public static final PortDeferredItem<Item> WOOD_BOOMERANG = ITEMS.registerSimpleItem("wood_boomerang", new Item.Properties());

    public static final PortDeferredItem<Item> BEIDOU_BOOMERANG = ITEMS.registerSimpleItem("beidou_boomerang", new Item.Properties());
    public static final PortDeferredItem<Item> DEVELOPER_BOOMERANG = ITEMS.registerSimpleItem("developer_boomerang", new Item.Properties());
}
