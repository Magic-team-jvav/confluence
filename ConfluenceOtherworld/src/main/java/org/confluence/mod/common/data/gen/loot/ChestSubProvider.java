package org.confluence.mod.common.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.CrateBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;

import java.util.function.BiConsumer;

public record ChestSubProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        // VanillaChestLoot
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        output.accept(Confluence.asResourceKey(Registries.LOOT_TABLE, "chests/frozen_chests"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE).setWeight(15))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS).setWeight(15))
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES).setWeight(15))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE).setWeight(15))
                        .add(LootItem.lootTableItem(TEBoomerangItems.ICE_BOOMERANG).setWeight(15))
                        .add(LootItem.lootTableItem(TGItems.SNOWBALL_CANNON).setWeight(15))
                        .add(LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR).setWeight(5))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.ICE_MIRROR))
                        .add(EmptyLootItem.emptyItem().setWeight(4))
                )
                .withPool(LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(ModLootTables.INITIAL_WORLD_UNDERGROUND_CHEST))
                )
        );
        // 困难模式前箱子通用
        output.accept(Confluence.asResourceKey(Registries.LOOT_TABLE, "chests/initial_world_underground_chest"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BOMB).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 19))))
                        .add(EmptyLootItem.emptyItem().setWeight(4))
                )
                // 缺天使雕像
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModBlocks.ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                        .add(EmptyLootItem.emptyItem().setWeight(4))
                )
                .withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                .add(LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                .add(LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                .add(EmptyLootItem.emptyItem().setWeight(4))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.SHURIKEN).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 49))))
                        .add(LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 49))))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PotionItems.REGENERATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.SHINE_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.ARCHERY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.GILLS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
                .withPool(LootPool.lootPool()
                       .add(LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20))))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(2))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 89))))
                        .add(EmptyLootItem.emptyItem())
                )
        );
    }
}