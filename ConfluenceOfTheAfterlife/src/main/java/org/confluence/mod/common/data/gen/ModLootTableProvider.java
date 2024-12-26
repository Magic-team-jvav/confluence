package org.confluence.mod.common.data.gen;

import com.google.common.collect.Iterables;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.common.block.natural.BaseHerbBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.common.init.block.DecorativeBlocks.*;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.*;
import static org.confluence.mod.common.init.block.OreBlocks.*;
import static org.confluence.mod.common.init.item.ConsumableItems.LIFE_CRYSTAL;
import static org.confluence.mod.common.init.item.MaterialItems.*;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockSub::new, LootContextParamSets.BLOCK)
        ), lookup);
    }

    public static class BlockSub extends BlockLootSubProvider {
        public BlockSub(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
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
            dropSelf(TR_CRIMSON_BLOCK.get());
            dropSelf(RAW_TR_CRIMSON_BLOCK.get());
            dropSelf(COBALT_BLOCK.get());
            dropSelf(RAW_COBALT_BLOCK.get());
            dropSelf(PALLADIUM_BLOCK.get());
            dropSelf(RAW_PLATINUM_BLOCK.get());
            dropSelf(MITHRIL_BLOCK.get());
            dropSelf(RAW_MITHRIL_BLOCK.get());
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
            dropSelf(HELLSTONE_BLOCK.get());
            dropSelf(RAW_HELLSTONE_BLOCK.get());
            dropSelf(DESERT_FOSSIL.get());
            dropSelf(SLUSH.get());
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
            dropSelf(DEEPSLATE_PRESSURE_PLATE.get());

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
            add(TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
//            add(SANCTIFICATION_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
//            add(CORRUPTION_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
//            add(FLESHIFICATION_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
            add(DEEPSLATE_TR_EMERALD_ORE.get(), block -> createOreDrop(block, TR_EMERALD.get()));
            add(SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
            add(SANCTIFICATION_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
            add(CORRUPTION_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
            add(FLESHIFICATION_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
            add(DEEPSLATE_SAPPHIRE_ORE.get(), block -> createOreDrop(block, SAPPHIRE.get()));
            add(TR_AMETHYST_ORE.get(), block -> createOreDrop(block, TR_AMETHYST.get()));
            add(SANCTIFICATION_TR_AMETHYST_ORE.get(), block -> createOreDrop(block, TR_AMETHYST.get()));
            add(CORRUPTION_TR_AMETHYST_ORE.get(), block -> createOreDrop(block, TR_AMETHYST.get()));
            add(FLESHIFICATION_TR_AMETHYST_ORE.get(), block -> createOreDrop(block, TR_AMETHYST.get()));
            add(DEEPSLATE_TR_AMETHYST_ORE.get(), block -> createOreDrop(block, TR_AMETHYST.get()));
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
            add(TR_CRIMSON_ORE.get(), block -> createOreDrop(block, RAW_TR_CRIMSON.get()));
            add(DEEPSLATE_TR_CRIMSON_ORE.get(), block -> createOreDrop(block, RAW_TR_CRIMSON.get()));
            add(DEEPSLATE_COBALT_ORE.get(), block -> createOreDrop(block, RAW_COBALT.get()));
            add(DEEPSLATE_PALLADIUM_ORE.get(), block -> createOreDrop(block, RAW_PALLADIUM.get()));
            add(DEEPSLATE_MITHRIL_ORE.get(), block -> createOreDrop(block, RAW_MITHRIL.get()));
            add(DEEPSLATE_ORICHALCUM_ORE.get(), block -> createOreDrop(block, RAW_ORICHALCUM.get()));
            add(DEEPSLATE_ADAMANTITE_ORE.get(), block -> createOreDrop(block, RAW_ADAMANTITE.get()));
            add(DEEPSLATE_TITANIUM_ORE.get(), block -> createOreDrop(block, RAW_TITANIUM.get()));
            add(CHLOROPHYTE_ORE.get(), block -> createOreDrop(block, RAW_CHLOROPHYTE.get()));
            add(HELLSTONE.get(), block -> createOreDrop(block, RAW_HELLSTONE.get()));
            add(ASH_HELLSTONE.get(), block -> createOreDrop(block, RAW_HELLSTONE.get()));
            // endregion ore

            // region natural
            dropSelf(EBONY_COBBLESTONE.get());
            dropSelf(EBONY_SAND.get());
            dropSelf(PEARL_COBBLESTONE.get());
            dropSelf(PEARL_SAND.get());
            dropSelf(TR_CRIMSON_COBBLESTONE.get());
            dropSelf(TR_CRIMSON_SAND.get());
            dropSelf(ASH_BLOCK.get());
            dropOther(TR_CRIMSON_GRASS_BLOCK.get(), Items.DIRT);
            dropOther(CORRUPT_GRASS_BLOCK.get(), Items.DIRT);
            dropOther(HALLOW_GRASS_BLOCK.get(), Items.DIRT);
            dropOther(TR_CRIMSON_STONE.get(), TR_CRIMSON_COBBLESTONE.get());
            dropOther(EBONY_STONE.get(), EBONY_COBBLESTONE.get());
            dropOther(PEARL_STONE.get(), PEARL_COBBLESTONE.get());
            dropOther(OPAL_ORE.get(), OPAL.get());
            dropOther(GELSTONE_ORE.get(), GELSTONE.get());
            dropSelf(EBONY_LOG_BLOCKS.getLog().get());
            dropSelf(SHADOW_LOG_BLOCKS.getLog().get());
            dropSelf(PEARL_LOG_BLOCKS.getLog().get());
            dropSelf(PALM_LOG_BLOCKS.getLog().get());
            dropSelf(ASH_LOG_BLOCKS.getLog().get());
//            dropSelf(LIFE_PLANKS.get());
//            dropSelf(LIFE_LOG.get());
            dropSelf(STONY_LOG.get());

            dropSelf(BASE_CHEST_BLOCK.get());

            dropOther(NATURES_GIFT.get(), AccessoryItems.NATURES_GIFT.get());
            add(JUNGLE_ROSE.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(JUNGLE_ROSE.get()).when(LootItemRandomChanceCondition.randomChance(0.05f)))));
            // endregion natural

            dropSelf(BIG_RUBY_BLOCK.get());
            dropSelf(BIG_AMBER_BLOCK.get());
            dropSelf(BIG_TOPAZ_BLOCK.get());
            dropSelf(BIG_SAPPHIRE_BLOCK.get());
            dropSelf(BIG_TR_AMETHYST_BLOCK.get());
            dropSelf(BIG_TR_EMERALD_BLOCK.get());

            dropSelf(HARDENED_SAND_BLOCK.get());
            dropSelf(RED_HARDENED_SAND_BLOCK.get());
            dropSelf(EBONY_HARDENED_SAND_BLOCK.get());
            dropSelf(PEARL_HARDENED_SAND_BLOCK.get());
            dropSelf(TR_CRIMSON_HARDENED_SAND_BLOCK.get());

            dropOther(LIFE_CRYSTAL_BLOCK.get(), LIFE_CRYSTAL.get());
            dropOther(MUSHROOM_GRASS_BLOCK.get(), Items.MUD);

            //chain
            dropSelf(RUBY_CHAIN.get());
            dropSelf(AMBER_CHAIN.get());
            dropSelf(TOPAZ_CHAIN.get());
            dropSelf(EMERALD_CHAIN.get());
            dropSelf(SAPPHIRE_CHAIN.get());
            dropSelf(DIAMOND_CHAIN.get());
            dropSelf(AMETHYST_CHAIN.get());

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
            dropOther(NatureBlocks.GLOWING_MUSHROOM.get(), MaterialItems.GLOWING_MUSHROOM.get()); // TODO: 掉落概率不是100%；掉落蘑菇草种子
            dropOther(NatureBlocks.LIFE_MUSHROOM.get(), MaterialItems.LIFE_MUSHROOM.get());
            add(NatureBlocks.JUNGLE_SPORE.get(), LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.JUNGLE_SPORE.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))));
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return Iterables.concat(
                    getIterableFromRegister(ModBlocks.BLOCKS),
                    getIterableFromRegister(OreBlocks.BLOCKS),
                    getIterableFromRegister(DecorativeBlocks.BLOCKS),
                    getIterableFromRegister(CrateBlocks.BLOCKS),
                    getIterableFromRegister(FunctionalBlocks.BLOCKS),
                    getIterableFromRegister(FunctionalBlocks.HIDDEN),
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

        // TODO: 时运 再生法杖 再生之斧
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
}
