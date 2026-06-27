package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.item.flail.FlailStrategy;
import org.confluence.mod.common.item.flail.BaseFlailItem;

/**
 * 连枷物品注册
 */
public class FlailItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    /** 链锤 */
    public static final DeferredItem<BaseFlailItem> MACE = ITEMS.register("mace", () ->
            new BaseFlailItem(FlailComponent.MACE, ModRarity.WHITE));

    /** 火焰链锤 */
    public static final DeferredItem<BaseFlailItem> FLAMING_MACE = ITEMS.register("flaming_mace", () ->
            new BaseFlailItem(FlailComponent.FLAMING_MACE, ModRarity.BLUE));

    /** 风锚 */
    public static final DeferredItem<BaseFlailItem> WIND_ANCHOR = ITEMS.register("wind_anchor", () ->
            new BaseFlailItem(FlailComponent.WIND_ANCHOR, ModRarity.BLUE));

    /** 守卫链球 */
    public static final DeferredItem<BaseFlailItem> GUARDIAN_FLAIL = ITEMS.register("guardian_flail", () ->
            new BaseFlailItem(FlailComponent.GUARDIAN_FLAIL, ModRarity.GREEN,() -> 
            new FlailStrategy.GuardianAttackStrategy(false)));

    /** 远古守卫链球 */
    public static final DeferredItem<BaseFlailItem> ANCIENT_GUARDIAN_FLAIL = ITEMS.register("ancient_guardian_flail", () ->
            new BaseFlailItem(FlailComponent.ANCIENT_GUARDIAN_FLAIL, ModRarity.ORANGE,() -> 
            new FlailStrategy.GuardianAttackStrategy(true)));//三激光

    /** 链球 */
    public static final DeferredItem<BaseFlailItem> BALL_O_HURT = ITEMS.register("ball_o_hurt", () ->
            new BaseFlailItem(FlailComponent.BALL_O_HURT, ModRarity.BLUE));

    /** 血肉之球 */
    public static final DeferredItem<BaseFlailItem> THE_MEATBALL = ITEMS.register("the_meatball", () ->
            new BaseFlailItem(FlailComponent.THE_MEATBALL, ModRarity.BLUE));

    /** 蓝月 */
    public static final DeferredItem<BaseFlailItem> BLUE_MOON = ITEMS.register("blue_moon", () ->
            new BaseFlailItem(FlailComponent.BLUE_MOON, ModRarity.GREEN));

    /** 阳炎之怒 */
    public static final DeferredItem<BaseFlailItem> SUNFURY = ITEMS.register("sunfury", () ->
            new BaseFlailItem(FlailComponent.SUNFURY, ModRarity.ORANGE));

    /** 太极连枷 */
    public static final DeferredItem<BaseFlailItem> DAO_OF_POW = ITEMS.register("dao_of_pow", () ->
            new BaseFlailItem(FlailComponent.DAO_OF_POW, ModRarity.PINK));

    /** 花之力 */
    public static final DeferredItem<BaseFlailItem> FLOWER_POWER = ITEMS.register("flower_power", () ->
            new BaseFlailItem(FlailComponent.FLOWER_POWER, ModRarity.BLUE,
                    FlailStrategy.FlowerAttackStrategy::new));

    /** 滴滴怪致残者 */
    public static final DeferredItem<BaseFlailItem> DRIPPLER_CRIPPLER = ITEMS.register("drippler_crippler", () ->
            new BaseFlailItem(FlailComponent.DRIPPLER_CRIPPLER, ModRarity.BLUE,
                    FlailStrategy.DripplerCripplerAttackStrategy::new));
}
