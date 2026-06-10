package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseShovelItem;
import org.confluence.mod.common.item.shovel.GraveDiggersShovel;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class ShovelItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BaseShovelItem> COPPER_SHOVEL = ITEMS.register("copper_shovel", () -> new BaseShovelItem(ModTiers.COPPER, 3.5f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> TIN_SHOVEL = ITEMS.register("tin_shovel", () -> new BaseShovelItem(ModTiers.TIN, 3.7f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel", () -> new BaseShovelItem(ModTiers.LEAD, 4.0f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> SILVER_SHOVEL = ITEMS.register("silver_shovel", () -> new BaseShovelItem(ModTiers.SILVER, 4.3f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> TUNGSTEN_SHOVEL = ITEMS.register("tungsten_shovel", () -> new BaseShovelItem(ModTiers.TUNGSTEN, 4.7f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> GOLDEN_SHOVEL = ITEMS.register("golden_shovel", () -> new BaseShovelItem(ModTiers.GOLD, 5.0f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> PLATINUM_SHOVEL = ITEMS.register("platinum_shovel", () -> new BaseShovelItem(ModTiers.PLATINUM, 5.5f, 1,ModRarity.COMMON));
    public static final RegistryObject<BaseShovelItem> SHADOW_SHOVEL = ITEMS.register("shadow_shovel", () -> new BaseShovelItem(ModTiers.DEMONITE, 6f, 1,unbreakable(),ModRarity.BLUE));
    public static final RegistryObject<BaseShovelItem> MINER = ITEMS.register("miner", () -> new BaseShovelItem(ModTiers.CRIMTANE, 6.5f, 1,unbreakable(),ModRarity.BLUE));
    public static final RegistryObject<GraveDiggersShovel> GRAVE_DIGGERS_SHOVEL = ITEMS.register("grave_diggers_shovel", () -> new GraveDiggersShovel(4.5f, 1, ModRarity.GREEN));
}
