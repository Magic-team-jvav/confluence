package org.confluence.terraentity.registries.chester;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;

import java.util.function.Supplier;

/**
 * 切斯特可以打开的全局的菜单
 */
public class ChesterType {

    Component name;
    Supplier<? extends MenuProvider> menuProviderSupplier;
    public ChesterType(String name, Supplier<? extends MenuProvider> menuProviderSupplier) {
        this.name = Component.translatable(name);
        this.menuProviderSupplier = menuProviderSupplier;
    }
    public ChesterType(Component name, Supplier<? extends MenuProvider> menuProviderSupplier) {
        this.name = name;
        this.menuProviderSupplier = menuProviderSupplier;
    }
    public Component getName() {
        return name;
    }

    public Supplier<? extends MenuProvider> getMenuProviderSupplier() {
        return menuProviderSupplier;
    }
}
