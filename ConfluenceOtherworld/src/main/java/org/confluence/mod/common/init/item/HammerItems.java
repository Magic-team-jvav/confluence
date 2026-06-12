package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.SimpleTier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHammerItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HammerItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BaseHammerItem> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new BaseHammerItem(Tiers.WOOD, 2, 0.8F, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> EBONWOOD_HAMMER = ITEMS.register("ebonwood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 5, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> SHADEWOOD_HAMMER = ITEMS.register("shadewood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 6, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> ASH_WOOD_HAMMER = ITEMS.register("ash_wood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 6, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> PEARLWOOD_HAMMER = ITEMS.register("pearlwood_hammer", () -> new BaseHammerItem(Tiers.WOOD, 10, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));

    public static final RegistryObject<BaseHammerItem> COPPER_HAMMER = ITEMS.register("copper_hammer", () -> new BaseHammerItem(ModTiers.COPPER, 4, 0.9F, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> TIN_HAMMER = ITEMS.register("tin_hammer", () -> new BaseHammerItem(ModTiers.TIN, 6, 0.9F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new BaseHammerItem(ModTiers.IRON, 7, 1.0F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> LEAD_HAMMER = ITEMS.register("lead_hammer", () -> new BaseHammerItem(ModTiers.LEAD, 8, 1.1F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> SILVER_HAMMER = ITEMS.register("silver_hammer", () -> new BaseHammerItem(ModTiers.SILVER, 9, 1.2F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> TUNGSTEN_HAMMER = ITEMS.register("tungsten_hammer", () -> new BaseHammerItem(ModTiers.TUNGSTEN, 9, 1.3F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new BaseHammerItem(ModTiers.GOLD, 9, 1.5F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> PLATINUM_HAMMER = ITEMS.register("platinum_hammer", () -> new BaseHammerItem(ModTiers.PLATINUM, 10, 1.6F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final RegistryObject<BaseHammerItem> THE_BREAKER = ITEMS.register("the_breaker", () -> new BaseHammerItem(ModTiers.DEMONITE, 11, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final RegistryObject<BaseHammerItem> FLESH_GRINDER = ITEMS.register("flesh_grinder", () -> new BaseHammerItem(ModTiers.CRIMTANE, 12, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final RegistryObject<BaseHammerItem> ROCKFISH = ITEMS.register("rockfish", () -> new BaseHammerItem(new SimpleTier(ModTags.Blocks.NEEDS_4_LEVEL, 10000, 9, 3, 10, () -> Ingredient.of(ItemTags.FISHES)), 13, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.ORANGE).hasImage());

    public static final RegistryObject<BaseHammerItem> PWNHAMMER = ITEMS.register("pwnhammer", () -> new BaseHammerItem(ModTiers.HALLOWED, 14, 2.0F, unbreakable(), attributes(0, 0.75), ModRarity.LIGHT_RED));
    public static final RegistryObject<BaseHammerItem> HAMMUSH = ITEMS.register("hammush", () -> new BaseHammerItem(ModTiers.SHROOMITE, 16, 2.2F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final RegistryObject<BaseHammerItem> CHLOROPHYTE_WARHAMMER = ITEMS.register("chlorophyte_warhammer", () -> new BaseHammerItem(ModTiers.CHLOROPHYTE, 20, 2.4F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final RegistryObject<BaseHammerItem> CHLOROPHYTE_JACKHAMMER = ITEMS.register("chlorophyte_jackhammer", () -> new BaseHammerItem(ModTiers.CHLOROPHYTE, 24, 4, unbreakable(), attributes(0, 0.52), ModRarity.LIME));
}
