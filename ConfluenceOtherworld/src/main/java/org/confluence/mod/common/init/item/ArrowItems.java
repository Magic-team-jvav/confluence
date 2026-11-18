package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.arrow.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;


/// 以木箭MC4伤，TR5伤为起点，TR伤害每+1，MC伤害+0.2
public class ArrowItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<FlamingArrowItem> FLAMING_ARROW = registerItem("flaming_arrow", "烈焰箭", FlamingArrowItem::new);
    public static final PortDeferredItem<UnholyArrowItem> UNHOLY_ARROW = registerItem("unholy_arrow", "邪箭", UnholyArrowItem::new);
    public static final PortDeferredItem<StarArrowItem> STAR_ARROW = registerItem("star_arrow", "耀星之箭", StarArrowItem::new);
    public static final PortDeferredItem<HellfireArrowItem> HELLFIRE_ARROW = registerItem("hellfire_arrow", "狱炎箭", HellfireArrowItem::new);
    public static final PortDeferredItem<FrostburnArrowItem> FROSTBURN_ARROW = registerItem("frostburn_arrow", "霜冻箭", FrostburnArrowItem::new);
    public static final PortDeferredItem<BoneArrowItem> BONE_ARROW = registerItem("bone_arrow", "骨箭", BoneArrowItem::new);
    public static final PortDeferredItem<ShimmerArrowItem> SHIMMER_ARROW = registerItem("shimmer_arrow", "微光箭", ShimmerArrowItem::new);
    public static final PortDeferredItem<FossilArrowItem> FOSSIL_ARROW = registerItem("fossil_arrow", "化石箭", FossilArrowItem::new);
    public static final PortDeferredItem<FlyFishArrowItem> FLY_FISH_ARROW = registerItem("fly_fish_arrow", "飞鱼箭", FlyFishArrowItem::new);
    /// 风矢：更慢、更痛、能穿过一个敌怪并减速的箭矢。
    public static final PortDeferredItem<WindArrowItem> WIND_ARROW = registerItem("wind_arrow", "风矢", WindArrowItem::new);
    /// 叶绿箭：打在方块上反弹 4 次，反弹后伤害 -33%，附近有敌怪时朝敌怪反弹。
    public static final PortDeferredItem<ChlorophyteArrowItem> CHLOROPHYTE_ARROW = registerItem("chlorophyte_arrow", "叶绿箭", ChlorophyteArrowItem::new);
    /// 圣箭：击中方块/敌怪后从天空落下两颗神圣之星。
    public static final PortDeferredItem<HolyArrowItem> HOLY_ARROW = registerItem("holy_arrow", "圣箭", HolyArrowItem::new);
    /// 诅咒箭：附加 7 秒诅咒狱火。
    public static final PortDeferredItem<CursedArrowItem> CURSED_ARROW = registerItem("cursed_arrow", "诅咒箭", CursedArrowItem::new);
    /// 灵液箭：附加灵液（敌怪 10 秒、玩家 15 秒）。
    public static final PortDeferredItem<IchorArrowItem> ICHOR_ARROW = registerItem("ichor_arrow", "灵液箭", IchorArrowItem::new);
    /// 毒液箭：附加 10 秒酸性毒液与 2 秒烈性酸液。
    public static final PortDeferredItem<VenomArrowItem> VENOM_ARROW = registerItem("venom_arrow", "毒液箭", VenomArrowItem::new);
    /// 夜明箭：最多命中 4 个目标、每次 -4%，消失后放出复刻弹道的尾迹。
    public static final PortDeferredItem<LuminiteArrowItem> LUMINITE_ARROW = registerItem("luminite_arrow", "夜明箭", LuminiteArrowItem::new);

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        Confluence.addLanguageProvider(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
