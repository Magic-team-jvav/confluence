package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
import org.confluence.mod.common.item.bow.*;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

/// 弓箭位置修正参考[ArrowInBowRenderer]
public class BowItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 短弓
    public static final DeferredItem<ShortBowItem> WOODEN_SHORT_BOW = registerItem("wooden_short_bow", "木短弓", () -> new ShortBowItem(4.0F, 384));
    public static final DeferredItem<ShortBowItem> EBONWOOD_SHORT_BOW = registerItem("ebonwood_short_bow", "乌木短弓", () -> new ShortBowItem(4.3F, 404));
    public static final DeferredItem<ShortBowItem> SHADEWOOD_SHORT_BOW = registerItem("shadewood_short_bow", "暗影木短弓", () -> new ShortBowItem(4.4F, 424));
    public static final DeferredItem<ShortBowItem> ASH_WOOD_SHORT_BOW = registerItem("ash_wood_short_bow", "灰烬木短弓", () -> new ShortBowItem(4.5F, 444));
    public static final DeferredItem<ShortBowItem> PEARLWOOD_SHORT_BOW = registerItem("pearlwood_short_bow", "珍珠木短弓", () -> new ShortBowItem(5.0F, 1000));
    public static final DeferredItem<ShortBowItem> COPPER_SHORT_BOW = registerItem("copper_short_bow", "铜短弓", () -> new ShortBowItem(4.5F, 640));
    public static final DeferredItem<ShortBowItem> TIN_SHORT_BOW = registerItem("tin_short_bow", "锡短弓", () -> new ShortBowItem(4.5F, 768));
    public static final DeferredItem<ShortBowItem> IRON_SHORT_BOW = registerItem("iron_short_bow", "铁短弓", () -> new ShortBowItem(5.0F, 896));
    public static final DeferredItem<ShortBowItem> LEAD_SHORT_BOW = registerItem("lead_short_bow", "铅短弓", () -> new ShortBowItem(5.0F, 1024));
    public static final DeferredItem<ShortBowItem> SILVER_SHORT_BOW = registerItem("silver_short_bow", "银短弓", () -> new ShortBowItem(5.5F, 1152));
    public static final DeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = registerItem("tungsten_short_bow", "钨短弓", () -> new ShortBowItem(5.5F, 1280));
    public static final DeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = registerItem("golden_short_bow", "金短弓", () -> new ShortBowItem(6.0F, 1408));
    public static final DeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = registerItem("platinum_short_bow", "铂金短弓", () -> new ShortBowItem(6.0F, 1536));

    // 无效果蓄力弓
    public static final DeferredItem<BaseTerraBowItem> EBONWOOD_BOW = register("ebonwood_bow", "乌木弓", 3.0F, 404);
    public static final DeferredItem<BaseTerraBowItem> SHADEWOOD_BOW = register("shadewood_bow", "暗影木弓", 3.1F, 424);
    public static final DeferredItem<BaseTerraBowItem> ASH_WOOD_BOW = register("ash_wood_bow", "灰烬木弓", 3.2F, 444);
    public static final DeferredItem<BaseTerraBowItem> PEARLWOOD_BOW = register("pearlwood_bow", "珍珠木弓", 3.5F, 1000);
    public static final DeferredItem<BaseTerraBowItem> COPPER_BOW = register("copper_bow", "铜弓", 3.0F, 640);
    public static final DeferredItem<BaseTerraBowItem> TIN_BOW = register("tin_bow", "锡弓", 3.0F, 768);
    public static final DeferredItem<BaseTerraBowItem> IRON_BOW = register("iron_bow", "铁弓", 3.5F, 896);
    public static final DeferredItem<BaseTerraBowItem> LEAD_BOW = register("lead_bow", "铅弓", 3.5F, 1024);
    public static final DeferredItem<BaseTerraBowItem> SILVER_BOW = register("silver_bow", "银弓", 4.0F, 1152);
    public static final DeferredItem<BaseTerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", "钨弓", 4.0F, 1280);
    public static final DeferredItem<BaseTerraBowItem> GOLDEN_BOW = register("golden_bow", "金弓", 4.5F, 1408);
    public static final DeferredItem<BaseTerraBowItem> PLATINUM_BOW = register("platinum_bow", "铂金弓", 4.5F, 1536);

    // DIY蓄力弓
    /**
     * 如果需要速射，加上tag {@link org.confluence.mod.common.init.ModTags.Items#FAST_BOW}
     */
    public static final DeferredItem<FossilBow> FOSSIL_BOW = registerItem("fossil_bow", "石骸弓", FossilBow::new);
    public static final DeferredItem<HuntingBow> HUNTING_BOW = registerItem("hunting_bow", "猎手弓", HuntingBow::new);
    public static final DeferredItem<DemonBow> DEMON_BOW = registerItem("demon_bow", "恶魔弓", DemonBow::new);
    public static final DeferredItem<TendonBow> TENDON_BOW = registerItem("tendon_bow", "肌腱弓", TendonBow::new);
    public static final DeferredItem<MoltenFury> MOLTEN_FURY = registerItem("molten_fury", "熔火之怒", MoltenFury::new);
    public static final DeferredItem<TheBeesKnees> THE_BEES_KNEES = registerItemWithEnglishName("the_bees_knees", "蜂膝弓", "The Bee's Knees", TheBeesKnees::new);
    public static final DeferredItem<HellwingBow> HELLWING_BOW = registerItem("hellwing_bow", "地狱之翼弓", HellwingBow::new);

    // 稻草人弓 - 驱离鸟妖，对飞行单位造成1.5倍伤害
    public static final DeferredItem<Scarebow> SCAREBOW = registerItem("scarebow", "稻草人弓", Scarebow::new);

    // 代达罗斯风暴弓
    public static final DeferredItem<DaedalusStormbow> DAEDALUS_STORM_BOW = registerItemWithEnglishName("daedalus_storm_bow", "代达罗斯风暴弓", "Daedalus Stormbow", () -> new DaedalusStormbow(12f, ModRarity.PURPLE));

    // 开发者弓
    public static final DeferredItem<DeveloperBow> DEVELOPER_BOW = registerItem("developer_bow", "开发者弓", DeveloperBow::new);

    // 魂师弓：击杀额外收魂，灵魂高于阈值时箭矢增伤（见 LivingEntityEvents）
    public static final DeferredItem<SoulShortBowItem> SOUL_SHORT_BOW = registerItem("soul_short_bow", "灵魂短弓", () -> new SoulShortBowItem(5.5F, 1024));
    public static final DeferredItem<SoulBowItem> SOUL_BOW = registerItem("soul_bow", "灵魂弓", () -> new SoulBowItem(4.0F, 1024));

    // 风与星系列弓
    /// 风弦：把木箭替换为风矢，并对具有击退抗性的敌怪额外增伤。
    public static final DeferredItem<WindStringBowItem> WIND_STRING = registerItem("wind_string", "风弦", () -> new WindStringBowItem(4.6F, 1200));
    /// 附魔弓：把木箭替换为耀星之箭。无耐久。
    public static final DeferredItem<EnchantedBowItem> ENCHANTED_BOW = registerItem("enchanted_bow", "附魔弓", () -> new EnchantedBowItem(4.5F));
    /// 星泣：未命中的箭累积成坠星，下一次命中时释放。无耐久。
    public static final DeferredItem<StarWeepBowItem> STAR_WEEP = registerItem("star_weep", "星泣", () -> new StarWeepBowItem(4.8F));
    /// 战弓：左键横扫、右键射箭。无耐久。
    public static final DeferredItem<WarBowItem> WAR_BOW = registerItem("war_bow", "战弓", () -> new WarBowItem(6.0F));
    /// 极昼：箭矢途经范围伤害，命中的敌人持续受创。无耐久。
    public static final DeferredItem<PolarDayBowItem> POLAR_DAY = registerItem("polar_day", "极昼", () -> new PolarDayBowItem(5.0F));

    public static DeferredItem<BaseTerraBowItem> register(String name, Supplier<BaseTerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    /// 注册有耐久的弓
    public static DeferredItem<BaseTerraBowItem> register(String name, float damage, int durability) {
        return register(name, () -> new BaseTerraBowItem(damage, new Item.Properties().durability(durability)));
    }

    private static DeferredItem<BaseTerraBowItem> register(String registryId, String chineseName, Supplier<BaseTerraBowItem> supplier) {
        DeferredItem<BaseTerraBowItem> entry = ITEMS.register(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseTerraBowItem> register(String registryId, String chineseName, float damage, int durability) {
        DeferredItem<BaseTerraBowItem> entry = register(registryId, () -> new BaseTerraBowItem(damage, new Item.Properties().durability(durability)));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
