package org.confluence.mod.common.data.gen.loot;

import com.google.common.collect.Iterables;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.herbs.BaseHerbBlock;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;

import java.util.Set;

import static org.confluence.mod.common.init.block.DecorativeBlocks.*;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.ModBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.*;
import static org.confluence.mod.common.init.block.OreBlocks.*;
import static org.confluence.mod.common.init.item.ConsumableItems.LIFE_CRYSTAL;
import static org.confluence.mod.common.init.item.MaterialItems.*;

public final class BlockSubProvider extends BlockLootSubProvider {
    public BlockSubProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        LootPoolSingletonContainer.Builder<?> emptyWeight59 = EmptyLootItem.emptyItem().setWeight(59);

        // region ore
        dropSelf(TIN_BLOCK.get());
        dropSelf(RAW_TIN_BLOCK.get());
        dropSelf(LEAD_BLOCK.get());
        dropSelf(RAW_LEAD_BLOCK.get());
        dropSelf(SILVER_BLOCK.get());
        dropSelf(RAW_SILVER_BLOCK.get());
        dropSelf(TUNGSTEN_BLOCK.get());
        dropSelf(RAW_TUNGSTEN_BLOCK.get());
        dropSelf(PLATINUM_BLOCK.get());
        dropSelf(RAW_PLATINUM_BLOCK.get());
        dropSelf(DEMONITE_BLOCK.get());
        dropSelf(RAW_DEMONITE_BLOCK.get());
        dropSelf(CRIMTANE_BLOCK.get());
        dropSelf(METEORITE_BLOCK.get());
        dropSelf(RAW_METEORITE_BLOCK.get());
        dropSelf(HELLSTONE_BLOCK.get());
        dropSelf(RAW_CRIMTANE_BLOCK.get());
        dropSelf(COBALT_BLOCK.get());
        dropSelf(RAW_COBALT_BLOCK.get());
        dropSelf(PALLADIUM_BLOCK.get());
        dropSelf(RAW_PLATINUM_BLOCK.get());
        dropSelf(MYTHRIL_BLOCK.get());
        dropSelf(RAW_MYTHRIL_BLOCK.get());
        dropSelf(ORICHALCUM_BLOCK.get());
        dropSelf(RAW_ORICHALCUM_BLOCK.get());
        dropSelf(ADAMANTITE_BLOCK.get());
        dropSelf(RAW_ADAMANTITE_BLOCK.get());
        dropSelf(TITANIUM_BLOCK.get());
        dropSelf(RAW_TITANIUM_BLOCK.get());
        dropSelf(CHLOROPHYTE_BLOCK.get());
        dropSelf(RAW_CHLOROPHYTE_BLOCK.get());
        dropSelf(LUMINITE_BLOCK.get());
        dropSelf(RAW_LUMINITE_BLOCK.get());
        dropSelf(RAW_HELLSTONE_BLOCK.get());
        dropSelf(HELLSTONE_BRICKS.get());
        dropSelf(DESERT_FOSSIL.get());
        dropSelf(SLUSH.get());
        dropSelf(SILT_BLOCK.get());
        dropSelf(MARINE_GRAVEL.get());
        dropSelf(DIATOMACEOUS.get());
        dropSelf(CLOUD_BLOCK.get());
        dropSelf(RAIN_CLOUD_BLOCK.get());
        dropSelf(SNOW_CLOUD_BLOCK.get());
        //dropSelf(FLOATING_WHEAT_BALE.get());
        dropSelf(SHADOW_SAPLING.get());
        dropSelf(EBONY_SAPLING.get());
        dropSelf(PEARL_SAPLING.get());
        dropSelf(PALM_SAPLING.get());
        dropSelf(ASH_SAPLING.get());
        dropSelf(STURDY_FOSSIL_BLOCK.get());

        dropSelf(EXTRACTINATOR.get());
        dropSelf(SKY_MILL.get());
        dropSelf(COOKING_POT.get());
        dropSelf(HEAVY_WORK_BENCH.get());
        dropSelf(HELLFORGE.get());
        dropSelf(WEATHER_VANE.get());
        dropSelf(ALCHEMY_TABLE.get());
        dropSelf(LEAD_ANVIL.get());
        dropSelf(CHIPPED_LEAD_ANVIL.get());
        dropSelf(DAMAGED_LEAD_ANVIL.get());
        dropSelf(DEEPSLATE_PRESSURE_PLATE.get());
        dropSelf(STONE_PRESSURE_PLATE.get());
        dropSelf(SIGNAL_ADAPTER.get());
        dropSelf(SWITCH.get());
        dropSelf(TIMERS_BLOCK_1_1.get());
        dropSelf(TIMERS_BLOCK_3_1.get());
        dropSelf(TIMERS_BLOCK_5_1.get());
        dropSelf(TIMERS_BLOCK_1_2.get());
        dropSelf(TIMERS_BLOCK_1_4.get());
        dropSelf(EVER_POWERED_RAIL.get());
        dropSelf(SHARPENING_STATION.get());
        dropSelf(BEWITCHING_TABLE.get());
        dropSelf(AMMO_BOX.get());
        dropSelf(SILLY_BALLOON_MACHINE.get());
        dropSelf(ECHO_BLOCK.get());
        dropSelf(JUNGLE_HIVE_BLOCK.get());
        dropSelf(INSTANTANEOUS_EXPLOSION_TNT.get());
        dropSelf(DART_TRAP.get());
        dropSelf(STONE_DART_TRAP.get());
        dropSelf(DEEPSLATE_DART_TRAP.get());
        dropSelf(PIGGY_BANK.get());
        dropSelf(SAFE.get());
        dropSelf(ANNOUNCEMENT_BOX.get());
        dropSelf(KEG.get());
        dropSelf(SOLIDIFIER.get());
        dropSelf(CAULDRON.get());
        dropSelf(TREE_HOLES_BLOCK.get());
        dropSelf(MAGIC_MAIL_BOX.get());
        dropSelf(SAWMILL.get());

        add(SANCTIFICATION_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(CORRUPTION_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(FLESHIFICATION_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(TIN_ORE.get(), this::createTinOreDrop);
        add(SANCTIFICATION_TIN_ORE.get(), this::createTinOreDrop);
        add(CORRUPTION_TIN_ORE.get(), this::createTinOreDrop);
        add(FLESHIFICATION_TIN_ORE.get(), this::createTinOreDrop);
        add(DEEPSLATE_TIN_ORE.get(), this::createTinOreDrop);
        add(SANCTIFICATION_COPPER_ORE.get(), super::createCopperOreDrops);
        add(CORRUPTION_COPPER_ORE.get(), super::createCopperOreDrops);
        add(FLESHIFICATION_COPPER_ORE.get(), super::createCopperOreDrops);
        add(LEAD_ORE.get(), block -> createOreDrop(block, RAW_LEAD.get()));
        add(SANCTIFICATION_LEAD_ORE.get(), block -> createOreDrop(block, RAW_LEAD.get()));
        add(CORRUPTION_LEAD_ORE.get(), block -> createOreDrop(block, RAW_LEAD.get()));
        add(FLESHIFICATION_LEAD_ORE.get(), block -> createOreDrop(block, RAW_LEAD.get()));
        add(DEEPSLATE_LEAD_ORE.get(), block -> createOreDrop(block, RAW_LEAD.get()));
        add(SANCTIFICATION_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(CORRUPTION_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(FLESHIFICATION_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(SILVER_ORE.get(), block -> createOreDrop(block, RAW_SILVER.get()));
        add(SANCTIFICATION_SILVER_ORE.get(), block -> createOreDrop(block, RAW_SILVER.get()));
        add(CORRUPTION_SILVER_ORE.get(), block -> createOreDrop(block, RAW_SILVER.get()));
        add(FLESHIFICATION_SILVER_ORE.get(), block -> createOreDrop(block, RAW_SILVER.get()));
        add(DEEPSLATE_SILVER_ORE.get(), block -> createOreDrop(block, RAW_SILVER.get()));
        add(TUNGSTEN_ORE.get(), block -> createOreDrop(block, RAW_TUNGSTEN.get()));
        add(SANCTIFICATION_TUNGSTEN_ORE.get(), block -> createOreDrop(block, RAW_TUNGSTEN.get()));
        add(CORRUPTION_TUNGSTEN_ORE.get(), block -> createOreDrop(block, RAW_TUNGSTEN.get()));
        add(FLESHIFICATION_TUNGSTEN_ORE.get(), block -> createOreDrop(block, RAW_TUNGSTEN.get()));
        add(DEEPSLATE_TUNGSTEN_ORE.get(), block -> createOreDrop(block, RAW_TUNGSTEN.get()));
        add(SANCTIFICATION_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(CORRUPTION_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(FLESHIFICATION_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(PLATINUM_ORE.get(), block -> createOreDrop(block, RAW_PLATINUM.get()));
        add(SANCTIFICATION_PLATINUM_ORE.get(), block -> createOreDrop(block, RAW_PLATINUM.get()));
        add(CORRUPTION_PLATINUM_ORE.get(), block -> createOreDrop(block, RAW_PLATINUM.get()));
        add(FLESHIFICATION_PLATINUM_ORE.get(), block -> createOreDrop(block, RAW_PLATINUM.get()));
        add(DEEPSLATE_PLATINUM_ORE.get(), block -> createOreDrop(block, RAW_PLATINUM.get()));
        // 红石青金石
        add(SANCTIFICATION_LAPIS_ORE.get(), super::createLapisOreDrops);
        add(CORRUPTION_LAPIS_ORE.get(), super::createLapisOreDrops);
        add(FLESHIFICATION_LAPIS_ORE.get(), super::createLapisOreDrops);
        // 宝石
        add(RUBY_ORE.get(), block -> createOreDrop(block, RUBY.get()));
        add(SANCTIFICATION_RUBY_ORE.get(), block -> createOreDrop(block, RUBY.get()));
        add(CORRUPTION_RUBY_ORE.get(), block -> createOreDrop(block, RUBY.get()));
        add(FLESHIFICATION_RUBY_ORE.get(), block -> createOreDrop(block, RUBY.get()));
        add(DEEPSLATE_RUBY_ORE.get(), block -> createOreDrop(block, RUBY.get()));
        add(AMBER_ORE.get(), block -> createOreDrop(block, AMBER.get()));
        add(SANCTIFICATION_AMBER_ORE.get(), block -> createOreDrop(block, AMBER.get()));
        add(CORRUPTION_AMBER_ORE.get(), block -> createOreDrop(block, AMBER.get()));
        add(FLESHIFICATION_AMBER_ORE.get(), block -> createOreDrop(block, AMBER.get()));
        add(RED_SAND_AMBER_ORE.get(), block -> createOreDrop(block, AMBER.get()));
        add(TOPAZ_ORE.get(), block -> createOreDrop(block, TOPAZ.get()));
        add(SANCTIFICATION_TOPAZ_ORE.get(), block -> createOreDrop(block, TOPAZ.get()));
        add(CORRUPTION_TOPAZ_ORE.get(), block -> createOreDrop(block, TOPAZ.get()));
        add(FLESHIFICATION_TOPAZ_ORE.get(), block -> createOreDrop(block, TOPAZ.get()));
        add(DEEPSLATE_TOPAZ_ORE.get(), block -> createOreDrop(block, TOPAZ.get()));
        add(JADE_ORE.get(), block -> createOreDrop(block, JADE.get()));
//            add(SANCTIFICATION_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
//            add(CORRUPTION_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
//            add(FLESHIFICATION_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
        add(DEEPSLATE_JADE_ORE.get(), block -> createOreDrop(block, JADE.get()));
        add(SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
        add(SANCTIFICATION_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
        add(CORRUPTION_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
        add(FLESHIFICATION_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
        add(DEEPSLATE_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
        add(AMETHYST_ORE.get(), block -> createOreDrop(block, AMETHYST.get()));
        add(SANCTIFICATION_AMETHYST_ORE.get(), block -> createOreDrop(block, AMETHYST.get()));
        add(CORRUPTION_AMETHYST_ORE.get(), block -> createOreDrop(block, AMETHYST.get()));
        add(FLESHIFICATION_AMETHYST_ORE.get(), block -> createOreDrop(block, AMETHYST.get()));
        add(DEEPSLATE_AMETHYST_ORE.get(), block -> createOreDrop(block, AMETHYST.get()));
        add(SANCTIFICATION_EMERALD_ORE.get(), block -> createOreDrop(block, Items.EMERALD));
        add(CORRUPTION_EMERALD_ORE.get(), block -> createOreDrop(block, Items.EMERALD));
        add(FLESHIFICATION_EMERALD_ORE.get(), block -> createOreDrop(block, Items.EMERALD));
        add(SANCTIFICATION_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));
        add(CORRUPTION_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));
        add(FLESHIFICATION_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));

        add(METEORITE_ORE.get(), block -> createOreDrop(block, RAW_METEORITE.get()));
        add(DEMONITE_ORE.get(), block -> createOreDrop(block, RAW_DEMONITE.get()));
        add(DEEPSLATE_DEMONITE_ORE.get(), block -> createOreDrop(block, RAW_DEMONITE.get()));
        add(SANCTIFICATION_DEMONITE_ORE.get(), block -> createOreDrop(block, RAW_DEMONITE.get()));
        add(CORRUPTION_DEMONITE_ORE.get(), block -> createOreDrop(block, RAW_DEMONITE.get()));
        add(FLESHIFICATION_DEMONITE_ORE.get(), block -> createOreDrop(block, RAW_DEMONITE.get()));
        add(CRIMTANE_ORE.get(), block -> createOreDrop(block, RAW_CRIMTANE.get()));
        add(DEEPSLATE_CRIMTANE_ORE.get(), block -> createOreDrop(block, RAW_CRIMTANE.get()));
        add(DEEPSLATE_COBALT_ORE.get(), block -> createOreDrop(block, RAW_COBALT.get()));
        add(DEEPSLATE_PALLADIUM_ORE.get(), block -> createOreDrop(block, RAW_PALLADIUM.get()));
        add(DEEPSLATE_MYTHRIL_ORE.get(), block -> createOreDrop(block, RAW_MYTHRIL.get()));
        add(DEEPSLATE_ORICHALCUM_ORE.get(), block -> createOreDrop(block, RAW_ORICHALCUM.get()));
        add(DEEPSLATE_ADAMANTITE_ORE.get(), block -> createOreDrop(block, RAW_ADAMANTITE.get()));
        add(DEEPSLATE_TITANIUM_ORE.get(), block -> createOreDrop(block, RAW_TITANIUM.get()));
        add(CHLOROPHYTE_ORE.get(), block -> createOreDrop(block, RAW_CHLOROPHYTE.get()));
        add(HELLSTONE.get(), block -> createOreDrop(block, RAW_HELLSTONE.get()));
        add(ASH_HELLSTONE.get(), block -> createOreDrop(block, RAW_HELLSTONE.get()));

        add(COLD_CRYSTAL_ORE.get(), block -> createOreDrop(block, COLD_CRYSTAL.get()));
        add(GELSTONE_ORE.get(), block -> createOreDrop(block, GELSTONE.get()));
        // endregion ore

        // region natural
        dropSelf(COBBLED_EBONSTONE.get());
        dropSelf(EBONSAND.get());
        dropSelf(COBBLED_PEARLSTONE.get());
        dropSelf(PEARLSAND.get());
        dropSelf(COBBLED_CRIMSTONE.get());
        dropSelf(CRIMSAND.get());
        dropSelf(ASH_BLOCK.get());
        this.add(CRIMSTONE.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, COBBLED_CRIMSTONE));
        this.add(EBONSTONE.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, COBBLED_EBONSTONE));
        this.add(PEARLSTONE.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, COBBLED_PEARLSTONE));
        this.add(ASH_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, ASH_BLOCK));
        this.add(CORRUPT_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.DIRT));
        this.add(CORRUPT_JUNGLE_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        this.add(JUNGLE_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        this.add(CRIMSON_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.DIRT));
        this.add(CRIMSON_JUNGLE_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        this.add(MUSHROOM_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        this.add(HALLOW_GRASS_BLOCK.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.DIRT));

        dropSelf(EBONY_LOG_BLOCKS.getLog().get());
        dropSelf(YELLOW_WILLOW_LOG_BLOCKS.getLog().get());
        dropSelf(BAOBAB_LOG_BLOCKS.getLog().get());
        dropSelf(LIVING_LOG_BLOCKS.getLog().get());
        dropSelf(LIVING_MAHOGANY_BLOCKS.getLog().get());
        dropSelf(SHADOW_LOG_BLOCKS.getLog().get());
        dropSelf(PEARL_LOG_BLOCKS.getLog().get());
        dropSelf(PALM_LOG_BLOCKS.getLog().get());
        dropSelf(ASH_LOG_BLOCKS.getLog().get());
//            dropSelf(LIFE_PLANKS.get());
//            dropSelf(LIFE_LOG.get());
        dropSelf(STONY_LOG.get());


        dropSelf(CRISPY_HONEY_BLOCK.get());
        dropSelf(THIN_HONEY_BLOCK.get());
        dropSelf(LOOSE_HONEY_BLOCK.get());
        dropSelf(HONEY_CAULDRON.get());
        dropSelf(AETHERIUM_CAULDRON.get());
        dropSelf(CURSED_FLAME_BLOCK.get());
        dropSelf(POO.get());

        dropSelf(ROPE.get());
        dropSelf(SILK_ROPE.get());
        dropSelf(WEB_ROPE.get());
        dropSelf(VINE_ROPE.get());

        dropSelf(TOMBSTONE.get());
        dropSelf(GRAVE_MARKER.get());
        dropSelf(CROSS_GRAVE_MARKER.get());
        dropSelf(HEADSTONE.get());
        dropSelf(GRAVESTONE.get());
        dropSelf(OBELISK.get());
        dropSelf(GOLDEN_TOMBSTONE.get());
        dropSelf(GOLDEN_GRAVE_MARKER.get());
        dropSelf(GOLDEN_CROSS_GRAVE_MARKER.get());
        dropSelf(GOLDEN_HEADSTONE.get());
        dropSelf(GOLDEN_GRAVESTONE.get());

        dropOther(NATURES_GIFT.get(), AccessoryItems.NATURES_GIFT.get());
        add(JUNGLE_ROSE.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JUNGLE_ROSE.get()).when(LootItemRandomChanceCondition.randomChance(0.05f)))));
        // endregion natural

        dropSelf(RUBY_BLOCK.get());
        dropSelf(AMBER_BLOCK.get());
        dropSelf(TOPAZ_BLOCK.get());
        dropSelf(SAPPHIRE_BLOCK.get());
        dropSelf(AMETHYST_BLOCK.get());
        dropSelf(JADE_BLOCK.get());

        // decorative block
        dropSelf(CHISELED_OAK_PLANKS.get());
        dropSelf(CHISELED_SPRUCE_PLANKS.get());
        dropSelf(CHISELED_EBONY_PLANKS.get());
        dropSelf(CHISELED_SHADOW_PLANKS.get());
        dropSelf(CHISELED_PEARL_PLANKS.get());
        dropSelf(CHISELED_PALM_PLANKS.get());
        dropSelf(CHISELED_BAOBAB_PLANKS.get());
        dropSelf(CHISELED_YELLOW_WILLOW_PLANKS.get());
        dropSelf(CHISELED_LIVING_PLANKS.get());
        dropSelf(CHISELED_ASH_PLANKS.get());
        dropSelf(WOOD_STONE_SLATTED_BLOCKS.get());
        dropSelf(BLUE_ICE_BRICKS.get());
        dropSelf(BLUE_ICE_BRICKS_STAIRS.get());
        dropSelf(BLUE_ICE_BRICKS_SLAB.get());
        dropSelf(PACKED_ICE_BRICKS.get());
        dropSelf(PACKED_ICE_BRICKS_STAIRS.get());
        dropSelf(PACKED_ICE_BRICKS_SLAB.get());
        dropSelf(SNOW_BRICKS.get());
        dropSelf(SNOW_BRICKS_STAIRS.get());
        dropSelf(SNOW_BRICKS_SLAB.get());
        dropSelf(COPPER_BRICKS.get());
        dropSelf(COPPER_BRICKS_STAIRS.get());
        dropSelf(COPPER_BRICKS_SLAB.get());
        dropSelf(TIN_BRICKS.get());
        dropSelf(TIN_BRICKS_STAIRS.get());
        dropSelf(TIN_BRICKS_SLAB.get());
        dropSelf(IRON_BRICKS.get());
        dropSelf(IRON_BRICKS_STAIRS.get());
        dropSelf(IRON_BRICKS_SLAB.get());
        dropSelf(LEAD_BRICKS.get());
        dropSelf(LEAD_BRICKS_STAIRS.get());
        dropSelf(LEAD_BRICKS_SLAB.get());
        dropSelf(SILVER_BRICKS.get());
        dropSelf(SILVER_BRICKS_STAIRS.get());
        dropSelf(SILVER_BRICKS_SLAB.get());
        dropSelf(TUNGSTEN_BRICKS.get());
        dropSelf(TUNGSTEN_BRICKS_STAIRS.get());
        dropSelf(TUNGSTEN_BRICKS_SLAB.get());
        dropSelf(GOLDEN_BRICKS.get());
        dropSelf(GOLDEN_BRICKS_STAIRS.get());
        dropSelf(GOLDEN_BRICKS_SLAB.get());
        dropSelf(PLATINUM_BRICKS.get());
        dropSelf(PLATINUM_BRICKS_STAIRS.get());
        dropSelf(PLATINUM_BRICKS_SLAB.get());
        dropSelf(DEMONITE_ORE_BRICKS.get());
        dropSelf(DEMONITE_ORE_BRICKS_STAIRS.get());
        dropSelf(DEMONITE_ORE_BRICKS_SLAB.get());
        dropSelf(EBONSTONE_BRICKS.get());
        dropSelf(EBONSTONE_BRICKS_STAIRS.get());
        dropSelf(EBONSTONE_BRICKS_SLAB.get());
        dropSelf(METEORITE_BRICKS.get());
        dropSelf(METEORITE_BRICKS_STAIRS.get());
        dropSelf(METEORITE_BRICKS_SLAB.get());
        dropSelf(CRIMTANE_ORE_BRICKS.get());
        dropSelf(CRIMTANE_ORE_BRICKS_STAIRS.get());
        dropSelf(CRIMTANE_ORE_BRICKS_SLAB.get());
        dropSelf(CRIMSTONE_BRICKS.get());
        dropSelf(CRIMSTONE_BRICKS_STAIRS.get());
        dropSelf(CRIMSTONE_BRICKS_SLAB.get());
        dropSelf(PEARLSTONE_BRICKS.get());
        dropSelf(PEARLSTONE_BRICKS_STAIRS.get());
        dropSelf(PEARLSTONE_BRICKS_SLAB.get());
        dropSelf(GREEN_CANDY_BLOCK.get());
        dropSelf(RED_CANDY_BLOCK.get());
        dropSelf(FROZEN_GEL_BLOCK.get());
        dropSelf(BLUE_GEL_BLOCK.get());
        dropSelf(PINK_GEL_BLOCK.get());
        dropSelf(SUN_PLATE.get());
        dropSelf(SUN_PLATE_SLAB.get());
        dropSelf(SUN_PLATE_STAIRS.get());
        dropSelf(DISC_BLOCK.get());
        dropSelf(OBSIDIAN_BRICKS.get());
        dropSelf(OBSIDIAN_BRICKS_SLAB.get());
        dropSelf(OBSIDIAN_BRICKS_STAIRS.get());

        dropSelf(OBSIDIAN_SMALL_BRICKS.get());
        dropSelf(SMOOTH_OBSIDIAN.get());
        dropSelf(POLISHED_GRANITE.get());
        dropSelf(GRANITE_COLUMN.get());

        dropSelf(CHISELED_OBSIDIAN_BRICKS.get());
        dropSelf(BLUE_BRICKS.get());
        dropSelf(GREEN_BRICKS.get());
        dropSelf(PINK_BRICKS.get());
        dropSelf(BLUE_BRICK_STAIRS.get());
        dropSelf(GREEN_BRICK_STAIRS.get());
        dropSelf(PINK_BRICK_STAIRS.get());
        dropSelf(BLUE_BRICK_SLAB.get());
        dropSelf(GREEN_BRICK_SLAB.get());
        dropSelf(PINK_BRICK_SLAB.get());
        dropSelf(CHISELED_BLUE_BRICKS.get());
        dropSelf(CHISELED_GREEN_BRICKS.get());
        dropSelf(CHISELED_PINK_BRICKS.get());
        dropSelf(AETHERIUM_BRICKS.get());
        dropSelf(CRYSTAL_BLOCK.get());
        dropSelf(RAINBOW_BRICKS.get());
        dropSelf(FLOATING_WHEAT_BALE.get());
        dropSelf(BOUNCY_CLOUD_BLOCK.get());

        dropSelf(HARDENED_SAND_BLOCK.get());
        dropSelf(MOISTENED_SAND_BLOCK.get());
        dropSelf(HARDENED_RED_SAND_BLOCK.get());
        dropSelf(MOISTENED_RED_SAND_BLOCK.get());
        dropSelf(HARDENED_EBONSAND_BLOCK.get());
        dropSelf(MOISTENED_EBONSAND_BLOCK.get());
        dropSelf(HARDENED_PEARLSAND_BLOCK.get());
        dropSelf(MOISTENED_PEARLSAND_BLOCK.get());
        dropSelf(HARDENED_CRIMSAND_BLOCK.get());
        dropSelf(MOISTENED_CRIMSAND_BLOCK.get());

        dropSelf(GRANITE.get());

        dropSelf(AETHERIUM_BLOCK.get());
        dropSelf(DARK_AETHERIUM_BLOCK.get());

        dropSelf(JUNGLE_ROSE.get());

        dropOther(LIFE_CRYSTAL_BLOCK.get(), LIFE_CRYSTAL.get());

        dropWhenSilkTouch(PURE_GLASS.get());
        dropWhenSilkTouch(WHITE_PURE_GLASS.get());
        dropWhenSilkTouch(LIGHT_GRAY_PURE_GLASS.get());
        dropWhenSilkTouch(GRAY_PURE_GLASS.get());
        dropWhenSilkTouch(BLACK_PURE_GLASS.get());
        dropWhenSilkTouch(BROWN_PURE_GLASS.get());
        dropWhenSilkTouch(RED_PURE_GLASS.get());
        dropWhenSilkTouch(ORANGE_PURE_GLASS.get());
        dropWhenSilkTouch(YELLOW_PURE_GLASS.get());
        dropWhenSilkTouch(LIME_PURE_GLASS.get());
        dropWhenSilkTouch(GREEN_PURE_GLASS.get());
        dropWhenSilkTouch(CYAN_PURE_GLASS.get());
        dropWhenSilkTouch(LIGHT_BLUE_PURE_GLASS.get());
        dropWhenSilkTouch(BLUE_PURE_GLASS.get());
        dropWhenSilkTouch(PURPLE_PURE_GLASS.get());
        dropWhenSilkTouch(MAGENTA_PURE_GLASS.get());
        dropWhenSilkTouch(PINK_PURE_GLASS.get());


        //chain
        dropSelf(RUBY_CHAIN.get());
        dropSelf(AMBER_CHAIN.get());
        dropSelf(TOPAZ_CHAIN.get());
        dropSelf(JADE_CHAIN.get());
        dropSelf(SAPPHIRE_CHAIN.get());
        dropSelf(DIAMOND_CHAIN.get());
        dropSelf(AMETHYST_CHAIN.get());
        // 片
        dropWhenSilkTouch(SAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(RED_SAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(EBONSAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(CRIMSAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(PEARLSAND_LAYER_BLOCK.get());

        // 径
        this.add(ASH_PATH.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, ASH_BLOCK));
        this.add(JUNGLE_PATH.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        this.add(MUSHROOM_PATH.get(), p_251015_ -> this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));

        //door
        this.add(OBSIDIAN_BRICKS_DOOR.get(), this::createDoorTable);
        this.add(SKYWARE_DOOR.get(), this::createDoorTable);
        this.add(SKYWARE_GLASS_DOOR.get(), this::createDoorTable);
        this.add(DUNGEON_DOOR.get(), this::createDoorTable);

        // 发光蘑菇
        this.add(GLOWING_MUSHROOM_INDUSIUM_BLOCK.get(), p_249169_ -> this.createMushroomBlockDrop(p_249169_, MaterialItems.GLOWING_MUSHROOM));
        this.add(GLOWING_MUSHROOM_VINE.get(), p_249169_ -> this.createMushroomBlockDrop(p_249169_, MaterialItems.GLOWING_MUSHROOM));
        this.add(GLOWING_MUSHROOM_CATTAILS_HEAD.get(), p_249169_ -> this.createMushroomBlockDrop(p_249169_, MaterialItems.GLOWING_MUSHROOM));
        this.add(GLOWING_MUSHROOM_PILEUS_BLOCK.get(), p_249169_ -> this.createMushroomBlockDrop(p_249169_, MaterialItems.GLOWING_MUSHROOM));
        dropSelf(GLOWING_MUSHROOM_STEM_BLOCK.get());
        dropWhenSilkTouch(GLOWING_MUSHROOM_CATTAILS_BODY.get());

        this.add(LIFE_MUSHROOM_INDUSIUM_BLOCK.get(), p_249169_ -> this.createMushroomBlockDrop(p_249169_, MaterialItems.LIFE_MUSHROOM));
        this.add(LIFE_MUSHROOM_PILEUS_BLOCK.get(), p_249169_ -> this.createMushroomBlockDrop(p_249169_, MaterialItems.LIFE_MUSHROOM));
        dropSelf(LIFE_MUSHROOM_STEM_BLOCK.get());


        for (LogBlockSet logBlocks : LogBlockSet.LOG_BLOCK_SETS) {
            dropSelf(logBlocks.getPlanks().get());
            if (logBlocks.getStrippedLog() != null) dropSelf(logBlocks.getStrippedLog().get());
            if (logBlocks.getWood() != null) dropSelf(logBlocks.getWood().get());
            if (logBlocks.getStrippedWood() != null) dropSelf(logBlocks.getStrippedWood().get());
            if (logBlocks.getButton() != null) dropSelf(logBlocks.getButton().get());
            if (logBlocks.getFence() != null) dropSelf(logBlocks.getFence().get());
            if (logBlocks.getFenceGate() != null) dropSelf(logBlocks.getFenceGate().get());
            if (logBlocks.getPressurePlate() != null) dropSelf(logBlocks.getPressurePlate().get());
            if (logBlocks.getSlab() != null) add(logBlocks.getSlab().get(), this::createSlabItemTable);
            if (logBlocks.getStairs() != null) dropSelf(logBlocks.getStairs().get());
            if (logBlocks.getSign() != null) dropSelf(logBlocks.getSign().get());
            if (logBlocks.getTrapdoor() != null) dropSelf(logBlocks.getTrapdoor().get());
            if (logBlocks.getDoor() != null) add(logBlocks.getDoor().get(), this::createDoorTable);
        }

        CrateBlocks.BLOCKS.getEntries().forEach(block -> dropSelf(block.get()));
        // 草药
        addHerbDrop(ModBlocks.WATERLEAF.get(), MaterialItems.WATERLEAF.get(), FoodItems.WATERLEAF_SEED.get());
        addHerbDrop(ModBlocks.FIREBLOSSOM.get(), MaterialItems.FIREBLOSSOM.get(), FoodItems.FIREBLOSSOM_SEED.get());
        addHerbDrop(ModBlocks.MOONGLOW.get(), MaterialItems.MOONGLOW.get(), FoodItems.MOONGLOW_SEED.get());
        addHerbDrop(ModBlocks.BLINKROOT.get(), MaterialItems.BLINKROOT.get(), FoodItems.BLINKROOT_SEED.get());
        addHerbDrop(ModBlocks.SHIVERTHORN.get(), MaterialItems.SHIVERTHORN.get(), FoodItems.SHIVERTHORN_SEED.get());
        addHerbDrop(ModBlocks.DAYBLOOM.get(), MaterialItems.DAYBLOOM.get(), FoodItems.DAYBLOOM_SEED.get());
        addHerbDrop(ModBlocks.DEATHWEED.get(), MaterialItems.DEATHWEED.get(), FoodItems.DEATHWEED_SEED.get());

        dropOther(NatureBlocks.VICIOUS_MUSHROOM.get(), MaterialItems.VICIOUS_MUSHROOM.get());
        dropOther(NatureBlocks.VILE_MUSHROOM.get(), MaterialItems.VILE_MUSHROOM.get());
        add(NatureBlocks.GLOWING_MUSHROOM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.MUSHROOM_GRASS_SEED))
                        .add(EmptyLootItem.emptyItem().setWeight(39)))
        );
        dropOther(NatureBlocks.LIFE_MUSHROOM.get(), MaterialItems.LIFE_MUSHROOM.get());
        add(NatureBlocks.JUNGLE_SPORE.get(), LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.JUNGLE_SPORE.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))));
        add(NatureBlocks.AMBER_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AMBER))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AMBER_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.RUBY_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(RUBY))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(RUBY_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.TOPAZ_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TOPAZ))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TOPAZ_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.JADE_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(JADE))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(JADE_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.DIAMOND_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.DIAMOND))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(DIAMOND_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.SAPPHIRE_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SAPPHIRE))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SAPPHIRE_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.AMETHYST_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AMETHYST))
                        .add(emptyWeight59))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AMETHYST_SAPLING))
                        .add(emptyWeight59))
        );
        add(NatureBlocks.ASH_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SPICY_PEPPER.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(199)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.POMEGRANATE.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(199)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ASH_SAPLING.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(19)))
        );
        add(NatureBlocks.ASH_BRANCHES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SPICY_PEPPER.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(199)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.POMEGRANATE.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(199)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ASH_SAPLING.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(19)))
        );
        add(NatureBlocks.ASH_GRASS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ASH_GRASS.get()))
                        .when(this.hasSilkTouch())
                        .when(HAS_SHEARS))
        );
        add(DESERT_GRASS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(DESERT_GRASS.get()))
                        .when(this.hasSilkTouch())
                        .when(HAS_SHEARS))
        );
        add(DESERT_TALL_GRASS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(DESERT_TALL_GRASS.get()))
                        .when(this.hasSilkTouch())
                        .when(HAS_SHEARS))
        );
        add(CORRUPT_GRASS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(CORRUPT_GRASS.get()))
                        .when(this.hasSilkTouch())
                        .when(HAS_SHEARS))
        );
        add(HALLOW_GRASS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(HALLOW_GRASS.get()))
                        .when(this.hasSilkTouch())
                        .when(HAS_SHEARS))
        );
        add(CRIMSON_GRASS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(CRIMSON_GRASS.get()))
                        .when(this.hasSilkTouch())
                        .when(HAS_SHEARS))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Iterables.concat(
                getIterableFromRegister(ModBlocks.BLOCKS),
                getIterableFromRegister(OreBlocks.BLOCKS),
                getIterableFromRegister(DecorativeBlocks.BLOCKS),
                getIterableFromRegister(ChestBlocks.BLOCKS),
                getIterableFromRegister(CrateBlocks.BLOCKS),
                getIterableFromRegister(FunctionalBlocks.BLOCKS),
                getIterableFromRegister(NatureBlocks.BLOCKS),
                getIterableFromRegister(PotBlocks.BLOCKS)
        );
    }

    private Iterable<Block> getIterableFromRegister(DeferredRegister<Block> register) {
        return register.getEntries().stream().map(holder -> (Block) holder.get()).filter(block -> map.containsKey(block.getLootTable())).toList();
    }

    private LootTable.Builder createTinOreDrop(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        return createSilkTouchDispatchTable(block, applyExplosionDecay(block,
                LootItem.lootTableItem(RAW_TIN)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
        ));
    }

    private void addHerbDrop(BaseHerbBlock block, Item herb, Item seed) {
        add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(ConstantValue.exactly(0.5f))
                        .add(LootItem.lootTableItem(herb))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BaseHerbBlock.AGE, 2))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(seed)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BaseHerbBlock.AGE, 2))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(herb))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BaseHerbBlock.AGE, 1)))));
    }
}
