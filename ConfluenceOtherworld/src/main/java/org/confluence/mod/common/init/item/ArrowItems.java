package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.arrow.*;

/// 以木箭MC4伤，TR5伤为起点，TR伤害每+1，MC伤害+0.2
public class ArrowItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<FlamingArrowItem> FLAMING_ARROW = ITEMS.register("flaming_arrow", FlamingArrowItem::new);
    public static final DeferredItem<UnholyArrowItem> UNHOLY_ARROW = ITEMS.register("unholy_arrow", UnholyArrowItem::new);
    public static final DeferredItem<StarArrowItem> STAR_ARROW = ITEMS.register("star_arrow", StarArrowItem::new);
    public static final DeferredItem<HellfireArrowItem> HELLFIRE_ARROW = ITEMS.register("hellfire_arrow", HellfireArrowItem::new);
    public static final DeferredItem<FrostburnArrowItem> FROSTBURN_ARROW = ITEMS.register("frostburn_arrow", FrostburnArrowItem::new);
    public static final DeferredItem<BoneArrowItem> BONE_ARROW = ITEMS.register("bone_arrow", BoneArrowItem::new);
    public static final DeferredItem<ShimmerArrowItem> SHIMMER_ARROW = ITEMS.register("shimmer_arrow", ShimmerArrowItem::new);
    public static final DeferredItem<FossilArrowItem> FOSSIL_ARROW = ITEMS.register("fossil_arrow", FossilArrowItem::new);
    public static final DeferredItem<FlyFishArrowItem> FLY_FISH_ARROW = ITEMS.register("fly_fish_arrow", FlyFishArrowItem::new);
}
