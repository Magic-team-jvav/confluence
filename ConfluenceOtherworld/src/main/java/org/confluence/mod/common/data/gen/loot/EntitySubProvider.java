package org.confluence.mod.common.data.gen.loot;

import net.minecraft.advancements.critereon.*;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.common.loot.DifficultyChanceLootItemCondition;
import org.confluence.mod.common.loot.GamePhaseLootItemCondition;
import org.confluence.mod.mixin.data.loot.EntityLootSubProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;
import org.mesdag.portlib.diff.IPortItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.confluence.mod.common.init.item.MaterialItems.RAW_DEMONITE;
import static org.confluence.mod.common.init.item.MaterialItems.SHADOW_SCALE;

public final class EntitySubProvider extends EntityLootSubProvider {
    public EntitySubProvider() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        DateLootItemCondition.Builder halloweens = DateLootItemCondition.builder().from(Calendar.OCTOBER, 10).to(Calendar.NOVEMBER, 1);
        DateLootItemCondition.Builder christmas = DateLootItemCondition.builder().from(Calendar.DECEMBER, 15).to(Calendar.DECEMBER, 31);
        LootItemConditionalFunction.Builder<?> random0To1 = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F));
        LootItemConditionalFunction.Builder<?> random3To4 = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(3.0F, 4.0F));
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

        add(BossEntities.EATER_OF_WORLDS.get(), LootTable.lootTable()
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
        add(BossEntities.THE_DESTROYER_PROBE.get(), LootTable.lootTable());
        add(BossEntities.LUNATIC_CULTIST_CLONE.get(), LootTable.lootTable());
        add(MonsterEntities.VISUAL_NEURON.get(), LootTable.lootTable()
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
        add(MonsterEntities.GOBLIN_SCOUT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TATTERED_CLOTH).apply(count1To2)).apply(random0To1)
                )
        );
        // 丛林蜘蛛 无掉落物（仅钱币），掉落表已齐全
        add(MonsterEntities.JUNGLE_CREEPER.get(), LootTable.lootTable());
        // 沙漠幽魂 缺少未加入物品：沙漠幽魂灯(Desert Spirit Lamp, 2.5%)、神灯诅咒(Djinn's Curse, 3.25%)、生命吞噬者(Eater Of Life, 0.67%)
        add(MonsterEntities.DESERT_SPIRIT.get(), LootTable.lootTable());
        add(MonsterEntities.WALL_CREEPER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.FRIED_EGG)).add(EmptyLootItem.emptyItem().setWeight(29)))
        );
        // 黑隐士 缺少未加入物品：毒刺法杖(Poison Staff, 2.5%)
        add(MonsterEntities.BLACK_RECLUSE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SPIDER_FANG).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .when(() -> new DifficultyChanceLootItemCondition(0.5F, 0.9F)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.FRIED_EGG)).add(EmptyLootItem.emptyItem().setWeight(29)))
        );
        // 蚁狮 掉落物已齐全（蚁狮上颚、芭菲）
        add(MonsterEntities.ANTLION.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2)).add(EmptyLootItem.emptyItem().setWeight(2)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2)).add(emptyWeight98))
        );
        add(MonsterEntities.ANTLION_LARVA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE))
                        .add(EmptyLootItem.emptyItem().setWeight(5))));
        add(MonsterEntities.ANTLION_CHARGER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ANTLION_MANDIBLE).apply(count1To2))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.BANANA_SPLIT).setWeight(2)).add(emptyWeight98))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.MANDIBLE_BLADE).setWeight(2)).add(emptyWeight98))
        );
        add(MonsterEntities.ANTLION_SWARMER.get(), LootTable.lootTable()
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
        add(MonsterEntities.GIANT_ANTLION_SWARMER.get(), LootTable.lootTable()
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
        add(MonsterEntities.ANGER_BONES.get(), LootTable.lootTable()
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
        add(MonsterEntities.BIG_ANGER_BONES.get(), LootTable.lootTable()
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
        add(MonsterEntities.BIG_BONES.get(), LootTable.lootTable()
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
        add(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), LootTable.lootTable()
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
        add(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), LootTable.lootTable()
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
        add(MonsterEntities.SHORT_BONES.get(), LootTable.lootTable()
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
        // 水矢怪 掉落物已齐全（水矢）
        add(MonsterEntities.WATER_BOLT_MIMIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ManaWeaponItems.WATER_BOLT)).add(EmptyLootItem.emptyItem().setWeight(39)))
        );
        add(MonsterEntities.DARK_CASTER.get(), LootTable.lootTable()
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
                        .add(LootItem.lootTableItem(AccessoryItems.NAZAR).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CREAM_SODA).setWeight(3)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(197))
                )
        );
        add(MonsterEntities.BLOOD_CRAWLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(MonsterEntities.BLOOD_ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setWeight(67).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get()).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(995))
                )
        );
        // 僵尸新娘 缺少未加入物品：婚纱(Wedding Dress, 100%)
        add(MonsterEntities.THE_BRIDE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR)).add(EmptyLootItem.emptyItem().setWeight(4)))
        );
        // 僵尸新郎 缺少未加入物品：大脑(Brain, 75%)（高顶礼帽、血泪已实现）
        add(MonsterEntities.THE_GROOM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.TOP_HAT)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR))
                        .add(EmptyLootItem.emptyItem().setWeight(4))
                )
        );
        add(MonsterEntities.DRIPPLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.SHARK_TOOTH_NECKLACE).setWeight(67).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get()).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(995))
                )
        );
        add(MonsterEntities.BLOODY_SPORE.get(), LootTable.lootTable()
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
        add(MonsterEntities.CAVE_BAT.get(), batCommon()
        );
        add(MonsterEntities.GIANT_BAT.get(), batCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.TRIFOLD_MAP))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        // 巨型蝙蝠 缺少未加入物品：深度计(Depth Meter, 1%)（三折地图已实现）
        // 装甲骷髅 缺少未加入物品：光束剑(Beam Sword, 0.67%)
        add(MonsterEntities.ARMORED_SKELETON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.ARMOR_POLISH))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        // 岩石巨人 缺少未加入物品：岩石巨人头(Rock Golem Head, 33.33%)
        add(MonsterEntities.ROCK_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.STONE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20)))));
        // 狼人 缺少未加入物品：狼牙(Wolf Fang, 1.5%)
        add(MonsterEntities.WEREWOLF.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.MOON_CHARM)).add(EmptyLootItem.emptyItem().setWeight(59)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.ADHESIVE_BANDAGE))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        // 夜明蝙蝠 缺少未加入物品：结晶(Crystallize, 0.67%)（苹果派、蝙蝠棍已实现）
        add(MonsterEntities.ILLUMINANT_BAT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.APPLE_PIE)).add(EmptyLootItem.emptyItem().setWeight(149)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.BAT_BAT)).add(EmptyLootItem.emptyItem().setWeight(299))));
        add(MonsterEntities.LAVA_BAT.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.MAGMA_STONE)).add(EmptyLootItem.emptyItem().setWeight(49)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.BAT_BAT)).add(EmptyLootItem.emptyItem().setWeight(299))));
        add(MonsterEntities.SPORE_BAT.get(), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BoomerangItems.SHROOMERANG).setWeight(5).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(195))
                )
        );
        add(MonsterEntities.SPORE_ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025F, 0.01F))
                )
        );
        add(MonsterEntities.HAT_SPORE_ZOMBIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(count1To2).apply(random0To1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                        .add(LootItem.lootTableItem(MaterialItems.GLOWING_MUSHROOM).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025F, 0.01F))
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
                        .add(LootItem.lootTableItem(FoodItems.CARTON_OF_MILK).setWeight(67).apply(random0To1))
                        .add(EmptyLootItem.emptyItem().setWeight(9933))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BONE_SWORD).setQuality(1).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(9995))
                )
        );
        add(MonsterEntities.UNDEAD_VIKING.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(random0To1).apply(count1To2))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArmorItems.VIKING_HELMET).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).setWeight(4))
                        .add(EmptyLootItem.emptyItem().setWeight(96))
                )
        );
        // 冰雪巨人 缺少未加入物品：冰雪羽(Ice Feather, 33.3%)
        add(MonsterEntities.ICE_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.FROST_CORE)))
        );
        // 装甲维京海盗 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)（罗盘已实现）
        add(MonsterEntities.ARMORED_VIKING.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS))
                        .add(EmptyLootItem.emptyItem().setWeight(99)))
        );
        // 冰雪鱼人 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)、寒冰法杖(Frost Staff, 2%)
        add(MonsterEntities.ICY_MERMAN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.MILKSHAKE).apply(random0To1))
                        .add(EmptyLootItem.emptyItem().setWeight(74)))
        );
        // 冰雪精 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)、寒冰法杖(Frost Staff, 2%)
        add(MonsterEntities.ICE_ELEMENTAL.get(), LootTable.lootTable());
        // 冰雪陆龟 缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)（龟壳、奶昔已实现）
        add(MonsterEntities.ICE_TORTOISE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FROZEN_TURTLE_SHELL).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.MILKSHAKE).apply(random0To1))
                        .add(EmptyLootItem.emptyItem().setWeight(74)))
        );
        add(MonsterEntities.CRIMERA.get(), LootTable.lootTable()
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
        add(MonsterEntities.FACE_MONSTER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.VERTEBRA).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.TENTACLE_MACE).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(MonsterEntities.DECAYEDER.get(), LootTable.lootTable()
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
        add(MonsterEntities.DEMON_EYE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LENS).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.BLACK_LENS))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        // 僵尸鱼人 缺少未加入物品：血雨弓(Blood Rain Bow, 12.5%)、钱币槽(Money Trough, 6.67%)、鱼饵桶(Chum Bucket, 50%)
        add(MonsterEntities.ZOMBIE_MERMAN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SummonItems.VAMPIRE_FROG_STAFF)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FishingPoleItems.CHUM_CASTER)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.BLOOD_TEAR)).add(EmptyLootItem.emptyItem().setWeight(24))));
        add(MonsterEntities.WANDERING_EYE_FISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SummonItems.VAMPIRE_FROG_STAFF)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.BLOODY_SPINE).setWeight(20))
                        .add(EmptyLootItem.emptyItem().setWeight(80))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ENDER_EYE)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.RED_DYE)))
        );
        add(MonsterEntities.DEVOURER.get(), LootTable.lootTable()
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
                        .add(LootItem.lootTableItem(TCItems.METAL_DETECTOR))
                )
        );
        add(MonsterEntities.SNATCHER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        // 真菌球怪 / 巨型真菌球怪 无掉落物（仅钱币），掉落表已齐全
        add(MonsterEntities.FUNGI_BULB.get(), LootTable.lootTable());
        add(MonsterEntities.GIANT_FUNGI_BULB.get(), LootTable.lootTable());
        // 爬藤怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、生命吞噬者(Eater Of Life, 0.67%)（诅咒焰已实现，但缺专家掉率提升）
        add(MonsterEntities.CLINGER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModBlocks.CURSED_FLAME)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FunctionalBlocks.MEAT_GRINDER)).add(EmptyLootItem.emptyItem().setWeight(199))));
        add(MonsterEntities.MAN_EATER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.MAN_EATER_VINE)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COFFEE).setWeight(333).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(MonsterEntities.FLYING_FISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FILAMENTOUS_FIN).setWeight(33).setQuality(1)).apply(count1To2).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
        );
        add(MonsterEntities.EATER_OF_SOULS.get(), LootTable.lootTable()
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
                        .add(LootTableReference.lootTableReference(ModLootTables.CORRUPTION_CARRY).setWeight(19).setQuality(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9981))
                )
        );
        add(MonsterEntities.GIANT_SHELLY.get(), LootTable.lootTable()
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
        add(MonsterEntities.CRAWDAD.get(), LootTable.lootTable()
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
        add(MonsterEntities.GIANT_WORM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.WHOOPIE_CUSHION).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        // 挖掘怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、可疑苹果(Suspicious Looking Apple, 40%)
        add(MonsterEntities.DIGGER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.WHOOPIE_CUSHION).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        // 吞世怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、生命吞噬者(Eater Of Life, 0.67%)、吞世怪风筝(World Feeder Kite, 4%)、可疑苹果(Suspicious Looking Apple, 45%)
        add(MonsterEntities.WORLD_FEEDER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModBlocks.CURSED_FLAME))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                )
        );
        // 符文法师 缺少未加入物品：符文帽(Rune Hat, 100%)、符文长袍(Rune Robe, 100%)
        add(MonsterEntities.RUNE_WIZARD.get(), LootTable.lootTable());
        // 提姆 掉落物已齐全（巫师帽）
        add(MonsterEntities.TIM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ArmorItems.WIZARD_HAT)))
        );
        add(MonsterEntities.DOCTOR_BONES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.ARCHAEOLOGISTS_HAT)))
        );
        add(MonsterEntities.HARPY.get(), LootTable.lootTable()
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
        add(MonsterEntities.HELL_BAT.get(), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.MAGMA_STONE).setWeight(34))
                        .add(EmptyLootItem.emptyItem().setWeight(966))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
        );
        add(MonsterEntities.FIRE_IMP.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.OBSIDIAN_ROSE))
                        .add(EmptyLootItem.emptyItem().setWeight(19))
                )
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
        );
        // 愤怒翻滚怪 掉落物已齐全（玉米片）
        add(MonsterEntities.ANGRY_TUMBLER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS)).add(EmptyLootItem.emptyItem().setWeight(29))));
        add(MonsterEntities.WINDY_BALLOON.get(), LootTable.lootTable());
        add(MonsterEntities.OLD_SHAKING_CHEST.get(), LootTable.lootTable());
        add(MonsterEntities.CLUMSY_BALLOON_SLIME.get(), LootTable.lootTable());
        // 红魔鬼 缺少未加入物品：火焰羽(Fire Feather, 2%)、烈火之花(Flower of Fire, 3.33%)（热狗、邪恶三叉戟已实现）
        add(MonsterEntities.RED_DEVIL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.HOTDOG)).add(EmptyLootItem.emptyItem().setWeight(29))));
        add(MonsterEntities.DEMON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().when(afterSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(YoyoItems.CASCADE))
                        .add(EmptyLootItem.emptyItem().setWeight(399))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.DEMON_SCYTHE).setWeight(286))
                        .add(EmptyLootItem.emptyItem().setWeight(9714))
                )
        );
        add(MonsterEntities.VOODOO_DEMON.get(), LootTable.lootTable()
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
        // 青苔黄蜂 缺少未加入物品：破碎蜂翼(Tattered Bee Wing, 1%)（蜂刺、牛黄已实现）
        add(MonsterEntities.MOSS_HORNET.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.STINGER)).add(EmptyLootItem.emptyItem().setWeight(5)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.BEZOAR))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F))));
        add(MonsterEntities.HORNET.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER)).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BEZOAR).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(MonsterEntities.ICE_BAT.get(), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
        );
        add(MonsterEntities.SNOW_FLINX.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.FLINX_FUR)).apply(random0To1).apply(count1To2)
                )
        );
        add(MonsterEntities.JUNGLE_BAT.get(), batCommon()
        );
        add(MonsterEntities.PIRANHA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS).setWeight(133))
                        .add(EmptyLootItem.emptyItem().setWeight(9867))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.HOOK).setWeight(33))
                        .add(EmptyLootItem.emptyItem().setWeight(967))
                )
        );
        // 腐化金鱼 / 毒金鱼 无掉落物（仅钱币），掉落表已齐全
        add(MonsterEntities.CORRUPT_GOLDFISH.get(), LootTable.lootTable());
        add(MonsterEntities.VICIOUS_GOLDFISH.get(), LootTable.lootTable());
        // 琵琶鱼 缺少未加入物品：机器人帽(Robot Hat, 0.4%)（粘性绷带已实现）
        add(MonsterEntities.ANGLER_FISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.ROBOT_HAT)).add(EmptyLootItem.emptyItem().setWeight(249)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.ADHESIVE_BANDAGE))
                        .when(() -> new DifficultyChanceLootItemCondition(0.01F, 0.0199F)))
        );
        // 沙鲨 / 腐化沙鲨 / 猩红沙鲨 / 神圣沙鲨 缺少未加入物品：沙鲨风筝(Sand Shark Kite, 4%)
        add(MonsterEntities.SAND_SHARK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SHARK_FIN)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS)).add(EmptyLootItem.emptyItem().setWeight(29))));
        add(MonsterEntities.BONE_BITER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SHARK_FIN)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS)).add(EmptyLootItem.emptyItem().setWeight(29)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.DARK_SHARD)).add(EmptyLootItem.emptyItem().setWeight(24))));
        add(MonsterEntities.FLESH_REAVER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SHARK_FIN)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS)).add(EmptyLootItem.emptyItem().setWeight(29)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.DARK_SHARD)).add(EmptyLootItem.emptyItem().setWeight(24))));
        add(MonsterEntities.CRYSTAL_THRESHER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SHARK_FIN)).add(EmptyLootItem.emptyItem().setWeight(7)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.NACHOS)).add(EmptyLootItem.emptyItem().setWeight(29)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.LIGHT_SHARD)).add(EmptyLootItem.emptyItem().setWeight(24))));
        add(MonsterEntities.SHARK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP_PO_BOY).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DIVING_HELMET))
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
                        .add(LootItem.lootTableItem(FoodItems.HOTDOG).setWeight(333)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(667))
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
                        .add(LootItem.lootTableItem(FoodItems.HOTDOG).setWeight(333)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(667))
                )
        );
        add(MonsterEntities.ANGER_GOBLIN.get(), goblinCommon());
        add(MonsterEntities.GOBLIN_ARCHER.get(), goblinCommon());
        add(MonsterEntities.GOBLIN_PEON.get(), goblinCommon());
        add(MonsterEntities.GOBLIN_SORCERER.get(), goblinCommon());
        // 哥布林术士 缺少未加入物品：暗影焰弓(Shadowflame Bow, 33.3%)、暗影焰巫术娃娃(Shadowflame Hex Doll, 33.3%)、暗影焰刀(Shadowflame Knife, 33.3%)
        add(MonsterEntities.GOBLIN_WARLOCK.get(), goblinCommon());
        add(MonsterEntities.SHADOWFLAME_APPARITION.get(), LootTable.lootTable());
        // 侏儒 无掉落物（仅钱币），掉落表已齐全
        add(MonsterEntities.GNOME.get(), LootTable.lootTable());
        add(MonsterEntities.GOBLIN_THIEF.get(), goblinCommon());
        add(MonsterEntities.GOBLIN_WARRIOR.get(), goblinCommon());
        // 海盗七怪（水手/私船海盗/神射手/弩手/船长/诅咒/鹦鹉）缺少未加入物品：钱币枪、桶式发射器、海盗法杖、弯刀、水手帽/眼罩/水手上衣/水手裤、金平台、船长帽、诅咒之眼等
        add(MonsterEntities.PIRATE_DECKHAND.get(), pirateCommon(1));
        add(MonsterEntities.PIRATE_DEADEYE.get(), pirateCommon(1));
        add(MonsterEntities.PIRATE_CROSSBOWER.get(), pirateCommon(1));
        add(MonsterEntities.PIRATE_CORSAIR.get(), pirateCommon(1));
        add(MonsterEntities.PIRATE_CAPTAIN.get(), pirateCommon(4));
        add(MonsterEntities.PIRATE_PARROT.get(), LootTable.lootTable());
        add(MonsterEntities.PIRATES_CURSE.get(), LootTable.lootTable());
        add(NpcEntities.MECHANIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BoomerangItems.COMBAT_WRENCH))
                        .add(EmptyLootItem.emptyItem().setWeight(7))
                )
        );
        add(NpcEntities.STYLIST.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.STYLISH_SCISSORS))
                        .add(EmptyLootItem.emptyItem().setWeight(7))
                )
        );
        add(NpcEntities.DYE_TRADER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.EXOTIC_SCIMITAR))
                        .add(EmptyLootItem.emptyItem().setWeight(7))
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
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP_PO_BOY).setWeight(2))
                        .add(emptyWeight98).apply(random0To1)
                )
        );
        add(CritterEntities.HOSTILE_BUNNY.get(), LootTable.lootTable());
        add(CritterEntities.PENGUIN.get(), LootTable.lootTable());
        add(CritterEntities.MYSTIC_FROG.get(), LootTable.lootTable());
        add(CritterEntities.GOLDFISH.get(), LootTable.lootTable());
        // 腐化企鹅 / 猩红企鹅 缺少未加入物品：Pedguin 套装（兜帽/夹克/裤子，各 0.67%）
        add(MonsterEntities.CORRUPT_PENGUIN.get(), LootTable.lootTable());
        add(MonsterEntities.VICIOUS_PENGUIN.get(), LootTable.lootTable());
        add(CritterEntities.STINKBUG.get(), LootTable.lootTable());
        add(CritterEntities.FIREFLY.get(), LootTable.lootTable());
        add(CritterEntities.TRUFFLE_WORM.get(), LootTable.lootTable());
        add(CritterEntities.BUGGY.get(), LootTable.lootTable());
        add(CritterEntities.LIGHTNING_BUG.get(), LootTable.lootTable());
        add(MonsterEntities.ANGRY_NIMBUS.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ManaWeaponItems.NIMBUS_ROD)).add(EmptyLootItem.emptyItem().setWeight(14)))
        );
        // 愤怒蒲公英 掉落物已齐全（太阳花）
        add(MonsterEntities.ANGRY_DANDELION.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.DAYBLOOM).apply(count1To2))
                        .add(EmptyLootItem.emptyItem())
                )
        );
        // 花岗岩巨人 缺少未加入物品：晶洞(Geode, 5%)、夜视头盔(Night Vision Helmet, 3.3%)、响石(Snapping Stone, 1.25%)（花岗岩、意大利面已实现）
        add(MonsterEntities.GRANITE_GOLEM.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(NatureBlocks.GRANITE))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SPAGHETTI).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        add(MonsterEntities.GRANITE_ELEMENTAL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.SPAGHETTI).setWeight(2))
                        .add(emptyWeight98).apply(random0To1)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(NatureBlocks.GRANITE)).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))).apply(random0To1)
                )
        );
        // 装甲步兵 缺少未加入物品：压力球(Stress Ball, 1%)、角斗士胸甲(Gladiator Breastplate, 4.76%)（标枪、角斗士头盔/护腿、短剑、钩爪、披萨已实现）
        add(MonsterEntities.HOPLITE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.PIZZA).setWeight(2)).add(emptyWeight98))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.JAVELIN)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(40, 80)))).add(EmptyLootItem.emptyItem()))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArmorItems.GLADIATOR_HELMET))
                        .add(LootItem.lootTableItem(ArmorItems.GLADIATOR_CHESTPLATE))
                        .add(LootItem.lootTableItem(ArmorItems.GLADIATOR_LEGGINGS))
                        .add(LootItem.lootTableItem(ArmorItems.GLADIATOR_BOOTS))
                        .add(EmptyLootItem.emptyItem().setWeight(18)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(SwordItems.GLADIUS).setWeight(5)).add(EmptyLootItem.emptyItem().setWeight(95)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HOOK).setWeight(4)).add(EmptyLootItem.emptyItem().setWeight(96)))
        );
        add(MonsterEntities.METEOR_HEAD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().when(beforeSkeletronBehindWallOfFlesh)
                        .add(LootItem.lootTableItem(MaterialItems.RAW_METEORITE).setWeight(2))
                        .add(emptyWeight98).apply(random0To1)
                )
        );
        add(MonsterEntities.BLUE_SLIME.get(), slimeCommon(-10644993));
        add(MonsterEntities.DESERT_SLIME.get(), slimeCommon(-2727));
        add(MonsterEntities.GREEN_DUMPLING_SLIME.get(), slimeCommon(-8470674)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.GREEN_DUMPLING.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(MonsterEntities.GREEN_SLIME.get(), slimeCommon(-8470674));
        add(MonsterEntities.PURPLE_SLIME.get(), slimeCommon(-6326333));
        add(MonsterEntities.RED_SLIME.get(), slimeCommon(-1079407));
        add(MonsterEntities.YELLOW_SLIME.get(), slimeCommon(-871089));
        add(MonsterEntities.SLIMELING.get(), corruptionSlimeLoot(-6522185));
        add(MonsterEntities.JUNGLE_SLIME.get(), slimeCommon(-6570130));
        add(MonsterEntities.ICE_SLIME.get(), slimeCommon(-10628609)
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

                        )
                        .add(EmptyLootItem.emptyItem().setWeight(49))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get()))
                        .add(EmptyLootItem.emptyItem().setWeight(149))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.ICE_TOFU_BRICK.get())
                                .when(AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER_PLAYER, EntityPredicate.Builder.entity()
                                        .of(EntityType.PLAYER)
                                        .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                .mainhand(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.FROST_WALKER, MinMaxBounds.Ints.atLeast(1))).build()).build())))))
                        .add(EmptyLootItem.emptyItem().setWeight(14))
                )
        );
        add(MonsterEntities.LAVA_SLIME.get(), lavaSlimeLoot());
        add(MonsterEntities.BLACK_SLIME.get(), blackSlimeLoot());
        add(MonsterEntities.MOTHER_SLIME.get(), motherSlimeLoot());
        add(MonsterEntities.BABY_SLIME.get(), blackSlimeLoot());
        add(MonsterEntities.TROPIC_SLIME.get(), slimeCommon(-10644993)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.TROPICAL_FISH))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
        );
        add(MonsterEntities.PINK_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.SLIME_CARRY))
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
        add(MonsterEntities.SWAMP_SLIME.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.SLIME_CARRY))
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
        add(MonsterEntities.SPIKED_SLIME.get(), slimeCommon(-10644993)
        );
        add(MonsterEntities.SPIKED_ICE_SLIME.get(), slimeCommon(-10628609)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(933))
                )
        );
        add(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), slimeCommon(-6570130)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.STINGER.get()))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem())
                )
        );
        add(MonsterEntities.BLUE_JELLYFISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.PINK_JELLYFISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        // 肉后怪
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
        add(MonsterEntities.WRAITH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK).setWeight(2))
                        .add(emptyWeight98)
                )
        );
        // 血水母 缺少未加入物品：怪物肉(Monster Meat, 0.07%)
        add(MonsterEntities.BLOOD_JELLY.get(), LootTable.lootTable());
        // 蘑菇水母 无掉落物（仅钱币），掉落表已齐全
        add(MonsterEntities.FUNGO_FISH.get(), LootTable.lootTable());
        add(MonsterEntities.GREEN_JELLYFISH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.MEGAPHONE).setWeight(2))
                        .add(emptyWeight98)
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.JELLYFISH_NECKLACE))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.LUMINOUS_SLIME.get(), LootTable.lootTable()
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
                                .apply(setGelColor(-4040988))
                        )
        );
        add(MonsterEntities.CRIMSLIME.get(), corruptionSlimeLoot(-3386287));
        add(MonsterEntities.CORRUPT_SLIME.get(), corruptionSlimeLoot(-6522185));
        // 宝箱怪
        add(MonsterEntities.WOODEN_MIMIC.get(), mimicCommon()
        );
        add(MonsterEntities.GOLDEN_MIMIC.get(), mimicCommon()
        );
        add(MonsterEntities.SHADOW_MIMIC.get(), mimicCommon()
        );
        add(MonsterEntities.ICE_MIMIC.get(), LootTable.lootTable()
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
        add(MonsterEntities.ICE_MIMIC.get(),LootTable.lootTable()
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
        add(MonsterEntities.CRIMSON_MIMIC.get(), bigMimicCommon()
                .withPool(LootPool.lootPool()
                        // 夺命杖
                        // 飞镖手枪
                        // 臭虎爪
                        .add(LootItem.lootTableItem(HookItems.TENDON_HOOK))
                        .add(LootItem.lootTableItem(TCItems.FLESH_KNUCKLES))
                )
        );
        add(MonsterEntities.CORRUPT_MIMIC.get(), bigMimicCommon()
                .withPool(LootPool.lootPool()
                        // 爬藤怪法杖
                        // 飞镖步枪
                        .add(LootItem.lootTableItem(FlailItems.CHAIN_GUILLOTINES))
                        .add(LootItem.lootTableItem(HookItems.WORM_HOOK))
                        .add(LootItem.lootTableItem(TCItems.PUTRID_SCENT))
                )
        );
        add(MonsterEntities.HALLOWED_MIMIC.get(), bigMimicCommon()
                .withPool(LootPool.lootPool()
                        // 飞刀
                        .add(LootItem.lootTableItem(ManaWeaponItems.CRYSTAL_VILE_SHARD))
                        .add(LootItem.lootTableItem(BowItems.DAEDALUS_STORM_BOW))
                        .add(LootItem.lootTableItem(HookItems.ILLUMINANT_HOOK))
                )
        );
        add(MonsterEntities.JUNGLE_MIMIC.get(), LootTable.lootTable()
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
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ConsumableItems.SPARKLE_SLIME_BALLOON.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(69))))
                        .add(EmptyLootItem.emptyItem().setWeight(2))
                )
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
        add(MonsterEntities.MUMMY.get(), mummyCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FAST_CLOCK))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
        );
        add(MonsterEntities.DARK_MUMMY.get(), mummyCommon()
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
        add(MonsterEntities.BLOOD_MUMMY.get(), mummyCommon()
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
        add(MonsterEntities.LIGHT_MUMMY.get(), mummyCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.TRIFOLD_MAP))
                        .add(EmptyLootItem.emptyItem().setWeight(99))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.LIGHT_SHARD))
                        .add(EmptyLootItem.emptyItem().setWeight(9))
                )
        );
        add(MonsterEntities.DERPLING.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.GRAPE).setWeight(25))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(975))
                )
        );
        // 蛇蜥怪 缺少未加入物品：远古号角(Ancient Horn, 2%)（坚固化石已实现）
        add(MonsterEntities.BASILISK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))));
        add(MonsterEntities.GHOUL.get(), ghoulCommon()
        );
        add(MonsterEntities.VILE_GHOUL.get(), ghoulCommon()
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
        add(MonsterEntities.TAINTED_GHOUL.get(), ghoulCommon()
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
        add(MonsterEntities.DREAMER_GHOUL.get(), ghoulCommon()
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
        add(MonsterEntities.SAND_POACHER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.FRIED_EGG).setWeight(333))
                        .apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(9667))
                )
        );
        add(MonsterEntities.GIANT_TORTOISE.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.TURTLE_SHELL).setWeight(833))
                        .add(EmptyLootItem.emptyItem().setWeight(9167))
                )
        );
        add(MonsterEntities.GIANT_FLYING_FOX.get(), batCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.GRAPE))
                        .add(EmptyLootItem.emptyItem().setWeight(39))
                ));
        add(MonsterEntities.CORRUPTOR.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.ROTTEN_CHUNK).setWeight(33).setQuality(1)).apply(random0To1)
                        .add(EmptyLootItem.emptyItem().setWeight(67))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.VITAMINS).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98))
                )
        );
        add(MonsterEntities.SLIMER.get(), LootTable.lootTable());
        add(MonsterEntities.WINGLESS_SLIMER.get(), corruptionSlimeLoot(-6522185));
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
        // 混沌精 缺少未加入物品：混乱之杖(Rod of Discord, 0.25%)（苹果派已实现，需要补 0.67% 概率）
        add(MonsterEntities.CHAOS_ELEMENTAL.get(), LootTable.lootTable()
                // 混沌传送杖
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.APPLE_PIE).setWeight(67))
                        .add(EmptyLootItem.emptyItem().setWeight(9933)).apply(random0To1)
                )
        );
        add(MonsterEntities.ENCHANTED_SWORD.get(), LootTable.lootTable());
        add(MonsterEntities.PALADIN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.PALADINS_SHIELD))
                        .add(EmptyLootItem.emptyItem().setWeight(14))));
        add(MonsterEntities.BONE_LEE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLACK_BELT))
                        .add(EmptyLootItem.emptyItem().setWeight(11))));
        // 死灵法师 缺少未加入物品：暗影束法杖(Shadowbeam Staff, 9.75%)
        add(MonsterEntities.NECROMANCER.get(), LootTable.lootTable());
        add(MonsterEntities.DIABOLIST.get(), LootTable.lootTable());
        add(MonsterEntities.RAGGED_CASTER.get(), LootTable.lootTable());
        add(MonsterEntities.ARCH_WYVERN.get(), LootTable.lootTable());
        LootPool.Builder rainbowSheep = LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.MUTTON)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(shouldSmeltLoot()))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))));
        add(ModEntities.RAINBOW_SHEEP.get(), LootTable.lootTable().withPool(rainbowSheep));
        add(ModEntities.RAINBOW_SHEEP.get(), ModLootTables.SHEEP_RAINBOW_WOOL, LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.RAINBOW_WOOL)))
                .withPool(rainbowSheep)
        );
    }

    private AnyOfCondition.Builder shouldSmeltLoot() {
        return AnyOfCondition.anyOf(
                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build())),
                LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.DIRECT_KILLER,
                        EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.FIRE_ASPECT, MinMaxBounds.Ints.ANY)).build()).build())
                )
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
        LootItemConditionalFunction.Builder<?> random0To1 = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.SLIME_CARRY))
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
                        .apply(setGelColor(gelColor))
                );
    }

    /**
     * 岩浆史莱姆不携带额外物品也不掉落凝胶，只保留其独立的稀有史莱姆法杖掉落。
     */
    private LootTable.Builder lavaSlimeLoot() {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF))
                .add(EmptyLootItem.emptyItem().setWeight(7999))
        );
    }

    /**
     * 史莱姆之母不携带随机物品，但保留凝胶、史莱姆法杖和指南针掉落。
     */
    private LootTable.Builder motherSlimeLoot() {
        LootItemConditionalFunction.Builder<?> random0To1 = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF))
                        .add(EmptyLootItem.emptyItem().setWeight(6999)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(setGelColor(-7697782)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS))
                        .add(EmptyLootItem.emptyItem().setWeight(99)));
    }

    private LootTable.Builder blackSlimeLoot() {
        return slimeCommon(-7697782)
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COMPASS))
                        .add(EmptyLootItem.emptyItem().setWeight(99)));
    }

    /**
     * 腐化、猩红和恶翼史莱姆族系共用凝胶、黑暗免疫饰品与史莱姆法杖掉落。
     */
    private LootTable.Builder corruptionSlimeLoot(int gelColor) {
        LootItemConditionalFunction.Builder<?> random0To1 = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SummonItems.SLIME_STAFF))
                        .add(EmptyLootItem.emptyItem().setWeight(6999)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.GEL))
                        .apply(random0To1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(setGelColor(gelColor)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLINDFOLD).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(98)));
    }

    private static LootItemConditionalFunction.Builder<?> setGelColor(int color) {
        CompoundTag component = new CompoundTag();
        component.putInt("color", color);
        CompoundTag components = new CompoundTag();
        components.put("confluence_magic_lib:nbt", component);
        CompoundTag stackTag = new CompoundTag();
        stackTag.put(IPortItemStack.DATA_COMPONENTS, components);
        return SetNbtFunction.setTag(stackTag);
    }

    private LootTable.Builder pirateCommon(int multiplier) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.LUCKY_COIN))
                        .when(LootItemRandomChanceCondition.randomChance(0.0005F * multiplier)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.DISCOUNT_CARD))
                        .when(LootItemRandomChanceCondition.randomChance(0.001F * multiplier)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(AccessoryItems.GOLD_RING))
                        .when(LootItemRandomChanceCondition.randomChance(0.002F * multiplier)));
    }

    private LootTable.Builder goblinCommon() {
        LootItemConditionalFunction.Builder<?> count1To5 = SetItemCountFunction.setCount(UniformGenerator.between(1, 5));
        LootItemConditionalFunction.Builder<?> random0To1 = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F));
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
