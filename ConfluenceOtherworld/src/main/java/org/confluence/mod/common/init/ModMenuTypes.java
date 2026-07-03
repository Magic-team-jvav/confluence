package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.menu.*;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(Registries.MENU, Confluence.MODID);

    public static final RegistryObject<MenuType<SkyMillMenu>> SKY_MILL = TYPES.register("sky_mill", () -> new MenuType<>(SkyMillMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<HeavyWorkBenchMenu>> HEAVY_WORK_BENCH = TYPES.register("heavy_work_bench", () -> new MenuType<>(HeavyWorkBenchMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<HellforgeMenu>> HELLFORGE = TYPES.register("hellforge", () -> new MenuType<>(HellforgeMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<FletchingTableMenu>> FLETCHING_TABLE = TYPES.register("fletching_table", () -> new MenuType<>(FletchingTableMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<AlchemyTableMenu>> ALCHEMY_TABLE = TYPES.register("alchemy_table", () -> new MenuType<>(AlchemyTableMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ExtraInventoryMenu>> EXTRA_INVENTORY = TYPES.register("extra_inventory", () -> new MenuType<>(ExtraInventoryMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<CookingPotMenu>> COOKING_POT = TYPES.register("cooking_pot", () -> new MenuType<>(CookingPotMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<SawmillMenu>> SAWMILL = TYPES.register("sawmill", () -> new MenuType<>(SawmillMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<SolidifierMenu>> SOLIDIFIER = TYPES.register("solidifier", () -> new MenuType<>(SolidifierMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<CrystalBallMenu>> CRYSTAL_BALL = TYPES.register("crystal_ball", () -> new MenuType<>(CrystalBallMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<HardmodeAnvilMenu>> HARDMODE_ANVIL = TYPES.register("hardmode_anvil", () -> new MenuType<>(HardmodeAnvilMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<HardmodeForgeMenu>> HARDMODE_FORGE = TYPES.register("hardmode_forge", () -> new MenuType<>(HardmodeForgeMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<LoomMenu>> LOOM = TYPES.register("loom", () -> new MenuType<>(LoomMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<DyeVatMenu>> DYE_VAT = TYPES.register("dye_vat", () -> new MenuType<>(DyeVatMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<DyeMixMenu>> DYE_MIX = TYPES.register("dye_mix", () -> new MenuType<>(DyeMixMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<PiggyBankMenu>> PIGGY_BANK = TYPES.register("piggy_bank", () -> new MenuType<>(PiggyBankMenu::new, FeatureFlags.VANILLA_SET));

    public static final RegistryObject<MenuType<NPCReforgeMenu>> REFORGE_MENU = TYPES.register("reforge_menu", () -> new MenuType<>(NPCReforgeMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<NPCTradeMenu>> NPC_TRADE = TYPES.register("npc_trade", () -> IForgeMenuType.create(NPCTradeMenu::fromNetwork));
}
