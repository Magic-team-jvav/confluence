package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHoeItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseHoeItem> COPPER_HOE = registerItem("copper_hoe", "铜锄", () -> new BaseHoeItem(ModTiers.COPPER, 1, 1f, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> TIN_HOE = registerItem("tin_hoe", "锡锄", () -> new BaseHoeItem(ModTiers.TIN, 1, 1f, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> LEAD_HOE = registerItem("lead_hoe", "铅锄", () -> new BaseHoeItem(ModTiers.LEAD, 1, 2f, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> SILVER_HOE = registerItem("silver_hoe", "银锄", () -> new BaseHoeItem(ModTiers.SILVER, 1, 3, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> TUNGSTEN_HOE = registerItem("tungsten_hoe", "钨锄", () -> new BaseHoeItem(ModTiers.TUNGSTEN, 1, 3f, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> GOLDEN_HOE = registerItem("golden_hoe", "金锄", () -> new BaseHoeItem(ModTiers.GOLD, 1, 4f, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> PLATINUM_HOE = registerItem("platinum_hoe", "铂金锄", () -> new BaseHoeItem(ModTiers.PLATINUM, 1, 4f, ModRarity.COMMON));
    public static final DeferredItem<BaseHoeItem> SHADOW_HOE = registerItem("shadow_hoe", "暗影锄", () -> new BaseHoeItem(ModTiers.DEMONITE, 2, 4f, unbreakable(), ModRarity.BLUE));
    public static final DeferredItem<BaseHoeItem> CULTIVATOR = registerItem("cultivator", "耕耘者", () -> new BaseHoeItem(ModTiers.CRIMTANE, 2, 4f, unbreakable(), ModRarity.BLUE));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
