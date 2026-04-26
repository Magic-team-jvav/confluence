package org.confluence.mod.common.init;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.palettes.DecoBlockSet;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.GroupItem;
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

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Confluence.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NATURAL_BLOCKS = TABS.register("natural_blocks",
            () -> CreativeModeTab.builder().icon(IconItems.NATURE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.natural_blocks"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        LogBlockSet.acceptNature(output);
                        acceptAll(OreBlocks.BLOCKS, output);
                        acceptAll(NatureBlocks.BLOCKS, output);
                        acceptAll(PotBlocks.BLOCKS, output, "pot");
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
                        DecoBlockSet.acceptBuilding(output);
                        acceptAll(DecorativeBlocks.BLOCKS, output);
                        acceptAll(StatueBlocks.BLOCKS, output, "statue");
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
                        acceptAll(ChestBlocks.BLOCKS, output);
                        acceptAll(FunctionalBlocks.BLOCKS, output);
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
                        acceptAll(MaterialItems.ITEMS, output);
                        output.accept(ModBlocks.POO);
                    })
                    .withTabsBefore(MECHANICAL.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MISC = TABS.register("misc",
            () -> CreativeModeTab.builder().icon(IconItems.PRECIOUS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.misc"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(ModItems.ITEMS, output);
                        output.accept(TCItems.DEMON_HEART);
                        acceptAll(ConsumableItems.ITEMS, output);
                        acceptAll(TreasureBagItems.ITEMS, output, "treasure_bag");

                        acceptAll(ModBlocks.TOMBSTONES.keySet(), GroupItem.belongsTo("tombstone", output));

                        acceptAll(BaitItems.ITEMS, output, "bait");
                        acceptAll(QuestedFishes.ITEMS, output, "quested_fish");
                        acceptAll(CrateBlocks.BLOCKS, output, "crate");
                        acceptAll(PaintItems.ITEMS, output, "paint");
                    })
                    .withTabsBefore(MATERIALS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_AND_POTIONS = TABS.register("food_and_potions",
            () -> CreativeModeTab.builder().icon(IconItems.POTION_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.food_and_potions"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        // 不要改成group，已经够清晰了
                        acceptAll(PotionItems.ITEMS, output);
                        acceptAll(FoodItems.ITEMS, output);
                    })
                    .withTabsBefore(MISC.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS = TABS.register("tools",
            () -> CreativeModeTab.builder().icon(IconItems.TOOLS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.tools"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(ToolItems.ITEMS, output);

                        CreativeModeTab.Output rope = GroupItem.belongsTo("rope", output);
                        rope.accept(ModBlocks.ROPE);
                        rope.accept(ModBlocks.VINE_ROPE);
                        rope.accept(ModBlocks.SILK_ROPE);
                        rope.accept(ModBlocks.WEB_ROPE);

                        output.accept(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET);
                        output.accept(TCItems.MAGIC_MIRROR);
                        output.accept(TCItems.CELL_PHONE);

                        CreativeModeTab.Output wand = GroupItem.belongsTo("wand", output);
                        wand.accept(ModItems.LIVING_WOOD_WAND);
                        wand.accept(ModItems.LEAF_WAND);
                        wand.accept(ModItems.LIVING_MAHOGANY_WAND);
                        wand.accept(ModItems.RICH_MAHOGANY_LEAF_WAND);

                        acceptAll(AxeItems.ITEMS, output, "axe");
                        acceptAll(PickaxeItems.ITEMS, output, "pickaxe");
                        acceptAll(PickaxeAxeItems.ITEMS, output, "pickaxe_axe");
                        acceptAll(DrillItems.ITEMS, output, "drill");
                        acceptAll(ChainsawItems.ITEMS, output, "chainsaw");
                        acceptAll(HamaxeItems.ITEMS, output, "hamaxe");
                        acceptAll(HoeShovelItems.ITEMS, output, "how_shovel");
                        acceptAll(GardenShearsItems.ITEMS, output, "garden_shears");
                        acceptAll(HammerItems.ITEMS, output, "hammer");
                        acceptAll(HookItems.ITEMS, output, "hook");
                        acceptAll(MinecartItems.ITEMS, output, "minecart");
                        acceptAll(FishingPoleItems.ITEMS, output, "fishing_pole");
                        acceptAll(HoeItems.ITEMS, output, "hoe");
                        acceptAll(ShovelItems.ITEMS, output, "shovel");
                        acceptAll(BoatItems.BOAT_ITEMS, output, "boat");
                        acceptAll(BoatItems.CHEST_BOAT_ITEMS, output, "chest_boat");
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
                        acceptAll(ArmorItems.ITEMS, output);
                        acceptAll(VanityArmorItems.ITEMS, output);
                    })
                    .withTabsBefore(TCTabs.ACCESSORIES.getId()).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARRIORS = TABS.register("warriors",
            () -> CreativeModeTab.builder().icon(IconItems.MELEE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.warriors"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(SwordItems.ITEMS, output); // todo 分类肉前矿石，肉后矿石等
                        acceptAll(TEBoomerangItems.ITEMS, output, "boomerang");
                        acceptAll(SpearItems.ITEMS, output, "spear");
                        acceptAll(LanceItems.ITEMS, output, "lance");
                    })
                    .withTabsBefore(ARMORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOOTERS = TABS.register("rangers",
            () -> CreativeModeTab.builder().icon(IconItems.REMOTE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.rangers"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(BowItems.ITEMS, output, "bow");
                        acceptAll(CrossbowItems.ITEMS, output, "crossbow");
                        acceptAll(ArrowItems.ITEMS, output, "arrow");

                        CreativeModeTab.Output gun = GroupItem.belongsTo("gun",output);
                        acceptAll(TGItems.GUNS, gun);
                        acceptAll(GunItems.ITEMS, gun);
                        gun.accept(ManaWeaponItems.BEE_GUN);
                        gun.accept(ManaWeaponItems.SPACE_GUN);
                        acceptAll(TGItems.BULLETS, output, "bullet");
                    })
                    .withTabsBefore(WARRIORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGES = TABS.register("mages",
            () -> CreativeModeTab.builder().icon(IconItems.MAGIC_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.mages"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(ManaWeaponItems.ITEMS, output);
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
                        output = new WipNotDisplayOutput(output);

                        acceptAll(TESummonItems.ITEMS, output);
                        acceptAll(TEWhipItems.ITEMS, output);
                        acceptAll(LightPetItems.ITEMS, output);
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
                        acceptAll(ModItems.HIDDEN, output);
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

    private static void acceptAll(DeferredRegister.Items register, CreativeModeTab.Output output) {
        register.getEntries().forEach(holder -> output.accept(holder.get()));
    }

    private static void acceptAll(DeferredRegister.Items register, CreativeModeTab.Output output, String group) {
        List<ItemStack> values = register.getEntries().stream()
                .map(holder -> holder.get().getDefaultInstance())
                .toList();
        output.accept(GroupItem.of(Confluence.asResource(group), values));
    }

    private static void acceptAll(DeferredRegister.Blocks register, CreativeModeTab.Output output) {
        register.getEntries().forEach(holder -> {
            Item item = holder.get().asItem();
            if (item != Items.AIR) {
                output.accept(item);
            }
        });
    }

    private static void acceptAll(DeferredRegister.Blocks register, CreativeModeTab.Output output, String group) {
        List<ItemStack> values = register.getEntries().stream()
                .filter(holder -> holder.get().asItem() != Items.AIR)
                .map(holder -> holder.get().asItem().getDefaultInstance())
                .toList();
        output.accept(GroupItem.of(Confluence.asResource(group), values));
    }

    private static <T extends ItemLike> void acceptAll(Collection<? extends DeferredHolder<T, ? extends T>> holders, CreativeModeTab.Output output) {
        holders.forEach(holder -> output.accept(holder.get()));
    }
}
