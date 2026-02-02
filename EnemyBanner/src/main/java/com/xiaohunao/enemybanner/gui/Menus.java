package com.xiaohunao.enemybanner.gui;

import com.xiaohunao.enemybanner.EnemyBanner;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Menus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, EnemyBanner.MODID);

    public static final Supplier<MenuType<BannerBoxMenu>> BANNER_BOX_MENU =  REGISTER.register(
            "banner_box_menu",
            () -> IMenuTypeExtension.create(BannerBoxMenu::new)
    );

    public static void register(IEventBus bus){
        REGISTER.register(bus);
    }
}
