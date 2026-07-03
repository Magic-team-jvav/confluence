package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class WhipItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> LEATHER_WHIP = ITEMS.registerSimpleItem("leather_whip", new Item.Properties());
    public static final PortDeferredItem<Item> SLUB_WHIP = ITEMS.registerSimpleItem("slub_whip", new Item.Properties());
    public static final PortDeferredItem<Item> RUBY_WHIP = ITEMS.registerSimpleItem("ruby_whip", new Item.Properties());
    public static final PortDeferredItem<Item> AMBER_WHIP = ITEMS.registerSimpleItem("amber_whip", new Item.Properties());
    public static final PortDeferredItem<Item> TOPAZ_WHIP = ITEMS.registerSimpleItem("topaz_whip", new Item.Properties());
    public static final PortDeferredItem<Item> JADE_WHIP = ITEMS.registerSimpleItem("jade_whip", new Item.Properties());
    public static final PortDeferredItem<Item> DIAMOND_WHIP = ITEMS.registerSimpleItem("diamond_whip", new Item.Properties());
    public static final PortDeferredItem<Item> SAPPHIRE_WHIP = ITEMS.registerSimpleItem("sapphire_whip", new Item.Properties());
    public static final PortDeferredItem<Item> AMETHYST_WHIP = ITEMS.registerSimpleItem("amethyst_whip", new Item.Properties());
    public static final PortDeferredItem<Item> SWAMP_WHIP = ITEMS.registerSimpleItem("swamp_whip", new Item.Properties());
    public static final PortDeferredItem<Item> SNAPTHORN = ITEMS.registerSimpleItem("snapthorn", new Item.Properties());
    public static final PortDeferredItem<Item> SPINAL_TAP = ITEMS.registerSimpleItem("spinal_tap", new Item.Properties());
    public static final PortDeferredItem<Item> FIRECRACKER = ITEMS.registerSimpleItem("firecracker", new Item.Properties());
}
