package org.confluence.mod.common.data.gen.loot;

import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.common.loot.GamePhaseLootItemCondition;
import org.confluence.mod.mixin.data.loot.EntityLootSubProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;

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
        EnchantedCountIncreaseFunction.Builder random3To4 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(3.0F, 4.0F));
        AlternativesEntry.Builder hearts = AlternativesEntry.alternatives(
                LootItem.lootTableItem(ModItems.HEART).when(AllOfCondition.allOf(halloweens, christmas).invert()),
                LootItem.lootTableItem(ModItems.CANDY_APPLE).when(halloweens),
                LootItem.lootTableItem(ModItems.CANDY_CANE).when(christmas)
        );
        GamePhaseLootItemCondition.Builder afterSkeletronBehindWallOfFlesh = GamePhaseLootItemCondition.builder().from(GamePhase.AFTER_SKELETRON).to(GamePhase.WALL_OF_FLESH, false);
        GamePhaseLootItemCondition.Builder beforeSkeletronBehindWallOfFlesh = GamePhaseLootItemCondition.builder().from(GamePhase.BEFORE_SKELETRON, true).to(GamePhase.WALL_OF_FLESH, false);
        LootItemConditionalFunction.Builder<?> count1To2 = SetItemCountFunction.setCount(UniformGenerator.between(1, 2));
        LootItemConditionalFunction.Builder<?> count2To5 = SetItemCountFunction.setCount(UniformGenerator.between(2, 5));
        LootItemConditionalFunction.Builder<?> count2To6 = SetItemCountFunction.setCount(UniformGenerator.between(2, 6));
        LootPoolSingletonContainer.Builder<?> emptyWeight98 = EmptyLootItem.emptyItem().setWeight(98);
        LootPoolSingletonContainer.Builder<?> boneWeight2 = LootItem.lootTableItem(Items.BONE).setWeight(2);

        add(BossEntities.EATER_OF_WORLDS.get(), Confluence.asResource("entities/terra_entity/eater_of_worlds"), LootTable.lootTable()
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
        add(BossEntities.EATER_OF_WORLDS_SEGMENT.get(), Confluence.asResource("entities/terra_entity/eater_of_worlds_segment"), LootTable.lootTable()
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
        add(MonsterEntities.VISUAL_NEURON.get(), Confluence.asResource("entities/terra_entity/visual_neuron"), LootTable.lootTable()
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
        add(MonsterEntities.GOBLIN_SCOUT.get(), Confluence.asResource("entities/terra_entity/goblin_scout"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TATTERED_CLOTH).apply(count1To2)).apply(random0To1)
                )
        );
        add(MonsterEntities.ANTLION_SWARMER.get(), Confluence.asResource("entities/terra_entity/antlion_swarmer"), LootTable.lootTable()
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
        add(MonsterEntities.GIANT_ANTLION_SWARMER.get(), Confluence.asResource("entities/terra_entity/giant_antlion_swarmer"), LootTable.lootTable()
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
        add(MonsterEntities.ANGER_BONES.get(), Confluence.asResource("entities/terra_entity/anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_ANGER_BONES.get(), Confluence.asResource("entities/terra_entity/big_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_BONES.get(), Confluence.asResource("entities/terra_entity/big_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), Confluence.asResource("entities/terra_entity/big_helmet_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), Confluence.asResource("entities/terra_entity/big_muscle_anger_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.SHORT_BONES.get(), Confluence.asResource("entities/terra_entity/short_bones"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.DARK_CASTER.get(), Confluence.asResource("entities/terra_entity/dark_caster"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(9967))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.CURSED_SKULL.get(), Confluence.asResource("entities/terra_entity/cursed_skull"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
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
        add(MonsterEntities.BLOOD_CRAWLER.get(), Confluence.asResource("entities/terra_entity/blood_crawler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(MonsterEntities.BLOOD_ZOMBIE.get(), Confluence.asResource("entities/terra_entity/blood_zombie"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setWeight(67).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PetItems.WALLET.get()).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(995))
                )
        );
        add(MonsterEntities.DRIPPLER.get(), Confluence.asResource("entities/terra_entity/drippler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setWeight(67).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PetItems.WALLET.get()).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(995))
                )
        );
        add(MonsterEntities.BLOODY_SPORE.get(), Confluence.asResource("entities/terra_entity/bloody_spore"), LootTable.lootTable()
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
        add(MonsterEntities.CAVE_BAT.get(), Confluence.asResource("entities/terra_entity/cave_bat"), batCommon()
        );
        add(MonsterEntities.SPORE_BAT.get(), Confluence.asResource("entities/terra_entity/spore_bat"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BoomerangItems.SHROOMERANG).setWeight(5).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(195))
                )
        );
        add(MonsterEntities.SPORE_ZOMBIE.get(), Confluence.asResource("entities/terra_entity/spore_zombie"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                )
        );
        add(MonsterEntities.HAT_SPORE_ZOMBIE.get(), Confluence.asResource("entities/terra_entity/hat_spore_zombie"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                )
        );
        add(MonsterEntities.SPORE_SKELETON.get(), Confluence.asResource("entities/terra_entity/spore_skeleton"), LootTable.lootTable()
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
        add(MonsterEntities.UNDEAD_VIKING.get(), Confluence.asResource("entities/terra_entity/undead_viking"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(random0To1).apply(count1To2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArmorItems.VIKING_HELMET).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).setWeight(4))
                        .add(EmptyLootItem.emptyItem().setWeight(96))
                )
        );
        add(MonsterEntities.CRIMERA.get(), Confluence.asResource("entities/terra_entity/crimera"), LootTable.lootTable()
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
        add(MonsterEntities.FACE_MONSTER.get(), Confluence.asResource("entities/terra_entity/face_monster"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(MonsterEntities.DECAYEDER.get(), Confluence.asResource("entities/terra_entity/decayeder"), LootTable.lootTable()
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
        add(MonsterEntities.DEMON_EYE.get(), Confluence.asResource("entities/terra_entity/demon_eye"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LENS).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLACK_LENS))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.DEVOURER.get(), Confluence.asResource("entities/terra_entity/devourer"), LootTable.lootTable()
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
        add(MonsterEntities.DUNGEON_SLIME.get(), Confluence.asResource("entities/terra_entity/dungeon_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY)).apply(random0To1)
                )
        );
        add(MonsterEntities.HONEY_SLIME.get(), Confluence.asResource("entities/terra_entity/honey_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.HONEY_GUMMI)).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 7))).apply(random3To4)
                )
        );
        add(MonsterEntities.GOLDEN_SLIME.get(), Confluence.asResource("entities/terra_entity/golden_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN)).apply(SetItemCountFunction.setCount(ConstantValue.exactly(15)))
                )
        );
        add(MonsterEntities.NYMPH.get(), Confluence.asResource("entities/terra_entity/nymph"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.METAL_DETECTOR))
                )
        );
        add(MonsterEntities.SNATCHER.get(), Confluence.asResource("entities/terra_entity/snatcher"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(MonsterEntities.MAN_EATER.get(), Confluence.asResource("entities/terra_entity/man_eater"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.MAN_EATER_VINE)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(MonsterEntities.FLYING_FISH.get(), Confluence.asResource("entities/terra_entity/flying_fish"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FILAMENTOUS_FIN).setWeight(33).setQuality(1)).apply(count1To2).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
        );
        add(MonsterEntities.EATER_OF_SOULS.get(), Confluence.asResource("entities/terra_entity/eater_of_souls"), LootTable.lootTable()
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
                        .add(DynamicLoot.dynamicEntry(ModLootTables.CORRUPTION_CARRY).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(MonsterEntities.GIANT_SHELLY.get(), Confluence.asResource("entities/terra_entity/giant_shelly"), LootTable.lootTable()
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
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(YoyoItems.RALLY).setWeight(667))
                        .add(EmptyLootItem.emptyItem().setWeight(9333))
                )
        );
        add(MonsterEntities.CRAWDAD.get(), Confluence.asResource("entities/terra_entity/crawdad"), LootTable.lootTable()
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
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(YoyoItems.RALLY).setWeight(667))
                        .add(EmptyLootItem.emptyItem().setWeight(9333))
                )
        );
        add(MonsterEntities.GIANT_WORM.get(), Confluence.asResource("entities/terra_entity/giant_worm"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.WHOOPIE_CUSHION).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(MonsterEntities.HARPY.get(), Confluence.asResource("entities/terra_entity/harpy"), LootTable.lootTable()
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
        add(MonsterEntities.HELL_BAT.get(), Confluence.asResource("entities/terra_entity/hell_bat"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.MAGMA_STONE).setWeight(34))
                        .add(EmptyLootItem.emptyItem().setWeight(966))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
        );
        add(MonsterEntities.FIRE_IMP.get(), Confluence.asResource("entities/terra_entity/fire_imp"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.OBSIDIAN_ROSE))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
        );
        add(MonsterEntities.DEMON.get(), Confluence.asResource("entities/terra_entity/demon"), LootTable.lootTable()
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.DEMON_SCYTHE).setWeight(286))
                        .add(EmptyLootItem.emptyItem().setWeight(9714))
                )
        );
        add(MonsterEntities.VOODOO_DEMON.get(), Confluence.asResource("entities/terra_entity/voodoo_demon"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.GUIDE_VOODOO_DOLL))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.DEMON_SCYTHE).setWeight(286))
                        .add(EmptyLootItem.emptyItem().setWeight(9714))
                )
        );
        add(MonsterEntities.HORNET.get(), Confluence.asResource("entities/terra_entity/hornet"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER)).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BEZOAR).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(MonsterEntities.ICE_BAT.get(), Confluence.asResource("entities/terra_entity/ice_bat"), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
        );
        add(MonsterEntities.SNOW_FLINX.get(), Confluence.asResource("entities/terra_entity/snow_flinx"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FLINX_FUR)).apply(random0To1).apply(count1To2)
                )
        );
        add(MonsterEntities.JUNGLE_BAT.get(), Confluence.asResource("entities/terra_entity/jungle_bat"), batCommon()
        );
        add(MonsterEntities.PIRANHA.get(), Confluence.asResource("entities/terra_entity/piranha"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(133))
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(967))
                )
        );
        add(MonsterEntities.SHARK.get(), Confluence.asResource("entities/terra_entity/shark"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP_PO_BOY).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DIVING_HELMET).setWeight(1))
                        .add(LootItem.lootTableItem(MaterialItems.SHARK_FIN).setWeight(19))
                )
        );
        add(MonsterEntities.TOMB_CRAWLER.get(), Confluence.asResource("entities/terra_entity/tomb_crawler"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL)).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BONE_SERPENT.get(), Confluence.asResource("entities/terra_entity/bone_serpent"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE_BLOCK)).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.HOTDOG).setWeight(333)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(667))
                )
        );
        add(MonsterEntities.WITHER_BONE_SERPENT.get(), Confluence.asResource("entities/terra_entity/wither_bone_serpent"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE_BLOCK)).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.COAL_BLOCK)).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.HOTDOG).setWeight(333)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(667))
                )
        );
        add(MonsterEntities.ANGER_GOBLIN.get(), Confluence.asResource("entities/terra_entity/anger_goblin"), goblinCommon()
        );
        add(MonsterEntities.GOBLIN_ARCHER.get(), Confluence.asResource("entities/terra_entity/goblin_archer"), goblinCommon()
        );
        add(MonsterEntities.GOBLIN_PEON.get(), Confluence.asResource("entities/terra_entity/goblin_peon"), goblinCommon()
        );
        add(MonsterEntities.GOBLIN_SORCERER.get(), Confluence.asResource("entities/terra_entity/goblin_sorcerer"), goblinCommon()
        );
        add(MonsterEntities.GOBLIN_THIEF.get(), Confluence.asResource("entities/terra_entity/goblin_thief"), goblinCommon()
        );
        add(MonsterEntities.GOBLIN_WARRIOR.get(), Confluence.asResource("entities/terra_entity/goblin_warrior"), goblinCommon()
        );
        add(NpcEntities.MECHANIC.get(), Confluence.asResource("entities/terra_entity/mechanic"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BoomerangItems.COMBAT_WRENCH))
                        .add(EmptyLootItem.emptyItem().setWeight(7))
                )
        );
        add(NpcEntities.DYE_TRADER.get(), Confluence.asResource("entities/terra_entity/dye_trader"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.EXOTIC_SCIMITAR))
                        .add(EmptyLootItem.emptyItem().setWeight(7))
                )
        );
        add(NpcEntities.TRAVELING_MERCHANT.get(), Confluence.asResource("entities/terra_entity/traveling_merchant"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.PEDDLERS_HAT))
                )
        );
        add(NpcEntities.CLOTHIER.get(), Confluence.asResource("entities/terra_entity/clothier"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.CLOTHIERS_HAT))
                )
        );
        add(CritterEntities.DUCK.get(), Confluence.asResource("entities/terra_entity/duck"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_DUCK).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.BIRD.get(), Confluence.asResource("entities/terra_entity/bird"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_BIRD).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.BLUE_JAY.get(), Confluence.asResource("entities/terra_entity/blue_jay"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_BIRD).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.SQUIRREL.get(), Confluence.asResource("entities/terra_entity/squirrel"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_SQUIRREL).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.CARDINAL.get(), Confluence.asResource("entities/terra_entity/cardinal"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_BIRD).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.BUNNY.get(), Confluence.asResource("entities/terra_entity/bunny"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.RABBIT).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.CRAB.get(), Confluence.asResource("entities/terra_entity/crab"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP_PO_BOY).setWeight(2))
                        .add(emptyWeight98).apply(random0To1)
                )
        );
        add(MonsterEntities.GRANITE_ELEMENTAL.get(), Confluence.asResource("entities/terra_entity/granite_elemental"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SPAGHETTI).setWeight(2))
                        .add(emptyWeight98).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(NatureBlocks.GRANITE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))).apply(random0To1)
                )
        );
        add(MonsterEntities.METEOR_HEAD.get(), Confluence.asResource("entities/terra_entity/meteor_head"), LootTable.lootTable()
                .withPool(LootPool.lootPool().when(beforeSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(MaterialItems.RAW_METEORITE).setWeight(2))
                        .add(emptyWeight98).apply(random0To1)
                )
        );
        add(MonsterEntities.BLUE_SLIME.get(), Confluence.asResource("entities/terra_entity/blue_slime"), slimeCommon(-10644993));
        add(MonsterEntities.DESERT_SLIME.get(), Confluence.asResource("entities/terra_entity/desert_slime"), slimeCommon(-2727));
        add(MonsterEntities.GREEN_DUMPLING_SLIME.get(), Confluence.asResource("entities/terra_entity/green_dumpling_slime"), slimeCommon(-8470674)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.GREEN_DUMPLING.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(MonsterEntities.GREEN_SLIME.get(), Confluence.asResource("entities/terra_entity/green_slime"), slimeCommon(-8470674));
        add(MonsterEntities.PURPLE_SLIME.get(), Confluence.asResource("entities/terra_entity/purple_slime"), slimeCommon(-6326333));
        add(MonsterEntities.RED_SLIME.get(), Confluence.asResource("entities/terra_entity/red_slime"), slimeCommon(-1079407));
        add(MonsterEntities.YELLOW_SLIME.get(), Confluence.asResource("entities/terra_entity/yellow_slime"), slimeCommon(-871089));
        add(MonsterEntities.JUNGLE_SLIME.get(), Confluence.asResource("entities/terra_entity/jungle_slime"), slimeCommon(-6570130));
        add(MonsterEntities.ICE_SLIME.get(), Confluence.asResource("entities/terra_entity/ice_slime"), slimeCommon(-10628609)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get())
                                .when(DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType()
                                                .tag(TagPredicate.is(
                                                        DamageTypeTags.IS_FALL
                                                ))
                                ))
                                .setWeight(1)
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(49))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get())
                                .when(AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                        .of(EntityType.PLAYER)
                                        .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                .mainhand(ItemPredicate.Builder.item()
                                                        .withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(
                                                                List.of(new EnchantmentPredicate(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FROST_WALKER), MinMaxBounds.Ints.atLeast(1)))))))))))
                        .add(EmptyLootItem.emptyItem().setWeight(14))
                )
        );
        add(MonsterEntities.BLACK_SLIME.get(), Confluence.asResource("entities/terra_entity/black_slime"), slimeCommon(-7697782)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.TROPIC_SLIME.get(), Confluence.asResource("entities/terra_entity/tropic_slime"), slimeCommon(-10644993)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.TROPICAL_FISH))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
        );
        add(MonsterEntities.PINK_SLIME.get(), Confluence.asResource("entities/terra_entity/pink_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(DynamicLoot.dynamicEntry(ModLootTables.SLIME_CARRY))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.PINK_GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 30)))
                )
        );
        add(MonsterEntities.SWAMP_SLIME.get(), Confluence.asResource("entities/terra_entity/swamp_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(DynamicLoot.dynamicEntry(ModLootTables.SLIME_CARRY))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(6999))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.SKELETON_SKULL))
                        .add(EmptyLootItem.emptyItem().setWeight(39))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.SLIME_BALL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                )
        );
        add(MonsterEntities.SPIKED_SLIME.get(), Confluence.asResource("entities/terra_entity/spiked_slime"), slimeCommon(-10644993)
        );
        add(MonsterEntities.SPIKED_ICE_SLIME.get(), Confluence.asResource("entities/terra_entity/spiked_ice_slime"), slimeCommon(-10628609)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(933))
                )
        );
        add(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), Confluence.asResource("entities/terra_entity/spiked_jungle_slime"), slimeCommon(-6570130)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(MonsterEntities.BLUE_JELLYFISH.get(), Confluence.asResource("entities/terra_entity/blue_jellyfish"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.PINK_JELLYFISH.get(), Confluence.asResource("entities/terra_entity/pink_jellyfish"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        // 肉后怪
        add(MonsterEntities.WYVERN.get(), Confluence.asResource("entities/terra_entity/wyvern"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN)).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.SOUL_OF_FLIGHT)).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20))).apply(random0To1)
                )
        );
        add(MonsterEntities.PIXIE.get(), Confluence.asResource("entities/terra_entity/pixie"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.PIXIE_DUST)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(random0To1)
                )
        );
        add(MonsterEntities.WRAITH.get(), Confluence.asResource("entities/terra_entity/wraith"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(MonsterEntities.GREEN_JELLYFISH.get(), Confluence.asResource("entities/terra_entity/green_jellyfish"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.LUMINOUS_SLIME.get(), Confluence.asResource("entities/terra_entity/luminous_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(14))
                        .add(EmptyLootItem.emptyItem().setWeight(9986))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.APPLE_PIE).setWeight(67))
                        .add(EmptyLootItem.emptyItem().setWeight(9933)).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -4040988))))
                )
        );
        add(MonsterEntities.CRIMSLIME.get(), Confluence.asResource("entities/terra_entity/crimslime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -3386287))))
                )
        );
        add(MonsterEntities.CORRUPT_SLIME.get(), Confluence.asResource("entities/terra_entity/corrupt_slime"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -6522185))))
                )
        );
        // 宝箱怪
        add(MonsterEntities.WOODEN_MIMIC.get(), Confluence.asResource("entities/terra_entity/wooden_mimic"), mimicCommon()
        );
        add(MonsterEntities.GOLDEN_MIMIC.get(), Confluence.asResource("entities/terra_entity/golden_mimic"), mimicCommon()
        );
        add(MonsterEntities.SHADOW_MIMIC.get(), Confluence.asResource("entities/terra_entity/shadow_mimic"), mimicCommon()
        );
        add(MonsterEntities.ICE_MIMIC.get(), Confluence.asResource("entities/terra_entity/ice_mimic"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 冰雪弓
                        .add(LootItem.lootTableItem(ManaWeaponItems.FLOWER_OF_FROST))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(25)))
                ))
        );
        // todo秘密种子冰雪宝箱怪使用这个common
        /*
        add(MonsterEntities.ICE_MIMIC.get(), Confluence.asResource("entities/terra_entity/ice_mimic"),LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 玩具雪橇
                        // 冰雪弓
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS))
                        .add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE))
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))
                ))
        );
        */
        add(MonsterEntities.CRIMSON_MIMIC.get(), Confluence.asResource("entities/terra_entity/crimson_mimic"), bigMimicCommon()
                .withPool(LootPool.lootPool()
                        // 夺命杖
                        // 飞镖手枪
                        // 臭虎爪
                        .add(LootItem.lootTableItem(HookItems.TENDON_HOOK))
                        .add(LootItem.lootTableItem(TCItems.FLESH_KNUCKLES))
                )
        );
        add(MonsterEntities.CORRUPT_MIMIC.get(), Confluence.asResource("entities/terra_entity/corrupt_mimic"), bigMimicCommon()
                .withPool(LootPool.lootPool()
                        // 爬藤怪法杖
                        // 飞镖步枪
                        // 铁链血滴子
                        .add(LootItem.lootTableItem(HookItems.WORM_HOOK))
                        .add(LootItem.lootTableItem(TCItems.PUTRID_SCENT))
                )
        );
        add(MonsterEntities.HALLOWED_MIMIC.get(), Confluence.asResource("entities/terra_entity/hallowed_mimic"), bigMimicCommon()
                .withPool(LootPool.lootPool()
                        // 飞刀
                        .add(LootItem.lootTableItem(ManaWeaponItems.CRYSTAL_VILE_SHARD))
                        .add(LootItem.lootTableItem(BowItems.DAEDALUS_STORM_BOW))
                        .add(LootItem.lootTableItem(HookItems.ILLUMINANT_HOOK))
                )
        );
        add(MonsterEntities.JUNGLE_MIMIC.get(), Confluence.asResource("entities/terra_entity/jungle_mimic"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FART_IN_A_JAR))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.GOLDEN_SHOWER))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModBlocks.POO))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                // 天使雕像
                // 水枪
                // 闪耀史莱姆气球
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.COAL))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                        .add(EmptyLootItem.emptyItem().setWeight(2)
                        ))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PotionItems.RED_POTION))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5)))
                        .add(EmptyLootItem.emptyItem().setWeight(2)
                        ))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PotionItems.STINK_POTION))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10)))
                        .add(EmptyLootItem.emptyItem().setWeight(2)
                        ))

                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BaitItems.MASTER_BAIT))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .add(EmptyLootItem.emptyItem().setWeight(2)
                        ))
        );
        add(MonsterEntities.MUMMY.get(), Confluence.asResource("entities/terra_entity/mummy"), mummyCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.DARK_MUMMY.get(), Confluence.asResource("entities/terra_entity/dark_mummy"), mummyCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DARK_SHARD))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
        );
        add(MonsterEntities.BLOOD_MUMMY.get(), Confluence.asResource("entities/terra_entity/blood_mummy"), mummyCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DARK_SHARD))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
        );
        add(MonsterEntities.LIGHT_MUMMY.get(), Confluence.asResource("entities/terra_entity/light_mummy"), mummyCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.TRIFOLD_MAP))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LIGHT_SHARD))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
        );
        add(MonsterEntities.DERPLING.get(), Confluence.asResource("entities/terra_entity/derpling"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.GRAPE).setWeight(25))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(975))
                )
        );
        add(MonsterEntities.GHOUL.get(), Confluence.asResource("entities/terra_entity/ghoul"), ghoulCommon()
        );
        add(MonsterEntities.VILE_GHOUL.get(), Confluence.asResource("entities/terra_entity/vile_ghoul"), ghoulCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DARK_SHARD))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DARK_SHARD).setWeight(667))
                        .add(EmptyLootItem.emptyItem().setWeight(9333))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModBlocks.CURSED_FLAME))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
        );
        add(MonsterEntities.TAINTED_GHOUL.get(), Confluence.asResource("entities/terra_entity/tainted_ghoul"), ghoulCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DARK_SHARD))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DARK_SHARD).setWeight(667))
                        .add(EmptyLootItem.emptyItem().setWeight(9333))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ICHOR))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
        );
        add(MonsterEntities.DREAMER_GHOUL.get(), Confluence.asResource("entities/terra_entity/dreamer_ghoul"), ghoulCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LIGHT_SHARD).setWeight(667))
                        .add(EmptyLootItem.emptyItem().setWeight(9333))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ICHOR))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
        );
        add(MonsterEntities.DREAMER_GHOUL.get(), Confluence.asResource("entities/terra_entity/dreamer_ghoul"), ghoulCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LIGHT_SHARD).setWeight(667))
                        .add(EmptyLootItem.emptyItem().setWeight(9333))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ICHOR))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
        );
        add(MonsterEntities.SAND_POACHER.get(), Confluence.asResource("entities/terra_entity/sand_poacher"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.FRIED_EGG).setWeight(333))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        LootPool.Builder rainbowSheep = LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.MUTTON)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F))));
        add(ModEntities.RAINBOW_SHEEP.get(), LootTable.lootTable().withPool(rainbowSheep));
        add(ModEntities.RAINBOW_SHEEP.get(), ModLootTables.SHEEP_RAINBOW_WOOL, LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.RAINBOW_WOOL)))
                .withPool(rainbowSheep)
        );
    }

    private static LootTable.Builder ghoulCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANCIENT_CLOTH))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                );
    }

    private static LootTable.Builder mummyCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_MASK).setWeight(133))
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_SHIRT).setWeight(133))
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_PANTS).setWeight(133))
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_SHOES).setWeight(133))
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                );
    }

    private static LootTable.Builder mimicCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(HookItems.DUAL_HOOK))
                        .add(LootItem.lootTableItem(ManaWeaponItems.MAGIC_DAGGER))
                        .add(LootItem.lootTableItem(AccessoryItems.PHILOSOPHERS_STONE))
                        .add(LootItem.lootTableItem(TCItems.TITAN_GLOVE))
                        .add(LootItem.lootTableItem(TCItems.STAR_CLOAK))
                        .add(LootItem.lootTableItem(TCItems.CROSS_NECKLACE))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(25)))
                ));
    }

    private static LootTable.Builder bigMimicCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_MANA_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(7)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(7)))
                ));
    }

    private static LootTable.Builder mimicCommonSecret() {  // todo秘密种子宝箱怪使用这个common
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BAND_OF_REGENERATION))
                        .add(LootItem.lootTableItem(TCItems.MAGIC_MIRROR))
                        .add(LootItem.lootTableItem(TCItems.CLOUD_IN_A_BOTTLE))
                        .add(LootItem.lootTableItem(TCItems.HERMES_BOOTS))
                        .add(LootItem.lootTableItem(TCItems.SHOE_SPIKES))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5)))
                ));
    }

    private static LootTable.Builder batCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BAT_BAT).setWeight(3).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(997))
                );
    }

    private LootTable.Builder slimeCommon(int gelColor) {
        EnchantedCountIncreaseFunction.Builder random0To1 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(DynamicLoot.dynamicEntry(ModLootTables.SLIME_CARRY))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(6999))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", gelColor))))
                );
    }

    private LootTable.Builder goblinCommon() {
        LootItemConditionalFunction.Builder<?> count1To5 = SetItemCountFunction.setCount(UniformGenerator.between(1, 5));
        EnchantedCountIncreaseFunction.Builder random0To1 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.SPIKY_BALL)).apply(count1To5).apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                );
    }


    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.getEntities().stream().map(DeferredRegister::getEntries).flatMap(Collection::stream).map(RegistryObject::get);
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        generate();
        EntityLootSubProviderAccessor accessor = (EntityLootSubProviderAccessor) (Object) this;
        Set<ResourceLocation> set = new HashSet<>();
        getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach(holder -> {
            EntityType<?> entityType = holder.value();
            if (entityType.isEnabled(accessor.getAllowed())) {
                if (canHaveLootTable(entityType)) {
                    Map<ResourceLocation, LootTable.Builder> map = accessor.getMap().remove(entityType);
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
                    Map<ResourceLocation, LootTable.Builder> map1 = accessor.getMap().remove(entityType);
                    if (map1 != null) {
                        throw new IllegalStateException(String.format(
                                Locale.ROOT,
                                "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                                map1.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")),
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
