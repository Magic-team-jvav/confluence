package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Generates chests loot tables into loot_modifier path specified by ModLootModifiersProvider i.e. confluence/loot_table/with/chests/bat.json
 * @see org.confluence.mod.common.data.gen.ModLootModifiersProvider
 */
public class AddChestLootConfluenceSubProvider implements LootTableSubProvider, SyntheticLootTableProvider {
    public AddChestLootConfluenceSubProvider(HolderLookup.Provider provider) {

    }

    public List<AddedChestLoot> getAddedEntitiesLoot() {
        List<AddedChestLoot> entries = new ArrayList<>();
        entries.add(new AddedChestLoot(
            BuiltInLootTables.ABANDONED_MINESHAFT,
            LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(MaterialItems.SILVER_INGOT).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                    .add(LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                    .add(EmptyLootItem.emptyItem().setWeight(2)))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                    .add(LootItem.lootTableItem(MaterialItems.LEAD_INGOT).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                    .add(EmptyLootItem.emptyItem().setWeight(2)))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(Items.ARROW).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                    .add(LootItem.lootTableItem(ConsumableItems.SHURIKEN).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                    .add(EmptyLootItem.emptyItem().setWeight(2)))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                    .add(EmptyLootItem.emptyItem().setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(PotionItems.SHINE_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.REGENERATION_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.ARCHERY_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.MINING_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.GILLS_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.HUNTER_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(EmptyLootItem.emptyItem().setWeight(9)))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(Items.TORCH).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20)))))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(EmptyLootItem.emptyItem().setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(ModItems.SILVER_COIN).setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 8))))
                    .add(EmptyLootItem.emptyItem().setWeight(1)))
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.DESERT_PYRAMID,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.DUNERIDER_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(0.3f)))
                    .add(LootItem.lootTableItem(TCItems.SANDSTORM_IN_A_BOTTLE)
                        .when(LootItemRandomChanceCondition.randomChance(0.3f)))
                    .add(LootItem.lootTableItem(TCItems.FLYING_CARPET)
                        .when(LootItemRandomChanceCondition.randomChance(0.3f)))
            )
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.JUNGLE_TEMPLE,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.FERAL_CLAWS)
                        .when(LootItemRandomChanceCondition.randomChance(0.25f))
                    ).add(LootItem.lootTableItem(TCItems.ANKLET_OF_THE_WIND)
                        .when(LootItemRandomChanceCondition.randomChance(0.25f))
                    )
            )
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.RUINED_PORTAL,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(ModItems.MYSTERIOUS_NOTE).setWeight(5))
            )
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.SHIPWRECK_SUPPLY,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.AGLET)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.FLIPPER)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
            )
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.SHIPWRECK_TREASURE,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.AGLET)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.FLIPPER)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
            )
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.SPAWN_BONUS_CHEST,
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(SwordItems.COPPER_SHORT_SWORD)))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(AxeItems.COPPER_AXE)))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(PickaxeItems.COPPER_PICKAXE)))
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.STRONGHOLD_CORRIDOR,
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.FLASHLIGHT))
                    .when(LootItemRandomChanceCondition.randomChance(0.25f)))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.SHOT_PUT))
                    .when(LootItemRandomChanceCondition.randomChance(0.25f)))
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.STRONGHOLD_CROSSING,
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.FLASHLIGHT))
                    .when(LootItemRandomChanceCondition.randomChance(0.25f)))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.SHOT_PUT))
                    .when(LootItemRandomChanceCondition.randomChance(0.25f)))
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.UNDERWATER_RUIN_BIG,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.AGLET)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.FLIPPER)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
            )
        ));
        entries.add(new AddedChestLoot(
            BuiltInLootTables.UNDERWATER_RUIN_SMALL,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.AGLET)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.FLIPPER)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
                    .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS)
                        .when(LootItemRandomChanceCondition.randomChance(1.0f)))
            )
        ));

        // Trial Chambers loot tables
        LootTable.Builder trialChambersCorridor = LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(UniformGenerator.between(2, 2))
                .add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                .add(LootItem.lootTableItem(PotionItems.MANA_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                .add(LootItem.lootTableItem(Items.BREAD)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                .add(LootItem.lootTableItem(Items.ARROW)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 21))))
                .add(LootItem.lootTableItem(ConsumableItems.SHURIKEN)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 21))))
                .add(LootItem.lootTableItem(Items.APPLE)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                .add(LootItem.lootTableItem(Items.IRON_INGOT)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                .add(LootItem.lootTableItem(Items.TORCH)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 10))))
                .add(LootItem.lootTableItem(Items.OAK_LOG)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 7))))
                .add(LootItem.lootTableItem(PotionItems.RECALL_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
        );

        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/corridor")),
            trialChambersCorridor
        ));
        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/entrance")),
            trialChambersCorridor
        ));
        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/intersection")),
            trialChambersCorridor
        ));
        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/intersection_barrel")),
            LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(UniformGenerator.between(2, 2))
                    .add(LootItem.lootTableItem(Items.BREAD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                    .add(LootItem.lootTableItem(Items.APPLE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
            )
        ));

        LootTable.Builder trialChambersReward = LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3, 3))
                .add(LootItem.lootTableItem(PotionItems.THORNS_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.INFERNO_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.FEATHERFALL_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.SPELUNKER_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.MANA_REGENERATION_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.MAGIC_POWER_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.RECALL_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.HUNTER_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.LIFEFORCE_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.AMMO_RESERVATION_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.CRATE_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.SUMMONING_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(PotionItems.ENDURANCE_POTION)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                .add(LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                .add(LootItem.lootTableItem(MaterialItems.WINTER_MARROW)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                .add(LootItem.lootTableItem(MaterialItems.SPORE_ROOT)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                .add(LootItem.lootTableItem(MaterialItems.HEIM)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))));

        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward")),
            trialChambersReward
        ));
        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_ominous")),
            LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4, 4))
                    .add(LootItem.lootTableItem(PotionItems.THORNS_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.INFERNO_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.FEATHERFALL_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.SPELUNKER_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.MANA_REGENERATION_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.MAGIC_POWER_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.RECALL_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.HUNTER_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.LIFEFORCE_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.AMMO_RESERVATION_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.CRATE_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.SUMMONING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.ENDURANCE_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(MaterialItems.OPAL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                    .add(LootItem.lootTableItem(MaterialItems.GELSTONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                    .add(LootItem.lootTableItem(MaterialItems.COLD_CRYSTAL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                    .add(LootItem.lootTableItem(MaterialItems.AMBER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))))
        ));
        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/supply")),
            LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(PotionItems.MANA_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                    .add(LootItem.lootTableItem(Items.BREAD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 1))
                    .add(LootItem.lootTableItem(Items.ARROW)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 25)))))
        ));
        entries.add(new AddedChestLoot(
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/village/village_savanna_house")),
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(BowItems.HUNTING_BOW)
                        .when(LootItemRandomChanceCondition.randomChance(0.6f)))
            )
        ));
        return entries;
    }

    protected @NotNull ResourceKey<LootTable> getResourceKey(ResourceKey<LootTable> lootTable) {
        var path = getPath(lootTable);
        return Confluence.asResourceKey(Registries.LOOT_TABLE, path);
    }

    public @NotNull String getPath(ResourceKey<LootTable> lootTable) {
        return "with/" + lootTable.location().getPath();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        var entries = getAddedEntitiesLoot();
        for (var entry : entries) {
            output.accept(getResourceKey(entry.lootTable), entry.lootTableBuilder);
        }
    }

    @Override
    public List<String> getSyntheticLootTablePaths() {
        var paths = new ArrayList<String>();
        var entries = getAddedEntitiesLoot();
        for (var entry : entries) {
            paths.add(Confluence.asResource(getPath(entry.lootTable)).toString());
        }
        return paths;
    }

    public record AddedChestLoot(ResourceKey<LootTable> lootTable, LootTable.Builder lootTableBuilder) {}
}
