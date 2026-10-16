package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

// todo
public class LightPetItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> SHADOW_ORB = registerItem("shadow_orb", "暗影珠", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<Item> CRIMSON_HEART = registerItem("crimson_heart", "猩红之心", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.BLUE));
    public static final DeferredItem<Item> MAGIC_LANTERN = registerItem("magic_lantern", "魔法灯笼", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
