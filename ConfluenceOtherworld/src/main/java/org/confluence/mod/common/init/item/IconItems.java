package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.IconItem;

public class IconItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<IconItem> MATERIAL_ICON = register("material_icon");
    public static final DeferredItem<IconItem> BLOCKS_ICON = register("blocks_icon");
    public static final DeferredItem<IconItem> SORCERER_ICON = register("sorcerer_icon");
    public static final DeferredItem<IconItem> WARRIOR_ICON = register("warrior_icon");
    public static final DeferredItem<IconItem> RANGER_ICON = register("ranger_icon");
    public static final DeferredItem<IconItem> ARMOR_ICON = register("armor_icon");
    public static final DeferredItem<IconItem> NATURE_ICON = register("nature_icon");
    public static final DeferredItem<IconItem> POTION_ICON = register("potion_icon");
    public static final DeferredItem<IconItem> PRECIOUS_ICON = register("precious_icon");
    public static final DeferredItem<IconItem> SUMMONER_ICON = register("summoner_icon");
    public static final DeferredItem<IconItem> DEVELOPER_ICON = register("developer_icon");
    public static final DeferredItem<IconItem> TOOLS_ICON = register("tools_icon");
    public static final DeferredItem<IconItem> MECHANICAL_ICON = register("mechanical_icon");

    public static DeferredItem<IconItem> register(String name) {
        return ITEMS.register(name, IconItem::new);
    }
}
