package org.confluence.terra_curio.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.menu.WorkshopMenu;

import java.util.function.Supplier;

public final class TCMenus {
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.MENU, TerraCurio.MODID);

    public static final Supplier<MenuType<WorkshopMenu>> WORKSHOP = TYPES.register("workshop", () -> new MenuType<>(WorkshopMenu::new, FeatureFlags.VANILLA_SET));
}
