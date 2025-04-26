package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;

import java.util.function.Supplier;

// todo
public class LightPetItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<Item> SHADOW_ORB = ITEMS.register("shadow_orb", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final Supplier<Item> CRIMSON_HEART = ITEMS.register("crimson_heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final Supplier<Item> MAGIC_LANTERN = ITEMS.register("magic_lantern", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
}
