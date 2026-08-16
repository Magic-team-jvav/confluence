package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.confluence.mod.common.item.flail.DaoOfPowItem;
import org.confluence.mod.common.item.flail.IgnitingFlailItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

/// 连枷物品注册
public class FlailItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    /// 致伤球
    public static final PortDeferredItem<BaseFlailItem> MACE = ITEMS.register(
            "mace",
            () -> new BaseFlailItem(
                    FlailComponent.MACE.get(),
                    ModRarity.WHITE));

    /// 火焰链锤。
    public static final PortDeferredItem<IgnitingFlailItem> FLAMING_MACE =
            ITEMS.register(
                    "flaming_mace",
                    () -> new IgnitingFlailItem(
                            FlailComponent.FLAMING_MACE.get(),
                            ModRarity.BLUE,
                            1.0F / 6.0F));

    /// 风锚。
    public static final PortDeferredItem<BaseFlailItem> WIND_ANCHOR =
            ITEMS.register(
                    "wind_anchor",
                    () -> new BaseFlailItem(
                            FlailComponent.WIND_ANCHOR.get(),
                            ModRarity.BLUE));

    /// 守卫者链锤。
    public static final PortDeferredItem<BaseFlailItem> GUARDIAN_FLAIL =
            ITEMS.register(
                    "guardian_flail",
                    () -> new BaseFlailItem(
                            FlailComponent.GUARDIAN_FLAIL.get(),
                            ModRarity.GREEN));

    /// 远古守卫者链锤。
    public static final PortDeferredItem<BaseFlailItem> ANCIENT_GUARDIAN_FLAIL =
            ITEMS.register(
                    "ancient_guardian_flail",
                    () -> new BaseFlailItem(
                            FlailComponent.ANCIENT_GUARDIAN_FLAIL.get(),
                            ModRarity.ORANGE));

    /// 致伤球。
    public static final PortDeferredItem<BaseFlailItem> BALL_O_HURT =
            ITEMS.register(
                    "ball_o_hurt",
                    () -> new BaseFlailItem(
                            FlailComponent.BALL_O_HURT.get(),
                            ModRarity.BLUE));

    /// 血肉之球。
    public static final PortDeferredItem<BaseFlailItem> THE_MEATBALL =
            ITEMS.register(
                    "the_meatball",
                    () -> new BaseFlailItem(
                            FlailComponent.THE_MEATBALL.get(),
                            ModRarity.BLUE));

    /// 蓝月。
    public static final PortDeferredItem<BaseFlailItem> BLUE_MOON =
            ITEMS.register(
                    "blue_moon",
                    () -> new BaseFlailItem(
                            FlailComponent.BLUE_MOON.get(),
                            ModRarity.GREEN));

    /// 阳炎之怒。
    public static final PortDeferredItem<IgnitingFlailItem> SUNFURY =
            ITEMS.register(
                    "sunfury",
                    () -> new IgnitingFlailItem(
                            FlailComponent.SUNFURY.get(),
                            ModRarity.ORANGE,
                            0.25F));

    /// 太极连枷。
    public static final PortDeferredItem<DaoOfPowItem> DAO_OF_POW =
            ITEMS.register(
                    "dao_of_pow",
                    () -> new DaoOfPowItem(
                            FlailComponent.DAO_OF_POW.get(),
                            ModRarity.PINK));

    /// 花之力。
    public static final PortDeferredItem<BaseFlailItem> FLOWER_POWER =
            ITEMS.register(
                    "flower_power",
                    () -> new BaseFlailItem(
                            FlailComponent.FLOWER_POWER.get(),
                            ModRarity.BLUE));

    /// 滴滴怪致残者。
    public static final PortDeferredItem<BaseFlailItem> DRIPPLER_CRIPPLER =
            ITEMS.register(
                    "drippler_crippler",
                    () -> new BaseFlailItem(
                            FlailComponent.DRIPPLER_CRIPPLER.get(),
                            ModRarity.BLUE));

    /// 猪鲨链球。
    public static final PortDeferredItem<BaseFlailItem> FLAIRON =
            ITEMS.register(
                    "flairon",
                    () -> new BaseFlailItem(
                            FlailComponent.FLAIRON.get(),
                            ModRarity.ORANGE));

    /// 链刃。
    public static final PortDeferredItem<BaseFlailItem> CHAIN_KNIFE =
            ITEMS.register(
                    "chain_knife",
                    () -> new BaseFlailItem(
                            FlailComponent.CHAIN_KNIFE.get(),
                            ModRarity.WHITE));

    /// 锚。
    public static final PortDeferredItem<BaseFlailItem> ANCHOR =
            ITEMS.register(
                    "anchor",
                    () -> new BaseFlailItem(
                            FlailComponent.ANCHOR.get(),
                            ModRarity.WHITE));
}
