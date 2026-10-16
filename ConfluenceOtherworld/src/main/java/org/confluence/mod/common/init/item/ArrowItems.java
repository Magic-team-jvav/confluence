package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.arrow.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

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

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
