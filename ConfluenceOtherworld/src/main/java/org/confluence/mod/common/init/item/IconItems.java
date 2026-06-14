package org.confluence.mod.common.init.item;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.IconItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class IconItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<IconItem> MATERIAL_ICON = register("material_icon");
    public static final PortDeferredItem<IconItem> BLOCKS_ICON = register("blocks_icon");
    public static final PortDeferredItem<IconItem> MAGIC_ICON = register("magic_icon");
    public static final PortDeferredItem<IconItem> MELEE_ICON = register("melee_icon");
    public static final PortDeferredItem<IconItem> REMOTE_ICON = register("remote_icon");
    public static final PortDeferredItem<IconItem> ARMOR_ICON = register("armor_icon");
    public static final PortDeferredItem<IconItem> NATURE_ICON = register("nature_icon");
    public static final PortDeferredItem<IconItem> POTION_ICON = register("potion_icon");
    public static final PortDeferredItem<IconItem> PRECIOUS_ICON = register("precious_icon");
    public static final PortDeferredItem<IconItem> SUMMON_ICON = register("summon_icon");
    public static final PortDeferredItem<IconItem> DEVELOPER_ICON = register("developer_icon");
    public static final PortDeferredItem<IconItem> TOOLS_ICON = register("tools_icon");
    public static final PortDeferredItem<IconItem> MECHANICAL_ICON = register("mechanical_icon");

    public static PortDeferredItem<IconItem> register(String name) {
        return ITEMS.register(name, IconItem::new);
    }
}
