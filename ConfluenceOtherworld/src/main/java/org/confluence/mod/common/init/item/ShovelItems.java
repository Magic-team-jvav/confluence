package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseShovelItem;
import org.confluence.mod.common.item.shovel.GraveDiggersShovel;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class ShovelItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseShovelItem> COPPER_SHOVEL = registerItem("copper_shovel", "铜锹", () -> new BaseShovelItem(ModTiers.COPPER, 3.5f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> TIN_SHOVEL = registerItem("tin_shovel", "锡锹", () -> new BaseShovelItem(ModTiers.TIN, 3.7f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> LEAD_SHOVEL = registerItem("lead_shovel", "铅锹", () -> new BaseShovelItem(ModTiers.LEAD, 4.0f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> SILVER_SHOVEL = registerItem("silver_shovel", "银锹", () -> new BaseShovelItem(ModTiers.SILVER, 4.3f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> TUNGSTEN_SHOVEL = registerItem("tungsten_shovel", "钨锹", () -> new BaseShovelItem(ModTiers.TUNGSTEN, 4.7f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> GOLDEN_SHOVEL = registerItem("golden_shovel", "金锹", () -> new BaseShovelItem(ModTiers.GOLD, 5.0f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> PLATINUM_SHOVEL = registerItem("platinum_shovel", "铂金锹", () -> new BaseShovelItem(ModTiers.PLATINUM, 5.5f, 1, ModRarity.COMMON));
    public static final DeferredItem<BaseShovelItem> SHADOW_SHOVEL = registerItem("shadow_shovel", "暗影锹", () -> new BaseShovelItem(ModTiers.DEMONITE, 6f, 1, unbreakable(), ModRarity.BLUE));
    public static final DeferredItem<BaseShovelItem> MINER = registerItem("miner", "采掘者", () -> new BaseShovelItem(ModTiers.CRIMTANE, 6.5f, 1, unbreakable(), ModRarity.BLUE));
    public static final DeferredItem<GraveDiggersShovel> GRAVE_DIGGERS_SHOVEL = registerItemWithEnglishName("grave_diggers_shovel", "掘墓者铲子", "Gravedigger's Shovel", () -> new GraveDiggersShovel(4.5f, 1, ModRarity.GREEN));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
