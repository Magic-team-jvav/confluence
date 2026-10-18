package org.confluence.mod.common.init.item;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.IconItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class IconItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<IconItem> MATERIAL_ICON = register("material_icon", "材料图标");
    public static final PortDeferredItem<IconItem> BLOCKS_ICON = register("blocks_icon", "方块图标");
    public static final PortDeferredItem<IconItem> SORCERER_ICON = register("sorcerer_icon", "魔法职业图标");
    public static final PortDeferredItem<IconItem> SOULCERER_ICON = register("soulcerer_icon", "魂师职业图标");
    public static final PortDeferredItem<IconItem> WARRIOR_ICON = register("warrior_icon", "近战职业图标");
    public static final PortDeferredItem<IconItem> RANGER_ICON = register("ranger_icon", "远程职业图标");
    public static final PortDeferredItem<IconItem> ARMOR_ICON = register("armor_icon", "盔甲图标");
    public static final PortDeferredItem<IconItem> NATURE_ICON = register("nature_icon", "自然图标");
    public static final PortDeferredItem<IconItem> POTION_ICON = register("potion_icon", "药水图标");
    public static final PortDeferredItem<IconItem> PRECIOUS_ICON = register("precious_icon", "贵重品图标");
    public static final PortDeferredItem<IconItem> SUMMONER_ICON = register("summoner_icon", "召唤职业图标");
    public static final PortDeferredItem<IconItem> DEVELOPER_ICON = register("developer_icon", "开发者图标");
    public static final PortDeferredItem<IconItem> TOOLS_ICON = register("tools_icon", "工具图标");
    public static final PortDeferredItem<IconItem> MECHANICAL_ICON = register("mechanical_icon", "机械图标");
    public static final PortDeferredItem<IconItem> ENTITY_ICON = register("entity_icon", "实体图标");

    public static PortDeferredItem<IconItem> register(String name) {
        return ITEMS.register(name, IconItem::new);
    }

    private static PortDeferredItem<IconItem> register(String registryId, String chineseName) {
        PortDeferredItem<IconItem> entry = ITEMS.register(registryId, IconItem::new);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
