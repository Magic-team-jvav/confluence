package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.CursedFlameItem;
import org.confluence.mod.common.item.common.GelItem;
import org.confluence.mod.common.item.common.MushroomItem;

public class MaterialItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> GEL = ITEMS.register("gel", GelItem::new);
    public static final DeferredItem<Item> PINK_GEL = register("pink_gel", ModRarity.PINK);
    public static final DeferredItem<Item> SILK = register("silk", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_ASPHALT = ITEMS.register("raw_asphalt", () -> new TooltipItem(new Item.Properties().stacksTo(LibUtils.MAX_STACK_SIZE), ModRarity.WHITE, "tooltip.item.confluence.raw_asphalt.0"));

    public static final DeferredItem<Item> RAW_TIN = register("raw_tin");
    public static final DeferredItem<Item> TIN_INGOT = register("tin_ingot");
    public static final DeferredItem<Item> TIN_NUGGET = register("tin_nugget");
    public static final DeferredItem<Item> RAW_LEAD = register("raw_lead");
    public static final DeferredItem<Item> LEAD_INGOT = register("lead_ingot");
    public static final DeferredItem<Item> LEAD_NUGGET = register("lead_nugget");
    public static final DeferredItem<Item> RAW_SILVER = register("raw_silver");
    public static final DeferredItem<Item> SILVER_INGOT = register("silver_ingot");
    public static final DeferredItem<Item> SILVER_NUGGET = register("silver_nugget");
    public static final DeferredItem<Item> RAW_TUNGSTEN = register("raw_tungsten");
    public static final DeferredItem<Item> TUNGSTEN_INGOT = register("tungsten_ingot");
    public static final DeferredItem<Item> TUNGSTEN_NUGGET = register("tungsten_nugget");
    public static final DeferredItem<Item> RAW_PLATINUM = register("raw_platinum", ModRarity.BLUE);
    public static final DeferredItem<Item> PLATINUM_INGOT = register("platinum_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> PLATINUM_NUGGET = register("platinum_nugget", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_METEORITE = register("raw_meteorite", ModRarity.BLUE);
    public static final DeferredItem<Item> METEORITE_INGOT = register("meteorite_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> METEORITE_NUGGET = register("meteorite_nugget", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_DEMONITE = register("raw_demonite", ModRarity.BLUE);
    public static final DeferredItem<Item> DEMONITE_INGOT = register("demonite_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> DEMONITE_NUGGET = register("demonite_nugget", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_CRIMTANE = register("raw_crimtane", ModRarity.BLUE);
    public static final DeferredItem<Item> CRIMTANE_INGOT = register("crimtane_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> CRIMTANE_NUGGET = register("crimtane_nugget", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_HELLSTONE = ITEMS.registerSimpleItem("raw_hellstone", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> HELLSTONE_INGOT = ITEMS.registerSimpleItem("hellstone_ingot", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> HELLSTONE_NUGGET = ITEMS.registerSimpleItem("hellstone_nugget", new Item.Properties().fireResistant());

    public static final DeferredItem<Item> RAW_COBALT = register("raw_cobalt", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> COBALT_INGOT = register("cobalt_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_PALLADIUM = register("raw_palladium", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> PALLADIUM_INGOT = register("palladium_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_MYTHRIL = register("raw_mythril", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> MYTHRIL_INGOT = register("mythril_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_ORICHALCUM = register("raw_orichalcum", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> ORICHALCUM_INGOT = register("orichalcum_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_ADAMANTITE = register("raw_adamantite", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> ADAMANTITE_INGOT = register("adamantite_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_TITANIUM = register("raw_titanium", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> TITANIUM_INGOT = register("titanium_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> HALLOWED_INGOT = register("hallowed_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_CHLOROPHYTE = register("raw_chlorophyte", ModRarity.ORANGE);
    public static final DeferredItem<Item> CHLOROPHYTE_INGOT = register("chlorophyte_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> SHROOMITE_INGOT = register("shroomite_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> SPECTRE_INGOT = register("spectre_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> RAW_LUMINITE = register("raw_luminite", ModRarity.ORANGE);
    public static final DeferredItem<Item> LUMINITE_INGOT = register("luminite_ingot", ModRarity.ORANGE);



    public static final DeferredItem<Item> AMBER = register("amber");
    public static final DeferredItem<Item> AMETHYST = register("amethyst");
    public static final DeferredItem<Item> JADE = register("jade");
    public static final DeferredItem<Item> RUBY = register("ruby");
    public static final DeferredItem<Item> SAPPHIRE = register("sapphire");
    public static final DeferredItem<Item> TOPAZ = register("topaz");

    public static final DeferredItem<Item> STAR_PETALS = register("star_petals", ModRarity.BLUE);
    public static final DeferredItem<Item> FLOATING_WHEAT_HEADS = register("floating_wheat_heads", ModRarity.BLUE);
    public static final DeferredItem<Item> FALLING_STAR = ITEMS.register("falling_star", () -> new Item(new Item.Properties().stacksTo(9999)));
    public static final DeferredItem<Item> WEAVING_CLOUD_COTTON = register("weaving_cloud_cotton", ModRarity.BLUE);
    public static final DeferredItem<Item> ROTTEN_CHUNK = register("rotten_chunk");
    public static final DeferredItem<Item> WORM_TOOTH = register("worm_tooth");
    public static final DeferredItem<Item> VERTEBRA = register("vertebra");
    public static final DeferredItem<Item> BLOOD_CLOT_POWDER = register("blood_clot_powder", ModRarity.BLUE);
    public static final DeferredItem<Item> ROTTEN_BONE = register("rotten_bone", ModRarity.BLUE);
    public static final DeferredItem<Item> STINGER = register("stinger", ModRarity.BLUE);
    public static final DeferredItem<Item> MAN_EATER_VINE = register("man_eater_vine", ModRarity.BLUE);
    public static final DeferredItem<Item> BLACK_INK = register("black_ink");
    public static final DeferredItem<Item> SHARK_FIN = register("shark_fin");
    public static final DeferredItem<Item> ANTLION_MANDIBLE = register("antlion_mandible");
    public static final DeferredItem<Item> HOOK = register("hook");
    public static final DeferredItem<Item> FILAMENTOUS_FIN = register("filamentous_fin");
    public static final DeferredItem<Item> TATTERED_CLOTH = register("tattered_cloth");
    public static final DeferredItem<Item> LENS = register("lens");
    public static final DeferredItem<Item> BLACK_LENS = register("black_lens");
    public static final DeferredItem<Item> STURDY_FOSSIL = register("sturdy_fossil", ModRarity.BLUE);
    public static final DeferredItem<Item> OPAL = register("opal", ModRarity.BLUE);
    public static final DeferredItem<Item> HEIM = register("heim", ModRarity.BLUE);
    public static final DeferredItem<Item> GELSTONE = register("gelstone", ModRarity.BLUE);
    public static final DeferredItem<Item> SPORE_ROOT = register("spore_root", ModRarity.BLUE);
    public static final DeferredItem<Item> COLD_CRYSTAL = register("cold_crystal", ModRarity.BLUE);
    public static final DeferredItem<Item> WINTER_MARROW = register("winter_marrow", ModRarity.BLUE);
    public static final DeferredItem<Item> SHADOW_SCALE = register("shadow_scale", ModRarity.BLUE);
    public static final DeferredItem<Item> TISSUE_SAMPLE = register("tissue_sample", ModRarity.BLUE);
    public static final DeferredItem<Item> ROYAL_WAX = register("royal_wax", ModRarity.BLUE);
    public static final DeferredItem<Item> HARPY_FEATHER = register("harpy_feather", ModRarity.BLUE);
    public static final DeferredItem<Item> GIANT_HARPY_FEATHER = register("giant_harpy_feather", ModRarity.BLUE);
    public static final DeferredItem<Item> FLINX_FUR = register("flinx_fur", ModRarity.BLUE);
    public static final DeferredItem<Item> AETHERIUM_SHARD = register("aetherium_shard", ModRarity.ORANGE);
    public static final DeferredItem<Item> AETHERIUM_GOLD = register("aetherium_gold", ModRarity.ORANGE);

    public static final DeferredItem<Item> PEARL = register("pearl", ModRarity.BLUE);
    public static final DeferredItem<Item> BLACK_PEARL = register("black_pearl", ModRarity.BLUE);
    public static final DeferredItem<Item> PINK_PEARL = register("pink_pearl", ModRarity.BLUE);

    public static final DeferredItem<Item> CHINA_PLATE = register("china_plate", ModRarity.WHITE);
    public static final DeferredItem<Item> CHINA_BOWL = register("china_bowl", ModRarity.WHITE);

    public static final DeferredItem<Item> EMPTY_BULLET = register("empty_bullet", ModRarity.WHITE);
    public static final DeferredItem<Item> EXPLOSIVE_POWDER = register("explosive_powder", ModRarity.WHITE);
    public static final DeferredItem<Item> GOLD_DUST = register("gold_dust", ModRarity.WHITE);
    public static final DeferredItem<Item> COG = register("cog", ModRarity.WHITE);
    public static final DeferredItem<Item> NANITES = register("nanites", ModRarity.WHITE);
    public static final DeferredItem<Item> CONFETTI = register("confetti", ModRarity.WHITE);
    public static final DeferredItem<Item> VIAL_OF_VENOM = register("vial_of_venom", ModRarity.WHITE);
    public static final DeferredItem<Item> PEARLWOOD_STICK = register("pearlwood_stick", ModRarity.WHITE);

    // 草药
    public static final DeferredItem<Item> WATERLEAF = ITEMS.registerSimpleItem("waterleaf");
    public static final DeferredItem<Item> FIREBLOSSOM = ITEMS.registerSimpleItem("fireblossom", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> MOONGLOW = ITEMS.registerSimpleItem("moonglow");
    public static final DeferredItem<Item> BLINKROOT = ITEMS.registerSimpleItem("blinkroot");
    public static final DeferredItem<Item> SHIVERTHORN = ITEMS.registerSimpleItem("shiverthorn");
    public static final DeferredItem<Item> DAYBLOOM = ITEMS.registerSimpleItem("daybloom");
    public static final DeferredItem<Item> DEATHWEED = ITEMS.registerSimpleItem("deathweed");
    // 蘑菇
    public static final DeferredItem<Item> VICIOUS_MUSHROOM = ITEMS.register("vicious_mushroom", () -> new MushroomItem(NatureBlocks.VICIOUS_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> VILE_MUSHROOM = ITEMS.register("vile_mushroom", () -> new MushroomItem(NatureBlocks.VILE_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> GLOWING_MUSHROOM = ITEMS.register("glowing_mushroom", () -> new MushroomItem(NatureBlocks.GLOWING_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> LIFE_MUSHROOM = ITEMS.register("life_mushroom", () -> new MushroomItem(NatureBlocks.LIFE_MUSHROOM.get(), 6.0F));
    public static final DeferredItem<Item> JUNGLE_SPORE = ITEMS.register("jungle_spore", () -> new Item(new Item.Properties()));

    // 困难模式
    public static final DeferredItem<Item> SOUL_OF_VOIGHT = ITEMS.register("soul_of_voight", () -> new TooltipItem(new Item.Properties(), ModRarity.ORANGE, TooltipItem.getTooltipsFromString("soul_of_voight", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_LIGHT = ITEMS.register("soul_of_light", () -> new TooltipItem(new Item.Properties(), ModRarity.ORANGE, TooltipItem.getTooltipsFromString("soul_of_light", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_NIGHT = ITEMS.register("soul_of_night", () -> new TooltipItem(new Item.Properties(), ModRarity.ORANGE, TooltipItem.getTooltipsFromString("soul_of_night", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_FLIGHT = ITEMS.register("soul_of_flight", () -> new TooltipItem(new Item.Properties(), ModRarity.ORANGE, TooltipItem.getTooltipsFromString("soul_of_flight", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_FRIGHT = ITEMS.register("soul_of_fright", () -> new TooltipItem(new Item.Properties(), ModRarity.PINK, TooltipItem.getTooltipsFromString("soul_of_fright", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_MIGHT = ITEMS.register("soul_of_might", () -> new TooltipItem(new Item.Properties(), ModRarity.PINK, TooltipItem.getTooltipsFromString("soul_of_might", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_SIGHT = ITEMS.register("soul_of_sight", () -> new TooltipItem(new Item.Properties(), ModRarity.PINK, TooltipItem.getTooltipsFromString("soul_of_sight", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<Item> SOUL_OF_BRIGHT = ITEMS.register("soul_of_bright", () -> new TooltipItem(new Item.Properties(), ModRarity.PINK, TooltipItem.getTooltipsFromString("soul_of_bright", 1, ChatFormatting.GRAY)));
    public static final DeferredItem<BlockItem> CRYSTAL_SHARDS = ITEMS.register("crystal_shards", () -> new BlockItem(NatureBlocks.CRYSTAL_SHARDS.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final DeferredItem<Item> CURSED_FLAME = ITEMS.register("cursed_flame", CursedFlameItem::new);
    public static final DeferredItem<Item> ICHOR = register("ichor", ModRarity.ORANGE);
    public static final DeferredItem<Item> PIXIE_DUST = register("pixie_dust", ModRarity.BLUE);
    public static final DeferredItem<Item> UNICORN_HORN = register("unicorn_horn", ModRarity.BLUE);
    public static final DeferredItem<Item> SPIDER_FANG = register("spider_fang", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> DARK_SHARD = register("dark_shard", ModRarity.GREEN);
    public static final DeferredItem<Item> LIGHT_SHARD = register("light_shard", ModRarity.GREEN);
    public static final DeferredItem<Item> ANCIENT_CLOTH = register("ancient_cloth", ModRarity.BLUE);
    public static final DeferredItem<Item> SPELL_TOME = register("spell_tome", ModRarity.BLUE);
    public static final DeferredItem<Item> ECTOPLASM = register("ectoplasm", ModRarity.YELLOW);
    public static final DeferredItem<Item> FORBIDDEN_FRAGMENT = register("forbidden_fragment", ModRarity.PINK);
    public static final DeferredItem<Item> FROST_CORE = register("frost_core", ModRarity.PINK);
    public static final DeferredItem<Item> MECHANICAL_WHEEL_PIECE = register("mechanical_wheel_piece", ModRarity.MASTER);
    public static final DeferredItem<Item> MECHANICAL_WAGON_PIECE = register("mechanical_wagon_piece", ModRarity.MASTER);
    public static final DeferredItem<Item> MECHANICAL_BATTERY_PIECE = register("mechanical_battery_piece", ModRarity.MASTER);

    public static DeferredItem<Item> register(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));

    }

    public static DeferredItem<Item> register(String id, ModRarity rarity) {
        return ITEMS.register(id, () -> new CustomRarityItem(rarity));
    }
}
