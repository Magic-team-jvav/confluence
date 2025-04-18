package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hammer.HammerItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HammerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HammerItem> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new HammerItem(Tiers.WOOD, 2, 60.0F / 25, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> RICH_MAHOGANY_HAMMER = ITEMS.register("rich_mahogany_hammer", () -> new HammerItem(Tiers.WOOD, 4, 60.0F / 23, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> PALM_WOOD_HAMMER = ITEMS.register("palm_wood_hammer", () -> new HammerItem(Tiers.WOOD, 4, 60.0F / 23, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> SPRUCE_WOOD_HAMMER = ITEMS.register("spruce_wood_hammer", () -> new HammerItem(Tiers.WOOD, 4, 60.0F / 23, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> EBONWOOD_WOOD_HAMMER = ITEMS.register("ebonwood_wood_hammer", () -> new HammerItem(Tiers.WOOD, 7, 60.0F / 20, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> SHADEWOOD_HAMMER = ITEMS.register("shadewood_wood_hammer", () -> new HammerItem(Tiers.WOOD, 7, 60.0F / 20, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> ASH_WOOD_HAMMER = ITEMS.register("ash_wood_hammer", () -> new HammerItem(Tiers.WOOD, 9, 60.0F / 20, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> PEARLWOOD_HAMMER = ITEMS.register("pearlwood_hammer", () -> new HammerItem(Tiers.WOOD, 10, 60.0F / 19, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));

    public static final DeferredItem<HammerItem> COPPER_HAMMER = ITEMS.register("copper_hammer", () -> new HammerItem(ModTiers.COPPER, 4, 60.0F / 23, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> TIN_HAMMER = ITEMS.register("tin_hammer", () -> new HammerItem(ModTiers.TIN, 6, 60.0F / 21, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new HammerItem(ModTiers.IRON, 7, 60.0F / 20, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> LEAD_HAMMER = ITEMS.register("lead_hammer", () -> new HammerItem(ModTiers.LEAD, 8, 60.0F / 19, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> SILVER_HAMMER = ITEMS.register("silver_hammer", () -> new HammerItem(ModTiers.SILVER, 9, 60.0F / 19, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> TUNGSTEN_HAMMER = ITEMS.register("tungsten_hammer", () -> new HammerItem(ModTiers.TUNGSTEN, 9, 60.0F / 25, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new HammerItem(ModTiers.GOLD, 9, 60.0F / 23, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> PLATINUM_HAMMER = ITEMS.register("platinum_hammer", () -> new HammerItem(ModTiers.PLATINUM, 10, 60.0F / 21, unbreakable(), attributes(0, 0.55), ModRarity.WHITE));
    public static final DeferredItem<HammerItem> THE_BREAKER = ITEMS.register("the_breaker", () -> new HammerItem(ModTiers.DEMONITE, 24, 60.0F / 19, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));
    public static final DeferredItem<HammerItem> FLESH_GRINDER = ITEMS.register("flesh_grinder", () -> new HammerItem(ModTiers.TR_CRIMSON, 23, 60.0F / 19, unbreakable(), attributes(0, 0.6), ModRarity.BLUE));
    // todo 岩石鱼锤

    public static final DeferredItem<HammerItem> PWNHAMMER = ITEMS.register("pwnhammer", () -> new HammerItem(ModTiers.HALLOWED, 26, 60.0F / 14, unbreakable(), attributes(0, 0.75), ModRarity.LIGHT_RED));
    public static final DeferredItem<HammerItem> HAMMUSH = ITEMS.register("hammush", () -> new HammerItem(ModTiers.SHROOMITE, 26, 60.0F / 14, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final DeferredItem<HammerItem> CHLOROPHYTE_WARHAMMER = ITEMS.register("chlorophyte_warhammer", () -> new HammerItem(ModTiers.CHLOROPHYTE, 7, 1, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    // todo 叶绿手提钻

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
