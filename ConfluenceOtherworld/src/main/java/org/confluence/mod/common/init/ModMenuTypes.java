package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.*;

import java.util.function.Supplier;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Confluence.MODID);

    public static final Supplier<MenuType<SkyMillMenu>> SKY_MILL = TYPES.register("sky_mill", () -> new MenuType<>(SkyMillMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<HeavyWorkBenchMenu>> HEAVY_WORK_BENCH = TYPES.register("heavy_work_bench", () -> new MenuType<>(HeavyWorkBenchMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<HellforgeMenu>> HELLFORGE = TYPES.register("hellforge", () -> new MenuType<>(HellforgeMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<FletchingTableMenu>> FLETCHING_TABLE = TYPES.register("fletching_table", () -> new MenuType<>(FletchingTableMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<AlchemyTableMenu>> ALCHEMY_TABLE = TYPES.register("alchemy_table", () -> new MenuType<>(AlchemyTableMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<ExtraInventoryMenu>> EXTRA_INVENTORY = TYPES.register("extra_inventory", () -> new MenuType<>(ExtraInventoryMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<CookingPotMenu>> COOKING_POT = TYPES.register("cooking_pot", () -> new MenuType<>(CookingPotMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<SawmillMenu>> SAWMILL = TYPES.register("sawmill", () -> new MenuType<>(SawmillMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<SolidifierMenu>> SOLIDIFIER = TYPES.register("solidifier", () -> new MenuType<>(SolidifierMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<CrystalBallMenu>> CRYSTAL_BALL = TYPES.register("crystal_ball", () -> new MenuType<>(CrystalBallMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<HardmodeAnvilMenu>> HARDMODE_ANVIL = TYPES.register("hardmode_anvil", () -> new MenuType<>(HardmodeAnvilMenu::new, FeatureFlags.VANILLA_SET));

    public static final Supplier<MenuType<NPCTradesForgeMenu>> NPC_TRADES_MENU = TYPES.register("npc_trades", () -> new MenuType<>(NPCTradesForgeMenu::new, FeatureFlags.VANILLA_SET));

//    public static final Supplier<MenuType<NPCTradesMenu>> MAID_TRADES_MENU = TYPES.register("maid_trades", () -> new MenuType<>((id, inv)->new NPCTradesMenu(id, inv, null, true), FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<NPCReforgeMenu>> REFORGE_MENU = TYPES.register("reforge_menu", () -> new MenuType<>(NPCReforgeMenu::new, FeatureFlags.VANILLA_SET));
}
