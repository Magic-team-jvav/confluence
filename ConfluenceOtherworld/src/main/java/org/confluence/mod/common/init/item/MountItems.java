package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.mount.MountItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

/// 本体坐骑相关物品。
///
/// 普通坐骑物品只填写对应实体的延迟引用。移动速度、能力数值和环境规则
/// 直接写在对应实体类中，不在物品表重复声明。
public final class MountItems {
    private MountItems() {
    }

    /// 触发类加载，使 PortLib 收集本类的延迟注册项。
    public static void init() {
    }

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> FUZZY_CARROT = ITEMS.register("fuzzy_carrot", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
    public static final PortDeferredItem<Item> SLIMY_SADDLE = ITEMS.register("slimy_saddle", () -> new MountItem<>(ModEntities.RIDEABLE_SLIME));
    public static final PortDeferredItem<Item> HONEYED_GOGGLES = ITEMS.register("honeyed_goggles", () -> new MountItem<>(ModEntities.RIDEABLE_BEE));
}
