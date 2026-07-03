package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class SummonItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> SLIME_STAFF = ITEMS.registerSimpleItem("slime_staff", new Item.Properties());
    public static final PortDeferredItem<Item> HORNET_STAFF = ITEMS.registerSimpleItem("hornet_staff", new Item.Properties());
    public static final PortDeferredItem<Item> IMP_STAFF = ITEMS.registerSimpleItem("imp_staff", new Item.Properties());
    public static final PortDeferredItem<Item> FINCH_STAFF = ITEMS.registerSimpleItem("finch_staff", new Item.Properties());
    public static final PortDeferredItem<Item> SNOW_FLINX_STAFF = ITEMS.registerSimpleItem("snow_flinx_staff", new Item.Properties());
    public static final PortDeferredItem<Item> SCULK_WISP_STAFF = ITEMS.registerSimpleItem("sculk_wisp_staff", new Item.Properties());
    public static final PortDeferredItem<Item> IRON_GOLEM_STAFF = ITEMS.registerSimpleItem("iron_golem_staff", new Item.Properties());
    public static final PortDeferredItem<Item> TERRAPRISMA = ITEMS.registerSimpleItem("terraprisma", new Item.Properties());
}
