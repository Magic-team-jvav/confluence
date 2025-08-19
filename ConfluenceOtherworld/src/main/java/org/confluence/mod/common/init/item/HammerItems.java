package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hammer.HammerItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HammerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HammerItem> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new HammerItem(Tiers.WOOD, 2, 0.8F, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> EBONWOOD_HAMMER = ITEMS.register("ebonwood_hammer", () -> new HammerItem(Tiers.WOOD, 5, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> SHADEWOOD_HAMMER = ITEMS.register("shadewood_hammer", () -> new HammerItem(Tiers.WOOD, 6, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> ASH_WOOD_HAMMER = ITEMS.register("ash_wood_hammer", () -> new HammerItem(Tiers.WOOD, 6, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> PEARLWOOD_HAMMER = ITEMS.register("pearlwood_hammer", () -> new HammerItem(Tiers.WOOD, 10, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));

    public static final DeferredItem<HammerItem> COPPER_HAMMER = ITEMS.register("copper_hammer", () -> new HammerItem(ModTiers.COPPER, 4, 0.9F, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> TIN_HAMMER = ITEMS.register("tin_hammer", () -> new HammerItem(ModTiers.TIN, 6, 0.9F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new HammerItem(ModTiers.IRON, 7, 1.0F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> LEAD_HAMMER = ITEMS.register("lead_hammer", () -> new HammerItem(ModTiers.LEAD, 8, 1.1F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> SILVER_HAMMER = ITEMS.register("silver_hammer", () -> new HammerItem(ModTiers.SILVER, 9, 1.2F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> TUNGSTEN_HAMMER = ITEMS.register("tungsten_hammer", () -> new HammerItem(ModTiers.TUNGSTEN, 9, 1.3F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new HammerItem(ModTiers.GOLD, 9, 1.5F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> PLATINUM_HAMMER = ITEMS.register("platinum_hammer", () -> new HammerItem(ModTiers.PLATINUM, 10, 1.6F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> THE_BREAKER = ITEMS.register("the_breaker", () -> new HammerItem(ModTiers.DEMONITE, 11, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));
    public static final DeferredItem<HammerItem> FLESH_GRINDER = ITEMS.register("flesh_grinder", () -> new HammerItem(ModTiers.CRIMTANE, 12, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));
    public static final DeferredItem<HammerItem> ROCKFISH = ITEMS.register("rockfish", () -> new HammerItem(new SimpleTier(ModTags.Blocks.NEEDS_4_LEVEL, 10000, 9, 3, 10, () -> Ingredient.of(ItemTags.FISHES)), 13, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.ORANGE));

    public static final DeferredItem<HammerItem> PWNHAMMER = ITEMS.register("pwnhammer", () -> new HammerItem(ModTiers.HALLOWED, 14, 2.0F, unbreakable(), attributes(0, 0.75), ModRarity.LIGHT_RED));
    public static final DeferredItem<HammerItem> HAMMUSH = ITEMS.register("hammush", () -> new HammerItem(ModTiers.SHROOMITE, 16, 2.2F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final DeferredItem<HammerItem> CHLOROPHYTE_WARHAMMER = ITEMS.register("chlorophyte_warhammer", () -> new HammerItem(ModTiers.CHLOROPHYTE, 20, 2.4F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final DeferredItem<HammerItem> CHLOROPHYTE_JACKHAMMER = ITEMS.register("chlorophyte_jackhammer", () -> new HammerItem(ModTiers.CHLOROPHYTE, 24, 4, unbreakable(), attributes(0, 0.52), ModRarity.LIME));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
