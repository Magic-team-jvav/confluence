package org.confluence.mod.common.init;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFRegistries;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.confluence.terraentity.init.item.TEWhipItems;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Confluence.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NATURAL_BLOCKS = TABS.register("natural_blocks",
            () -> CreativeModeTab.builder().icon(IconItems.NATURE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.natural_blocks"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        LogBlockSet.acceptNature(output);
                        CreativeModeTab.Output finalOutput = output;
                        OreBlocks.BLOCKS.getEntries().forEach(block -> finalOutput.accept(block.get()));
                        NatureBlocks.BLOCKS.getEntries().forEach(block -> {
                            Item item = block.get().asItem();
                            if (item != Items.AIR) finalOutput.accept(item);
                        });
                        PotBlocks.BLOCKS.getEntries().forEach(block -> finalOutput.accept(block.get()));
                        output.accept(OreBlocks.COLD_CRYSTAL_ORE);
                    })
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUILDING_BLOCKS = TABS.register("building_blocks",
            () -> CreativeModeTab.builder().icon(IconItems.BLOCKS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.building_blocks"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        LogBlockSet.acceptBuilding(output);
                        CreativeModeTab.Output finalOutput = output;
                        DecorativeBlocks.BLOCKS.getEntries().forEach(block -> finalOutput.accept(block.get()));
                        StatueBlocks.BLOCKS.getEntries().forEach(block -> finalOutput.accept(block.get()));
                    })
                    .withTabsBefore(NATURAL_BLOCKS.getId())
                    .build()
    );
    /* 家具 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MECHANICAL = TABS.register("mechanical",
            () -> CreativeModeTab.builder().icon(IconItems.MECHANICAL_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.mechanical"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        output.accept(ToolItems.RED_WRENCH);
                        output.accept(ToolItems.GREEN_WRENCH);
                        output.accept(ToolItems.BLUE_WRENCH);
                        output.accept(ToolItems.YELLOW_WRENCH);
                        output.accept(ToolItems.WIRE_CUTTER);
                        CreativeModeTab.Output finalOutput = output;
                        ChestBlocks.BLOCKS.getEntries().forEach(block -> finalOutput.accept(block.get()));
                        FunctionalBlocks.BLOCKS.getEntries().forEach(block -> finalOutput.accept(block.get()));
                        output.accept(TFBlocks.GLASS_KILN);
                        output.accept(TFBlocks.LIVING_LOOM);
                        output.accept(TFBlocks.ICE_MACHINE);
                        output.accept(TFBlocks.TRASH_CAN);
                        output.accept(FunctionalBlocks.WATER_CANDLE);
                        output.accept(ModBlocks.ENEMY_BANNER);
                    })
                    .withTabsBefore(TFRegistries.FURNITURE.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS = TABS.register("materials",
            () -> CreativeModeTab.builder().icon(IconItems.MATERIAL_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.materials"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        output.accept(ConsumableItems.MANA_CRYSTAL);
                        output.accept(ConsumableItems.LIFE_CRYSTAL);
                        output.accept(ConsumableItems.LIFE_FRUIT);
                        output.accept(FoodItems.END_DRAGON_PEPPER);
                        output.accept(FoodItems.END_DRAGON_PEPPER_SEED);
                        CreativeModeTab.Output finalOutput = output;
                        MaterialItems.ITEMS.getEntries().forEach(item -> finalOutput.accept(item.get()));
                        output.accept(ModBlocks.POO);
                    }).withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "misc")).withTabsBefore(MECHANICAL.getId()).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MISC = TABS.register("misc",
            () -> CreativeModeTab.builder().icon(IconItems.PRECIOUS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.misc"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        CreativeModeTab.Output finalOutput = output;
                        Consumer<DeferredHolder<?, ?>> action = item -> finalOutput.accept((ItemLike) item.get());
                        ModItems.ITEMS.getEntries().forEach(action);
                        output.accept(TCItems.DEMON_HEART);
                        ConsumableItems.ITEMS.getEntries().forEach(action);
                        TreasureBagItems.ITEMS.getEntries().forEach(action);
                        ModBlocks.TOMBSTONES.keySet().forEach(action);
                        BaitItems.ITEMS.getEntries().forEach(action);
                        QuestedFishes.ITEMS.getEntries().forEach(action);
                        CrateBlocks.BLOCKS.getEntries().forEach(action);
                        PaintItems.ITEMS.getEntries().forEach(action);
                    })
                    .withTabsBefore(MATERIALS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_AND_POTIONS = TABS.register("food_and_potions",
            () -> CreativeModeTab.builder().icon(IconItems.POTION_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.food_and_potions"))
                    .displayItems((parameters, output) -> {
                        WipNotDisplayOutput wrappedOutput = new WipNotDisplayOutput(output);
                        PotionItems.ITEMS.getEntries().forEach(item -> wrappedOutput.accept(item.get()));
                        FoodItems.ITEMS.getEntries().forEach(item -> wrappedOutput.accept(item.get()));
                    })
                    .withTabsBefore(MISC.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS = TABS.register("tools",
            () -> CreativeModeTab.builder().icon(IconItems.TOOLS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.tools"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        CreativeModeTab.Output finalOutput = output;
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> finalOutput.accept(item.get());
                        ToolItems.ITEMS.getEntries().forEach(action);
                        output.accept(ModBlocks.ROPE);
                        output.accept(ModBlocks.VINE_ROPE);
                        output.accept(ModBlocks.SILK_ROPE);
                        output.accept(ModBlocks.WEB_ROPE);
                        output.accept(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET);
                        output.accept(TCItems.MAGIC_MIRROR);
                        output.accept(TCItems.CELL_PHONE);
                        output.accept(TCItems.DIVING_HELMET);
                        output.accept(ModItems.LIVING_WOOD_WAND);
                        output.accept(ModItems.LEAF_WAND);
                        output.accept(ModItems.LIVING_MAHOGANY_WAND);
                        output.accept(ModItems.RICH_MAHOGANY_LEAF_WAND);
                        AxeItems.ITEMS.getEntries().forEach(action);
                        PickaxeItems.ITEMS.getEntries().forEach(action);
                        PickaxeAxeItems.ITEMS.getEntries().forEach(action);
                        DrillItems.ITEMS.getEntries().forEach(action);
                        ChainsawItems.ITEMS.getEntries().forEach(action);
                        HamaxeItems.ITEMS.getEntries().forEach(action);
                        HoeShovelItems.ITEMS.getEntries().forEach(action);
                        GardenShearsItems.ITEMS.getEntries().forEach(action);
                        HammerItems.ITEMS.getEntries().forEach(action);
                        HookItems.ITEMS.getEntries().forEach(action);
                        MinecartItems.ITEMS.getEntries().forEach(action);
                        FishingPoleItems.ITEMS.getEntries().forEach(action);
                        HoeItems.ITEMS.getEntries().forEach(action);
                        ShovelItems.ITEMS.getEntries().forEach(action);
                        BoatItems.forEach(action);
                    })
                    .withTabsBefore(FOOD_AND_POTIONS.getId())
                    .build());
    /* 饰品 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMORS = TABS.register("armors",
            () -> CreativeModeTab.builder().icon(IconItems.ARMOR_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.armors"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        output.accept(TCItems.DIVING_HELMET);
                        CreativeModeTab.Output finalOutput = output;
                        ArmorItems.ITEMS.getEntries().forEach(item -> finalOutput.accept(item.get()));
                        VanityArmorItems.ITEMS.getEntries().forEach(item -> finalOutput.accept(item.get()));
                    })
                    .withTabsBefore(TCTabs.ACCESSORIES.getId()).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARRIORS = TABS.register("warriors",
            () -> CreativeModeTab.builder().icon(IconItems.MELEE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.warriors"))
                    .displayItems((parameters, output) -> {
                        WipNotDisplayOutput wrappedOutput = new WipNotDisplayOutput(output);
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> wrappedOutput.accept(item.get());
                        SwordItems.ITEMS.getEntries().forEach(action);
                        TEBoomerangItems.ITEMS.getEntries().forEach(action);
                        SpearItems.ITEMS.getEntries().forEach(action);
                        LanceItems.ITEMS.getEntries().forEach(action);
                    })
                    .withTabsBefore(ARMORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOOTERS = TABS.register("rangers",
            () -> CreativeModeTab.builder().icon(IconItems.REMOTE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.rangers"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        CreativeModeTab.Output finalOutput = output;
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> finalOutput.accept(item.get());
                        BowItems.ITEMS.getEntries().forEach(action);
                        CrossbowItems.ITEMS.getEntries().forEach(action);
                        ArrowItems.ITEMS.getEntries().forEach(action);
                        TGItems.GUNS.getEntries().forEach(action);
                        GunItems.ITEMS.getEntries().forEach(action);
                        output.accept(ManaWeaponItems.BEE_GUN);
                        output.accept(ManaWeaponItems.SPACE_GUN);
                        TGItems.BULLETS.getEntries().forEach(action);
                    })
                    .withTabsBefore(WARRIORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGES = TABS.register("mages",
            () -> CreativeModeTab.builder().icon(IconItems.MAGIC_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.mages"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        CreativeModeTab.Output finalOutput = output;
                        ManaWeaponItems.ITEMS.getEntries().forEach(item -> finalOutput.accept(item.get()));
                        HolderLookup.RegistryLookup<Enchantment> registryLookup = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.MANA_REGENERATION, 3));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.EFFICIENT_MAGIC, 1));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.MANA_MENDING, 3));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.CELESTIAL_ABSORPTION, 2));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.SOOTHED_MANA, 2));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.ARCANE_PROTECTION, 4));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.SPELL_DESPERATION, 2));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.MYSTIC_SURGE, 2));
                    })
                    .withTabsBefore(SHOOTERS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUMMONERS = TABS.register("summoners",
            () -> CreativeModeTab.builder().icon(IconItems.SUMMON_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.summoners"))
                    .displayItems((parameters, output) -> {
                        WipNotDisplayOutput wrappedOutput = new WipNotDisplayOutput(output);
                        TESummonItems.ITEMS.getEntries().forEach(item -> wrappedOutput.accept(item.get()));
                        TEWhipItems.ITEMS.getEntries().forEach(item -> wrappedOutput.accept(item.get()));
                        LightPetItems.ITEMS.getEntries().forEach(item -> wrappedOutput.accept(item.get()));
                    })
                    .withTabsAfter(TEItems.NEO_TERRA.getId())
                    .withTabsBefore(MAGES.getId())
                    .build());
    /* 生物 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEVELOPER = TABS.register("developer",
            () -> CreativeModeTab.builder().icon(IconItems.DEVELOPER_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.developer"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        output.accept(ModBlocks.ANDESITE_CASING);
                        CreativeModeTab.Output finalOutput = output;
                        ModItems.HIDDEN.getEntries().forEach(item -> finalOutput.accept(item.get()));
                        output.accept(FoodItems.PINK_COLA);
                        output.accept(FoodItems.DONGDONGS_FLATBREAD);
                        output.accept(FoodItems.PIGLIN_STEW);
                        output.accept(TEBoomerangItems.BeiDou_BOOMERANG);
                        output.accept(ToolItems.DEV_BUG_NET);
                        output.accept(SwordItems.DEVELOPER_SWORD);
                        output.accept(TEBoomerangItems.DEVELOPER_BOOMERANG);
                        output.accept(BowItems.DEVELOPER_BOW);
                        output.accept(FishingPoleItems.DEV_FISHING_ROD);
                        output.accept(ModBlocks.TEST_BLOCK);
                        output.accept(ModBlocks.AETHERIUM_CAULDRON);
                        output.accept(ModBlocks.HONEY_CAULDRON);
                    })
                    .withTabsBefore(TEItems.NEO_TERRA.getId())
                    .build());
}
