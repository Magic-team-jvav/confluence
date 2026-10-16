package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.flail.*;
import org.confluence.mod.common.item.flail.BaseFlailItem.Parameters;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

/// 连枷物品注册
public class FlailItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    /// 致伤球
    public static final PortDeferredItem<BaseFlailItem> MACE = registerItem("mace", "链锤", () -> new BaseFlailItem(Parameters.MACE.get(), ModRarity.WHITE));

    /// 火焰链锤。
    public static final PortDeferredItem<IgnitingFlailItem> FLAMING_MACE = registerItem("flaming_mace", "烈焰链锤", () -> new IgnitingFlailItem(Parameters.FLAMING_MACE.get(), ModRarity.BLUE, 1.0F / 6.0F, true));

    /// 风锚。
    public static final PortDeferredItem<WindAnchorItem> WIND_ANCHOR = registerItem("wind_anchor", "风锚", () -> new WindAnchorItem(Parameters.WIND_ANCHOR.get(), ModRarity.BLUE));

    /// 守卫者链锤。
    public static final PortDeferredItem<BaseFlailItem> GUARDIAN_FLAIL = registerItem("guardian_flail", "守卫链球", () -> new BaseFlailItem(Parameters.GUARDIAN_FLAIL.get(), ModRarity.GREEN));

    /// 远古守卫者链锤。
    public static final PortDeferredItem<BaseFlailItem> ANCIENT_GUARDIAN_FLAIL = registerItem("ancient_guardian_flail", "远古守卫链球", () -> new BaseFlailItem(Parameters.ANCIENT_GUARDIAN_FLAIL.get(), ModRarity.ORANGE));

    /// 致伤球。
    public static final PortDeferredItem<BaseFlailItem> BALL_O_HURT = registerItemWithEnglishName("ball_o_hurt", "链球", "Ball O' Hurt", () -> new BaseFlailItem(Parameters.BALL_O_HURT.get(), ModRarity.BLUE));

    /// 血肉之球。
    public static final PortDeferredItem<BaseFlailItem> THE_MEATBALL = registerItem("the_meatball", "血肉之球", () -> new BaseFlailItem(Parameters.THE_MEATBALL.get(), ModRarity.BLUE));

    /// 蓝月。
    public static final PortDeferredItem<BaseFlailItem> BLUE_MOON = registerItem("blue_moon", "蓝月", () -> new BaseFlailItem(Parameters.BLUE_MOON.get(), ModRarity.GREEN));

    /// 阳炎之怒。
    public static final PortDeferredItem<IgnitingFlailItem> SUNFURY = registerItem("sunfury", "阳炎之怒", () -> new IgnitingFlailItem(Parameters.SUNFURY.get(), ModRarity.ORANGE, 0.25F));

    /// 太极连枷。
    public static final PortDeferredItem<DaoOfPowItem> DAO_OF_POW = registerItemWithEnglishName("dao_of_pow", "太极连枷", "Dao of Pow", () -> new DaoOfPowItem(Parameters.DAO_OF_POW.get(), ModRarity.PINK));

    /// 花之力。
    public static final PortDeferredItem<BaseFlailItem> FLOWER_POWER = registerItem("flower_power", "花之力", () -> new BaseFlailItem(Parameters.FLOWER_POWER.get(), ModRarity.BLUE));

    /// 滴滴怪致残者。
    public static final PortDeferredItem<BaseFlailItem> DRIPPLER_CRIPPLER = registerItem("drippler_crippler", "滴滴怪致残者", () -> new BaseFlailItem(Parameters.DRIPPLER_CRIPPLER.get(), ModRarity.BLUE));

    /// 猪鲨链球。
    public static final PortDeferredItem<FlaironItem> FLAIRON = registerItem("flairon", "猪鲨链球", () -> new FlaironItem(Parameters.FLAIRON.get(), ModRarity.ORANGE));

    /// 链刃。
    public static final PortDeferredItem<BaseFlailItem> CHAIN_KNIFE = registerItem("chain_knife", "链刀", () -> new BaseFlailItem(Parameters.CHAIN_KNIFE.get(), ModRarity.WHITE));

    /// 铁链血滴子：自动挥舞，可同时维持多枚往返射弹。
    public static final PortDeferredItem<BaseFlailItem> CHAIN_GUILLOTINES = registerItem("chain_guillotines", "铁链血滴子", () -> new BaseFlailItem(Parameters.CHAIN_GUILLOTINES.get(), ModRarity.PINK));

    /// 石巨人之拳：延伸足够远后命中会产生冲击波。
    public static final PortDeferredItem<BaseFlailItem> GOLEM_FIST = registerItem("golem_fist", "石巨人之拳", () -> new BaseFlailItem(Parameters.GOLEM_FIST.get(), ModRarity.LIME));

    /// 致胜炮：拳套回收后可立即再次发射。
    public static final PortDeferredItem<BaseFlailItem> KO_CANNON = registerItemWithEnglishName("ko_cannon", "致胜炮", "KO Cannon", () -> new BaseFlailItem(Parameters.KO_CANNON.get(), ModRarity.LIGHT_RED));

    /// 锚。
    public static final PortDeferredItem<BaseFlailItem> ANCHOR = registerItem("anchor", "锚", () -> new BaseFlailItem(Parameters.ANCHOR.get(), ModRarity.WHITE));

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
