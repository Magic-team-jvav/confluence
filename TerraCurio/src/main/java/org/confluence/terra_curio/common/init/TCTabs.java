package org.confluence.terra_curio.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.terra_curio.TerraCurio;

public final class TCTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraCurio.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ACCESSORIES = TABS.register("accessories",
            () -> CreativeModeTab.builder().icon(() -> TCItems.ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.terra_curio"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        output.accept(TCItems.WORKSHOP.get());
                        output.accept(TCItems.DEMON_HEART.get());
                        output.accept(TCItems.MAGIC_MIRROR.get());
                        output.accept(TCItems.CELL_PHONE.get());
                        output.accept(TCItems.DIVING_HELMET.get());
                        CreativeModeTab.Output finalOutput = output;
                        TCItems.CURIOS.getEntries().forEach(entry -> finalOutput.accept(entry.get()));
                    })
                    .withTabsAfter(ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ConfluenceMagicLib.CONFLUENCE_ID, "armors")))
                    .withTabsBefore(
                            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ConfluenceMagicLib.CONFLUENCE_ID, "tools")),
                            CreativeModeTabs.SPAWN_EGGS
                    ).build()
    );
}
