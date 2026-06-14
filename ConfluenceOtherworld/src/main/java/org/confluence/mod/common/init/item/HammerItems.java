package org.confluence.mod.common.init.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHammerItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.common.PortSimpleTier;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HammerItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseHammerItem> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new BaseHammerItem(Tiers.WOOD, 2, 0.8F, new PortItem.PortProperties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> EBONWOOD_HAMMER = ITEMS.register("ebonwood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 5, 0.8F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> SHADEWOOD_HAMMER = ITEMS.register("shadewood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 6, 0.8F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> ASH_WOOD_HAMMER = ITEMS.register("ash_wood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 6, 0.8F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> PEARLWOOD_HAMMER = ITEMS.register("pearlwood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 10, 0.8F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));

    public static final PortDeferredItem<BaseHammerItem> COPPER_HAMMER = ITEMS.register("copper_hammer", () -> new BaseHammerItem(ModTiers.COPPER, 4, 0.9F, new PortItem.PortProperties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> TIN_HAMMER = ITEMS.register("tin_hammer", () -> new BaseHammerItem(ModTiers.TIN, 6, 0.9F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new BaseHammerItem(ModTiers.IRON, 7, 1.0F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> LEAD_HAMMER = ITEMS.register("lead_hammer", () -> new BaseHammerItem(ModTiers.LEAD, 8, 1.1F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> SILVER_HAMMER = ITEMS.register("silver_hammer", () -> new BaseHammerItem(ModTiers.SILVER, 9, 1.2F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> TUNGSTEN_HAMMER = ITEMS.register("tungsten_hammer", () -> new BaseHammerItem(ModTiers.TUNGSTEN, 9, 1.3F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new BaseHammerItem(ModTiers.GOLD, 9, 1.5F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> PLATINUM_HAMMER = ITEMS.register("platinum_hammer", () -> new BaseHammerItem(ModTiers.PLATINUM, 10, 1.6F, new PortItem.PortProperties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> THE_BREAKER = ITEMS.register("the_breaker", () -> new BaseHammerItem(ModTiers.DEMONITE, 11, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final PortDeferredItem<BaseHammerItem> FLESH_GRINDER = ITEMS.register("flesh_grinder", () -> new BaseHammerItem(ModTiers.CRIMTANE, 12, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final PortDeferredItem<BaseHammerItem> ROCKFISH = ITEMS.register("rockfish", () -> new BaseHammerItem(new PortSimpleTier(ModTags.Blocks.NEEDS_4_LEVEL, 10000, 9, 3, 10, () -> Ingredient.of(ItemTags.FISHES)), 13, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.ORANGE).hasImage());

    public static final PortDeferredItem<BaseHammerItem> PWNHAMMER = ITEMS.register("pwnhammer", () -> new BaseHammerItem(ModTiers.HALLOWED, 14, 2.0F, unbreakable(), attributes(0, 0.75), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseHammerItem> HAMMUSH = ITEMS.register("hammush", () -> new BaseHammerItem(ModTiers.SHROOMITE, 16, 2.2F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final PortDeferredItem<BaseHammerItem> CHLOROPHYTE_WARHAMMER = ITEMS.register("chlorophyte_warhammer", () -> new BaseHammerItem(ModTiers.CHLOROPHYTE, 20, 2.4F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final PortDeferredItem<BaseHammerItem> CHLOROPHYTE_JACKHAMMER = ITEMS.register("chlorophyte_jackhammer", () -> new BaseHammerItem(ModTiers.CHLOROPHYTE, 24, 4, unbreakable(), attributes(0, 0.52), ModRarity.LIME));
}
