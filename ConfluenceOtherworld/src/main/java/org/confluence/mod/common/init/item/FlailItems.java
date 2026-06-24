package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.item.flail.AncientGuardianFlailItem;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.confluence.mod.common.item.flail.FlowerPowerItem;
import org.confluence.mod.common.item.flail.GuardianFlailItem;

/**
 * 连枷物品注册
 */
public class FlailItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    /** 参考数值为 泰拉wiki中的伤害÷2后 + 2为基础值 */
    /** 链锤 */
    public static final DeferredItem<BaseFlailItem> MACE = ITEMS.register("mace", () ->
            new BaseFlailItem(FlailComponent.MACE.get(), ModRarity.WHITE));
    /** 火焰链锤 */
    public static final DeferredItem<BaseFlailItem> FLAMING_MACE = ITEMS.register("flaming_mace", () ->
            new BaseFlailItem(FlailComponent.FLAMING_MACE.get(), ModRarity.BLUE));

    /** 风锚 */
    public static final DeferredItem<BaseFlailItem> WIND_ANCHOR  = ITEMS.register("wind_anchor", () ->
            new BaseFlailItem(FlailComponent.WIND_ANCHOR.get(), ModRarity.BLUE));

    /** 守卫链球 */
    public static final DeferredItem<GuardianFlailItem> GUARDIAN_FLAIL  = ITEMS.register("guardian_flail", () ->
            new GuardianFlailItem(FlailComponent.GUARDIAN_FLAIL.get(), ModRarity.GREEN));

    /** 远古守卫链球 */
    public static final DeferredItem<AncientGuardianFlailItem> ANCIENT_GUARDIAN_FLAIL  = ITEMS.register("ancient_guardian_flail", () ->
            new AncientGuardianFlailItem(FlailComponent.ANCIENT_GUARDIAN_FLAIL.get(), ModRarity.ORANGE));

    /** 链球 */
    public static final DeferredItem<BaseFlailItem> BALL_O_HURT = ITEMS.register("ball_o_hurt", () ->
            new BaseFlailItem(FlailComponent.BALL_O_HURT.get(), ModRarity.BLUE));

    /** 血肉之球 */
    public static final DeferredItem<BaseFlailItem> THE_MEATBALL  = ITEMS.register("the_meatball", () ->
            new BaseFlailItem(FlailComponent.THE_MEATBALL.get(), ModRarity.BLUE));

    /** 蓝月  */
    public static final DeferredItem<BaseFlailItem> BLUE_MOON   = ITEMS.register("blue_moon", () ->
            new BaseFlailItem(FlailComponent.BLUE_MOON.get(), ModRarity.GREEN));

    /** 阳炎之怒  */
    public static final DeferredItem<BaseFlailItem> SUNFURY = ITEMS.register("sunfury", () ->
            new BaseFlailItem(FlailComponent.SUNFURY.get(), ModRarity.ORANGE));

    /** 太极连枷  */
    public static final DeferredItem<BaseFlailItem> DAO_OF_POW = ITEMS.register("dao_of_pow", () ->
            new BaseFlailItem(FlailComponent.DAO_OF_POW.get(), ModRarity.PINK));

    /** 花之力 */
    public static final DeferredItem<FlowerPowerItem> FLOWER_POWER  = ITEMS.register("flower_power", () ->
            new FlowerPowerItem(FlailComponent.FLOWER_POWER.get(), ModRarity.BLUE));
}
