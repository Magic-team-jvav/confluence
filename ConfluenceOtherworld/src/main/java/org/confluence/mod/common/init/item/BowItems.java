package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
import org.confluence.mod.common.item.bow.*;

import java.util.function.Supplier;

/// 弓箭位置修正参考[ArrowInBowRenderer]
public class BowItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 短弓
    public static final DeferredItem<ShortBowItem> WOODEN_SHORT_BOW = ITEMS.register("wooden_short_bow", () -> new ShortBowItem(4.0F, 384));
    public static final DeferredItem<ShortBowItem> EBONWOOD_SHORT_BOW = ITEMS.register("ebonwood_short_bow", () -> new ShortBowItem(4.3F, 404));
    public static final DeferredItem<ShortBowItem> SHADEWOOD_SHORT_BOW = ITEMS.register("shadewood_short_bow", () -> new ShortBowItem(4.4F, 424));
    public static final DeferredItem<ShortBowItem> ASH_WOOD_SHORT_BOW = ITEMS.register("ash_wood_short_bow", () -> new ShortBowItem(4.5F, 444));
    public static final DeferredItem<ShortBowItem> PEARLWOOD_SHORT_BOW = ITEMS.register("pearlwood_short_bow", () -> new ShortBowItem(5.0F, 1000));
    public static final DeferredItem<ShortBowItem> COPPER_SHORT_BOW = ITEMS.register("copper_short_bow", () -> new ShortBowItem(4.5F, 640));
    public static final DeferredItem<ShortBowItem> TIN_SHORT_BOW = ITEMS.register("tin_short_bow", () -> new ShortBowItem(4.5F, 768));
    public static final DeferredItem<ShortBowItem> IRON_SHORT_BOW = ITEMS.register("iron_short_bow", () -> new ShortBowItem(5.0F, 896));
    public static final DeferredItem<ShortBowItem> LEAD_SHORT_BOW = ITEMS.register("lead_short_bow", () -> new ShortBowItem(5.0F, 1024));
    public static final DeferredItem<ShortBowItem> SILVER_SHORT_BOW = ITEMS.register("silver_short_bow", () -> new ShortBowItem(5.5F, 1152));
    public static final DeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = ITEMS.register("tungsten_short_bow", () -> new ShortBowItem(5.5F, 1280));
    public static final DeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = ITEMS.register("golden_short_bow", () -> new ShortBowItem(6.0F, 1408));
    public static final DeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = ITEMS.register("platinum_short_bow", () -> new ShortBowItem(6.0F, 1536));


    // 无效果蓄力弓
    public static final DeferredItem<BaseTerraBowItem> EBONWOOD_BOW = register("ebonwood_bow", 3.0F, 404);
    public static final DeferredItem<BaseTerraBowItem> SHADEWOOD_BOW = register("shadewood_bow", 3.1F, 424);
    public static final DeferredItem<BaseTerraBowItem> ASH_WOOD_BOW = register("ash_wood_bow", 3.2F, 444);
    public static final DeferredItem<BaseTerraBowItem> PEARLWOOD_BOW = register("pearlwood_bow", 3.5F, 1000);
    public static final DeferredItem<BaseTerraBowItem> COPPER_BOW = register("copper_bow", 3.0F, 640);
    public static final DeferredItem<BaseTerraBowItem> TIN_BOW = register("tin_bow", 3.0F, 768);
    public static final DeferredItem<BaseTerraBowItem> IRON_BOW = register("iron_bow", 3.5F, 896);
    public static final DeferredItem<BaseTerraBowItem> LEAD_BOW = register("lead_bow", 3.5F, 1024);
    public static final DeferredItem<BaseTerraBowItem> SILVER_BOW = register("silver_bow", 4.0F, 1152);
    public static final DeferredItem<BaseTerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", 4.0F, 1280);
    public static final DeferredItem<BaseTerraBowItem> GOLDEN_BOW = register("golden_bow", 4.5F, 1408);
    public static final DeferredItem<BaseTerraBowItem> PLATINUM_BOW = register("platinum_bow", 4.5F, 1536);

    // DIY蓄力弓
    /**
     * 如果需要速射，加上tag {@link org.confluence.mod.common.init.ModTags.Items#FAST_BOW}
     */
    public static final DeferredItem<FossilBow> FOSSIL_BOW = ITEMS.register("fossil_bow", FossilBow::new);
    public static final DeferredItem<HuntingBow> HUNTING_BOW = ITEMS.register("hunting_bow", HuntingBow::new);
    public static final DeferredItem<DemonBow> DEMON_BOW = ITEMS.register("demon_bow", DemonBow::new);
    public static final DeferredItem<TendonBow> TENDON_BOW = ITEMS.register("tendon_bow", TendonBow::new);
    public static final DeferredItem<MoltenFury> MOLTEN_FURY = ITEMS.register("molten_fury", MoltenFury::new);
    public static final DeferredItem<TheBeesKnees> THE_BEES_KNEES = ITEMS.register("the_bees_knees", TheBeesKnees::new);
    public static final DeferredItem<HellwingBow> HELLWING_BOW = ITEMS.register("hellwing_bow", HellwingBow::new);

    // 稻草人弓 - 驱离鸟妖，对飞行单位造成1.5倍伤害
    public static final DeferredItem<Scarebow> SCAREBOW = ITEMS.register("scarebow", Scarebow::new);

    // 代达罗斯风暴弓
    public static final DeferredItem<DaedalusStormbow> DAEDALUS_STORM_BOW = ITEMS.register("daedalus_storm_bow", () -> new DaedalusStormbow(12f, ModRarity.PURPLE));

    // 开发者弓
    public static final DeferredItem<DeveloperBow> DEVELOPER_BOW = ITEMS.register("developer_bow", DeveloperBow::new);


    public static DeferredItem<BaseTerraBowItem> register(String name, Supplier<BaseTerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    /// 注册有耐久的弓
    public static DeferredItem<BaseTerraBowItem> register(String name, float damage, int durability) {
        return register(name, () -> new BaseTerraBowItem(damage, new Item.Properties().durability(durability)));
    }
}
