package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.common.item.common.ColoredItem;
import org.confluence.mod.common.item.common.MushroomItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.jetbrains.annotations.NotNull;

public class MaterialItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> GEL = ITEMS.register("gel", () -> new ColoredItem(ModRarity.WHITE){
        @Override
        public @NotNull ItemStack getDefaultInstance() {
            ItemStack itemStack = new ItemStack(GEL.get());
            setColor(itemStack, 0xFF66CCFF);
            return itemStack;
        }
    });
    public static final DeferredItem<Item> PINK_GEL = register("pink_gel", ModRarity.PINK);

    public static final DeferredItem<Item> RAW_TIN = register("raw_tin");
    public static final DeferredItem<Item> TIN_INGOT = register("tin_ingot");
    public static final DeferredItem<Item> RAW_LEAD = register("raw_lead");
    public static final DeferredItem<Item> LEAD_INGOT = register("lead_ingot");
    public static final DeferredItem<Item> RAW_SILVER = register("raw_silver");
    public static final DeferredItem<Item> SILVER_INGOT = register("silver_ingot");
    public static final DeferredItem<Item> RAW_TUNGSTEN = register("raw_tungsten");
    public static final DeferredItem<Item> TUNGSTEN_INGOT = register("tungsten_ingot");
    public static final DeferredItem<Item> RAW_PLATINUM = register("raw_platinum", ModRarity.BLUE);
    public static final DeferredItem<Item> PLATINUM_INGOT = register("platinum_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_METEORITE = register("raw_meteorite", ModRarity.BLUE);
    public static final DeferredItem<Item> METEORITE_INGOT = register("meteorite_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_DEMONITE = register("raw_demonite", ModRarity.BLUE);
    public static final DeferredItem<Item> DEMONITE_INGOT = register("demonite_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_TR_CRIMSON = register("raw_tr_crimson", ModRarity.BLUE);
    public static final DeferredItem<Item> TR_CRIMSON_INGOT = register("tr_crimson_ingot", ModRarity.BLUE);
    public static final DeferredItem<Item> HELLSTONE_INGOT = register("hellstone_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> RAW_HELLSTONE = register("raw_hellstone", ModRarity.ORANGE);

    public static final DeferredItem<Item> RAW_COBALT = register("raw_cobalt", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> COBALT_INGOT = register("cobalt_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_PALLADIUM = register("raw_palladium", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> PALLADIUM_INGOT = register("palladium_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_MITHRIL = register("raw_mithril", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> MITHRIL_INGOT = register("mithril_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_ORICHALCUM = register("raw_orichalcum", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> ORICHALCUM_INGOT = register("orichalcum_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_ADAMANTITE = register("raw_adamantite", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> ADAMANTITE_INGOT = register("adamantite_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_TITANIUM = register("raw_titanium", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> TITANIUM_INGOT = register("titanium_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> HALLOWED_INGOT = register("hallowed_ingot", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> CHLOROPHYTE_INGOT = register("chlorophyte_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> RAW_CHLOROPHYTE = register("raw_chlorophyte", ModRarity.ORANGE);
    public static final DeferredItem<Item> SHROOMITE_INGOT = register("shroomite_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> SPECTRE_INGOT = register("spectre_ingot", ModRarity.ORANGE);
    public static final DeferredItem<Item> RAW_LUMINITE = register("raw_luminite", ModRarity.ORANGE);
    public static final DeferredItem<Item> LUMINITE_INGOT = register("luminite_ingot", ModRarity.ORANGE);

    public static final DeferredItem<Item> AMBER = register("amber");
    public static final DeferredItem<Item> TR_AMETHYST = register("tr_amethyst");
    public static final DeferredItem<Item> TR_EMERALD = register("tr_emerald");
    public static final DeferredItem<Item> RUBY = register("ruby");
    public static final DeferredItem<Item> SAPPHIRE = register("sapphire");
    public static final DeferredItem<Item> TOPAZ = register("topaz");

    public static final DeferredItem<Item> STAR_PETALS = register("star_petals",ModRarity.BLUE);
    public static final DeferredItem<Item> FLOATING_WHEAT_HEADS = register("floating_wheat_heads",ModRarity.BLUE);
    public static final DeferredItem<Item> FALLING_STAR = register("falling_star");
    public static final DeferredItem<Item> WEAVING_CLOUD_COTTON = register("weaving_cloud_cotton",ModRarity.BLUE);
    public static final DeferredItem<Item> ROTTEN_CHUNK = register("rotten_chunk");
    public static final DeferredItem<Item> WORM_TOOTH = register("worm_tooth");
    public static final DeferredItem<Item> VERTEBRA = register("vertebra");
    public static final DeferredItem<Item> BLOOD_CLOT_POWDER = register("blood_clot_powder",ModRarity.BLUE);
    public static final DeferredItem<Item> ROTTEN_BONE = register("rotten_bone",ModRarity.BLUE);
    public static final DeferredItem<Item> VILE_POWDER = register("vile_powder");
    public static final DeferredItem<Item> VICIOUS_POWDER = register("vicious_powder");
    public static final DeferredItem<Item> PURIFICATION_POWDER = register("purification_powder");
    public static final DeferredItem<Item> BLACK_INK = register("black_ink");
    public static final DeferredItem<Item> LEAD_NUGGET = register("lead_nugget");
    public static final DeferredItem<Item> PURPLE_MUCUS = register("purple_mucus");
    public static final DeferredItem<Item> SHARK_FIN = register("shark_fin");
    public static final DeferredItem<Item> ANTLION_MANDIBLE = register("antlion_mandible");
    public static final DeferredItem<Item> HOOK = register("hook");
    public static final DeferredItem<Item> FILAMENTOUS_FIN = register("filamentous_fin");
    public static final DeferredItem<Item> LENS = register("lens");
    public static final DeferredItem<Item> BLACK_LENS = register("black_lens");
    public static final DeferredItem<Item> STURDY_FOSSIL = register("sturdy_fossil",ModRarity.BLUE);
    public static final DeferredItem<Item> OPAL = register("opal",ModRarity.BLUE);
    public static final DeferredItem<Item> HEIM = register("heim",ModRarity.BLUE);
    public static final DeferredItem<Item> GELSTONE = register("gelstone",ModRarity.BLUE);
    public static final DeferredItem<Item> SPORE_ROOT = register("spore_root",ModRarity.BLUE);
    public static final DeferredItem<Item> WINTER_MARROW = register("winter_marrow",ModRarity.BLUE);
    public static final DeferredItem<Item> COLD_CRYSTAL = register("cold_crystal",ModRarity.BLUE);
    public static final DeferredItem<Item> SHADOW_SCALE = register("shadow_scale",ModRarity.BLUE);
    public static final DeferredItem<Item> TISSUE_SAMPLE = register("tissue_sample",ModRarity.BLUE);

    public static final DeferredItem<Item> CRYSTAL_SHARDS_ITEM = register("crystal_shards_item",ModRarity.BLUE);
    public static final DeferredItem<Item> CURSED_FLAME = register("cursed_flame",ModRarity.ORANGE);
    public static final DeferredItem<Item> ICHOR = register("ichor",ModRarity.ORANGE);
    public static final DeferredItem<Item> PIXIE_DUST = register("pixie_dust",ModRarity.BLUE);

    public static final DeferredItem<Item> PEARL = register("pearl", ModRarity.BLUE);
    public static final DeferredItem<Item> BLACK_PEARL = register("black_pearl",ModRarity.BLUE);
    public static final DeferredItem<Item> PINK_PEARL = register("pink_pearl",ModRarity.BLUE);

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
    // TODO: 发光蘑菇可以放置，但此蘑菇非彼蘑菇
    public static final DeferredItem<Item> GLOWING_MUSHROOM = ITEMS.register("glowing_mushroom", () -> new MushroomItem(NatureBlocks.GLOWING_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> LIFE_MUSHROOM = ITEMS.register("life_mushroom", () -> new MushroomItem(NatureBlocks.LIFE_MUSHROOM.get(), 6.0F));
    public static final DeferredItem<Item> JUNGLE_SPORE = ITEMS.register("jungle_spore", () -> new Item(new Item.Properties()));

    public static DeferredItem<Item> register(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));

    }

    public static DeferredItem<Item> register(String id, ModRarity rarity) {
        return ITEMS.register(id, () -> new CustomRarityItem(rarity));
    }

    public static void init() {}
}
