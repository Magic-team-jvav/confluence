package org.confluence.mod.common.data.gen.loot;

import com.google.common.collect.Streams;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.mixin.accessor.EntityLootSubProviderAccessor;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.confluence.mod.common.init.item.MaterialItems.RAW_DEMONITE;
import static org.confluence.mod.common.init.item.MaterialItems.SHADOW_SCALE;

public class EntitySubProvider extends EntityLootSubProvider {
    public EntitySubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        DateLootItemCondition.Builder halloweens = DateLootItemCondition.builder().from(Calendar.OCTOBER, 10).to(Calendar.NOVEMBER, 1);
        DateLootItemCondition.Builder christmas = DateLootItemCondition.builder().from(Calendar.DECEMBER, 15).to(Calendar.DECEMBER, 31);
        add(TEBossEntities.EATER_OF_WORLDS.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_worlds"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(ModItems.HEART).when(AllOfCondition.allOf(halloweens, christmas).invert()),
                                LootItem.lootTableItem(ModItems.CANDY_APPLE).when(halloweens),
                                LootItem.lootTableItem(ModItems.CANDY_CANE).when(christmas)
                        ).append(EmptyLootItem.emptyItem().setWeight(3)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(RAW_DEMONITE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SHADOW_SCALE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(TEMonsterEntities.GOBLIN_SCOUT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_scout"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TATTERED_CLOTH).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                      )
                );
        add(TEMonsterEntities.ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/antlion_swarmer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
        );
        add(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_antlion_swarmer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
        );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return Streams.concat(
                ModEntities.ENTITIES.getEntries().stream().map(DeferredHolder::get),
                TEEntities.ENTITIES.getEntries().stream().map(DeferredHolder::get)
        );
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        generate();
        EntityLootSubProviderAccessor accessor = (EntityLootSubProviderAccessor) this;
        Set<ResourceKey<LootTable>> set = new HashSet<>();
        getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach(holder -> {
            EntityType<?> entityType = holder.value();
            if (entityType.isEnabled(accessor.getAllowed())) {
                if (canHaveLootTable(entityType)) {
                    Map<ResourceKey<LootTable>, LootTable.Builder> map = accessor.getMap().remove(entityType);
                    if (map != null) {
                        map.forEach((key, builder) -> {
                            if (!set.add(key)) {
                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", key, holder.key().location()));
                            } else {
                                output.accept(key, builder);
                            }
                        });
                    }
                } else {
                    Map<ResourceKey<LootTable>, LootTable.Builder> map1 = accessor.getMap().remove(entityType);
                    if (map1 != null) {
                        throw new IllegalStateException(String.format(
                                Locale.ROOT,
                                "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                                map1.keySet().stream().map(p_335190_ -> p_335190_.location().toString()).collect(Collectors.joining(",")),
                                holder.key().location()
                        ));
                    }
                }
            }
        });
        if (!accessor.getMap().isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + accessor.getMap().keySet());
        }
    }
}
