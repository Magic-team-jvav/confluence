package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.IconItem;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class IconItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<IconItem> MATERIAL_ICON = register("material_icon", "材料图标");
    public static final DeferredItem<IconItem> BLOCKS_ICON = register("blocks_icon", "方块图标");
    public static final DeferredItem<IconItem> SORCERER_ICON = register("sorcerer_icon", "魔法职业图标");
    public static final DeferredItem<IconItem> WARRIOR_ICON = register("warrior_icon", "近战职业图标");
    public static final DeferredItem<IconItem> RANGER_ICON = register("ranger_icon", "远程职业图标");
    public static final DeferredItem<IconItem> ARMOR_ICON = register("armor_icon", "盔甲图标");
    public static final DeferredItem<IconItem> NATURE_ICON = register("nature_icon", "自然图标");
    public static final DeferredItem<IconItem> POTION_ICON = register("potion_icon", "药水图标");
    public static final DeferredItem<IconItem> PRECIOUS_ICON = register("precious_icon", "贵重品图标");
    public static final DeferredItem<IconItem> SUMMONER_ICON = register("summoner_icon", "召唤职业图标");
    public static final DeferredItem<IconItem> DEVELOPER_ICON = register("developer_icon", "开发者图标");
    public static final DeferredItem<IconItem> TOOLS_ICON = register("tools_icon", "工具图标");
    public static final DeferredItem<IconItem> MECHANICAL_ICON = register("mechanical_icon", "机械图标");
    public static final DeferredItem<IconItem> ENTITY_ICON = register("entity_icon", "实体图标");

    public static DeferredItem<IconItem> register(String name) {
        return ITEMS.register(name, IconItem::new);
    }

    private static DeferredItem<IconItem> register(String registryId, String chineseName) {
        DeferredItem<IconItem> entry = ITEMS.register(registryId, IconItem::new);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
