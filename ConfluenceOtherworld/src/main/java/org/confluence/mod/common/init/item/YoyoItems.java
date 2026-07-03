package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

// todo yoyo
public class YoyoItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> WOODEN_YOYO = ITEMS.registerSimpleItem("wooden_yoyo", new Item.Properties());
    public static final PortDeferredItem<Item> RALLY = ITEMS.registerSimpleItem("rally", new Item.Properties());
    public static final PortDeferredItem<Item> MALAISE = ITEMS.registerSimpleItem("malaise", new Item.Properties());
    public static final PortDeferredItem<Item> ARTERY = ITEMS.registerSimpleItem("artery", new Item.Properties());
    public static final PortDeferredItem<Item> AMAZON = ITEMS.registerSimpleItem("amazon", new Item.Properties());
    public static final PortDeferredItem<Item> CODE_1 = ITEMS.registerSimpleItem("code_1", new Item.Properties());
    public static final PortDeferredItem<Item> HIVE_FIVE = ITEMS.registerSimpleItem("hive_five", new Item.Properties());
    public static final PortDeferredItem<Item> CASCADE = ITEMS.registerSimpleItem("cascade", new Item.Properties());
    public static final PortDeferredItem<Item> VALOR = ITEMS.registerSimpleItem("valor", new Item.Properties());
}
