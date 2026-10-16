package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class LightPetItems {
    /// 光宠物物品注册。
    /// 当前阶段只保留与 1.21 侧一致的物品与标签接入，实际跟随光源实体会在光宠系统统一迁移时补齐。
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> SHADOW_ORB = registerItem("shadow_orb", "暗影珠", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final PortDeferredItem<Item> CRIMSON_HEART = registerItem("crimson_heart", "猩红之心", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final PortDeferredItem<Item> MAGIC_LANTERN = registerItem("magic_lantern", "魔法灯笼", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
