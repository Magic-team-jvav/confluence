package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHoeItem;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BaseHoeItem> COPPER_HOE = ITEMS.register("copper_hoe", () -> new BaseHoeItem(ModTiers.COPPER, 1, 1f, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> TIN_HOE = ITEMS.register("tin_hoe", () -> new BaseHoeItem(ModTiers.TIN, 1, 1f, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> LEAD_HOE = ITEMS.register("lead_hoe", () -> new BaseHoeItem(ModTiers.LEAD, 1, 2f, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> SILVER_HOE = ITEMS.register("silver_hoe", () -> new BaseHoeItem(ModTiers.SILVER, 1, 3, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> TUNGSTEN_HOE = ITEMS.register("tungsten_hoe", () -> new BaseHoeItem(ModTiers.TUNGSTEN, 1, 3f, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> GOLDEN_HOE = ITEMS.register("golden_hoe", () -> new BaseHoeItem(ModTiers.GOLD, 1, 4f, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> PLATINUM_HOE = ITEMS.register("platinum_hoe", () -> new BaseHoeItem(ModTiers.PLATINUM, 1, 4f, ModRarity.COMMON));
    public static final RegistryObject<BaseHoeItem> SHADOW_HOE = ITEMS.register("shadow_hoe", () -> new BaseHoeItem(ModTiers.DEMONITE, 2, 4f, unbreakable(), ModRarity.BLUE));
    public static final RegistryObject<BaseHoeItem> CULTIVATOR = ITEMS.register("cultivator", () -> new BaseHoeItem(ModTiers.CRIMTANE, 2, 4f, unbreakable(), ModRarity.BLUE));
}
