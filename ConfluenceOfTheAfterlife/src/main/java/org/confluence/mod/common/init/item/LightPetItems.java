package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

public class LightPetItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<Item> SHADOW_ORB = ITEMS.register("shadow_orb", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE)); // todo
    public static final Supplier<Item> CRIMSON_HEART = ITEMS.register("crimson_heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE)); // todo
}
