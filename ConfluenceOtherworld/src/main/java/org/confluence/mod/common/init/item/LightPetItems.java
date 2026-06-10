package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;

// todo
public class LightPetItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<Item> SHADOW_ORB = ITEMS.register("shadow_orb", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final RegistryObject<Item> CRIMSON_HEART = ITEMS.register("crimson_heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final RegistryObject<Item> MAGIC_LANTERN = ITEMS.register("magic_lantern", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
}
