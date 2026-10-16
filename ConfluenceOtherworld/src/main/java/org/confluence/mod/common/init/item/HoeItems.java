package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHoeItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseHoeItem> COPPER_HOE = registerItem("copper_hoe", "铜锄", () -> new BaseHoeItem(ModTiers.COPPER, 1, 1f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> TIN_HOE = registerItem("tin_hoe", "锡锄", () -> new BaseHoeItem(ModTiers.TIN, 1, 1f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> LEAD_HOE = registerItem("lead_hoe", "铅锄", () -> new BaseHoeItem(ModTiers.LEAD, 1, 2f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> SILVER_HOE = registerItem("silver_hoe", "银锄", () -> new BaseHoeItem(ModTiers.SILVER, 1, 3, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> TUNGSTEN_HOE = registerItem("tungsten_hoe", "钨锄", () -> new BaseHoeItem(ModTiers.TUNGSTEN, 1, 3f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> GOLDEN_HOE = registerItem("golden_hoe", "金锄", () -> new BaseHoeItem(ModTiers.GOLD, 1, 4f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> PLATINUM_HOE = registerItem("platinum_hoe", "铂金锄", () -> new BaseHoeItem(ModTiers.PLATINUM, 1, 4f, ModRarity.COMMON));
    public static final PortDeferredItem<BaseHoeItem> SHADOW_HOE = registerItem("shadow_hoe", "暗影锄", () -> new BaseHoeItem(ModTiers.DEMONITE, 2, 4f, unbreakable(), ModRarity.BLUE));
    public static final PortDeferredItem<BaseHoeItem> CULTIVATOR = registerItem("cultivator", "耕耘者", () -> new BaseHoeItem(ModTiers.CRIMTANE, 2, 4f, unbreakable(), ModRarity.BLUE));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
