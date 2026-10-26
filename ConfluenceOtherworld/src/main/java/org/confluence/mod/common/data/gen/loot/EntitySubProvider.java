package org.confluence.mod.common.data.gen.loot;

import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.common.loot.*;
import org.confluence.mod.mixin.data.loot.EntityLootSubProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;
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

        LootPoolSingletonContainer.Builder<?> boneWeight2 = LootItem.lootTableItem(Items.BONE).setWeight(2);

        add(BossEntities.EATER_OF_WORLDS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(25, hearts))))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(RAW_DEMONITE).apply(count2To5).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(SHADOW_SCALE).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(BossEntities.EATER_OF_WORLDS_SEGMENT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(25, hearts))))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(RAW_DEMONITE).apply(count2To5).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(SHADOW_SCALE).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(BossEntities.THE_DESTROYER_PROBE.get(), LootTable.lootTable());
        add(BossEntities.LUNATIC_CULTIST_CLONE.get(), LootTable.lootTable());
        add(MonsterEntities.VISUAL_NEURON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(50, hearts))))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 12))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.TISSUE_SAMPLE).apply(count2To5).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.GOBLIN_SCOUT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TATTERED_CLOTH).apply(count1To2)).apply(random0To1)
                )
        );
        add(MonsterEntities.JUNGLE_CREEPER.get(), LootTable.lootTable());
        /// 沙漠幽魂 缺少未加入物品：沙漠幽魂灯(Desert Spirit Lamp, 2.5%)、神灯诅咒(Djinn's Curse, 3.25%)、生命吞噬者(Eater Of Life, 0.67%)
        add(MonsterEntities.DESERT_SPIRIT.get(), LootTable.lootTable());
        add(MonsterEntities.WALL_CREEPER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.FRIED_EGG).when(lootingScaledChance(registries, 0.03333333333333333F))))
        );
        /// 黑隐士 缺少未加入物品：毒刺法杖(Poison Staff, 2.5%)
        add(MonsterEntities.BLACK_RECLUSE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SPIDER_FANG).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .when(() -> new DifficultyChanceLootItemCondition(0.5F, 0.9F)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.FRIED_EGG).when(lootingScaledChance(registries, 0.03333333333333333F))))
        );
        add(MonsterEntities.ANTLION.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).when(lootingScaledChance(registries, 0.02F))))
        );
        add(MonsterEntities.ANTLION_LARVA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))));
        add(MonsterEntities.ANTLION_CHARGER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).when(lootingScaledChance(registries, 0.02F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).when(lootingScaledChance(registries, 0.02F))))
        );
        add(MonsterEntities.ANTLION_SWARMER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(FoodItems.BANANA_SPLIT))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).when(lootingScaledChance(registries, 0.02F)))
                )
        );
        add(MonsterEntities.GIANT_ANTLION_SWARMER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(FoodItems.BANANA_SPLIT))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).when(lootingScaledChance(registries, 0.02F)))
                )
        );
        add(MonsterEntities.ANGER_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_ANGER_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.SHORT_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.WATER_BOLT_MIMIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ManaWeaponItems.WATER_BOLT).when(lootingScaledChance(registries, 0.025F))))
        );
        add(MonsterEntities.DARK_CASTER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.CLOTHIER_VOODOO_DOLL).when(lootingScaledChance(registries, 1F / 300)))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.CURSED_SKULL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.DUNGEON_DEMON_BONE).apply(count2To6).apply(random0To1).setWeight(97))
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY).setWeight(2))
                        .add(LootItem.lootTableItem(TCItems.TALLY_COUNTER))
                )
                .withPool(LootPool.lootPool()
                        .add(boneWeight2).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.NAZAR).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 70, LootItem.lootTableItem(FoodItems.CREAM_SODA))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.BLOOD_CRAWLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 33 / 99, LootItem.lootTableItem(MaterialItems.VERTEBRA).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
        );
        add(MonsterEntities.BLOOD_ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setQuality(1).when(difficultyLootChance(registries, 100F / 150, 100F / 75)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR.get()).when(lootingScaledChance(registries, 0.01F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get()).when(difficultyLootChance(registries, 0.5F, 1F)))
                )
        );
        /// 僵尸新娘 缺少未加入物品：婚纱(Wedding Dress, 100%)
        add(MonsterEntities.THE_BRIDE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
        );
        /// 僵尸新郎 缺少未加入物品：大脑(Brain, 75%)（高顶礼帽、血泪已实现）
        add(MonsterEntities.THE_GROOM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.TOP_HAT)))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.DRIPPLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setQuality(1).when(difficultyLootChance(registries, 100F / 150, 100F / 75)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR.get()).when(lootingScaledChance(registries, 0.01F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get()).when(difficultyLootChance(registries, 0.5F, 1F)))
                )
        );
        add(MonsterEntities.BLOODY_SPORE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLOOD_CLOT_POWDER)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(33, LootItem.lootTableItem(MaterialItems.VERTEBRA).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
        );
        add(MonsterEntities.CAVE_BAT.get(), batCommon(registries)
        );
        add(MonsterEntities.GIANT_BAT.get(), batCommon(registries)
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.TRIFOLD_MAP))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        /// 巨型蝙蝠 缺少未加入物品：深度计(Depth Meter, 1%)（三折地图已实现）
        /// 装甲骷髅 缺少未加入物品：光束剑(Beam Sword, 0.67%)
        add(MonsterEntities.ARMORED_SKELETON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.ARMOR_POLISH))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        /// 岩石巨人 缺少未加入物品：岩石巨人头(Rock Golem Head, 33.33%)
        add(MonsterEntities.ROCK_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.STONE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20)))));
        /// 狼人 缺少未加入物品：狼牙(Wolf Fang, 1.5%)
        add(MonsterEntities.WEREWOLF.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.MOON_CHARM).when(lootingScaledChance(registries, 0.016666666666666666F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.ADHESIVE_BANDAGE))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        /// 夜明蝙蝠 缺少未加入物品：结晶(Crystallize, 0.67%)（苹果派、蝙蝠棍已实现）
        add(MonsterEntities.ILLUMINANT_BAT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.APPLE_PIE).when(lootingScaledChance(registries, 0.006666666666666667F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.BAT_BAT).when(lootingScaledChance(registries, 0.0033333333333333335F)))));
        add(MonsterEntities.LAVA_BAT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.MAGMA_STONE).when(lootingScaledChance(registries, 0.02F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.BAT_BAT).when(lootingScaledChance(registries, 0.0033333333333333335F)))));
        add(MonsterEntities.SPORE_BAT.get(), batCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BoomerangItems.SHROOMERANG).setQuality(1).when(lootingScaledChance(registries, 0.025F)))
                )
        );
        add(MonsterEntities.SPORE_ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                )
        );
        add(MonsterEntities.HAT_SPORE_ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                )
        );
        add(MonsterEntities.ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
        );
        add(MonsterEntities.SPORE_SKELETON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(random0To1).apply(count1To2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CARTON_OF_MILK).when(lootingScaledChance(registries, 1F / 150)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BONE_SWORD).setQuality(1).when(lootingScaledChance(registries, 0.00492525F)))
                )
        );
        add(MonsterEntities.UNDEAD_VIKING.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(random0To1).apply(count1To2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArmorItems.VIKING_HELMET).when(lootingScaledChance(registries, 0.02F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 2 / 100F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).when(lootingScaledChance(registries, 0.04F)))
                )
        );
        /// 冰雪巨人 缺少未加入物品：冰雪羽(Ice Feather, 33.3%)
        add(MonsterEntities.ICE_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.FROST_CORE)))
        );
        /// 装甲维京海盗 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)（罗盘已实现）
        add(MonsterEntities.ARMORED_VIKING.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 2 / 100F)))
                )
        );
        /// 冰雪鱼人 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)、寒冰法杖(Frost Staff, 2%)
        add(MonsterEntities.ICY_MERMAN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.MILKSHAKE).when(lootingScaledChance(registries, 0.013333333333333334F)))
                )
        );
        /// 冰雪精 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)、寒冰法杖(Frost Staff, 2%)
        add(MonsterEntities.ICE_ELEMENTAL.get(), LootTable.lootTable());
        /// 冰雪陆龟 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)（龟壳、奶昔已实现）
        add(MonsterEntities.ICE_TORTOISE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FROZEN_TURTLE_SHELL).when(lootingScaledChance(registries, 0.02F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.MILKSHAKE).when(lootingScaledChance(registries, 0.013333333333333334F)))
                )
        );
        add(MonsterEntities.CRIMERA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 33 / 99, LootItem.lootTableItem(MaterialItems.VERTEBRA).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(1, LootItem.lootTableItem(FoodItems.BURGER))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.FACE_MONSTER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 33 / 99, LootItem.lootTableItem(MaterialItems.VERTEBRA).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
        );
        add(MonsterEntities.DECAYEDER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(33, LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_BONE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
        );
        add(MonsterEntities.DEMON_EYE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(33, LootItem.lootTableItem(MaterialItems.LENS).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLACK_LENS).when(lootingScaledChance(registries, 0.01F)))
                )
        );
        /// 僵尸鱼人 缺少未加入物品：血雨弓(Blood Rain Bow, 12.5%)、钱币槽(Money Trough, 6.67%)、鱼饵桶(Chum Bucket, 50%)
        add(MonsterEntities.ZOMBIE_MERMAN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(SummonItems.VAMPIRE_FROG_STAFF).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(FishingPoleItems.CHUM_CASTER).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR).when(lootingScaledChance(registries, 0.04F)))));
        add(MonsterEntities.WANDERING_EYE_FISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(SummonItems.VAMPIRE_FROG_STAFF).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
        );
        add(MonsterEntities.DEVOURER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 33 / 99, LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.WORM_TOOTH)).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 8))).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
        );
        add(MonsterEntities.DUNGEON_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY)).apply(random0To1)
                )
        );
        add(MonsterEntities.DUNGEON_SPIRIT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ECTOPLASM)).apply(count1To2))
        );
        add(MonsterEntities.SWEET_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.HONEY_GUMMI)).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 7))).apply(random3To4)
                )
        );
        add(MonsterEntities.GOLDEN_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN)).apply(SetItemCountFunction.setCount(ConstantValue.exactly(15)))
                )
        );
        add(MonsterEntities.NYMPH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.METAL_DETECTOR)).when(() -> new DifficultyChanceLootItemCondition(50F / 100F, 100F / 100F))
                )
        );
        add(MonsterEntities.SNATCHER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 333 / 9990, LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.FUNGI_BULB.get(), LootTable.lootTable());
        add(MonsterEntities.GIANT_FUNGI_BULB.get(), LootTable.lootTable());
        /// 爬藤怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、生命吞噬者(Eater Of Life, 0.67%)（诅咒焰已实现，但缺专家掉率提升）
        add(MonsterEntities.CLINGER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.CURSED_FLAME)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FunctionalBlocks.MEAT_GRINDER).when(lootingScaledChance(registries, 0.005F)))));
        add(MonsterEntities.MAN_EATER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.MAN_EATER_VINE).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        .when(() -> new DifficultyChanceLootItemCondition(50 / 100F, 100 / 100F))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 333 / 9990, LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.FLYING_FISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(33, LootItem.lootTableItem(MaterialItems.FILAMENTOUS_FIN).setWeight(33).setQuality(1).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.EATER_OF_SOULS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 33 / 99, LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(1, LootItem.lootTableItem(FoodItems.BURGER))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(ModLootTables.CORRUPTION_CARRY).setQuality(1).when(lootingScaledChance(registries, 0.0019F)))
                )
        );
        add(MonsterEntities.GIANT_SHELLY.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 79F / 6400)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER).when(lootingScaledChance(registries, 0.0125F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 75, LootItem.lootTableItem(FoodItems.POTATO_CHIPS))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 6241 / 96000, LootItem.lootTableItem(YoyoItems.RALLY).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.CRAWDAD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 79F / 6400)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER).when(lootingScaledChance(registries, 0.0125F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 75, LootItem.lootTableItem(FoodItems.POTATO_CHIPS))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 6241 / 96000, LootItem.lootTableItem(YoyoItems.RALLY).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.GIANT_WORM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.WHOOPIE_CUSHION).when(lootingScaledChance(registries, 0.02F)))
                )
        );
        /// 挖掘怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、可疑苹果(Suspicious Looking Apple, 40%)
        add(MonsterEntities.DIGGER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.WHOOPIE_CUSHION).when(lootingScaledChance(registries, 0.02F)))
                )
        );
        /// 吞世怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、生命吞噬者(Eater Of Life, 0.67%)、吞世怪风筝(World Feeder Kite, 4%)、可疑苹果(Suspicious Looking Apple, 45%)
        add(MonsterEntities.WORLD_FEEDER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.CURSED_FLAME))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                )
        );
        /// 符文法师 缺少未加入物品：符文帽(Rune Hat, 100%)、符文长袍(Rune Robe, 100%)
        add(MonsterEntities.RUNE_WIZARD.get(), LootTable.lootTable());
        add(MonsterEntities.TIM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ArmorItems.WIZARD_HAT)))
        );
        add(MonsterEntities.DOCTOR_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.ARCHAEOLOGISTS_HAT)))
        );
        add(MonsterEntities.HARPY.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GIANT_HARPY_FEATHER).when(lootingScaledChance(registries, 0.006666666666666667F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.HARPY_FEATHER).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(FoodItems.CHICKEN_NUGGET))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.HELL_BAT.get(), batCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.MAGMA_STONE).when(lootingScaledChance(registries, 1F / 150)))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE).when(lootingScaledChance(registries, 0.0025F)))
                )
        );
        add(MonsterEntities.FIRE_IMP.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.OBSIDIAN_ROSE).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE).when(lootingScaledChance(registries, 0.0025F)))
                )
        );
        add(MonsterEntities.ANGRY_TUMBLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS).when(lootingScaledChance(registries, 0.03333333333333333F)))));
        add(MonsterEntities.WINDY_BALLOON.get(), LootTable.lootTable());
        add(MonsterEntities.OLD_SHAKING_CHEST.get(), LootTable.lootTable());
        add(MonsterEntities.CLUMSY_BALLOON_SLIME.get(), LootTable.lootTable());
        /// 红魔鬼 缺少未加入物品：火焰羽(Fire Feather, 2%)、烈火之花(Flower of Fire, 3.33%)（热狗、邪恶三叉戟已实现）
        add(MonsterEntities.RED_DEVIL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.HOTDOG).when(lootingScaledChance(registries, 0.03333333333333333F)))));
        add(MonsterEntities.DEMON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE).when(lootingScaledChance(registries, 0.0025F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.DEMON_SCYTHE).when(lootingScaledChance(registries, 1F / 35)))
                )
        );
        add(MonsterEntities.VOODOO_DEMON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.GUIDE_VOODOO_DOLL))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE).when(lootingScaledChance(registries, 0.0025F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.DEMON_SCYTHE).when(lootingScaledChance(registries, 1F / 35)))
                )
        );
        /// 青苔黄蜂 缺少未加入物品：破碎蜂翼(Tattered Bee Wing, 1%)（蜂刺、牛黄已实现）
        add(MonsterEntities.MOSS_HORNET.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(MaterialItems.STINGER).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.BEZOAR))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        add(MonsterEntities.HORNET.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER)).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BEZOAR).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
        );
        add(MonsterEntities.ICE_BAT.get(), batCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 150, LootItem.lootTableItem(FoodItems.ICE_CREAM))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.SNOW_FLINX.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 2 / 100F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FLINX_FUR)).apply(random0To1).apply(count1To2)
                )
        );
        add(MonsterEntities.JUNGLE_BAT.get(), batCommon(registries)
        );
        add(MonsterEntities.PIRANHA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 1F / 75)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).when(lootingScaledChance(registries, 0.0332F)))
                )
        );
        add(MonsterEntities.CORRUPT_GOLDFISH.get(), LootTable.lootTable());
        add(MonsterEntities.VICIOUS_GOLDFISH.get(), LootTable.lootTable());
        /// 琵琶鱼 缺少未加入物品：机器人帽(Robot Hat, 0.4%)（粘性绷带已实现）
        add(MonsterEntities.ANGLER_FISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.ROBOT_HAT).when(lootingScaledChance(registries, 0.004F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.ADHESIVE_BANDAGE))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F)))
        );
        /// 沙鲨 / 腐化沙鲨 / 猩红沙鲨 / 神圣沙鲨 缺少未加入物品：沙鲨风筝(Sand Shark Kite, 4%)
        add(MonsterEntities.SAND_SHARK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SHARK_FIN).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS).when(lootingScaledChance(registries, 0.03333333333333333F)))));
        add(MonsterEntities.BONE_BITER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SHARK_FIN).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS).when(lootingScaledChance(registries, 0.03333333333333333F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.DARK_SHARD).when(lootingScaledChance(registries, 0.04F)))));
        add(MonsterEntities.FLESH_REAVER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SHARK_FIN).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS).when(lootingScaledChance(registries, 0.03333333333333333F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.DARK_SHARD).when(lootingScaledChance(registries, 0.04F)))));
        add(MonsterEntities.CRYSTAL_THRESHER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SHARK_FIN).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS).when(lootingScaledChance(registries, 0.03333333333333333F))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.LIGHT_SHARD).when(lootingScaledChance(registries, 0.04F)))));
        add(MonsterEntities.SHARK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP_PO_BOY).when(lootingScaledChance(registries, 0.02F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DIVING_HELMET).setWeight(1))
                        .add(LootItem.lootTableItem(MaterialItems.SHARK_FIN).setWeight(19))
                )
        );
        add(MonsterEntities.TOMB_CRAWLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL)).apply(count1To2).apply(random0To1)
                )
        );
        add(MonsterEntities.BONE_SERPENT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE_BLOCK)).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(FoodItems.HOTDOG).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.WITHER_BONE_SERPENT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE_BLOCK)).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.COAL_BLOCK)).apply(count1To2).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 333 / 1000, LootItem.lootTableItem(FoodItems.HOTDOG).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.ANGER_GOBLIN.get(), goblinCommon(50F)
        );
        add(MonsterEntities.GOBLIN_ARCHER.get(), goblinCommon(49.75F)
        );
        add(MonsterEntities.GOBLIN_PEON.get(), goblinCommon(49.75F)
        );
        add(MonsterEntities.GOBLIN_SORCERER.get(), goblinCommon(49.75F)
        );
        /// 哥布林术士 缺少未加入物品：暗影焰弓(Shadowflame Bow, 33.3%)、暗影焰巫术娃娃(Shadowflame Hex Doll, 33.3%)、暗影焰刀(Shadowflame Knife, 33.3%)
        add(MonsterEntities.GOBLIN_WARLOCK.get(), goblinCommon(50F));
        add(MonsterEntities.SHADOWFLAME_APPARITION.get(), LootTable.lootTable());
        add(MonsterEntities.GNOME.get(), LootTable.lootTable());
        add(MonsterEntities.GOBLIN_THIEF.get(), goblinCommon(49.75F)
        );
        add(MonsterEntities.GOBLIN_WARRIOR.get(), goblinCommon(49.75F)
        );
        /// 海盗七怪（水手/私船海盗/神射手/弩手/船长/诅咒/鹦鹉）缺少未加入物品：钱币枪、桶式发射器、海盗法杖、弯刀、水手帽/眼罩/水手上衣/水手裤、金平台、船长帽、诅咒之眼等
        add(MonsterEntities.PIRATE_DECKHAND.get(), pirateCommon(registries, 1));
        add(MonsterEntities.PIRATE_DEADEYE.get(), pirateCommon(registries, 1));
        add(MonsterEntities.PIRATE_CROSSBOWER.get(), pirateCommon(registries, 1));
        add(MonsterEntities.PIRATE_CORSAIR.get(), pirateCommon(registries, 1));
        add(MonsterEntities.PIRATE_CAPTAIN.get(), pirateCommon(registries, 4));
        add(MonsterEntities.PIRATE_PARROT.get(), LootTable.lootTable());
        add(MonsterEntities.PIRATES_CURSE.get(), LootTable.lootTable());
        add(MonsterEntities.MARTIAN_PROBE.get(), LootTable.lootTable());
        ///火星人事件
        add(MonsterEntities.MARTIAN_ENGINEER.get(), LootTable.lootTable());
        add(MonsterEntities.BRAIN_SCRAMBLER.get(), LootTable.lootTable());
        add(MonsterEntities.GRAY_GRUNT.get(), LootTable.lootTable());
        add(MonsterEntities.GIGAZAPPER.get(), LootTable.lootTable());
        add(MonsterEntities.GIGAZAPPER_AF2.get(), LootTable.lootTable());
        add(MonsterEntities.MARTIAN_OFFICER.get(), LootTable.lootTable());
        add(MonsterEntities.MARTIAN_WALKER.get(), LootTable.lootTable());
        add(MonsterEntities.TESLA_TURRET.get(), LootTable.lootTable());
        add(MonsterEntities.RAY_GUNNER.get(), LootTable.lootTable());
        add(MonsterEntities.SCUTLIX.get(), LootTable.lootTable());
        add(NpcEntities.MECHANIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(BoomerangItems.COMBAT_WRENCH).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(NpcEntities.STYLIST.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(SwordItems.STYLISH_SCISSORS).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(NpcEntities.DYE_TRADER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(SwordItems.EXOTIC_SCIMITAR).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(NpcEntities.TRAVELING_MERCHANT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.PEDDLERS_HAT))
                )
        );
        add(NpcEntities.CLOTHIER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.CLOTHIERS_HAT))
                )
        );
        add(CritterEntities.CLOUD_SHEEP.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.MUTTON).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).apply(SmeltItemFunction.smelted().when(shouldSmeltLoot()))))
        );
        add(CritterEntities.GLOWING_MOOSHROOM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.BEEF).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(SmeltItemFunction.smelted().when(shouldSmeltLoot()))))
        );
        add(CritterEntities.CLUCKSHROOM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.CHICKEN).apply(SmeltItemFunction.smelted().when(shouldSmeltLoot()))))
        );
        add(CritterEntities.GLOWING_CLUCKSHROOM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.CHICKEN).apply(SmeltItemFunction.smelted().when(shouldSmeltLoot()))))
        );
        add(CritterEntities.DUCK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_DUCK).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.BIRD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_BIRD).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.BLUE_JAY.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_BIRD).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.SQUIRREL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_SQUIRREL).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.RED_SQUIRREL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_SQUIRREL).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.CARDINAL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.RAW_BIRD).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.BUNNY.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.RABBIT).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.EXPLOSIVE_BUNNY.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.RABBIT).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())).apply(random0To1)
                        )
                )
        );
        add(CritterEntities.CRAB.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(FoodItems.SHRIMP_PO_BOY))
                        )).apply(random0To1)
                )
        );
        add(CritterEntities.HOSTILE_BUNNY.get(), LootTable.lootTable());
        add(CritterEntities.PENGUIN.get(), LootTable.lootTable());
        add(CritterEntities.MYSTIC_FROG.get(), LootTable.lootTable());
        add(CritterEntities.GOLDFISH.get(), LootTable.lootTable());
        /// 腐化企鹅 / 猩红企鹅 缺少未加入物品：Pedguin 套装（兜帽/夹克/裤子，各 0.67%）
        add(MonsterEntities.CORRUPT_PENGUIN.get(), LootTable.lootTable());
        add(MonsterEntities.VICIOUS_PENGUIN.get(), LootTable.lootTable());
        add(CritterEntities.STINKBUG.get(), LootTable.lootTable());
        add(CritterEntities.FIREFLY.get(), LootTable.lootTable());
        add(CritterEntities.TRUFFLE_WORM.get(), LootTable.lootTable());
        add(CritterEntities.BUGGY.get(), LootTable.lootTable());
        add(CritterEntities.LIGHTNING_BUG.get(), LootTable.lootTable());
        add(MonsterEntities.ANGRY_NIMBUS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(ManaWeaponItems.NIMBUS_ROD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
        );
        add(MonsterEntities.ANGRY_DANDELION.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.DAYBLOOM).apply(count1To2).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        /// 花岗岩巨人 缺少未加入物品：晶洞(Geode, 5%)、夜视头盔(Night Vision Helmet, 3.3%)、响石(Snapping Stone, 1.25%)（花岗岩、意大利面已实现）
        add(MonsterEntities.GRANITE_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(NatureBlocks.GRANITE))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SPAGHETTI).when(lootingScaledChance(registries, 0.02F)))
                )
        );
        add(MonsterEntities.GRANITE_ELEMENTAL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(FoodItems.SPAGHETTI))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(NatureBlocks.GRANITE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))).apply(random0To1)
                )
        );
        /// 装甲步兵 缺少未加入物品：压力球(Stress Ball, 1%)、角斗士胸甲(Gladiator Breastplate, 4.76%)（标枪、角斗士头盔/护腿、短剑、钩爪、披萨已实现）
        add(MonsterEntities.HOPLITE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.PIZZA).when(lootingScaledChance(registries, 0.02F))))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ConsumableItems.JAVELIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(40, 80))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(ArmorItems.GLADIATOR_HELMET)),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(ArmorItems.GLADIATOR_CHESTPLATE)),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(ArmorItems.GLADIATOR_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(ArmorItems.GLADIATOR_BOOTS))
                        )))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(SwordItems.GLADIUS).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HOOK).when(lootingScaledChance(registries, 0.04F))))
        );
        add(MonsterEntities.METEOR_HEAD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().when(beforeSkeletronBehindWallOfFlesh)
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(MaterialItems.RAW_METEORITE))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.BLUE_SLIME.get(), slimeCommon(-10644993, 100F / 10000, 100F / 7000));
        add(MonsterEntities.DESERT_SLIME.get(), slimeCommon(-2727, 100F / 8000, 100F / 5600));
        add(MonsterEntities.GREEN_DUMPLING_SLIME.get(), slimeCommon(-8470674, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(FoodItems.GREEN_DUMPLING.get()).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.GREEN_SLIME.get(), slimeCommon(-8470674, 100F / 10000, 100F / 7000));
        add(MonsterEntities.PURPLE_SLIME.get(), slimeCommon(-6326333, 100F / 10000, 100F / 7000));
        add(MonsterEntities.RED_SLIME.get(), slimeCommon(-1079407, 100F / 10000, 100F / 7000));
        add(MonsterEntities.YELLOW_SLIME.get(), slimeCommon(-871089, 100F / 10000, 100F / 7000));
        add(MonsterEntities.SLIMELING.get(), corruptionSlimeLoot(-6522185));
        add(MonsterEntities.JUNGLE_SLIME.get(), slimeCommon(-6570130, 100F / 10000, 100F / 7000));
        add(MonsterEntities.ICE_SLIME.get(), slimeCommon(-10628609, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 150, LootItem.lootTableItem(FoodItems.ICE_CREAM.get()))
                        ))
                        .apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get())
                                .when(DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType()
                                                .tag(TagPredicate.is(
                                                        registries.lookupOrThrow(Registries.DAMAGE_TYPE)
                                                                .getOrThrow(DamageTypeTags.IS_FALL).key()
                                                ))
                                ))

                        .when(lootingScaledChance(registries, 0.02F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get()).when(lootingScaledChance(registries, 0.006666666666666667F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get()).apply(LootingBonusCountFunction.lootingBonusCount(registries))
                                .when(AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                        .of(EntityType.PLAYER)
                                        .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                .mainhand(ItemPredicate.Builder.item()
                                                        .withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(
                                                                List.of(new EnchantmentPredicate(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FROST_WALKER), MinMaxBounds.Ints.atLeast(1)))))))))))
                        ))
                )
        );
        /// 岩浆史莱姆不携带额外物品也不掉落凝胶，只保留其独立的稀有史莱姆法杖掉落。
        add(MonsterEntities.LAVA_SLIME.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).when(lootingScaledChance(registries, 0.000125F)))
        ));
        add(MonsterEntities.BLACK_SLIME.get(), slimeCommon(-7697782, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 0.01F)))
                )
        );
        /// 史莱姆之母不携带随机物品，但保留凝胶、史莱姆法杖和指南针掉落。
        add(MonsterEntities.MOTHER_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).when(difficultyLootChance(registries, 100F / 10000, 100F / 7000)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -7697782))))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 2 / 100F)))
                )
        );
        add(MonsterEntities.BABY_SLIME.get(), slimeCommon(-7697782, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).when(lootingScaledChance(registries, 0.01F)))
                )
        );
        add(MonsterEntities.TROPIC_SLIME.get(), slimeCommon(-10644993, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.TROPICAL_FISH).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.PINK_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, NestedLootTable.lootTableReference(ModLootTables.SLIME_CARRY))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(1, LootItem.lootTableItem(SummonItems.SLIME_STAFF).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setQuality(1).when(() -> new DifficultyChanceLootItemCondition(1, 0))),
                                PercentageLootEntry.rule(100.0 / 70, LootItem.lootTableItem(SummonItems.SLIME_STAFF).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setQuality(1).when(() -> new DifficultyChanceLootItemCondition(0, 1)))
                        ))
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
        add(MonsterEntities.SWAMP_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, NestedLootTable.lootTableReference(ModLootTables.SLIME_CARRY))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1).when(difficultyLootChance(registries, 100F / 10000, 100F / 7000)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.SKELETON_SKULL).when(lootingScaledChance(registries, 0.025F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.SLIME_BALL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                )
        );
        add(MonsterEntities.SPIKED_SLIME.get(), slimeCommon(-10644993, 100F / 10000, 100F / 7000)
        );
        add(MonsterEntities.SPIKED_ICE_SLIME.get(), slimeCommon(-10628609, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 150, LootItem.lootTableItem(FoodItems.ICE_CREAM.get()))
                        ))
                        .apply(random0To1)
                )
        );
        add(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), slimeCommon(-6570130, 100F / 10000, 100F / 7000)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER.get()).apply(LootingBonusCountFunction.lootingBonusCount(registries)))

                        .when(() -> new DifficultyChanceLootItemCondition(50 / 100F, 100 / 100F))
                )
        );
        add(MonsterEntities.BLUE_JELLYFISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE).when(lootingScaledChance(registries, 0.01F)))
                )
        );
        add(MonsterEntities.PINK_JELLYFISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE).when(lootingScaledChance(registries, 0.01F)))
                )
        );
        /// 肉后怪
        add(MonsterEntities.WYVERN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN)).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.SOUL_OF_FLIGHT)).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20))).apply(random0To1)
                )
        );
        add(MonsterEntities.PIXIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.PIXIE_DUST)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(random0To1)
                )
        );
        add(MonsterEntities.WRAITH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
        );
        /// 鬼魂：44% 概率掉落引渡者
        add(MonsterEntities.GHOST.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SoulItems.SOUL_SKILL_BOOK)
                                .when(LootItemRandomChanceCondition.randomChance(0.44F))
                                .apply(SetComponentsFunction.setComponent(ModDataComponentTypes.SOUL_SKILL.get(), SoulBooks.PSYCHOPOMP))
                                .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.MOD_RARITY.get(), SoulBooks.rarity(SoulBooks.PSYCHOPOMP))))
                )
        );
        /// 血水母 缺少未加入物品：怪物肉(Monster Meat, 0.07%)
        add(MonsterEntities.BLOOD_JELLY.get(), LootTable.lootTable());
        add(MonsterEntities.FUNGO_FISH.get(), LootTable.lootTable());
        add(MonsterEntities.GREEN_JELLYFISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE).when(lootingScaledChance(registries, 0.01F)))
                )
        );
        add(MonsterEntities.LUMINOUS_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(14).when(difficultyLootChance(registries, 100F / 10000, 100F / 7000)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 150, LootItem.lootTableItem(FoodItems.APPLE_PIE))
                        )).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -4040988))))
                )
        );
        add(MonsterEntities.CRIMSLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).when(difficultyLootChance(registries, 100F / 10000, 100F / 7000)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -3386287))))
                )
        );
        add(MonsterEntities.CORRUPT_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).when(difficultyLootChance(registries, 100F / 10000, 100F / 7000)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", -6522185))))
                )
        );
        add(MonsterEntities.WINGLESS_SLIMER.get(), corruptionSlimeLoot(-6522185));
        /// 宝箱怪
        add(MonsterEntities.WOODEN_MIMIC.get(), mimicCommon(registries)
        );
        add(MonsterEntities.GOLDEN_MIMIC.get(), mimicCommon(registries)
        );
        add(MonsterEntities.SHADOW_MIMIC.get(), mimicCommon(registries)
        );
        add(MonsterEntities.ICE_MIMIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        /// 冰雪弓
                        .add(LootItem.lootTableItem(ManaWeaponItems.FLOWER_OF_FROST))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(new ConstantValue(25)))
                ))
        );
        /// todo秘密种子冰雪宝箱怪使用这个common
        /*
        add(MonsterEntities.ICE_MIMIC.get(),LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        /// 玩具雪橇
                        /// 冰雪弓
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS))
                        .add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE))
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(new ConstantValue(2)))
                ))
        );
        */
        add(MonsterEntities.CRIMSON_MIMIC.get(), bigMimicCommon(registries)
                .withPool(LootPool.lootPool()
                        /// 夺命杖
                        /// 飞镖手枪
                        /// 臭虎爪
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(HookItems.TENDON_HOOK)),
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(TCItems.FLESH_KNUCKLES))
                        ))
                )
        );
        add(MonsterEntities.CORRUPT_MIMIC.get(), bigMimicCommon(registries)
                .withPool(LootPool.lootPool()
                        /// 爬藤怪法杖
                        /// 飞镖步枪
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(FlailItems.CHAIN_GUILLOTINES)),
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(HookItems.WORM_HOOK)),
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(TCItems.PUTRID_SCENT))
                        ))
                )
        );
        add(MonsterEntities.HALLOWED_MIMIC.get(), bigMimicCommon(registries)
                .withPool(LootPool.lootPool()
                        /// 飞刀
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ManaWeaponItems.CRYSTAL_VILE_SHARD)),
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(BowItems.DAEDALUS_STORM_BOW)),
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(HookItems.ILLUMINANT_HOOK))
                        ))
                )
        );
        add(MonsterEntities.JUNGLE_MIMIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(TCItems.FART_IN_A_JAR).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ManaWeaponItems.GOLDEN_SHOWER).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(NatureBlocks.POO).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                /// 天使雕像
                /// 水枪
                /// 闪耀史莱姆气球
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.COAL).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.RED_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.STINK_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))

                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        )))
        );
        add(MonsterEntities.MUMMY.get(), mummyCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
        );
        add(MonsterEntities.DARK_MUMMY.get(), mummyCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(MaterialItems.DARK_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.BLOOD_MUMMY.get(), mummyCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(MaterialItems.DARK_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.LIGHT_MUMMY.get(), mummyCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.TRIFOLD_MAP).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(MaterialItems.LIGHT_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.DERPLING.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 25 / 1000, LootItem.lootTableItem(FoodItems.GRAPE))
                        ))
                        .apply(random0To1)
                )
        );
        /// 蛇蜥怪 缺少未加入物品：远古号角(Ancient Horn, 2%)（坚固化石已实现）
        add(MonsterEntities.BASILISK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))));
        add(MonsterEntities.GHOUL.get(), ghoulCommon(registries)
        );
        add(MonsterEntities.VILE_GHOUL.get(), ghoulCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(0, LootItem.lootTableItem(MaterialItems.DARK_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MaterialItems.DARK_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.CURSED_FLAME).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.TAINTED_GHOUL.get(), ghoulCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(0, LootItem.lootTableItem(MaterialItems.DARK_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MaterialItems.DARK_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.ICHOR).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.DREAMER_GHOUL.get(), ghoulCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MaterialItems.LIGHT_SHARD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.ICHOR).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.SAND_POACHER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(FoodItems.FRIED_EGG))
                        ))
                        .apply(random0To1)
                )
        );
        add(MonsterEntities.GIANT_TORTOISE.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 12, LootItem.lootTableItem(MaterialItems.TURTLE_SHELL).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                )
        );
        add(MonsterEntities.GIANT_FLYING_FOX.get(), batCommon(registries)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.GRAPE).when(lootingScaledChance(registries, 0.025F)))
                ));
        add(MonsterEntities.CORRUPTOR.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 33 / 99, LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).apply(LootingBonusCountFunction.lootingBonusCount(registries)).setWeight(33).setQuality(1))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.VITAMINS).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                )
        );
        add(MonsterEntities.SLIMER.get(), LootTable.lootTable());
        add(MonsterEntities.BLOOD_FEEDER.get(), LootTable.lootTable());
        add(MonsterEntities.UNICORN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.UNICORN_HORN)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MountItems.BLESSED_APPLE))
                        .when(() -> new DifficultyChanceLootItemCondition(1.0F / 40.0F, 1.0F / 30.0F))));
        add(MonsterEntities.GASTROPOD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10)))
                        .apply(random0To1)));
        /// 混沌精 缺少未加入物品：混乱之杖(Rod of Discord, 0.25%)（苹果派已实现，需要补 0.67% 概率）
        add(MonsterEntities.CHAOS_ELEMENTAL.get(), LootTable.lootTable()
                /// 混沌传送杖
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 150, LootItem.lootTableItem(FoodItems.APPLE_PIE))
                        )).apply(random0To1)
                )
        );
        add(MonsterEntities.ENCHANTED_SWORD.get(), LootTable.lootTable());
        add(MonsterEntities.PALADIN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.PALADINS_SHIELD).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        .when(() -> new DifficultyChanceLootItemCondition(1F / 10, 1F - (1F - 1F / 10) * (1F - 1F / 10)))));
        add(MonsterEntities.BONE_LEE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLACK_BELT).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        .when(() -> new DifficultyChanceLootItemCondition(1F / 12, 1F - (1F - 1F / 12) * (1F - 1F / 12)))));
        /// 死灵法师 缺少未加入物品：暗影束法杖(Shadowbeam Staff, 9.75%)
        add(MonsterEntities.NECROMANCER.get(), LootTable.lootTable());
        add(MonsterEntities.DIABOLIST.get(), LootTable.lootTable());
        add(MonsterEntities.RAGGED_CASTER.get(), LootTable.lootTable());
        add(MonsterEntities.ARCH_WYVERN.get(), LootTable.lootTable());
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

    private static LootTable.Builder ghoulCommon(HolderLookup.Provider registries) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(MaterialItems.ANCIENT_CLOTH).apply(LootingBonusCountFunction.lootingBonusCount(registries)))
                        ))
                );
    }

    private static LootTable.Builder mummyCommon(HolderLookup.Provider registries) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_MASK).when(lootingScaledChance(registries, 1F / 75)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_SHIRT).when(lootingScaledChance(registries, 1F / 75)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_PANTS).when(lootingScaledChance(registries, 1F / 75)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VanityArmorItems.MUMMY_SHOES).when(lootingScaledChance(registries, 0.0133F)))
                );
    }

    private static LootTable.Builder mimicCommon(HolderLookup.Provider registries) {
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
                        .apply(SetItemCountFunction.setCount(new ConstantValue(25)))
                ));
    }

    private static LootTable.Builder bigMimicCommon(HolderLookup.Provider registries) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_MANA_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(new ConstantValue(7)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(new ConstantValue(7)))
                ));
    }

    private static LootTable.Builder mimicCommonSecret(HolderLookup.Provider registries) {  // todo秘密种子宝箱怪使用这个common
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BAND_OF_REGENERATION))
                        .add(LootItem.lootTableItem(TCItems.MAGIC_MIRROR))
                        .add(LootItem.lootTableItem(TCItems.CLOUD_IN_A_BOTTLE))
                        .add(LootItem.lootTableItem(TCItems.HERMES_BOOTS))
                        .add(LootItem.lootTableItem(TCItems.SHOE_SPIKES))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(new ConstantValue(5)))
                ));
    }

    private static LootTable.Builder batCommon(HolderLookup.Provider registries) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DEPTH_METER).when(lootingScaledChance(registries, 0.01F)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BAT_BAT).setQuality(1).when(lootingScaledChance(registries, 0.003F)))
                );
    }

    /// 复用凝胶和携带物掉落；法杖概率按普通、专家分别填写百分比，沙漠史莱姆使用自己的数值。
    private LootTable.Builder slimeCommon(int gelColor, float normalPercent, float expertPercent) {
        EnchantedCountIncreaseFunction.Builder random0To1 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, NestedLootTable.lootTableReference(ModLootTables.SLIME_CARRY))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1).when(difficultyLootChance(registries, normalPercent, expertPercent)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", gelColor))))
                );
    }

    /// 腐化、猩红和恶翼史莱姆族系共用凝胶、黑暗免疫饰品与史莱姆法杖掉落。
    private LootTable.Builder corruptionSlimeLoot(int gelColor) {
        EnchantedCountIncreaseFunction.Builder random0To1 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).when(difficultyLootChance(registries, 100F / 10000, 100F / 7000)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(SetComponentsFunction.setComponent(ConfluenceMagicLib.NBT.get(), NbtComponent.create(tag -> tag.putInt("color", gelColor))))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).when(difficultyLootChance(registries, 1.0F, 1.99F)))
                );
    }

    private static LootTable.Builder pirateCommon(HolderLookup.Provider registries, int multiplier) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.LUCKY_COIN))
                        .when(LootItemRandomChanceCondition.randomChance(0.0005F * multiplier)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.DISCOUNT_CARD))
                        .when(LootItemRandomChanceCondition.randomChance(0.001F * multiplier)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.GOLD_RING))
                        .when(LootItemRandomChanceCondition.randomChance(0.002F * multiplier)));
    }

    /// 尖球概率直接填写百分比；普通哥布林计入鱼叉枪失败链，扩展哥布林保留原概率。
    private LootTable.Builder goblinCommon(float chancePercent) {
        LootItemConditionalFunction.Builder<?> count1To5 = SetItemCountFunction.setCount(UniformGenerator.between(1, 5));
        EnchantedCountIncreaseFunction.Builder random0To1 = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(chancePercent,
                                LootItem.lootTableItem(ConsumableItems.SPIKY_BALL).apply(count1To5).apply(LootingBonusCountFunction.lootingBonusCount(registries)))))
                );
    }

    /// 低概率池专用的抢夺适配：基础掉落率为 {@code base}，抢夺等级每高一级使最终掉落率多乘一倍。

    /// 即 无抢夺 {@code base} → 抢夺I {@code 2 * base} → 抢夺II {@code 3 * base} → 抢夺III及以上 {@code 4 * base}。

    /// 注意必须这样构造，不能用 {@code randomChanceAndLootingBoost}：原版

    /// {@code LootItemRandomChanceWithEnchantedBonusCondition} 在等级大于 0 时只取

    /// {@code enchantedChance.calculate(level)}，完全忽略 {@code unenchanted_chance}，

    /// 而 {@code randomChanceAndLootingBoost} 生成的 {@code Linear(base + perLevel, perLevel)}

    /// 是“加算”的，等级越高越会退化成必定掉落。

    /// 抢夺III之后按III级封顶：{@code Linear(1, 1)} 在等级 3 得 4，等级 4 得 5，

    /// 这里通过 {@code LevelBasedValue.Lookup} 显式封顶。
    private static LootItemCondition.Builder lootingScaledChance(HolderLookup.Provider registries, float base) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
        LevelBasedValue multiplier = LevelBasedValue.lookup(List.of(base * 2.0F, base * 3.0F), LevelBasedValue.constant(base * 4.0F));
        return () -> new LootItemRandomChanceWithEnchantedBonusCondition(
                base, multiplier, enchantments.getOrThrow(Enchantments.LOOTING));
    }

    /// 按难度选择百分比概率，并保留原有抢夺倍率；1、1.99 分别表示普通 1% 和专家 1.99%。
    private static LootItemCondition.Builder difficultyLootChance(HolderLookup.Provider registries, float normalPercent, float expertPercent) {
        return AnyOfCondition.anyOf(
                AllOfCondition.allOf(() -> new DifficultyChanceLootItemCondition(1, 0),
                        lootingScaledChance(registries, normalPercent / 100F)),
                AllOfCondition.allOf(() -> new DifficultyChanceLootItemCondition(0, 1),
                        lootingScaledChance(registries, expertPercent / 100F)));
    }

    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(ModEntities.ENTITIES, BossEntities.ENTITIES, CritterEntities.ENTITIES, MonsterEntities.ENTITIES, NpcEntities.ENTITIES)
                .map(DeferredRegister::getEntries).flatMap(Collection::stream).map(DeferredHolder::get);
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
