package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.mixin.data.loot.EntityLootSubProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Generates entities loot tables into loot_modifier path specified by ModLootModifiersProvider i.e. confluence/loot_table/with/entities/bat.json
///
/// @see org.confluence.mod.common.data.gen.ModLootModifiersProvider
public class AddEntityLootConfluenceSubProvider extends EntityLootSubProvider implements SyntheticLootTableProvider {
    public AddEntityLootConfluenceSubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    public List<AddedEntityLoot> getAddedEntitiesLoot() {
        List<AddedEntityLoot> entries = new ArrayList<>();
        entries.add(new AddedEntityLoot(EntityType.BAT,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(SwordItems.BAT_BAT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.003f))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.ELDER_GUARDIAN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TCItems.HAND_DRILL)
                                        .when(LootItemRandomChanceCondition.randomChance(1.0f))
                                        .when(DamageSourceCondition.hasDamageSource(
                                                DamageSourcePredicate.Builder.damageType()
                                                        .isDirect(true)
                                                        .direct(EntityPredicate.Builder.entity()
                                                                .of(EntityType.PLAYER)
                                                        )
                                        ))
                                )
                )
        ));
        LootItemCondition.Builder playerHasSmeltsLootEnchantment = LootItemEntityPropertyCondition.hasProperties(
                LootContext.EntityTarget.DIRECT_ATTACKER,
                EntityPredicate.Builder.entity()
                        .equipment(
                                EntityEquipmentPredicate.Builder.equipment()
                                        .mainhand(ItemPredicate.Builder.item()
                                                .withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(
                                                                List.of(new EnchantmentPredicate(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.SMELTS_LOOT), MinMaxBounds.Ints.atLeast(1)))
                                                        )
                                                )
                                        )
                        )
        );
        entries.add(new AddedEntityLoot(EntityType.FROG,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.RAW_FROG)
                                        .apply(SmeltItemFunction.smelted()
                                                .when(AnyOfCondition.anyOf(
                                                        LootItemEntityPropertyCondition.hasProperties(
                                                                LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity()
                                                                        .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))
                                                        ),
                                                        playerHasSmeltsLootEnchantment
                                                ))
                                        )
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                                this.registries,
                                                UniformGenerator.between(0.0f, 1.0f)
                                        ))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.PARROT,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.RAW_BIRD)
                                        .apply(SmeltItemFunction.smelted()
                                                .when(AnyOfCondition.anyOf(
                                                        LootItemEntityPropertyCondition.hasProperties(
                                                                LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity()
                                                                        .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))
                                                        ),
                                                        playerHasSmeltsLootEnchantment
                                                ))
                                        )
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                                this.registries,
                                                UniformGenerator.between(0.0f, 1.0f)
                                        ))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.PIGLIN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.DONGDONGS_FLATBREAD)
                                        .when(LootItemRandomChanceCondition.randomChance(0.004f))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.SKELETON,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(SwordItems.BONE_SWORD)
                                        .when(LootItemRandomChanceCondition.randomChance(0.0049f))
                                )
                ).withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.CARTON_OF_MILK)
                                        .when(LootItemRandomChanceCondition.randomChance(0.0067f))
                                )
                ).withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(MaterialItems.HOOK)
                                        .when(LootItemRandomChanceCondition.randomChance(0.04f))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.SLIME,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(NestedLootTable.lootTableReference(
                                        ModLootTables.SLIME_CARRY
                                ).setWeight(1))
                                .add(EmptyLootItem.emptyItem().setWeight(19))
                ).withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(2))
                                .add(EmptyLootItem.emptyItem().setWeight(98))
                ).withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TESummonItems.SLIME_STAFF).setWeight(1))
                                .add(EmptyLootItem.emptyItem().setWeight(6999))
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.WARDEN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TESummonItems.SCULK_WISP_STAFF).setWeight(1))
                                .add(EmptyLootItem.emptyItem().setWeight(4))
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.WITHER_SKELETON,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TCItems.HOLY_WATER)
                                        .when(LootItemRandomChanceCondition.randomChance(0.06f))
                                        .when(DamageSourceCondition.hasDamageSource(
                                                DamageSourcePredicate.Builder.damageType()
                                                        .isDirect(true)
                                                        .direct(EntityPredicate.Builder.entity()
                                                                .of(EntityType.PLAYER)
                                                        )
                                        ))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.ZOMBIE,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(SwordItems.ZOMBIE_ARM)
                                        .when(LootItemRandomChanceCondition.randomChance(0.004f))
                                )
                ).withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TCItems.SHACKLE)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                )
                )
        ));
        entries.add(new AddedEntityLoot(EntityType.ZOMBIFIED_PIGLIN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(TCItems.ENERGY_BAR).setWeight(2))
                                .add(EmptyLootItem.emptyItem().setWeight(98))
                )
        ));
        return entries;
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        var entries = getAddedEntitiesLoot();
        List<EntityType<?>> entityTypes = new ArrayList<>();
        for (var entry : entries) {
            entityTypes.add(entry.entityType);
        }
        return entityTypes.stream();
    }

    @Override
    public void generate() {
        var entries = getAddedEntitiesLoot();
        for (var entry : entries) {
            add(entry.entityType, getResourceKey(entry.entityType), entry.lootTableBuilder);
        }
    }

    protected @NotNull ResourceKey<LootTable> getResourceKey(EntityType<?> entityType) {
        var path = getPath(entityType);
        return Confluence.asResourceKey(Registries.LOOT_TABLE, path);
    }

    public @NotNull String getPath(EntityType<?> entityType) {
        return "with/" + entityType.getDefaultLootTable().location().getPath();
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

    @Override
    public List<String> getSyntheticLootTablePaths() {
        var entries = getAddedEntitiesLoot();
        List<String> paths = new ArrayList<>();
        for (var entry : entries) {
            paths.add(Confluence.asResource(getPath(entry.entityType)).toString());
        }
        return paths;
    }

    public record AddedEntityLoot(EntityType<?> entityType, LootTable.Builder lootTableBuilder) {}
}
