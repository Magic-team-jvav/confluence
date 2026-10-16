package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.mount.MountItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class MountItems {
    private MountItems() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> FUZZY_CARROT = registerItem("fuzzy_carrot", "绒毛胡萝卜", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
    public static final DeferredItem<Item> SLIMY_SADDLE = registerItem("slimy_saddle", "粘鞍", () -> new MountItem<>(ModEntities.RIDEABLE_SLIME));
    public static final DeferredItem<Item> HONEYED_GOGGLES = registerItem("honeyed_goggles", "涂蜜护目镜", () -> new MountItem<>(ModEntities.RIDEABLE_BEE));
    public static final DeferredItem<Item> BLESSED_APPLE = registerItem("blessed_apple", "恩赐苹果", () -> new MountItem<>(ModEntities.RIDEABLE_UNICORN));
    public static final DeferredItem<Item> SUPERHEATED_BLOOD = registerItem("superheated_blood", "过热的血", () -> new MountItem<>(ModEntities.RIDEABLE_LAVA_SHARK));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
