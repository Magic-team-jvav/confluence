package org.confluence.mod.common.data.gen.loot;

import com.google.common.collect.Streams;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.confluence.mod.common.init.item.MaterialItems.RAW_DEMONITE;
import static org.confluence.mod.common.init.item.MaterialItems.SHADOW_SCALE;

public final class EntitySubProvider extends EntityLootSubProvider {
    public EntitySubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        DateLootItemCondition.Builder halloweens = DateLootItemCondition.builder().from(Calendar.OCTOBER, 10).to(Calendar.NOVEMBER, 1);
        DateLootItemCondition.Builder christmas = DateLootItemCondition.builder().from(Calendar.DECEMBER, 15).to(Calendar.DECEMBER, 31);
        EnchantedCountIncreaseFunction.Builder random0To1 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        AlternativesEntry.Builder hearts = AlternativesEntry.alternatives(
                LootItem.lootTableItem(ModItems.HEART).when(AllOfCondition.allOf(halloweens, christmas).invert()),
                LootItem.lootTableItem(ModItems.CANDY_APPLE).when(halloweens),
                LootItem.lootTableItem(ModItems.CANDY_CANE).when(christmas)
        );
        LootItemConditionalFunction.Builder<?> count1To2 = SetItemCountFunction.setCount(UniformGenerator.between(1, 2));
        LootItemConditionalFunction.Builder<?> count2To5 = SetItemCountFunction.setCount(UniformGenerator.between(2, 5));
        LootItemConditionalFunction.Builder<?> count2To6 = SetItemCountFunction.setCount(UniformGenerator.between(2, 6));
        LootPoolSingletonContainer.Builder<?> emptyWeight98 = EmptyLootItem.emptyItem().setWeight(98);
        LootPoolSingletonContainer.Builder<?> boneWeight2 = LootItem.lootTableItem(Items.BONE).setWeight(2);

        add(TEBossEntities.EATER_OF_WORLDS.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_worlds"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(hearts.append(EmptyLootItem.emptyItem().setWeight(3))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(RAW_DEMONITE).apply(count2To5))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SHADOW_SCALE).apply(count1To2))
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_worlds_segment"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(hearts.append(EmptyLootItem.emptyItem().setWeight(3))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(RAW_DEMONITE).apply(count2To5))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SHADOW_SCALE).apply(count1To2))
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(TEMonsterEntities.VISUAL_NEURON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/visual_neuron"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(hearts.append(EmptyLootItem.emptyItem())))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 12))))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TISSUE_SAMPLE).apply(count2To5))
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(TEMonsterEntities.GOBLIN_SCOUT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_scout"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TATTERED_CLOTH).apply(count1To2)).apply(random0To1)
                )
        );
        add(TEMonsterEntities.ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/antlion_swarmer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2)).apply(random0To1)
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_antlion_swarmer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2).apply(random0To1))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2)).apply(random0To1)
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(TEMonsterEntities.ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.BIG_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.BIG_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_helmet_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_muscle_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.SHORT_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/short_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.DARK_CASTER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dark_caster"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(TEMonsterEntities.CURSED_SKULL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cursed_skull"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DUNGEON_DEMON_BONE).apply(count2To6).setWeight(97)).apply(random0To1)
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.NAZAR).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CREAM_SODA).setWeight(3)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(197))
                )
        );
        add(TEMonsterEntities.BLOOD_CRAWLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blood_crawler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
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
                        .add(LootItem.lootTableItem(MaterialItems.BLOOD_CLOT_POWDER)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.CAVE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cave_bat"), batCommon()
        );
        add(TEMonsterEntities.SPORE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_bat"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TEBoomerangItems.SHROOMERANG).setWeight(5).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(195))
                )
        );
        add(TEMonsterEntities.SPORE_ZOMBIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_zombie"), batCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                )
        );
        add(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hat_spore_zombie"), batCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                )
        );
        add(TEMonsterEntities.SPORE_SKELETON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_skeleton"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(random0To1).apply(count1To2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CARTON_OF_MILK).setWeight(67).apply(random0To1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BONE_SWORD).setQuality(1).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(9995))
                )
        );
        add(TEMonsterEntities.UNDEAD_VIKING.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/undead_viking"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(random0To1).apply(count1To2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArmorItems.VIKING_HELMET).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).setWeight(4))
                        .add(EmptyLootItem.emptyItem().setWeight(96))
                )
        );
        add(TEMonsterEntities.CRIMSON_KEMERA.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crimson_kemera"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BURGER)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(TEMonsterEntities.FACE_MONSTER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/face_monster"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.DECAYEDER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/decayeder"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_BONE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.DEMON_EYE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/demon_eye"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LENS).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLACK_LENS))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(TEMonsterEntities.DEVOURER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/devourer"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).apply(count1To2).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.WORM_TOOTH)).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 8))).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.DUNGEON_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dungeon_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY)).apply(random0To1)
                )
        );
        add(TEMonsterEntities.GOLDEN_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/golden_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.GOLDEN_COIN)).apply(SetItemCountFunction.setCount(ConstantValue.exactly(15)))
                )
        );
        add(TEMonsterEntities.NYMPH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/nymph"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.METAL_DETECTOR))
                )
        );
        add(TEMonsterEntities.SNATCHER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/snatcher"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(TEMonsterEntities.MAN_EATER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/man_eater"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.MAN_EATER_VINE)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(TEMonsterEntities.FLYING_FISH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/flying_fish"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FILAMENTOUS_FIN).setWeight(33).setQuality(1)).apply(count1To2).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
        );
        add(TEMonsterEntities.EATER_OF_SOULS.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_souls"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.BURGER)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(ModLootTables.CORRUPTION_CARRY).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(TEMonsterEntities.GIANT_SHELLY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_shelly"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(123))
                        .add(EmptyLootItem.emptyItem().setWeight(9877))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER).setWeight(125))
                        .add(EmptyLootItem.emptyItem().setWeight(9875))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.POTATO_CHIPS).setWeight(133)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                )
        );
        add(TEMonsterEntities.GIANT_WORM.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_worm"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.WHOOPIE_CUSHION).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(TEMonsterEntities.HARPY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/harpy"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GIANT_HARPY_FEATHER))
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HARPY_FEATHER))
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CHICKEN_NUGGET)).apply(random0To1)
                        .add(emptyWeight98)
                )
        );
        add(TEMonsterEntities.HELL_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hell_bat"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.MAGMA_STONE).setWeight(34))
                        .add(EmptyLootItem.emptyItem().setWeight(966))
                )
        );
        add(TEMonsterEntities.FIRE_IMP.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/fire_imp"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.OBSIDIAN_ROSE))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
        );
        add(TEMonsterEntities.DEMON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/demon"), LootTable.lootTable()
        );
        add(TEMonsterEntities.VOODOO_DEMON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/voodoo_demon"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.GUIDE_VOODOO_DOLL))
                )
        );
        add(TEMonsterEntities.HORNET.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hornet"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER)).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BEZOAR).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(TEMonsterEntities.ICE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/ice_bat"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
        );
        add(TEMonsterEntities.JUNGLE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/jungle_bat"), batCommon()
        );
        add(TEMonsterEntities.TOMB_CRAWLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/tomb_crawler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL)).apply(count1To2).apply(random0To1)
                )
        );
        add(TENpcEntities.MECHANIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/mechanic"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TEBoomerangItems.COMBAT_WRENCH))
                        .add(EmptyLootItem.emptyItem().setWeight(7))
                )
        );
    }

    private static LootTable.Builder batCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BAT_BAT).setWeight(4).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(996))
                );
    }


    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        return Streams.concat(
                ModEntities.ENTITIES.getEntries().stream().map(DeferredHolder::get),
                TEEntities.ENTITIES.getEntries().stream().map(DeferredHolder::get)
        );
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        generate();
        EntityLootSubProviderAccessor accessor = (EntityLootSubProviderAccessor) (Object) this;
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
