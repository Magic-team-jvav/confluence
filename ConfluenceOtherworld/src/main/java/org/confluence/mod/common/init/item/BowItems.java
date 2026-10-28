package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.bow.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

/// 弓箭位置修正参考[ArrowInBowRenderer]
public class BowItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 短弓
    public static final PortDeferredItem<ShortBowItem> WOODEN_SHORT_BOW = registerItem("wooden_short_bow", "木短弓", () -> new ShortBowItem(4.0F, 384));
    public static final PortDeferredItem<ShortBowItem> EBONWOOD_SHORT_BOW = registerItem("ebonwood_short_bow", "乌木短弓", () -> new ShortBowItem(4.3F, 404));
    public static final PortDeferredItem<ShortBowItem> SHADEWOOD_SHORT_BOW = registerItem("shadewood_short_bow", "暗影木短弓", () -> new ShortBowItem(4.4F, 424));
    public static final PortDeferredItem<ShortBowItem> ASH_WOOD_SHORT_BOW = registerItem("ash_wood_short_bow", "灰烬木短弓", () -> new ShortBowItem(4.5F, 444));
    public static final PortDeferredItem<ShortBowItem> PEARLWOOD_SHORT_BOW = registerItem("pearlwood_short_bow", "珍珠木短弓", () -> new ShortBowItem(5.0F, 1000));
    public static final PortDeferredItem<ShortBowItem> COPPER_SHORT_BOW = registerItem("copper_short_bow", "铜短弓", () -> new ShortBowItem(4.5F, 640, Ingredient.of(Tags.Items.INGOTS_COPPER)));
    public static final PortDeferredItem<ShortBowItem> TIN_SHORT_BOW = registerItem("tin_short_bow", "锡短弓", () -> new ShortBowItem(4.5F, 768, Ingredient.of(ModTags.Items.INGOTS_TIN)));
    public static final PortDeferredItem<ShortBowItem> IRON_SHORT_BOW = registerItem("iron_short_bow", "铁短弓", () -> new ShortBowItem(5.0F, 896, Ingredient.of(Tags.Items.INGOTS_IRON)));
    public static final PortDeferredItem<ShortBowItem> LEAD_SHORT_BOW = registerItem("lead_short_bow", "铅短弓", () -> new ShortBowItem(5.0F, 1024, Ingredient.of(ModTags.Items.INGOTS_LEAD)));
    public static final PortDeferredItem<ShortBowItem> SILVER_SHORT_BOW = registerItem("silver_short_bow", "银短弓", () -> new ShortBowItem(5.5F, 1152, Ingredient.of(ModTags.Items.INGOTS_SILVER)));
    public static final PortDeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = registerItem("tungsten_short_bow", "钨短弓", () -> new ShortBowItem(5.5F, 1280, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN)));
    public static final PortDeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = registerItem("golden_short_bow", "金短弓", () -> new ShortBowItem(6.0F, 1408, Ingredient.of(Tags.Items.INGOTS_GOLD)));
    public static final PortDeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = registerItem("platinum_short_bow", "铂金短弓", () -> new ShortBowItem(6.0F, 1536, Ingredient.of(ModTags.Items.INGOTS_PLATINUM)));

    // 无效果蓄力弓
    public static final PortDeferredItem<BaseTerraBowItem> EBONWOOD_BOW = register("ebonwood_bow", "乌木弓", 3.0F, 404);
    public static final PortDeferredItem<BaseTerraBowItem> SHADEWOOD_BOW = register("shadewood_bow", "暗影木弓", 3.1F, 424);
    public static final PortDeferredItem<BaseTerraBowItem> ASH_WOOD_BOW = register("ash_wood_bow", "灰烬木弓", 3.2F, 444);
    public static final PortDeferredItem<BaseTerraBowItem> PEARLWOOD_BOW = register("pearlwood_bow", "珍珠木弓", 3.5F, 1000);
    public static final PortDeferredItem<BaseTerraBowItem> COPPER_BOW = register("copper_bow", "铜弓", 3.0F, 640, Ingredient.of(Tags.Items.INGOTS_COPPER));
    public static final PortDeferredItem<BaseTerraBowItem> TIN_BOW = register("tin_bow", "锡弓", 3.0F, 768, Ingredient.of(ModTags.Items.INGOTS_TIN));
    public static final PortDeferredItem<BaseTerraBowItem> IRON_BOW = register("iron_bow", "铁弓", 3.5F, 896, Ingredient.of(Tags.Items.INGOTS_IRON));
    public static final PortDeferredItem<BaseTerraBowItem> LEAD_BOW = register("lead_bow", "铅弓", 3.5F, 1024, Ingredient.of(ModTags.Items.INGOTS_LEAD));
    public static final PortDeferredItem<BaseTerraBowItem> SILVER_BOW = register("silver_bow", "银弓", 4.0F, 1152, Ingredient.of(ModTags.Items.INGOTS_SILVER));
    public static final PortDeferredItem<BaseTerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", "钨弓", 4.0F, 1280, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN));
    public static final PortDeferredItem<BaseTerraBowItem> GOLDEN_BOW = register("golden_bow", "金弓", 4.5F, 1408, Ingredient.of(Tags.Items.INGOTS_GOLD));
    public static final PortDeferredItem<BaseTerraBowItem> PLATINUM_BOW = register("platinum_bow", "铂金弓", 4.5F, 1536, Ingredient.of(ModTags.Items.INGOTS_PLATINUM));

    // DIY蓄力弓
    /**
     * 如果需要速射，加上tag {@link org.confluence.mod.common.init.ModTags.Items#FAST_BOW}
     */
    public static final PortDeferredItem<FossilBow> FOSSIL_BOW = registerItem("fossil_bow", "石骸弓", FossilBow::new);
    public static final PortDeferredItem<HuntingBow> HUNTING_BOW = registerItem("hunting_bow", "猎手弓", HuntingBow::new);
    public static final PortDeferredItem<DemonBow> DEMON_BOW = registerItem("demon_bow", "恶魔弓", DemonBow::new);
    public static final PortDeferredItem<TendonBow> TENDON_BOW = registerItem("tendon_bow", "肌腱弓", TendonBow::new);
    public static final PortDeferredItem<MoltenFury> MOLTEN_FURY = registerItem("molten_fury", "熔火之怒", MoltenFury::new);
    public static final PortDeferredItem<TheBeesKnees> THE_BEES_KNEES = registerItemWithEnglishName("the_bees_knees", "蜂膝弓", "The Bee's Knees", TheBeesKnees::new);
    public static final PortDeferredItem<HellwingBow> HELLWING_BOW = registerItem("hellwing_bow", "地狱之翼弓", HellwingBow::new);

    // 稻草人弓 - 手持时驱离 32 格内的鸟妖，箭矢对鸟妖额外造成 30 点伤害
    public static final PortDeferredItem<Scarebow> SCAREBOW = registerItem("scarebow", "稻草人弓", Scarebow::new);

    // 代达罗斯风暴弓
    public static final PortDeferredItem<DaedalusStormbow> DAEDALUS_STORM_BOW = registerItemWithEnglishName("daedalus_storm_bow", "代达罗斯风暴弓", "Daedalus Stormbow", () -> new DaedalusStormbow(12f, ModRarity.PURPLE));

    // 开发者弓
    public static final PortDeferredItem<DeveloperBow> DEVELOPER_BOW = registerItem("developer_bow", "开发者弓", DeveloperBow::new);

    // 魂师弓：击杀额外收魂，灵魂高于阈值时箭矢增伤（见 LivingEntityEvents）；两把都不可破坏
    public static final PortDeferredItem<SoulShortBowItem> SOUL_SHORT_BOW = registerItem("soul_short_bow", "灵魂短弓", () -> new SoulShortBowItem(5.5F));
    public static final PortDeferredItem<SoulBowItem> SOUL_BOW = registerItem("soul_bow", "灵魂弓", () -> new SoulBowItem(4.0F));

    // 风与星系列弓
    /// 风弦：把木箭替换为风矢，并对具有击退抗性的敌怪额外增伤。
    public static final PortDeferredItem<WindStringBowItem> WIND_STRING = registerItem("wind_string", "风弦", () -> new WindStringBowItem(4.6F, 1200));
    /// 附魔弓：把木箭替换为耀星之箭。无耐久。
    public static final PortDeferredItem<EnchantedBowItem> ENCHANTED_BOW = registerItem("enchanted_bow", "附魔弓", () -> new EnchantedBowItem(4.5F));
    /// 星泣：未命中的箭累积成坠星，下一次命中时释放。无耐久。
    public static final PortDeferredItem<StarWeepBowItem> STAR_WEEP = registerItem("star_weep", "星泣", () -> new StarWeepBowItem(4.8F));
    /// 战弓：左键横扫、右键射箭。无耐久。
    public static final PortDeferredItem<WarBowItem> WAR_BOW = registerItem("war_bow", "战弓", () -> new WarBowItem(6.0F));
    /// 极昼：箭矢途经范围伤害，命中的敌人持续受创。无耐久。
    public static final PortDeferredItem<PolarDayBowItem> POLAR_DAY = registerItem("polar_day", "极昼", () -> new PolarDayBowItem(5.0F));

    public static PortDeferredItem<BaseTerraBowItem> register(String name, Supplier<BaseTerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    /// 注册有耐久的弓
    public static PortDeferredItem<BaseTerraBowItem> register(String name, float damage, int durability) {
        return register(name, () -> new BaseTerraBowItem(damage, new Item.Properties().durability(durability)));
    }

    private static PortDeferredItem<BaseTerraBowItem> register(String registryId, String chineseName, Supplier<BaseTerraBowItem> supplier) {
        PortDeferredItem<BaseTerraBowItem> entry = ITEMS.register(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseTerraBowItem> register(String registryId, String chineseName, float damage, int durability) {
        PortDeferredItem<BaseTerraBowItem> entry = register(registryId, () -> new BaseTerraBowItem(damage, new Item.Properties().durability(durability)));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    /// 注册有耐久、并在铁砧里可用对应材质锭修复的弓
    private static PortDeferredItem<BaseTerraBowItem> register(String registryId, String chineseName, float damage, int durability, Ingredient repair) {
        PortDeferredItem<BaseTerraBowItem> entry = register(registryId, () -> new BaseTerraBowItem(damage, new Item.Properties().durability(durability), repair));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
