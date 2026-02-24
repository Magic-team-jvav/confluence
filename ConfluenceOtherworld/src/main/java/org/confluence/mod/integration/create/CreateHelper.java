package org.confluence.mod.integration.create;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTabs;

import java.util.function.BiConsumer;

public class CreateHelper {
    public static final String MODID = "create";
    public static final boolean IS_LOADED = LibUtils.isModLoaded(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<Item> CRUSHED_RAW_TUNGSTEN = ITEMS.register("crushed_raw_tungsten", () -> new Item(new Item.Properties()));
    public static void register(IEventBus eventBus) {
        if (IS_LOADED) {
            ITEMS.register(eventBus);
            eventBus.addListener(CreateHelper::buildCreativeModeTabContents);
        }
    }
    private static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModTabs.MATERIALS.get()) {
            WipNotDisplayOutput output = new WipNotDisplayOutput(event);
            ITEMS.getEntries().forEach(item -> output.accept(item.get()));
        }
    }

    public static void addTranslateKeys(BiConsumer<DeferredHolder<Item, ? extends Item>, String> consumer, boolean en) {
        if (en) {
            ITEMS.getEntries().forEach(item -> consumer.accept(item, LibUtils.toTitleCase(item.getId().getPath())));
        } else {
            consumer.accept(CRUSHED_RAW_TUNGSTEN, "粉碎钨矿石");
        }
    }
}
