
package org.confluence.terraentity.data.gen.loot;

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
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.terraentity.TerraEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record TENPCLoot(HolderLookup.Provider registries) implements LootTableSubProvider {

    public static ResourceKey<LootTable> Angler = ResourceKey.create(Registries.LOOT_TABLE, TerraEntity.space("npc/angler"));

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        output.accept(Angler, LootTable.lootTable()
                        .withPool(addIngotsPool.apply(LootPool.lootPool()))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.FISHING_ROD).setWeight(10))
                        .add(EmptyLootItem.emptyItem().setWeight(50))
                )
        );



        this.spawnerLootTables(output);
    }
    /**
     * 添加各种锭
     */
    static Function<LootPool.Builder, LootPool.Builder> addIngotsPool = (builder) -> builder
            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
            .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))))
            .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))))
            .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(3)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
            .add(LootItem.lootTableItem(Items.COAL).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))));

    public void spawnerLootTables(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

    }


    public HolderLookup.Provider registries() {
        return this.registries;
    }
}
