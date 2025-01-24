package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.pickaxe.BasePickaxeItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

public class PickaxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<PickaxeItem> CACTUS_PICKAXE = ITEMS.register("cactus_pickaxe", () -> new BasePickaxeItem(ModTiers.CACTUS, 2, 1)),
            COPPER_PICKAXE = ITEMS.register("copper_pickaxe", () -> new BasePickaxeItem(ModTiers.COPPER, 2, 1)),
            TIN_PICKAXE = ITEMS.register("tin_pickaxe", () -> new BasePickaxeItem(ModTiers.TIN, 2, 1)),
            LEAD_PICKAXE = ITEMS.register("lead_pickaxe", () -> new BasePickaxeItem(ModTiers.LEAD, 3, 1)),
            SILVER_PICKAXE = ITEMS.register("silver_pickaxe", () -> new BasePickaxeItem(ModTiers.SILVER, 3, 1)),
            TUNGSTEN_PICKAXE = ITEMS.register("tungsten_pickaxe", () -> new BasePickaxeItem(ModTiers.TUNGSTEN, 4, 1)),
            GOLDEN_PICKAXE = ITEMS.register("golden_pickaxe", () -> new BasePickaxeItem(ModTiers.GOLD, 5, 1)),
            CANDY_CANE_PICKAXE = ITEMS.register("candy_cane_pickaxe", () -> new BasePickaxeItem(ModTiers.CANDY_CANE, 3, 1)),
            FOSSIL_PICKAXE = ITEMS.register("fossil_pickaxe", () -> new BasePickaxeItem(ModTiers.FOSSIL, 5, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.BLUE)),
            BONE_PICKAXE = ITEMS.register("bone_pickaxe", () -> new BasePickaxeItem(ModTiers.BONE, 1, 1, ModRarity.BLUE)),
            PLATINUM_PICKAXE = ITEMS.register("platinum_pickaxe", () -> new BasePickaxeItem(ModTiers.PLATINUM, 5, 1)),
            REAVER_SHARK_PICKAXE = ITEMS.register("reaver_shark_pickaxe", () -> new BasePickaxeItem(ModTiers.REAVER_SHARK, 1, 1, ModRarity.ORANGE)),
            NIGHTMARE_PICKAXE = ITEMS.register("nightmare_pickaxe", () -> new BasePickaxeItem(ModTiers.DEMONITE, 6, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.BLUE)),
            DEATHBRINGER_PICKAXE = ITEMS.register("deathbringer_pickaxe", () -> new BasePickaxeItem(ModTiers.TR_CRIMSON, 7, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.BLUE)),
            MOLTEN_PICKAXE = ITEMS.register("molten_pickaxe", () -> new BasePickaxeItem(ModTiers.HELLSTONE, 12, 1, new Item.Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.ORANGE));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
