package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.confluence.mod.common.item.flail.BaseFlailItem.Parameters;
import org.confluence.mod.common.item.flail.DaoOfPowItem;
import org.confluence.mod.common.item.flail.FlaironItem;
import org.confluence.mod.common.item.flail.IgnitingFlailItem;

/// 连枷物品注册
public class FlailItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    /// 致伤球
    public static final DeferredItem<BaseFlailItem> MACE = ITEMS.register("mace", () -> new BaseFlailItem(Parameters.MACE.get(), ModRarity.WHITE));

    /// 火焰链锤。
    public static final DeferredItem<IgnitingFlailItem> FLAMING_MACE = ITEMS.register("flaming_mace", () -> new IgnitingFlailItem(Parameters.FLAMING_MACE.get(), ModRarity.BLUE, 1.0F / 6.0F));

    /// 风锚。
    public static final DeferredItem<BaseFlailItem> WIND_ANCHOR = ITEMS.register("wind_anchor", () -> new BaseFlailItem(Parameters.WIND_ANCHOR.get(), ModRarity.BLUE));

    /// 守卫者链锤。
    public static final DeferredItem<BaseFlailItem> GUARDIAN_FLAIL = ITEMS.register("guardian_flail", () -> new BaseFlailItem(Parameters.GUARDIAN_FLAIL.get(), ModRarity.GREEN));

    /// 远古守卫者链锤。
    public static final DeferredItem<BaseFlailItem> ANCIENT_GUARDIAN_FLAIL = ITEMS.register("ancient_guardian_flail", () -> new BaseFlailItem(Parameters.ANCIENT_GUARDIAN_FLAIL.get(), ModRarity.ORANGE));

    /// 致伤球。
    public static final DeferredItem<BaseFlailItem> BALL_O_HURT = ITEMS.register("ball_o_hurt", () -> new BaseFlailItem(Parameters.BALL_O_HURT.get(), ModRarity.BLUE));

    /// 血肉之球。
    public static final DeferredItem<BaseFlailItem> THE_MEATBALL = ITEMS.register("the_meatball", () -> new BaseFlailItem(Parameters.THE_MEATBALL.get(), ModRarity.BLUE));

    /// 蓝月。
    public static final DeferredItem<BaseFlailItem> BLUE_MOON = ITEMS.register("blue_moon", () -> new BaseFlailItem(Parameters.BLUE_MOON.get(), ModRarity.GREEN));

    /// 阳炎之怒。
    public static final DeferredItem<IgnitingFlailItem> SUNFURY = ITEMS.register("sunfury", () -> new IgnitingFlailItem(Parameters.SUNFURY.get(), ModRarity.ORANGE, 0.25F));

    /// 太极连枷。
    public static final DeferredItem<DaoOfPowItem> DAO_OF_POW = ITEMS.register("dao_of_pow", () -> new DaoOfPowItem(Parameters.DAO_OF_POW.get(), ModRarity.PINK));

    /// 花之力。
    public static final DeferredItem<BaseFlailItem> FLOWER_POWER = ITEMS.register("flower_power", () -> new BaseFlailItem(Parameters.FLOWER_POWER.get(), ModRarity.BLUE));

    /// 滴滴怪致残者。
    public static final DeferredItem<BaseFlailItem> DRIPPLER_CRIPPLER = ITEMS.register("drippler_crippler", () -> new BaseFlailItem(Parameters.DRIPPLER_CRIPPLER.get(), ModRarity.BLUE));

    /// 猪鲨链球。
    public static final DeferredItem<FlaironItem> FLAIRON = ITEMS.register("flairon", () -> new FlaironItem(Parameters.FLAIRON.get(), ModRarity.ORANGE));

    /// 链刃。
    public static final DeferredItem<BaseFlailItem> CHAIN_KNIFE = ITEMS.register("chain_knife", () -> new BaseFlailItem(Parameters.CHAIN_KNIFE.get(), ModRarity.WHITE));

    /// 铁链血滴子：自动挥舞，可同时维持多枚往返射弹。
    public static final DeferredItem<BaseFlailItem> CHAIN_GUILLOTINES = ITEMS.register("chain_guillotines", () -> new BaseFlailItem(Parameters.CHAIN_GUILLOTINES.get(), ModRarity.PINK));

    /// 石巨人之拳：延伸足够远后命中会产生冲击波。
    public static final DeferredItem<BaseFlailItem> GOLEM_FIST = ITEMS.register("golem_fist", () -> new BaseFlailItem(Parameters.GOLEM_FIST.get(), ModRarity.LIME));

    /// 致胜炮：拳套回收后可立即再次发射。
    public static final DeferredItem<BaseFlailItem> KO_CANNON = ITEMS.register("ko_cannon", () -> new BaseFlailItem(Parameters.KO_CANNON.get(), ModRarity.LIGHT_RED));

    /// 锚。
    public static final DeferredItem<BaseFlailItem> ANCHOR = ITEMS.register("anchor", () -> new BaseFlailItem(Parameters.ANCHOR.get(), ModRarity.WHITE));
}
