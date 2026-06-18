package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
import org.confluence.mod.common.item.bow.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.item.PortItem;

/// 弓箭位置修正参考[ArrowInBowRenderer]
public class BowItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 短弓
    public static final PortDeferredItem<ShortBowItem> WOODEN_SHORT_BOW = ITEMS.register("wooden_short_bow", () -> new ShortBowItem(4.0F, 384));
    public static final PortDeferredItem<ShortBowItem> EBONWOOD_SHORT_BOW = ITEMS.register("ebonwood_short_bow", () -> new ShortBowItem(4.3F, 404));
    public static final PortDeferredItem<ShortBowItem> SHADEWOOD_SHORT_BOW = ITEMS.register("shadewood_short_bow", () -> new ShortBowItem(4.4F, 424));
    public static final PortDeferredItem<ShortBowItem> ASH_WOOD_SHORT_BOW = ITEMS.register("ash_wood_short_bow", () -> new ShortBowItem(4.5F, 444));
    public static final PortDeferredItem<ShortBowItem> PEARLWOOD_SHORT_BOW = ITEMS.register("pearlwood_short_bow", () -> new ShortBowItem(5.0F, 1000));
    public static final PortDeferredItem<ShortBowItem> COPPER_SHORT_BOW = ITEMS.register("copper_short_bow", () -> new ShortBowItem(4.5F, 640));
    public static final PortDeferredItem<ShortBowItem> TIN_SHORT_BOW = ITEMS.register("tin_short_bow", () -> new ShortBowItem(4.5F, 768));
    public static final PortDeferredItem<ShortBowItem> IRON_SHORT_BOW = ITEMS.register("iron_short_bow", () -> new ShortBowItem(5.0F, 896));
    public static final PortDeferredItem<ShortBowItem> LEAD_SHORT_BOW = ITEMS.register("lead_short_bow", () -> new ShortBowItem(5.0F, 1024));
    public static final PortDeferredItem<ShortBowItem> SILVER_SHORT_BOW = ITEMS.register("silver_short_bow", () -> new ShortBowItem(5.5F, 1152));
    public static final PortDeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = ITEMS.register("tungsten_short_bow", () -> new ShortBowItem(5.5F, 1280));
    public static final PortDeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = ITEMS.register("golden_short_bow", () -> new ShortBowItem(6.0F, 1408));
    public static final PortDeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = ITEMS.register("platinum_short_bow", () -> new ShortBowItem(6.0F, 1536));


    // 无效果蓄力弓
    public static final PortDeferredItem<BaseTerraBowItem> EBONWOOD_BOW = register("ebonwood_bow", 3.0F, 404);
    public static final PortDeferredItem<BaseTerraBowItem> SHADEWOOD_BOW = register("shadewood_bow", 3.1F, 424);
    public static final PortDeferredItem<BaseTerraBowItem> ASH_WOOD_BOW = register("ash_wood_bow", 3.2F, 444);
    public static final PortDeferredItem<BaseTerraBowItem> PEARLWOOD_BOW = register("pearlwood_bow", 3.5F, 1000);
    public static final PortDeferredItem<BaseTerraBowItem> COPPER_BOW = register("copper_bow", 3.0F, 640);
    public static final PortDeferredItem<BaseTerraBowItem> TIN_BOW = register("tin_bow", 3.0F, 768);
    public static final PortDeferredItem<BaseTerraBowItem> IRON_BOW = register("iron_bow", 3.5F, 896);
    public static final PortDeferredItem<BaseTerraBowItem> LEAD_BOW = register("lead_bow", 3.5F, 1024);
    public static final PortDeferredItem<BaseTerraBowItem> SILVER_BOW = register("silver_bow", 4.0F, 1152);
    public static final PortDeferredItem<BaseTerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", 4.0F, 1280);
    public static final PortDeferredItem<BaseTerraBowItem> GOLDEN_BOW = register("golden_bow", 4.5F, 1408);
    public static final PortDeferredItem<BaseTerraBowItem> PLATINUM_BOW = register("platinum_bow", 4.5F, 1536);

    // DIY蓄力弓
    /**
     * 如果需要速射，加上tag {@link org.confluence.mod.common.init.ModTags.Items#FAST_BOW}
     */
    public static final PortDeferredItem<FossilBow> FOSSIL_BOW = ITEMS.register("fossil_bow", FossilBow::new);
    public static final PortDeferredItem<HuntingBow> HUNTING_BOW = ITEMS.register("hunting_bow", HuntingBow::new);
    public static final PortDeferredItem<DemonBow> DEMON_BOW = ITEMS.register("demon_bow", DemonBow::new);
    public static final PortDeferredItem<TendonBow> TENDON_BOW = ITEMS.register("tendon_bow", TendonBow::new);
    public static final PortDeferredItem<MoltenFury> MOLTEN_FURY = ITEMS.register("molten_fury", MoltenFury::new);
    public static final PortDeferredItem<TheBeesKnees> THE_BEES_KNEES = ITEMS.register("the_bees_knees", TheBeesKnees::new);
    public static final PortDeferredItem<HellwingBow> HELLWING_BOW = ITEMS.register("hellwing_bow", HellwingBow::new);

    // 稻草人弓 - 驱离鸟妖，对飞行单位造成1.5倍伤害
    public static final PortDeferredItem<Scarebow> SCAREBOW = ITEMS.register("scarebow", Scarebow::new);

    // 代达罗斯风暴弓
    public static final PortDeferredItem<DaedalusStormbow> DAEDALUS_STORM_BOW = ITEMS.register("daedalus_storm_bow", () -> new DaedalusStormbow(12f, ModRarity.PURPLE));

    // 开发者弓
    public static final PortDeferredItem<DeveloperBow> DEVELOPER_BOW = ITEMS.register("developer_bow", DeveloperBow::new);


    public static PortDeferredItem<BaseTerraBowItem> register(String name, Supplier<BaseTerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    /// 注册有耐久的弓
    public static PortDeferredItem<BaseTerraBowItem> register(String name, float damage, int durability) {
        return register(name, () -> new BaseTerraBowItem(damage, new PortItem.PortProperties().durability(durability)));
    }
}
