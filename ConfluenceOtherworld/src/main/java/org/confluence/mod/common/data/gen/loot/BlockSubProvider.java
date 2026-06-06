package org.confluence.mod.common.data.gen.loot;

import com.google.common.collect.Streams;
import net.minecraft.advancements.critereon.ItemPredicate;
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
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.common.block.natural.CoinPileBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.SwordInStoneBlock;
import org.confluence.mod.common.block.natural.herbs.BaseHerbBlock;
import org.confluence.mod.common.block.palettes.DecoBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;

import java.util.Set;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.Blocks.WHITE_WOOL;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount;
import static net.minecraft.world.level.storage.loot.predicates.ExplosionCondition.survivesExplosion;
import static org.confluence.mod.common.init.block.DecorativeBlocks.*;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.ModBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.*;
import static org.confluence.mod.common.init.block.OreBlocks.*;
import static org.confluence.mod.common.init.item.ConsumableItems.LIFE_CRYSTAL;
import static org.confluence.mod.common.init.item.MaterialItems.*;

@SuppressWarnings("all")
public final class BlockSubProvider extends BlockLootSubProvider {
    public static final LootItemCondition.Builder HAS_SHEARS;
    public BlockSubProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
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
        dropSelf(HELLSTONE_BRICKS.FULL.get());
        dropSelf(DESERT_FOSSIL.get());
        dropSelf(SLUSH.get());
        dropSelf(SILT_BLOCK.get());
        dropSelf(MARINE_GRAVEL.get());
        dropSelf(DIATOMACEOUS.get());
        dropSelf(CLOUD_BLOCK.get());
        dropSelf(RAIN_CLOUD_BLOCK.get());
        dropSelf(SNOW_CLOUD_BLOCK.get());
        //dropSelf(FLOATING_WHEAT_BALE.get());
        dropSelf(STURDY_FOSSIL_BLOCK.get());
        dropSelf(OPAL_BLOCK.get());
        dropSelf(GELSTONE_BLOCK.get());
        dropSelf(COLD_CRYSTAL_BLOCK.get());
        this.add(NatureBlocks.CRYSTAL_SHARDS.get(), p_344211_ -> this.createSilkTouchDispatchTable(p_344211_, (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(p_344211_, LootItem.lootTableItem(NatureBlocks.CRYSTAL_SHARDS.get().asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))).apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))));
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
        dropSelf(DETONATOR.get());
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
        dropSelf(SHIMMER_TRAP.get());
        dropSelf(GRAVITATION_TRAP.get());
        dropSelf(PNEUMATIC_TRAP.get());
        dropSelf(PIGGY_BANK.get());
        dropSelf(LIFE_CAMPFIRE.get());
        dropSelf(LOOM.get());
        dropSelf(DYE_VAT.get());
        dropSelf(SAFE.get());
        dropSelf(ANNOUNCEMENT_BOX.get());
        dropSelf(KEG.get());
        dropSelf(CRYSTAL_BALL.get());
        dropSelf(MYTHRIL_ANVIL.get());
        dropSelf(ORICHALCUM_ANVIL.get());
        dropSelf(ADAMANTITE_FORGE.get());
        dropSelf(TITANIUM_FORGE.get());
        dropSelf(CHLOROPHYTE_EXTRACTINATOR.get());
        dropSelf(SOLIDIFIER.get());
        dropSelf(CAULDRON.get());
        dropSelf(TREE_HOLES_BLOCK.get());
        dropSelf(MAGIC_MAIL_BOX.get());
        dropSelf(SAWMILL.get());
        dropSelf(HEART_LANTERN.get());
        dropSelf(STAR_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_FLIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_LIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_FRIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_NIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_MIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_SIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_BRIGHT_IN_A_BOTTLE.get());
        dropSelf(SOUL_OF_VOIGHT_IN_A_BOTTLE.get());
        dropSelf(TUFF_BOOTH.get());

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
        add(SANCTIFICATION_REDSTONE_ORE.get(), super::createRedstoneOreDrops);
        add(CORRUPTION_REDSTONE_ORE.get(), super::createRedstoneOreDrops);
        add(FLESHIFICATION_REDSTONE_ORE.get(), super::createRedstoneOreDrops);
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
        add(SANCTIFICATION_JADE_ORE.get(), block -> createOreDrop(block, JADE.get()));
        add(CORRUPTION_JADE_ORE.get(), block -> createOreDrop(block, JADE.get()));
        add(FLESHIFICATION_JADE_ORE.get(), block -> createOreDrop(block, JADE.get()));
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

        add(CORRUPTION_CRIMTANE_ORE.get(), block -> createOreDrop(block, RAW_CRIMTANE.get()));
        add(SANCTIFICATION_CRIMTANE_ORE.get(), block -> createOreDrop(block, RAW_CRIMTANE.get()));
        add(FLESHIFICATION_CRIMTANE_ORE.get(), block -> createOreDrop(block, RAW_CRIMTANE.get()));
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

        add(LUNARTEAR_ORE.get(), block -> createOreDrop(block, LUNARTEAR.get()));
        add(DRAGONSAL_ORE.get(), block -> createOreDrop(block, DRAGONSAL.get()));
        // endregion ore

        // region natural
        dropSelf(COBBLED_EBONSTONE.get());
        dropSelf(EBONSAND.get());
        dropSelf(COBBLED_PEARLSTONE.get());
        dropSelf(PEARLSAND.get());
        dropSelf(COBBLED_CRIMSTONE.get());
        dropSelf(CRIMSAND.get());
        dropSelf(ASH_BLOCK.get());
        dropSelf(PACKED_DIRT.get());
        add(CRIMSTONE.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, COBBLED_CRIMSTONE));
        add(EBONSTONE.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, COBBLED_EBONSTONE));
        add(PEARLSTONE.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, COBBLED_PEARLSTONE));
        add(ASH_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, ASH_BLOCK));
        add(CORRUPT_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.DIRT));
        add(CORRUPT_JUNGLE_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        add(JUNGLE_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        add(CRIMSON_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.DIRT));
        add(CRIMSON_JUNGLE_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        add(MUSHROOM_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        add(HALLOW_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.DIRT));
        add(VOID_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, END_DIRT));
        add(INVERSE_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, END_DIRT));
        add(MOONLIT_GRASS_BLOCK.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, END_DIRT));

        add(CORRUPT_CACTUS.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.CACTUS));
        add(CRIMSON_CACTUS.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.CACTUS));
        add(HALLOW_CACTUS.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.CACTUS));

        add(SHIMMER_DROOPING_VINE.get(), this::createShimmerBerriesDrop);
        add(SHIMMER_DROOPING_VINE_PLANT.get(), this::createShimmerBerriesDrop);

        dropSelf(EBONY_LOG_BLOCKS.LOG.get());
        dropSelf(YELLOW_WILLOW_LOG_BLOCKS.LOG.get());
        dropSelf(BAOBAB_LOG_BLOCKS.LOG.get());
        dropSelf(LIVING_LOG_BLOCKS.LOG.get());
        dropSelf(LIVING_MAHOGANY_LOG_BLOCKS.LOG.get());
        dropSelf(SHADOW_LOG_BLOCKS.LOG.get());
        dropSelf(PEARL_LOG_BLOCKS.LOG.get());
        dropSelf(PALM_LOG_BLOCKS.LOG.get());
        dropSelf(ASH_LOG_BLOCKS.LOG.get());
        dropSelf(DYNASTY_LOG_BLOCKS.LOG.get());
        dropSelf(STONY_LOG.get());


        dropSelf(CRISPY_HONEY_BLOCK.get());
        dropSelf(THIN_HONEY_BLOCK.get());
        dropSelf(LOOSE_HONEY_BLOCK.get());
        dropSelf(HONEY_CAULDRON.get());
        dropSelf(AETHERIUM_CAULDRON.get());
        dropSelf(CURSED_FLAME_BLOCK.get());
        dropSelf(POO.get());
        dropSelf(POO_BLOCK.get());

        dropSelf(ROPE.get());
        dropSelf(SILK_ROPE.get());
        dropSelf(WEB_ROPE.get());
        dropSelf(VINE_ROPE.get());
        dropSelf(PINE_NEEDLE_HANDMADE_ROPE_SET.get());

        dropSelf(WATER_CANDLE.get());
        dropSelf(PEACE_CANDLE.get());

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
        dropSelf(WOOD_STONE_SLATTED_BLOCKS.get());
        dropSelf(BLUE_ICE_BRICKS.FULL.get());
        dropSelf(BLUE_ICE_BRICKS.STAIRS.get());
        dropSelf(BLUE_ICE_BRICKS.SLAB.get());
        dropSelf(BLUE_ICE_BRICKS.WALL.get());
        dropSelf(PACKED_ICE_BRICKS.FULL.get());
        dropSelf(PACKED_ICE_BRICKS.STAIRS.get());
        dropSelf(PACKED_ICE_BRICKS.SLAB.get());
        dropSelf(PACKED_ICE_BRICKS.WALL.get());
        dropSelf(SNOW_BRICKS.FULL.get());
        dropSelf(SNOW_BRICKS.STAIRS.get());
        dropSelf(SNOW_BRICKS.SLAB.get());
        dropSelf(SNOW_BRICKS.WALL.get());
        dropSelf(COPPER_BRICKS.FULL.get());
        dropSelf(COPPER_BRICKS.STAIRS.get());
        dropSelf(COPPER_BRICKS.SLAB.get());
        dropSelf(COPPER_BRICKS.WALL.get());
        dropSelf(CHISELED_COPPER_BRICKS.get());
        dropSelf(COPPER_TILES.get());
        dropSelf(TIN_BRICKS.FULL.get());
        dropSelf(TIN_BRICKS.STAIRS.get());
        dropSelf(TIN_BRICKS.SLAB.get());
        dropSelf(TIN_BRICKS.WALL.get());
        dropSelf(CHISELED_TIN_BRICKS.get());
        dropSelf(TIN_TILES.get());
        dropSelf(IRON_BRICKS.FULL.get());
        dropSelf(IRON_BRICKS.STAIRS.get());
        dropSelf(IRON_BRICKS.SLAB.get());
        dropSelf(IRON_BRICKS.WALL.get());
        dropSelf(CHISELED_IRON_BRICKS.get());
        dropSelf(LEAD_BRICKS.FULL.get());
        dropSelf(LEAD_BRICKS.STAIRS.get());
        dropSelf(LEAD_BRICKS.SLAB.get());
        dropSelf(LEAD_BRICKS.WALL.get());
        dropSelf(CHISELED_LEAD_BRICKS.get());
        dropSelf(SILVER_BRICKS.FULL.get());
        dropSelf(SILVER_BRICKS.STAIRS.get());
        dropSelf(SILVER_BRICKS.SLAB.get());
        dropSelf(SILVER_BRICKS.WALL.get());
        dropSelf(CHISELED_SILVER_BRICKS.get());
        dropSelf(TUNGSTEN_BRICKS.FULL.get());
        dropSelf(TUNGSTEN_BRICKS.STAIRS.get());
        dropSelf(TUNGSTEN_BRICKS.SLAB.get());
        dropSelf(TUNGSTEN_BRICKS.WALL.get());
        dropSelf(CHISELED_TUNGSTEN_BRICKS.get());
        dropSelf(GOLDEN_BRICKS.FULL.get());
        dropSelf(GOLDEN_BRICKS.STAIRS.get());
        dropSelf(GOLDEN_BRICKS.SLAB.get());
        dropSelf(GOLDEN_BRICKS.WALL.get());
        dropSelf(CHISELED_GOLDEN_BRICKS.get());
        dropSelf(PLATINUM_BRICKS.FULL.get());
        dropSelf(PLATINUM_BRICKS.STAIRS.get());
        dropSelf(PLATINUM_BRICKS.SLAB.get());
        dropSelf(PLATINUM_BRICKS.WALL.get());
        dropSelf(CHISELED_PLATINUM_BRICKS.get());
        dropSelf(DEMONITE_ORE_BRICKS.FULL.get());
        dropSelf(DEMONITE_ORE_BRICKS.STAIRS.get());
        dropSelf(DEMONITE_ORE_BRICKS.SLAB.get());
        dropSelf(DEMONITE_ORE_BRICKS.WALL.get());
        dropSelf(EBONSTONE_BRICKS.FULL.get());
        dropSelf(EBONSTONE_BRICKS.STAIRS.get());
        dropSelf(EBONSTONE_BRICKS.SLAB.get());
        dropSelf(EBONSTONE_BRICKS.WALL.get());
        dropSelf(METEORITE_BRICKS.FULL.get());
        dropSelf(METEORITE_BRICKS.STAIRS.get());
        dropSelf(METEORITE_BRICKS.SLAB.get());
        dropSelf(METEORITE_BRICKS.WALL.get());
        dropSelf(CRIMTANE_ORE_BRICKS.FULL.get());
        dropSelf(CRIMTANE_ORE_BRICKS.STAIRS.get());
        dropSelf(CRIMTANE_ORE_BRICKS.SLAB.get());
        dropSelf(CRIMTANE_ORE_BRICKS.WALL.get());
        dropSelf(CRIMSTONE_BRICKS.FULL.get());
        dropSelf(CRIMSTONE_BRICKS.STAIRS.get());
        dropSelf(CRIMSTONE_BRICKS.SLAB.get());
        dropSelf(CRIMSTONE_BRICKS.WALL.get());
        dropSelf(PEARLSTONE_BRICKS.FULL.get());
        dropSelf(PEARLSTONE_BRICKS.STAIRS.get());
        dropSelf(PEARLSTONE_BRICKS.SLAB.get());
        dropSelf(PEARLSTONE_BRICKS.WALL.get());
        dropSelf(GREEN_CANDY_BLOCK.get());
        dropSelf(RED_CANDY_BLOCK.get());
        dropSelf(FROZEN_GEL_BLOCK.get());
        dropSelf(BLUE_GEL_BLOCK.get());
        dropSelf(PINK_GEL_BLOCK.get());
        dropSelf(SUN_PLATE.FULL.get());
        dropSelf(SUN_PLATE.SLAB.get());
        dropSelf(SUN_PLATE.STAIRS.get());
        dropSelf(SUN_PLATE.WALL.get());
        dropSelf(DISC_BLOCK.FULL.get());
        dropSelf(DISC_BLOCK.SLAB.get());
        dropSelf(DISC_BLOCK.STAIRS.get());
        dropSelf(DISC_BLOCK.WALL.get());
        dropSelf(MOON_PLATE.FULL.get());
        dropSelf(MOON_PLATE.SLAB.get());
        dropSelf(MOON_PLATE.STAIRS.get());
        dropSelf(MOON_PLATE.WALL.get());
        dropSelf(OBSIDIAN_BRICKS.FULL.get());
        dropSelf(OBSIDIAN_BRICKS.SLAB.get());
        dropSelf(OBSIDIAN_BRICKS.STAIRS.get());
        dropSelf(OBSIDIAN_BRICKS.WALL.get());
        dropSelf(GLOOM_OBSIDIAN.get());
        dropSelf(GLOOM_OBSIDIAN_BRICKS.FULL.get());
        dropSelf(GLOOM_OBSIDIAN_BRICKS.STAIRS.get());
        dropSelf(GLOOM_OBSIDIAN_BRICKS.SLAB.get());
        dropSelf(GLOOM_OBSIDIAN_BRICKS.WALL.get());

        dropSelf(OBSIDIAN_SMALL_BRICKS.get());
        dropSelf(SMOOTH_OBSIDIAN.get());

        dropSelf(GRANITE_BRICKS.FULL.get());
        dropSelf(GRANITE_BRICKS.STAIRS.get());
        dropSelf(GRANITE_BRICKS.SLAB.get());
        dropSelf(GRANITE_BRICKS.WALL.get());
        dropSelf(POLISHED_GRANITE.get());
        dropSelf(GRANITE_COLUMN.get());
        dropSelf(CHISELED_GRANITE_BRICKS.get());
        dropSelf(CRACKED_GRANITE_BRICKS.get());

        dropSelf(MARBLE_COLUMN.get());
        dropSelf(MARBLE_BRICKS.FULL.get());
        dropSelf(MARBLE_BRICKS.STAIRS.get());
        dropSelf(MARBLE_BRICKS.SLAB.get());
        dropSelf(MARBLE_BRICKS.WALL.get());

        dropSelf(MARBLE_SMALL_BRICKS.get());
        dropSelf(CRACKED_MARBLE_BRICKS.get());
        dropSelf(GILDED_MARBLE.get());
        dropSelf(POLISHED_MARBLE.get());
        dropSelf(CHISELED_MARBLE_BRICKS.get());
        dropSelf(MARBLE_CHESSBOARD_BRICKS.get());
        dropSelf(MARBLE_ETERNAL_CHESSBOARD_BRICKS.get());

        dropSelf(WHITE_BALLOON.get());
        dropSelf(LIGHT_GRAY_BALLOON.get());
        dropSelf(GRAY_BALLOON.get());
        dropSelf(BLACK_BALLOON.get());
        dropSelf(BROWN_BALLOON.get());
        dropSelf(RED_BALLOON.get());
        dropSelf(ORANGE_BALLOON.get());
        dropSelf(YELLOW_BALLOON.get());
        dropSelf(LIME_BALLOON.get());
        dropSelf(GREEN_BALLOON.get());
        dropSelf(CYAN_BALLOON.get());
        dropSelf(LIGHT_BLUE_BALLOON.get());
        dropSelf(BLUE_BALLOON.get());
        dropSelf(PURPLE_BALLOON.get());
        dropSelf(MAGENTA_BALLOON.get());
        dropSelf(PINK_BALLOON.get());


        add(ATTACHED_BALLOON_STEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BALLOON_SEED).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(EmptyLootItem.emptyItem()))
        );

        dropSelf(WHITE_PAPER_PANE.get());
        dropSelf(WHITE_PAPER_PANE_LAMP.get());
        dropSelf(MALACHITE_PAPER_PANE.get());
        dropSelf(MALACHITE_PAPER_PANE_LAMP.get());

        dropSelf(ASPHALT_BLOCK.get());

        dropSelf(FLINX_FUR_BLOCK.get());
        dropSelf(FLINX_FUR_CARPET.get());
        dropSelf(RAINBOW_WOOL.get());
        dropSelf(RAINBOW_CARPET.get());

        dropSelf(CHISELED_OBSIDIAN_BRICKS.get());
        dropSelf(BLUE_BRICKS.FULL.get());
        dropSelf(GREEN_BRICKS.FULL.get());
        dropSelf(PINK_BRICKS.FULL.get());
        dropSelf(SPIKE.get());
        dropSelf(ENCHANTED_BLUE_BRICKS.get());
        dropSelf(ENCHANTED_GREEN_BRICKS.get());
        dropSelf(ENCHANTED_PINK_BRICKS.get());
        dropSelf(BLUE_BRICKS.STAIRS.get());
        dropSelf(GREEN_BRICKS.STAIRS.get());
        dropSelf(PINK_BRICKS.STAIRS.get());
        dropSelf(BLUE_BRICKS.SLAB.get());
        dropSelf(GREEN_BRICKS.SLAB.get());
        dropSelf(PINK_BRICKS.SLAB.get());
        dropSelf(BLUE_BRICKS.WALL.get());
        dropSelf(GREEN_BRICKS.WALL.get());
        dropSelf(PINK_BRICKS.WALL.get());
        dropSelf(CHISELED_BLUE_BRICKS.get());
        dropSelf(CHISELED_GREEN_BRICKS.get());
        dropSelf(CHISELED_PINK_BRICKS.get());
        dropSelf(BLUE_BRICK_COLUMN.get());
        dropSelf(GREEN_BRICK_COLUMN.get());
        dropSelf(PINK_BRICK_COLUMN.get());
        dropSelf(AETHERIUM_BRICKS.FULL.get());
        dropSelf(CRYSTAL_BLOCK.get());
        dropSelf(RAINBOW_BRICKS.FULL.get());
        dropSelf(FLOATING_WHEAT_BALE.get());
        dropSelf(BOUNCY_CLOUD_BLOCK.get());
        dropSelf(STAR_CLOUD_BLOCK.get());

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

        dropSelf(REMAINS_BLOCK.get());

        dropSelf(AETHERIUM_BLOCK.get());
        dropSelf(DARK_AETHERIUM_BLOCK.get());
        dropSelf(FALLEN_STAR_BLOCK.get());

        dropSelf(JUNGLE_ROSE.get());
        dropSelf(VOID_VIOLET.get());

        dropSelf(LIHZAHRD_COLUMN.get());
        dropSelf(LIHZAHRD_TILES.get());
        dropSelf(POLISHED_LIHZAHRD.get());

        dropSelf(WHITE_PUMPKIN.get());
        dropSelf(CARVED_WHITE_PUMPKIN.get());
        dropSelf(JOHNNY_O_LANTERN.get());
        this.add(WHITE_PUMPKIN_STEM.get(), p_252178_ -> this.createStemDrops(p_252178_, FoodItems.WHITE_PUMPKIN_SEED.get()));
        this.add(ATTACHED_WHITE_PUMPKIN_STEM.get(), p_250849_ -> this.createAttachedStemDrops(p_250849_, FoodItems.WHITE_PUMPKIN_SEED.get()));

        this.add(ICE_MELON.get(), p_344241_ -> this.createSilkTouchDispatchTable(p_344241_, (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(p_344241_, LootItem.lootTableItem(FoodItems.ICE_MELON_SLICE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F))).apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))).apply(LimitCount.limitCount(IntRange.upperBound(9))))));
        this.add(GOLDEN_MELON.get(), p_344241_ -> this.createSilkTouchDispatchTable(p_344241_, (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(p_344241_, LootItem.lootTableItem(Items.GLISTERING_MELON_SLICE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F))).apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))).apply(LimitCount.limitCount(IntRange.upperBound(9))))));



        dropOther(LIFE_CRYSTAL_BLOCK.get(), LIFE_CRYSTAL.get());
        dropOther(LIFE_FRUIT.get(),ConsumableItems.LIFE_FRUIT.get());

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

        dropWhenSilkTouch(VOID_WEAVE.get());


        //chain
        dropSelf(RUBY_CHAIN.get());
        dropSelf(AMBER_CHAIN.get());
        dropSelf(TOPAZ_CHAIN.get());
        dropSelf(JADE_CHAIN.get());
        dropSelf(SAPPHIRE_CHAIN.get());
        dropSelf(DIAMOND_CHAIN.get());
        dropSelf(AMETHYST_CHAIN.get());
        // 圣物
        dropSelf(KING_SLIME_RELIC.get());
        dropSelf(EYE_OF_CTHULHU_RELIC.get());
        dropSelf(BRAIN_OF_CTHULHU_RELIC.get());
        dropSelf(EATER_OF_WORLDS_RELIC.get());
        dropSelf(QUEEN_BEE_RELIC.get());
        dropSelf(DEERCLOPS_RELIC.get());
        dropSelf(SKELETRON_RELIC.get());
        dropSelf(WALL_OF_FLESH_RELIC.get());
        dropSelf(HILL_OF_FLESH_RELIC.get());
        dropSelf(THE_TWINS_RELIC.get());
        dropSelf(SKELETRON_PRIME_RELIC.get());
        // 片
        dropWhenSilkTouch(SAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(RED_SAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(EBONSAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(CRIMSAND_LAYER_BLOCK.get());
        dropWhenSilkTouch(PEARLSAND_LAYER_BLOCK.get());

        // 径
        add(ASH_PATH.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, ASH_BLOCK));
        add(JUNGLE_PATH.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));
        add(MUSHROOM_PATH.get(), p_251015_ -> createSingleItemTableWithSilkTouch(p_251015_, Blocks.MUD));

        //door
        add(OBSIDIAN_BRICKS_DOOR.get(), this::createDoorTable);
        add(SKYWARE_DOOR.get(), this::createDoorTable);
        add(SKYWARE_GLASS_DOOR.get(), this::createDoorTable);
        add(DUNGEON_DOOR.get(), this::createDoorTable);
        add(TRADITIONAL_DYNASTY_DOOR.get(), this::createDoorTable);
        add(CHRISTMAS_PINE_DOOR.get(), this::createDoorTable);
        add(LIHZAHRD_DOOR.get(), this::createDoorTable);
        dropSelf(CHRISTMAS_PINE_TRAPDOOR.get());

        // 发光蘑菇
        add(GLOWING_MUSHROOM_INDUSIUM_BLOCK.get(), block -> createMushroomBlockDrop(block, MaterialItems.GLOWING_MUSHROOM));
        add(GLOWING_MUSHROOM_VINE.get(), block -> createMushroomBlockDrop(block, MaterialItems.GLOWING_MUSHROOM));
        add(GLOWING_MUSHROOM_CATTAIL_BLOCK.get(), block -> createMushroomBlockDrop(block, MaterialItems.GLOWING_MUSHROOM));
        add(GLOWING_MUSHROOM_PILEUS_BLOCK.get(), block -> createMushroomBlockDrop(block, MaterialItems.GLOWING_MUSHROOM));
        dropSelf(GLOWING_MUSHROOM_STEM_BLOCK.get());


        dropWhenSilkTouch(GLOWING_MUSHROOM_CATTAIL_BLOCK.get());

        add(LIFE_MUSHROOM_INDUSIUM_BLOCK.get(), block -> createMushroomBlockDrop(block, MaterialItems.LIFE_MUSHROOM));
        add(LIFE_MUSHROOM_PILEUS_BLOCK.get(), block -> createMushroomBlockDrop(block, MaterialItems.LIFE_MUSHROOM));
        add(LIFE_MUSHROOM_STEM_BLOCK.get(), block -> createMushroomBlockDrop(block, MaterialItems.LIFE_MUSHROOM));

        dropSelf(HANGING_MYCELIUM.get());
        dropSelf(MYCELIAL_DIRT.get());

        dropWhenSilkTouch(ICE_TAPERED_BLOCK.get());
        dropSelf(DESERT_TAPERED_BLOCK.get());
        dropSelf(MARBLE_TAPERED_BLOCK.get());
        dropSelf(GRANITE_TAPERED_BLOCK.get());

        dropSelf(CORRUPT_TAPERED_BLOCK.get());
        dropSelf(CRIMSON_TAPERED_BLOCK.get());
        dropSelf(HALLOW_TAPERED_BLOCK.get());

        dropSelf(END_DIRT.get());

        dropSelf(VOID_TREE_ROOT_BLOCK.get());




        for (LogBlockSet blockSet : LogBlockSet.LOG_BLOCK_SETS) {
            dropSelf(blockSet.PLANKS.get());
            if (blockSet.LOG.isBound()) dropSelf(blockSet.LOG.get());
            if (blockSet.STRIPPED_LOG.isBound()) dropSelf(blockSet.STRIPPED_LOG.get());
            if (blockSet.WOOD.isBound()) dropSelf(blockSet.WOOD.get());
            if (blockSet.STRIPPED_WOOD.isBound()) dropSelf(blockSet.STRIPPED_WOOD.get());
            if (blockSet.BUTTON.isBound()) dropSelf(blockSet.BUTTON.get());
            if (blockSet.FENCE.isBound()) dropSelf(blockSet.FENCE.get());
            if (blockSet.FENCE_GATE.isBound()) dropSelf(blockSet.FENCE_GATE.get());
            if (blockSet.PRESSURE_PLATE.isBound()) dropSelf(blockSet.PRESSURE_PLATE.get());
            if (blockSet.SLAB.isBound()) add(blockSet.SLAB.get(), this::createSlabItemTable);
            if (blockSet.STAIRS.isBound()) dropSelf(blockSet.STAIRS.get());
            if (blockSet.SIGN.isBound()) dropSelf(blockSet.SIGN.get());
            if (blockSet.TRAPDOOR.isBound()) dropSelf(blockSet.TRAPDOOR.get());
            if (blockSet.DOOR.isBound()) add(blockSet.DOOR.get(), this::createDoorTable);
            if (blockSet.HANGING_SIGN.isBound()) dropSelf(blockSet.HANGING_SIGN.get());
            if (blockSet.CHISELED_PLANKS.isBound()) dropSelf(blockSet.CHISELED_PLANKS.get());
            if (blockSet.SAPLING.isBound()) dropSelf(blockSet.SAPLING.get());
        }

        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            dropSelf(blockSet.FULL.get());
            dropSelf(blockSet.STAIRS.get());
            dropSelf(blockSet.SLAB.get());
            dropSelf(blockSet.WALL.get());
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
        add(NatureBlocks.JUNGLE_SPORE.get(), LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.JUNGLE_SPORE.get()).apply(setCount(UniformGenerator.between(1, 3))))));
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
                        .add(EmptyLootItem.emptyItem().setWeight(159)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.POMEGRANATE.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(159)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ASH_LOG_BLOCKS.SAPLING.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(15)))
        );
        addGrassLoot(ASH_GRASS.get(), ASH_GRASS.asItem());
        addGrassLoot(DESERT_GRASS.get(), DESERT_GRASS.asItem());
        addGrassLoot(DESERT_TALL_GRASS.get(), DESERT_TALL_GRASS.asItem());
        addGrassLoot(CORRUPT_GRASS.get(), CORRUPT_GRASS.asItem());
        addGrassLoot(HALLOW_GRASS.get(), HALLOW_GRASS.asItem());
        addGrassLoot(CRIMSON_GRASS.get(), CRIMSON_GRASS.asItem());
        addGrassLoot(VOID_GRASS.get(), VOID_GRASS.asItem());

        addGrassLoot(CATTAIL_BLOCK.get(), ModItems.CATTAIL.get());
        addGrassLoot(JUNGLE_CATTAIL_BLOCK.get(), ModItems.JUNGLE_CATTAIL.get());
        addGrassLoot(GLOWING_MUSHROOM_CATTAIL_BLOCK.get(), ModItems.GLOWING_MUSHROOM_CATTAIL.get());
        addGrassLoot(HALLOW_CATTAIL_BLOCK.get(), ModItems.HALLOW_CATTAIL.get());
        addGrassLoot(EBONY_CATTAIL_BLOCK.get(), ModItems.EBONY_CATTAIL.get());
        addGrassLoot(CRIMSON_CATTAIL_BLOCK.get(), ModItems.CRIMSON_CATTAIL.get());

        addGrassLoot(PINE_DROOPING_VINE.get(), NatureBlocks.PINE_DROOPING_VINE.asItem());

        addGrassLoot(SMALL_DESERT_PLANT.get(), SMALL_DESERT_PLANT.asItem());
        addGrassLoot(BIG_DESERT_PLANT.get(), BIG_DESERT_PLANT.asItem());

        add(SWORD_IN_STONE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SWORD_IN_STONE.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(SwordInStoneBlock.SWORD_TYPE, SwordInStoneBlock.SwordType.TERRAGRIM)))
                        .add(LootItem.lootTableItem(SwordItems.TERRAGRIM.get())))
                .withPool(LootPool.lootPool()
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SWORD_IN_STONE.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(SwordInStoneBlock.SWORD_TYPE, SwordInStoneBlock.SwordType.ENCHANTED_SWORD)))
                        .add(LootItem.lootTableItem(SwordItems.ENCHANTED_SWORD.get())))
                .withPool(LootPool.lootPool()
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SWORD_IN_STONE.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(SwordInStoneBlock.SWORD_TYPE, SwordInStoneBlock.SwordType.ROTTEN_SWORD)))
                        .add(LootItem.lootTableItem(SwordItems.FAKE_SWORD)))
        );
        add(SPORE_ROOT_BLOCK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SPORE_ROOT_BLOCK.get()).when(hasSilkTouch()))
                        .add(LootItem.lootTableItem(SPORE_ROOT.get())
                                .when(doesNotHaveShearsOrSilkTouch())
                                .when(survivesExplosion())
                                .apply(setCount(UniformGenerator.between(1, 2)))
                        ))
        );
        add(WINTER_MARROW_BLOCK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(WINTER_MARROW_BLOCK.get()).when(hasSilkTouch()))
                        .add(LootItem.lootTableItem(WINTER_MARROW.get())
                                .when(doesNotHaveShearsOrSilkTouch())
                                .when(survivesExplosion())
                                .apply(setCount(UniformGenerator.between(1, 2)))
                        ))
        );
        // 作物
        LootItemCondition.Builder lootitemcondition$builder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(FLOATING_WHEAT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
        add(NatureBlocks.FLOATING_WHEAT.get(), createCropDrops(NatureBlocks.FLOATING_WHEAT.get(), MaterialItems.FLOATING_WHEAT_HEADS.asItem(), FoodItems.FLOATING_WHEAT_SEED.get(), lootitemcondition$builder1));

        lootitemcondition$builder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(CLOUDWEAVER.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
        add(NatureBlocks.CLOUDWEAVER.get(), createCropDrops(NatureBlocks.CLOUDWEAVER.get(), MaterialItems.WEAVING_CLOUD_COTTON.asItem(), FoodItems.CLOUDWEAVER_SEED.get(), lootitemcondition$builder1));

        LootTable.Builder stellarLoot = LootTable.lootTable();

        for (int age = 1; age <= 5; age++) {
            stellarLoot.withPool(LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(STELLAR_BLOSSOM.get())
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(CropBlock.AGE, age)))
                    .add(LootItem.lootTableItem(FoodItems.STELLAR_BLOSSOM_SEED.get())));
        }

        stellarLoot.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(FoodItems.STELLAR_BLOSSOM_SEED.get()))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(STELLAR_BLOSSOM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 0))));

        stellarLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(STELLAR_BLOSSOM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 6)))
                .add(LootItem.lootTableItem(FoodItems.STELLAR_BLOSSOM_SEED.get())
                        .apply(setCount(ConstantValue.exactly(2)))));

        stellarLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(STELLAR_BLOSSOM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 6)))
                .add(LootItem.lootTableItem(STAR_PETALS.get())));

        stellarLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(STELLAR_BLOSSOM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 7)))
                .add(LootItem.lootTableItem(FoodItems.STELLAR_BLOSSOM_SEED.get())
                        .apply(setCount(UniformGenerator.between(2, 3)))));

        stellarLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(STELLAR_BLOSSOM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 7)))
                .add(LootItem.lootTableItem(STAR_PETALS.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                        .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                ));

        add(STELLAR_BLOSSOM.get(), stellarLoot);

        LootTable.Builder balloonLoot = LootTable.lootTable();

        for (int age = 1; age <= 5; age++) {
            balloonLoot.withPool(LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BALLOON_STEM.get())
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(CropBlock.AGE, age)))
                    .add(LootItem.lootTableItem(FoodItems.BALLOON_SEED.get())));
        }

        balloonLoot.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(FoodItems.BALLOON_SEED.get()))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BALLOON_STEM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 0))));

        balloonLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BALLOON_STEM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 6)))
                .add(LootItem.lootTableItem(FoodItems.BALLOON_SEED.get())
                        .apply(setCount(ConstantValue.exactly(2)))));

        balloonLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BALLOON_STEM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 6)))
                .add(LootItem.lootTableItem(FoodItems.BALLOON_SEED.get())));

        balloonLoot.withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BALLOON_STEM.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, 7)))
                .add(LootItem.lootTableItem(FoodItems.BALLOON_SEED.get())
                        .apply(setCount(UniformGenerator.between(2, 3)))));
        add(BALLOON_STEM.get(), balloonLoot);
        addCoinPileDrop(COPPER_COIN.get());
        addCoinPileDrop(SILVER_COIN.get());
        addCoinPileDrop(GOLD_COIN.get());
        addCoinPileDrop(PLATINUM_COIN.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Streams.concat(
                getStreamFromRegister(ModBlocks.BLOCKS),
                getStreamFromRegister(OreBlocks.BLOCKS),
                getStreamFromRegister(DecorativeBlocks.BLOCKS),
                getStreamFromRegister(ChestBlocks.BLOCKS),
                getStreamFromRegister(CrateBlocks.BLOCKS),
                getStreamFromRegister(FunctionalBlocks.BLOCKS),
                getStreamFromRegister(NatureBlocks.BLOCKS),
                getStreamFromRegister(PotBlocks.BLOCKS)
        )::iterator;
    }

    private Stream<Block> getStreamFromRegister(DeferredRegister<Block> register) {
        return (Stream<Block>) register.getEntries().stream().map(DeferredHolder::get).filter(block -> map.containsKey(block.getLootTable()));
    }

    private void addGrassLoot(Block block, Item dropItem) {
        add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(dropItem))
                        .when(hasShearsOrSilkTouch()))
        );
    }

    private LootTable.Builder createTinOreDrop(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        return createSilkTouchDispatchTable(block, applyExplosionDecay(block,
                LootItem.lootTableItem(RAW_TIN)
                        .apply(setCount(UniformGenerator.between(2.0F, 5.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
        ));
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return HAS_SHEARS.or(hasSilkTouch());
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return hasShearsOrSilkTouch().invert();
    }

    private void addHerbDrop(BaseHerbBlock block, Item herb, Item seed) {
        add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setBonusRolls(ConstantValue.exactly(0.5f))
                        .add(LootItem.lootTableItem(herb))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BaseHerbBlock.AGE, 2))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(seed).apply(setCount(UniformGenerator.between(1, 3))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BaseHerbBlock.AGE, 2))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(herb))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BaseHerbBlock.AGE, 1)))));
    }

    private void addCoinPileDrop(CoinPileBlock block) {
        LootTable.Builder lootTable = LootTable.lootTable();
        for (int heaps = 1; heaps <= 12; heaps++) {
            lootTable.withPool(LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(CoinPileBlock.HEAPS, String.valueOf(heaps))))
                    .add(LootItem.lootTableItem(block)
                            .apply(setCount(ConstantValue.exactly(heaps)))));
        }
        add(block, lootTable);
    }

    private LootTable.Builder createShimmerBerriesDrop(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.SHIMMER_BERRIES))
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CaveVines.BERRIES, true))
                                )
                );
    }

    static {
        HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_SHEAR));
    }
}
