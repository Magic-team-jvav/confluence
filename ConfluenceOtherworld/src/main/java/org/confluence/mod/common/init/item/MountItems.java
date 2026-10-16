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

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

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

    public static final PortDeferredItem<Item> FUZZY_CARROT = registerItem("fuzzy_carrot", "绒毛胡萝卜", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
    public static final PortDeferredItem<Item> SLIMY_SADDLE = registerItem("slimy_saddle", "粘鞍", () -> new MountItem<>(ModEntities.RIDEABLE_SLIME));
    public static final PortDeferredItem<Item> HONEYED_GOGGLES = registerItem("honeyed_goggles", "涂蜜护目镜", () -> new MountItem<>(ModEntities.RIDEABLE_BEE));
    public static final PortDeferredItem<Item> BLESSED_APPLE = registerItem("blessed_apple", "恩赐苹果", () -> new MountItem<>(ModEntities.RIDEABLE_UNICORN));
    public static final PortDeferredItem<Item> SUPERHEATED_BLOOD = registerItem("superheated_blood", "过热的血", () -> new MountItem<>(ModEntities.RIDEABLE_LAVA_SHARK));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
