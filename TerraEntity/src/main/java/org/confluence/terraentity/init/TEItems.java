package org.confluence.terraentity.init;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.item.*;
import org.confluence.terraentity.item.DebugItem;
import org.confluence.terraentity.item.HouseDetectItem;
import org.confluence.terraentity.runtime.TERuntime;
import org.confluence.terraentity.utils.TEUtils;

import java.util.function.Consumer;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TEItems {
    public static final DeferredRegister.Items TOOLS = DeferredRegister.createItems(MODID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item> HOUSE_DETECTOR = TOOLS.register("house_detector", () -> new HouseDetectItem(new Item.Properties().stacksTo(1)));


    // Sentry Items
//    public static final DeferredItem<Item> SENTRY_STAFF = SENTRY_ITEMS.register("sentry_staff", () -> new SentryItem<>(new Item.Properties(), TEEntities.SUMMON_HORNET, 1, 5));
    public static final DeferredItem<Item> DEBUG_ITEM = TOOLS.register("debug_item", () -> new DebugItem(new Item.Properties().stacksTo(1)));


    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> NEO_TERRA =
            TABS.register(MODID + "_tab", ()-> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.terraentity.title"))
                    .icon(()-> TESpawnEggItems.KING_SLIME_SPAWN_EGG.asItem().getDefaultInstance())
                    .displayItems((itemDisplayParameters, output) -> {
                        WipNotDisplayOutput wrappedOutput = new WipNotDisplayOutput(output);
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> wrappedOutput.accept(item.get());
                        TESpawnEggItems.ITEMS.getEntries().forEach(action);
                        if(!ConfluenceMagicLib.IS_CONFLUENCE_LOADED.get() || TERuntime.isDevMode()) {
                            TEBossSummonsItems.ITEMS.getEntries().forEach(action);
                        }
                        TERideableItems.ITEMS.getEntries().forEach(action);
                        TEPetItems.ITEMS.getEntries().forEach(action);
                        TESummonItems.ITEMS.getEntries().forEach(action);
                        TEWhipItems.ITEMS.getEntries().forEach(action);
                        TEBoomerangItems.ITEMS.getEntries().forEach(action);
                        TEYoyosItems.ITEMS.getEntries().forEach(action);
                        TEArmors.ITEMS.getEntries().forEach(action);
                        TEItems.TOOLS.getEntries().forEach(action);
                        TEBlocks.BLOCKITEMS.getEntries().forEach(action);
                        HolderLookup.RegistryLookup<Enchantment> registryLookup = itemDisplayParameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
                        registryLookup.listElements().forEach(enchantment -> {
                            if(enchantment.getKey() != null && enchantment.getKey().location().getNamespace().equals(MODID)) {
                                wrappedOutput.accept(TEUtils.enchantedBook(enchantment.getDelegate(), enchantment.value().getMaxLevel()));
                            }
                        });
//                        output.accept(TEUtils.enchantedBook(registryLookup, TEEnchantments.MULTI_BOOMERANG, 3));
//                        output.accept(TEUtils.enchantedBook(registryLookup, TEEnchantments.WHIP_SWEEP, 1));
                    })
                    //.withTabsAfter(ResourceKey.create(Registries.CREATIVE_MODE_TAB, TerraEntity.fromSpaceAndPath("terra_moment", "tab")))
                    .withTabsAfter(ResourceKey.create(Registries.CREATIVE_MODE_TAB, TerraEntity.fromSpaceAndPath("enemybanner", "enemybanner_tab")))
                    .withTabsBefore(
                            ResourceKey.create(Registries.CREATIVE_MODE_TAB, TerraEntity.fromSpaceAndPath("confluence", "summoners")),
                            CreativeModeTabs.SPAWN_EGGS
                    ).build());

    public static void register(IEventBus bus) {
        TESpawnEggItems.ITEMS.register(bus);
        TEBossSummonsItems.ITEMS.register(bus);
        TEPetItems.ITEMS.register(bus);
        TESummonItems.ITEMS.register(bus);
        TEWhipItems.ITEMS.register(bus);
        TEBoomerangItems.ITEMS.register(bus);
        TERideableItems.ITEMS.register(bus);
        TEItems.TOOLS.register(bus);
        TEYoyosItems.ITEMS.register(bus);
        TEArmors.register(bus);
//        SENTRY_ITEMS.register(bus);
        TABS.register(bus);

    }
}
