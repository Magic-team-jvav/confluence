package org.confluence.terra_guns.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_guns.TerraGuns;

public class TGTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraGuns.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GUN_TAB = TABS.register("gun_tab",
            () -> CreativeModeTab.builder().icon(TGItems.HAND_GUN::toStack)
                    .title(Component.translatable("creative_tab.terra_guns.gun_tab"))
                    .displayItems((parameters, output) -> {
                            TGItems.GUNS.getEntries().forEach(holder -> output.accept(holder.get()));
                            TGItems.BULLETS.getEntries().forEach(holder -> output.accept(holder.get()));
                    }).build()
    );
}
