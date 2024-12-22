package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.confluence.mod.common.menu.HellforgeMenu;
import org.confluence.mod.common.menu.SkyMillMenu;

import java.util.function.Supplier;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Confluence.MODID);

    public static final Supplier<MenuType<SkyMillMenu>> SKY_MILL = TYPES.register("sky_mill", () -> new MenuType<>(SkyMillMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<HeavyWorkBenchMenu>> HEAVY_WORK_BENCH = TYPES.register("heavy_work_bench", () -> new MenuType<>(HeavyWorkBenchMenu::new, FeatureFlags.VANILLA_SET));
    public static final Supplier<MenuType<HellforgeMenu>> HELLFORGE = TYPES.register("hellforge", () -> new MenuType<>(HellforgeMenu::new, FeatureFlags.VANILLA_SET));
}
