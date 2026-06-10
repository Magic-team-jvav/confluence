package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.IconItem;

public class IconItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<IconItem> MATERIAL_ICON = register("material_icon");
    public static final RegistryObject<IconItem> BLOCKS_ICON = register("blocks_icon");
    public static final RegistryObject<IconItem> MAGIC_ICON = register("magic_icon");
    public static final RegistryObject<IconItem> MELEE_ICON = register("melee_icon");
    public static final RegistryObject<IconItem> REMOTE_ICON = register("remote_icon");
    public static final RegistryObject<IconItem> ARMOR_ICON = register("armor_icon");
    public static final RegistryObject<IconItem> NATURE_ICON = register("nature_icon");
    public static final RegistryObject<IconItem> POTION_ICON = register("potion_icon");
    public static final RegistryObject<IconItem> PRECIOUS_ICON = register("precious_icon");
    public static final RegistryObject<IconItem> SUMMON_ICON = register("summon_icon");
    public static final RegistryObject<IconItem> DEVELOPER_ICON = register("developer_icon");
    public static final RegistryObject<IconItem> TOOLS_ICON = register("tools_icon");
    public static final RegistryObject<IconItem> MECHANICAL_ICON = register("mechanical_icon");

    public static RegistryObject<IconItem> register(String name) {
        return ITEMS.register(name, IconItem::new);
    }
}
