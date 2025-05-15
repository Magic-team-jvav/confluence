package org.confluence.mod.common.data.gen.loot;

import com.google.common.collect.Streams;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.mixin.accessor.EntityLootSubProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.item.TEBoomerangItems;

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
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                      )
                );
        add(TEMonsterEntities.ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/antlion_swarmer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
        );
        add(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_antlion_swarmer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
        );
        add(TEMonsterEntities.ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.BIG_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.BIG_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_helmet_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_muscle_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.SHORT_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/short_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.DARK_CASTER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dark_caster"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.CURSED_SKULL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cursed_skull"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))).setWeight(97))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(2))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.NAZAR).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CREAM_SODA).setWeight(3))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(197))
                )
        );
        add(TEMonsterEntities.BLOOD_CRAWLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blood_crawler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.BLOOD_ZOMBIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blood_zombie"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setWeight(67).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
        );
        add(TEMonsterEntities.DRIPPLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/drippler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setWeight(67).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
        );
        add(TEMonsterEntities.BLOODY_SPORE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/bloody_spore"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLOOD_CLOT_POWDER))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.CAVE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cave_bat"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BAT_BAT).setWeight(4).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(996))
                )
        );
        add(TEMonsterEntities.SPORE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_bat"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BAT_BAT).setWeight(4).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(996))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TEBoomerangItems.SHROOMERANG).setWeight(5).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(195))
                )
        );
        add(TEMonsterEntities.CRIMSON_KEMERA.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crimson_kemera"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BURGER).setWeight(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(TEMonsterEntities.FACE_MONSTER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/face_monster"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.DECAYEDER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/decayeder"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_BONE))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.DEMON_EYE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/demon_eye"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LENS).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLACK_LENS).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(TEMonsterEntities.DEVOURER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/devourer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .setWeight(33)
                        .setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.WORM_TOOTH))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 8)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.DUNGEON_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dungeon_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                )
        );
        add(TEMonsterEntities.NYMPH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/nymph"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.METAL_DETECTOR))
                )
        );
        add(TEMonsterEntities.SNATCHER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/snatcher"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(TEMonsterEntities.MAN_EATER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/man_eater"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.MAN_EATER_VINE).setWeight(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(TEMonsterEntities.FLYING_FISH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/flying_fish"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FILAMENTOUS_FIN).setWeight(33).setQuality(1))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
        );
        add(TEMonsterEntities.EATER_OF_SOULS.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_souls"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).setWeight(33).setQuality(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BURGER).setWeight(1))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(ModLootTables.CORRUPTION_CARRY).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
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
