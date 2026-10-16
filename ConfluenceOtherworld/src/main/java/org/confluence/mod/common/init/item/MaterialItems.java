package org.confluence.mod.common.init.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.CursedFlameItem;
import org.confluence.mod.common.item.common.GelItem;
import org.confluence.mod.common.item.common.MushroomItem;
import org.confluence.mod.common.item.common.VoidCrystalItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class MaterialItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> GEL = registerItem("gel", "凝胶", GelItem::new);
    public static final DeferredItem<Item> PINK_GEL = register("pink_gel", "粉凝胶", ModRarity.PINK);
    public static final DeferredItem<Item> SILK = register("silk", "丝绸", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_ASPHALT = registerItem("raw_asphalt", "生沥青", () -> new CustomRarityItem(new Item.Properties().stacksTo(LibUtils.MAX_STACK_SIZE), ModRarity.WHITE));

    public static final DeferredItem<Item> RAW_TIN = register("raw_tin", "粗锡");
    public static final DeferredItem<Item> TIN_INGOT = register("tin_ingot", "锡锭");
    public static final DeferredItem<Item> TIN_NUGGET = register("tin_nugget", "锡粒");
    public static final DeferredItem<Item> RAW_LEAD = register("raw_lead", "粗铅");
    public static final DeferredItem<Item> LEAD_INGOT = register("lead_ingot", "铅锭");
    public static final DeferredItem<Item> LEAD_NUGGET = register("lead_nugget", "铅粒");
    public static final DeferredItem<Item> RAW_SILVER = register("raw_silver", "粗银");
    public static final DeferredItem<Item> SILVER_INGOT = register("silver_ingot", "银锭");
    public static final DeferredItem<Item> SILVER_NUGGET = register("silver_nugget", "银粒");
    public static final DeferredItem<Item> RAW_TUNGSTEN = register("raw_tungsten", "粗钨");
    public static final DeferredItem<Item> TUNGSTEN_INGOT = register("tungsten_ingot", "钨锭");
    public static final DeferredItem<Item> TUNGSTEN_NUGGET = register("tungsten_nugget", "钨粒");
    public static final DeferredItem<Item> RAW_PLATINUM = register("raw_platinum", "粗铂金", ModRarity.BLUE);
    public static final DeferredItem<Item> PLATINUM_INGOT = register("platinum_ingot", "铂金锭", ModRarity.BLUE);
    public static final DeferredItem<Item> PLATINUM_NUGGET = register("platinum_nugget", "铂金粒", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_METEORITE = register("raw_meteorite", "粗陨铁", ModRarity.BLUE);
    public static final DeferredItem<Item> METEORITE_INGOT = register("meteorite_ingot", "陨铁锭", ModRarity.BLUE);
    public static final DeferredItem<Item> METEORITE_NUGGET = register("meteorite_nugget", "陨铁粒", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_DEMONITE = register("raw_demonite", "粗魔矿", ModRarity.BLUE);
    public static final DeferredItem<Item> DEMONITE_INGOT = register("demonite_ingot", "魔矿锭", ModRarity.BLUE);
    public static final DeferredItem<Item> DEMONITE_NUGGET = register("demonite_nugget", "魔矿粒", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_CRIMTANE = register("raw_crimtane", "粗猩红矿", ModRarity.BLUE);
    public static final DeferredItem<Item> CRIMTANE_INGOT = register("crimtane_ingot", "猩红矿锭", ModRarity.BLUE);
    public static final DeferredItem<Item> CRIMTANE_NUGGET = register("crimtane_nugget", "猩红矿粒", ModRarity.BLUE);
    public static final DeferredItem<Item> RAW_HELLSTONE = registerItemSimple("raw_hellstone", "粗狱石矿", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> HELLSTONE_INGOT = registerItemSimple("hellstone_ingot", "狱石矿锭", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> HELLSTONE_NUGGET = registerItemSimple("hellstone_nugget", "狱石矿粒", new Item.Properties().fireResistant());

    public static final DeferredItem<Item> RAW_COBALT = register("raw_cobalt", "粗钴", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> COBALT_INGOT = register("cobalt_ingot", "钴锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_PALLADIUM = register("raw_palladium", "粗钯金", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> PALLADIUM_INGOT = register("palladium_ingot", "钯金锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_MYTHRIL = register("raw_mythril", "粗秘银", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> MYTHRIL_INGOT = register("mythril_ingot", "秘银锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_ORICHALCUM = register("raw_orichalcum", "粗山铜", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> ORICHALCUM_INGOT = register("orichalcum_ingot", "山铜锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_ADAMANTITE = register("raw_adamantite", "粗精金", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> ADAMANTITE_INGOT = register("adamantite_ingot", "精金锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_TITANIUM = register("raw_titanium", "粗钛金", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> TITANIUM_INGOT = register("titanium_ingot", "钛金锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> HALLOWED_INGOT = register("hallowed_ingot", "神圣锭", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> RAW_CHLOROPHYTE = register("raw_chlorophyte", "粗叶绿", ModRarity.ORANGE);
    public static final DeferredItem<Item> CHLOROPHYTE_INGOT = register("chlorophyte_ingot", "叶绿锭", ModRarity.ORANGE);
    public static final DeferredItem<Item> SHROOMITE_INGOT = register("shroomite_ingot", "蘑菇矿锭", ModRarity.ORANGE);
    public static final DeferredItem<Item> SPECTRE_INGOT = register("spectre_ingot", "幽灵锭", ModRarity.ORANGE);
    public static final DeferredItem<Item> RAW_LUMINITE = register("raw_luminite", "粗夜明矿", ModRarity.ORANGE);
    public static final DeferredItem<Item> LUMINITE_INGOT = register("luminite_ingot", "夜明锭", ModRarity.ORANGE);

    public static final DeferredItem<Item> AMBER = register("amber", "琥珀");
    public static final DeferredItem<Item> AMETHYST = register("amethyst", "紫晶");
    public static final DeferredItem<Item> JADE = register("jade", "翡翠");
    public static final DeferredItem<Item> RUBY = register("ruby", "红玉");
    public static final DeferredItem<Item> SAPPHIRE = register("sapphire", "蓝玉");
    public static final DeferredItem<Item> TOPAZ = register("topaz", "黄玉");

    public static final DeferredItem<Item> STAR_PETALS = register("star_petals", "星辰花瓣", ModRarity.BLUE);
    public static final DeferredItem<Item> FLOATING_WHEAT_HEADS = register("floating_wheat_heads", "飘飘麦", ModRarity.BLUE);
    public static final DeferredItem<Item> FALLING_STAR = registerItem("falling_star", "坠落之星", () -> new Item(new Item.Properties().stacksTo(9999)));
    public static final DeferredItem<Item> WEAVING_CLOUD_COTTON = register("weaving_cloud_cotton", "织云棉", ModRarity.BLUE);
    public static final DeferredItem<Item> ROTTEN_CHUNK = register("rotten_chunk", "腐肉");
    public static final DeferredItem<Item> WORM_TOOTH = register("worm_tooth", "蠕虫毒牙");
    public static final DeferredItem<Item> VERTEBRA = register("vertebra", "椎骨");
    public static final DeferredItem<Item> BLOOD_CLOT_POWDER = register("blood_clot_powder", "血凝粉末", ModRarity.BLUE);
    public static final DeferredItem<Item> ROTTEN_BONE = register("rotten_bone", "腐蚀残骨", ModRarity.BLUE);
    public static final DeferredItem<Item> STINGER = register("stinger", "毒刺", ModRarity.BLUE);
    public static final DeferredItem<Item> MAN_EATER_VINE = register("man_eater_vine", "食人怪藤蔓", ModRarity.BLUE);
    public static final DeferredItem<Item> BLACK_INK = register("black_ink", "黑墨水");
    public static final DeferredItem<Item> SHARK_FIN = register("shark_fin", "鲨鱼鳍");
    public static final DeferredItem<Item> ANTLION_MANDIBLE = register("antlion_mandible", "蚁狮上颚");
    public static final DeferredItem<Item> HOOK = register("hook", "爪钩");
    public static final DeferredItem<Item> FILAMENTOUS_FIN = register("filamentous_fin", "羽状鳍");
    public static final DeferredItem<Item> TATTERED_CLOTH = register("tattered_cloth", "破布");
    public static final DeferredItem<Item> LENS = register("lens", "晶状体");
    public static final DeferredItem<Item> BLACK_LENS = register("black_lens", "黑晶状体");
    public static final DeferredItem<Item> STURDY_FOSSIL = register("sturdy_fossil", "坚固化石", ModRarity.BLUE);
    public static final DeferredItem<Item> OPAL = register("opal", "欧泊", ModRarity.BLUE);
    public static final DeferredItem<Item> HEIM = register("heim", "海沫", ModRarity.BLUE);
    public static final DeferredItem<Item> GELSTONE = register("gelstone", "黏晶", ModRarity.BLUE);
    public static final DeferredItem<Item> SPORE_ROOT = register("spore_root", "孢根", ModRarity.BLUE);
    public static final DeferredItem<Item> COLD_CRYSTAL = register("cold_crystal", "寒晶", ModRarity.BLUE);
    public static final DeferredItem<Item> WINTER_MARROW = register("winter_marrow", "霜髓", ModRarity.BLUE);
    public static final DeferredItem<Item> SHADOW_SCALE = register("shadow_scale", "暗影鳞片", ModRarity.BLUE);
    public static final DeferredItem<Item> TISSUE_SAMPLE = register("tissue_sample", "组织样本", ModRarity.BLUE);
    public static final DeferredItem<Item> ROYAL_WAX = register("royal_wax", "蜂王蜡", ModRarity.BLUE);
    public static final DeferredItem<Item> HARPY_FEATHER = register("harpy_feather", "鸟妖之羽", ModRarity.BLUE);
    public static final DeferredItem<Item> GIANT_HARPY_FEATHER = register("giant_harpy_feather", "巨型鸟妖之羽", ModRarity.BLUE);
    public static final DeferredItem<Item> FLINX_FUR = register("flinx_fur", "小雪怪皮毛", ModRarity.BLUE);
    public static final DeferredItem<Item> AETHERIUM_SHARD = register("aetherium_shard", "以太碎片", ModRarity.ORANGE);
    public static final DeferredItem<Item> AETHERIUM_GOLD = register("aetherium_gold", "嬗金", ModRarity.ORANGE);
    public static final DeferredItem<Item> LUNARTEAR = register("lunartear", "月泣石", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> DRAGONSAL = register("dragonsal", "龙涎石", ModRarity.PINK);

    public static final DeferredItem<Item> PEARL = register("pearl", "珍珠", ModRarity.BLUE);
    public static final DeferredItem<Item> BLACK_PEARL = register("black_pearl", "黑珍珠", ModRarity.BLUE);
    public static final DeferredItem<Item> PINK_PEARL = register("pink_pearl", "粉珍珠", ModRarity.BLUE);

    public static final DeferredItem<Item> CHINA_PLATE = register("china_plate", "陶瓷盘", ModRarity.WHITE);
    public static final DeferredItem<Item> CHINA_BOWL = register("china_bowl", "陶瓷碗", ModRarity.WHITE);

    public static final DeferredItem<Item> EMPTY_BULLET = register("empty_bullet", "空心子弹", ModRarity.WHITE);
    public static final DeferredItem<Item> EXPLOSIVE_POWDER = register("explosive_powder", "爆炸粉", ModRarity.WHITE);
    public static final DeferredItem<Item> GOLD_DUST = register("gold_dust", "金尘", ModRarity.WHITE);
    public static final DeferredItem<Item> COG = register("cog", "齿轮", ModRarity.WHITE);
    public static final DeferredItem<Item> NANITES = register("nanites", "纳米机器人", ModRarity.WHITE);
    public static final DeferredItem<Item> CONFETTI = register("confetti", "彩纸", ModRarity.WHITE);
    public static final DeferredItem<Item> VIAL_OF_VENOM = registerWithEnglishName("vial_of_venom", "小瓶毒液", "Vial of Venom", ModRarity.WHITE);
    public static final DeferredItem<Item> PEARLWOOD_STICK = register("pearlwood_stick", "珍珠木棍", ModRarity.WHITE);
    public static final DeferredItem<Item> BELL = register("bell", "铃铛", ModRarity.ORANGE);
    public static final DeferredItem<Item> HARP = register("harp", "竖琴", ModRarity.ORANGE);
    public static final DeferredItem<Item> FIRE_FEATHER = register("fire_feather", "火羽", ModRarity.PINK);

    // 草药
    public static final DeferredItem<Item> WATERLEAF = registerItemSimple("waterleaf", "水叶草");
    public static final DeferredItem<Item> FIREBLOSSOM = registerItemSimple("fireblossom", "火焰花", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> MOONGLOW = registerItemSimple("moonglow", "月光草");
    public static final DeferredItem<Item> BLINKROOT = registerItemSimple("blinkroot", "闪耀根");
    public static final DeferredItem<Item> SHIVERTHORN = registerItemSimple("shiverthorn", "寒颤棘");
    public static final DeferredItem<Item> DAYBLOOM = registerItemSimple("daybloom", "太阳花");
    public static final DeferredItem<Item> DEATHWEED = registerItemSimple("deathweed", "死亡草");
    // 蘑菇
    public static final DeferredItem<Item> VICIOUS_MUSHROOM = ITEMS.register("vicious_mushroom", () -> new MushroomItem(NatureBlocks.VICIOUS_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> VILE_MUSHROOM = ITEMS.register("vile_mushroom", () -> new MushroomItem(NatureBlocks.VILE_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> GLOWING_MUSHROOM = ITEMS.register("glowing_mushroom", () -> new MushroomItem(NatureBlocks.GLOWING_MUSHROOM.get(), 0.0F));
    public static final DeferredItem<Item> LIFE_MUSHROOM = ITEMS.register("life_mushroom", () -> new MushroomItem(NatureBlocks.LIFE_MUSHROOM.get(), 6.0F));
    public static final DeferredItem<Item> JUNGLE_SPORE = registerItem("jungle_spore", "丛林孢子", () -> new Item(new Item.Properties()));
    // 末地 - 紫颂主题物品
    public static final DeferredItem<Item> VOID_CRYSTAL = registerItem("void_crystal", "虚空水晶", VoidCrystalItem::new);

    // 困难模式
    public static final DeferredItem<Item> SOUL_OF_VOIGHT = registerItem("soul_of_voight", "虚空之魂", () -> new CustomRarityItem(new Item.Properties(), ModRarity.ORANGE));
    public static final DeferredItem<Item> SOUL_OF_LIGHT = registerItemWithEnglishName("soul_of_light", "光明之魂", "Soul of Light", () -> new CustomRarityItem(new Item.Properties(), ModRarity.ORANGE));
    public static final DeferredItem<Item> SOUL_OF_NIGHT = registerItemWithEnglishName("soul_of_night", "暗影之魂", "Soul of Night", () -> new CustomRarityItem(new Item.Properties(), ModRarity.ORANGE));
    public static final DeferredItem<Item> SOUL_OF_FLIGHT = registerItemWithEnglishName("soul_of_flight", "飞翔之魂", "Soul of Flight", () -> new CustomRarityItem(new Item.Properties(), ModRarity.ORANGE));
    public static final DeferredItem<Item> SOUL_OF_FRIGHT = registerItemWithEnglishName("soul_of_fright", "恐惧之魂", "Soul of Fright", () -> new CustomRarityItem(new Item.Properties(), ModRarity.PINK));
    public static final DeferredItem<Item> SOUL_OF_MIGHT = registerItemWithEnglishName("soul_of_might", "力量之魂", "Soul of Might", () -> new CustomRarityItem(new Item.Properties(), ModRarity.PINK));
    public static final DeferredItem<Item> SOUL_OF_SIGHT = registerItemWithEnglishName("soul_of_sight", "视域之魂", "Soul of Sight", () -> new CustomRarityItem(new Item.Properties(), ModRarity.PINK));
    public static final DeferredItem<Item> SOUL_OF_BRIGHT = registerItem("soul_of_bright", "启迪之魂", () -> new CustomRarityItem(new Item.Properties(), ModRarity.PINK));
    public static final DeferredItem<BlockItem> CRYSTAL_SHARDS = ITEMS.register("crystal_shards", () -> new BlockItem(NatureBlocks.CRYSTAL_SHARDS.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final DeferredItem<Item> CURSED_FLAME = ITEMS.register("cursed_flame", CursedFlameItem::new);
    public static final DeferredItem<Item> ICHOR = register("ichor", "灵液", ModRarity.ORANGE);
    public static final DeferredItem<Item> PIXIE_DUST = register("pixie_dust", "妖精尘", ModRarity.BLUE);
    public static final DeferredItem<Item> UNICORN_HORN = register("unicorn_horn", "独角兽角", ModRarity.BLUE);
    public static final DeferredItem<Item> SPIDER_FANG = register("spider_fang", "蜘蛛牙", ModRarity.LIGHT_RED);
    public static final DeferredItem<Item> DARK_SHARD = register("dark_shard", "暗黑碎块", ModRarity.GREEN);
    public static final DeferredItem<Item> LIGHT_SHARD = register("light_shard", "光明碎块", ModRarity.GREEN);
    public static final DeferredItem<Item> ANCIENT_CLOTH = register("ancient_cloth", "远古布匹", ModRarity.BLUE);
    public static final DeferredItem<Item> SPELL_TOME = register("spell_tome", "魔法书", ModRarity.BLUE);
    public static final DeferredItem<Item> ECTOPLASM = register("ectoplasm", "灵气", ModRarity.YELLOW);
    public static final DeferredItem<Item> FORBIDDEN_FRAGMENT = register("forbidden_fragment", "禁戒碎片", ModRarity.PINK);
    public static final DeferredItem<Item> FROST_CORE = register("frost_core", "寒霜核", ModRarity.PINK);
    public static final DeferredItem<Item> TURTLE_SHELL = register("turtle_shell", "海龟壳", ModRarity.LIME);
    public static final DeferredItem<Item> MECHANICAL_WHEEL_PIECE = register("mechanical_wheel_piece", "机械车轮片", ModRarity.MASTER);
    public static final DeferredItem<Item> MECHANICAL_WAGON_PIECE = register("mechanical_wagon_piece", "机械车体片", ModRarity.MASTER);
    public static final DeferredItem<Item> MECHANICAL_BATTERY_PIECE = register("mechanical_battery_piece", "机械电池片", ModRarity.MASTER);

    public static final DeferredItem<Item> SOLAR_FRAGMENT = register("solar_fragment", "日耀碎片", ModRarity.CYAN);
    public static final DeferredItem<Item> VORTEX_FRAGMENT = register("vortex_fragment", "星旋碎片", ModRarity.CYAN);
    public static final DeferredItem<Item> NEBULA_FRAGMENT = register("nebula_fragment", "星云碎片", ModRarity.CYAN);
    public static final DeferredItem<Item> STARDUST_FRAGMENT = register("stardust_fragment", "星尘碎片", ModRarity.CYAN);

    public static DeferredItem<Item> register(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));

    }

    public static DeferredItem<Item> register(String id, ModRarity rarity) {
        return ITEMS.register(id, () -> new CustomRarityItem(rarity));
    }

    private static DeferredItem<Item> register(String registryId, String chineseName) {
        DeferredItem<Item> entry = ITEMS.register(registryId, () -> new Item(new Item.Properties()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<Item> register(String registryId, String chineseName, ModRarity rarity) {
        DeferredItem<Item> entry = ITEMS.register(registryId, () -> new CustomRarityItem(rarity));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<Item> registerItemSimple(String registryId, String chineseName, Item.Properties properties) {
        DeferredItem<Item> entry = ITEMS.registerSimpleItem(registryId, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<Item> registerItemSimple(String registryId, String chineseName) {
        DeferredItem<Item> entry = ITEMS.registerSimpleItem(registryId);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<Item> registerWithEnglishName(String registryId, String chineseName, String englishName) {
        DeferredItem<Item> entry = ITEMS.register(registryId, () -> new Item(new Item.Properties()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static DeferredItem<Item> registerWithEnglishName(String registryId, String chineseName, String englishName, ModRarity rarity) {
        DeferredItem<Item> entry = ITEMS.register(registryId, () -> new CustomRarityItem(rarity));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
