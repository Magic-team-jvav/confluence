package org.confluence.mod.common.init;

import com.xiaohunao.enemybanner.EnemyBanner;
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
            () -> CreativeModeTab.builder().icon(() -> IconItems.NATURE_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.natural_blocks"))
                    .displayItems((parameters, output) -> {
                        LogBlockSet.acceptNature(output);
                        OreBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        NatureBlocks.BLOCKS.getEntries().forEach(block -> {
                            Item item = block.get().asItem();
                            if (item != Items.AIR) output.accept(item);
                        });
                        PotBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        output.accept(OreBlocks.COLD_CRYSTAL_ORE.get());
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "building_blocks"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUILDING_BLOCKS = TABS.register("building_blocks",
            () -> CreativeModeTab.builder().icon(() -> IconItems.BLOCKS_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.building_blocks"))
                    .displayItems((parameters, output) -> {
                        LogBlockSet.acceptBuilding(output);
                        DecorativeBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        StatueBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                    })
//                    .withTabsAfter(TFRegistries.FURNITURE.getId())
                    .withTabsBefore(NATURAL_BLOCKS.getId())
                    .build()
    );
    /* 家具 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MECHANICAL = TABS.register("mechanical",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MECHANICAL_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.mechanical"))
                    .displayItems((parameters, output) -> {
                        output.accept(ToolItems.RED_WRENCH.get());
                        output.accept(ToolItems.GREEN_WRENCH.get());
                        output.accept(ToolItems.BLUE_WRENCH.get());
                        output.accept(ToolItems.YELLOW_WRENCH.get());
                        output.accept(ToolItems.WIRE_CUTTER.get());
                        ChestBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        FunctionalBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        output.accept(TFBlocks.GLASS_KILN.get());
                        output.accept(TFBlocks.LIVING_LOOM.get());
                        output.accept(TFBlocks.ICE_MACHINE.get());
                        output.accept(TFBlocks.TRASH_CAN.get());
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "materials"))
                    .withTabsBefore(TFRegistries.FURNITURE.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS = TABS.register("materials",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MATERIAL_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.materials"))
                    .displayItems((parameters, output) -> {
                        output.accept(ConsumableItems.MANA_CRYSTAL.get());
                        output.accept(ConsumableItems.LIFE_CRYSTAL.get());
                        output.accept(ConsumableItems.LIFE_FRUIT.get());
                        MaterialItems.ITEMS.getEntries().forEach(item -> output.accept(item.get().getDefaultInstance()));
                        output.accept(ModBlocks.POO.get());
                    }).withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "misc")).withTabsBefore(MECHANICAL.getId()).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MISC = TABS.register("misc",
            () -> CreativeModeTab.builder().icon(() -> IconItems.PRECIOUS_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.misc"))
                    .displayItems((parameters, output) -> {
                        Consumer<Supplier<? extends ItemLike>> action = item -> output.accept(item.get());
                        ModItems.ITEMS.getEntries().forEach(action);
                        output.accept(TCItems.DEMON_HEART.get());
                        ConsumableItems.ITEMS.getEntries().forEach(action);
                        TreasureBagItems.ITEMS.getEntries().forEach(action);
                        ModBlocks.TOMBSTONES.keySet().forEach(action);
                        BaitItems.ITEMS.getEntries().forEach(action);
                        QuestedFishes.ITEMS.getEntries().forEach(action);
                        CrateBlocks.BLOCKS.getEntries().forEach(action);
                        PaintItems.ITEMS.getEntries().forEach(action);
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "food_and_potions"))
                    .withTabsBefore(MATERIALS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_AND_POTIONS = TABS.register("food_and_potions",
            () -> CreativeModeTab.builder().icon(() -> IconItems.POTION_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.food_and_potions"))
                    .displayItems((parameters, output) -> {
                        PotionItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        FoodItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "tools"))
                    .withTabsBefore(MISC.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS = TABS.register("tools",
            () -> CreativeModeTab.builder().icon(() -> IconItems.TOOLS_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.tools"))
                    .displayItems((parameters, output) -> {
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> output.accept(item.get());
                        ToolItems.ITEMS.getEntries().forEach(action);
                        output.accept(ModBlocks.ROPE.get());
                        output.accept(ModBlocks.VINE_ROPE.get());
                        output.accept(ModBlocks.SILK_ROPE.get());
                        output.accept(ModBlocks.WEB_ROPE.get());
                        output.accept(TCItems.MAGIC_MIRROR.get());
                        output.accept(TCItems.CELL_PHONE.get());
                        output.accept(TCItems.DIVING_HELMET.get());
                        output.accept(ModItems.LIVING_WOOD_WAND.get());
                        output.accept(ModItems.LEAF_WAND.get());
                        output.accept(ModItems.LIVING_MAHOGANY_WAND.get());
                        output.accept(ModItems.RICH_MAHOGANY_LEAF_WAND.get());
                        AxeItems.ITEMS.getEntries().forEach(action);
                        PickaxeItems.ITEMS.getEntries().forEach(action);
                        PickaxeAxeItems.ITEMS.getEntries().forEach(action);
                        DrillItems.ITEMS.getEntries().forEach(action);
                        ChainsawItems.ITEMS.getEntries().forEach(action);
                        HamaxeItems.ITEMS.getEntries().forEach(action);
                        HoeShovelItems.ITEMS.getEntries().forEach(action);
                        HammerItems.ITEMS.getEntries().forEach(action);
                        HookItems.ITEMS.getEntries().forEach(action);
                        MinecartItems.ITEMS.getEntries().forEach(action);
                        FishingPoleItems.ITEMS.getEntries().forEach(action);
                        HoeItems.ITEMS.getEntries().forEach(action);
                        ShovelItems.ITEMS.getEntries().forEach(action);
                        BoatItems.forEach(action);
                    })
//                    .withTabsAfter(TCTabs.ACCESSORIES.getId())
                    .withTabsBefore(FOOD_AND_POTIONS.getId())
                    .build());
    /* 饰品 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMORS = TABS.register("armors",
            () -> CreativeModeTab.builder().icon(() -> IconItems.ARMOR_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.armors"))
                    .displayItems((parameters, output) -> {
                        output.accept(TCItems.DIVING_HELMET.get());
                        ArmorItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        VanityArmorItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "warriors"))
                    .withTabsBefore(TCTabs.ACCESSORIES.getId()).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARRIORS = TABS.register("warriors",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MELEE_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.warriors"))
                    .displayItems((parameters, output) -> {
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> output.accept(item.get());
                        SwordItems.ITEMS.getEntries().forEach(action);
                        TEBoomerangItems.ITEMS.getEntries().forEach(action);
                        LanceItems.ITEMS.getEntries().forEach(action);
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "rangers"))
                    .withTabsBefore(ARMORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOOTERS = TABS.register("rangers",
            () -> CreativeModeTab.builder().icon(() -> IconItems.REMOTE_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.rangers"))
                    .displayItems((parameters, output) -> {
                        Consumer<DeferredHolder<Item, ? extends Item>> action = item -> output.accept(item.get());
                        BowItems.ITEMS.getEntries().forEach(action);
                        ArrowItems.ITEMS.getEntries().forEach(action);
                        TGItems.GUNS.getEntries().forEach(action);
                        GunItems.ITEMS.getEntries().forEach(action);
                        output.accept(ManaWeaponItems.BEE_GUN.get());
                        output.accept(ManaWeaponItems.SPACE_GUN.get());
                        TGItems.BULLETS.getEntries().forEach(action);
                    })
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "mages"))
                    .withTabsBefore(WARRIORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGES = TABS.register("mages",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MAGIC_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.mages"))
                    .displayItems((parameters, output) -> {
                        ManaWeaponItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
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
//                    .withTabsAfter(Confluence.asResourceKey(Registries.CREATIVE_MODE_TAB, "summoners"))
                    .withTabsBefore(SHOOTERS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUMMONERS = TABS.register("summoners",
            () -> CreativeModeTab.builder().icon(() -> IconItems.SUMMON_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.summoners"))
                    .displayItems((parameters, output) -> {
                        TESummonItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        TEWhipItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        LightPetItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .withTabsAfter(TEItems.NEO_TERRA.getId())
                    .withTabsBefore(MAGES.getId())
                    .build());
    /* 生物 */
    /* 事件 */
    /* 敌怪旗 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEVELOPER = TABS.register("developer",
            () -> CreativeModeTab.builder().icon(() -> IconItems.DEVELOPER_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.developer"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.ANDESITE_CASING.get());
                        ModItems.HIDDEN.getEntries().forEach(item -> output.accept(item.get()));
                        output.accept(FoodItems.PINK_COLA.get());
                        output.accept(FoodItems.DONGDONGS_FLATBREAD.get());
                        output.accept(FoodItems.PIGLIN_STEW.get());
                        output.accept(TEBoomerangItems.BeiDou_BOOMERANG.get());
                        output.accept(ToolItems.DEV_BUG_NET.get());
                        output.accept(SwordItems.DEVELOPER_SWORD.get());
                        output.accept(TEBoomerangItems.DEVELOPER_BOOMERANG.get());
                        output.accept(BowItems.DEVELOPER_BOW.get());
                    })
                    .withTabsBefore(EnemyBanner.TAB.getId())
                    .build());
}
