package org.confluence.terraentity.data.gen.loot;


import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.data.enchantment.TEEnchantments;
import org.confluence.terraentity.data.init.loot.conditioin.VariantCondition;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.item.*;

import java.util.ArrayList;
import java.util.stream.Stream;

public class TEEntityLootProvider extends EntityLootSubProvider {
    public TEEntityLootProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {

        // 宁芙
        var enchantbuilder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantbuilder.set(this.registries.lookup(Registries.ENCHANTMENT).get().get(TEEnchantments.MULTI_BOOMERANG).get(),1);
        this.add(TEMonsterEntities.NYMPH.get(), LootTable.lootTable().withPool(weightLootPool(
                singleItem(Items.ENCHANTED_BOOK, 1)
                        .apply(SetComponentsFunction.setComponent(DataComponents.ENCHANTMENTS,enchantbuilder.toImmutable())),
                0.5f
        )));

        // 抓人草
        var enchantbuilder1 = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantbuilder1.set(this.registries.lookup(Registries.ENCHANTMENT).get().get(TEEnchantments.WHIP_SWEEP).get(),1);
        var snatcherTable = LootTable.lootTable()
                .withPool(weightLootPool(singleItem(Items.ENCHANTED_BOOK, 1,5)
                        .apply(SetComponentsFunction.setComponent(DataComponents.ENCHANTMENTS,enchantbuilder1.toImmutable())), 0.1f))
                .withPool(singleItemPool(TEYoyosItems.AMAZON, 0.02f))
                ;
        this.add(TEMonsterEntities.SNATCHER.get(), snatcherTable);
        this.add(TEMonsterEntities.MAN_EATER.get(), snatcherTable);


        // 史王
        Stream.of(TEMonsterEntities.BLUE_SLIME, TEMonsterEntities.GREEN_SLIME, TEMonsterEntities.CORRUPT_SLIME, TEMonsterEntities.DESERT_SLIME, TEMonsterEntities.JUNGLE_SLIME, TEMonsterEntities.EVIL_SLIME, TEMonsterEntities.ICE_SLIME, TEMonsterEntities.LAVA_SLIME, TEMonsterEntities.LUMINOUS_SLIME, TEMonsterEntities.CRIMSLIME, TEMonsterEntities.PURPLE_SLIME, TEMonsterEntities.RED_SLIME, TEMonsterEntities.TROPIC_SLIME, TEMonsterEntities.YELLOW_SLIME, TEMonsterEntities.HONEY_SLIME, TEMonsterEntities.BLACK_SLIME, TEMonsterEntities.SWAMP_SLIME, TEMonsterEntities.GREEN_DUMPLING_SLIME, TEMonsterEntities.CRIMSLIME
                ).forEach(e->{
            this.add(e.get(), LootTable.lootTable()
                    .withPool(singleItemPool(TEBossSummonsItems.KING_SLIME_SUMMONS.get(), 0.01F))
                    .withPool(singleItemPool(Items.SLIME_BALL, 0.2F))
                    .withPool(singleItemPool(TESummonItems.SLIME_STAFF, 0.001F))
            );
        });
        this.add(TEMonsterEntities.PINK_SLIME.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEBossSummonsItems.KING_SLIME_SUMMONS.get(), 0.2F))
                .withPool(weightLootPool(singleItemIncrease(Items.SLIME_BALL, 1,2),0.2F))
                .withPool(singleItemPool(TESummonItems.SLIME_STAFF, 0.01F))
        );

        this.add(TEBossEntities.KING_SLIME.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.SLIME_STAFF, 0.33F))
                .withPool(singleItemPool(TEWhipItems.SWAMP_WHIP, 0.33F))
                .withPool(singleItemPool(TERideableItems.SLIMY_SADDLE, 0.2F))
                .withPool(singleItemPool(TEYoyosItems.CODE_1))
        );


        // 克眼
        this.add(TEMonsterEntities.DEMON_EYE.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEBossSummonsItems.EYE_OF_CTHULHU_SUMMONS, 0.05F))
                .withPool(weightLootPool(singleItemIncrease(Items.ENDER_EYE, 1,1), 0.05f))
        );

        this.add(TEBossEntities.EYE_OF_CTHULHU.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS, 0.5F))
                .withPool(singleItemPool(TEBossSummonsItems.EATER_OF_WORLDS_SUMMONS, 0.5F))
                .withPool(singleItemPool(TESummonItems.SLIME_STAFF, 1F))
        );


        // 克脑
        this.add(TEMonsterEntities.BLOODY_SPORE.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS, 0.2F))
                .withPool(singleItemPool(TEYoyosItems.ARTERY, 1F))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.GUNPOWDER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
                .withPool(LootPool.lootPool()
                        .add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                        .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS))))
                .withPool(weightLootPool(singleItem(Items.RED_DYE,1,1), 0.5f))
        );

        Stream.of(TEMonsterEntities.BLOOD_CRAWLER, TEMonsterEntities.DRIPPLER, TEMonsterEntities.BLOOD_ZOMBIE, TEMonsterEntities.CRIMERA)
                .forEach(e-> {
                    this.add(e.get(), LootTable.lootTable()
                            .withPool(singleItemPool(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS, 0.05F))
                            .withPool(weightLootPool(singleItem(Items.RED_DYE,1,1), 0.5f))
                    );
                });
        this.add(TEMonsterEntities.WANDERING_EYE_FISH.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS, 0.2F))
                .withPool(singleItemPool(Items.ENDER_EYE, 1,1))
                .withPool(weightLootPool(singleItem(Items.RED_DYE,1,1), 1f))
        );

        this.add(TEBossEntities.BRAIN_OF_CTHULHU.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.IRON_GOLEM_STAFF, 1F))
        );

        // 世吞
        Stream.of(TEMonsterEntities.EATER_OF_SOULS, TEMonsterEntities.DECAYEDER, TEMonsterEntities.DEVOURER)
                .forEach(e-> {
                    this.add(e.get(), LootTable.lootTable()
                            .withPool(singleItemPool(TEBossSummonsItems.EATER_OF_WORLDS_SUMMONS, 0.05F))
                    );
                });


        this.add(TEBossEntities.EATER_OF_WORLDS.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.IRON_GOLEM_STAFF))
        );

        // 蜂王
        this.add(TEMonsterEntities.HORNET.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEBossSummonsItems.QUEEN_BEE_SUMMONS, 0.05F))
                .withPool(singleItemPool(TEYoyosItems.HIVE_FIVE))
                .withPool(weightLootPool(singleItemIncrease(Items.HONEY_BOTTLE, 1, 2), 1f))
        );

        this.add(TEBossEntities.QUEEN_BEE.get(), LootTable.lootTable()
                .withPool(singleItemPool(Items.BEE_SPAWN_EGG, 1,1f))
                .withPool(singleItemPool(TERideableItems.HONEYED_GOGGLES,1, 0.2f))
        );

        // 骷髅王
        Stream.of(TEMonsterEntities.BASE_BONES, TEMonsterEntities.SHORT_BONES, TEMonsterEntities.BIG_BONES, TEMonsterEntities.ANGER_BONES,
                TEMonsterEntities.BIG_ANGER_BONES, TEMonsterEntities.BIG_HELMET_ANGER_BONES,TEMonsterEntities.BIG_MUSCLE_ANGER_BONES,
                TEMonsterEntities.CURSED_SKULL, TEMonsterEntities.SPORE_SKELETON, TEMonsterEntities.UNDEAD_VIKING).forEach(e->{
            this.add(e.get(), LootTable.lootTable()
                    .withPool(weightLootPool(singleItemIncrease(Items.BONE, 1, 3), 1f))
                    .withPool(singleItemPool(TEBossSummonsItems.SKELETRON_SUMMONS,1, 0.05f))
            );
        });


        this.add(TEBossEntities.SKELETRON_HAND.get(), LootTable.lootTable()
                .withPool(weightLootPool(singleItem(Items.BONE, 10, 20), 1f))
                .withPool(singleItemPool(TESummonItems.IRON_GOLEM_STAFF,1, 1f))
                .withPool(singleItemPool(TEYoyosItems.VALOR))

        );

        var table = LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.SUMMON_NETHERITE_SWORD_STAFF, 1,1f))
                .withPool(singleItemPool(TEBoomerangItems.FLAMARANG, 1,1f));

        // 肉山
        this.add(TEBossEntities.HILL_OF_FLESH.get(), table);
        this.add(TEBossEntities.WALL_OF_FLESH.get(), table);


    // 小怪



        // 地狱怪

        this.add(TEMonsterEntities.VOODOO_DEMON.get(), LootTable.lootTable()
//                .withPool(singleItemPool(TESummonItems.SUMMON_NETHERITE_SWORD_STAFF, 1,0.05f))
                .withPool(weightLootPool(singleItemIncrease(Items.BLAZE_POWDER, 1, 2), 1f))
                .withPool(weightLootPool(singleItemIncrease(TEYoyosItems.CASCADE, 1, 2), 0.003f))
                .withPool(singleItemPool(TEBossSummonsItems.HILL_OF_FLESH_SUMMONS, 1, 0.2f))
        );
        this.add(TEMonsterEntities.DEMON.get(), LootTable.lootTable()
//                .withPool(singleItemPool(TESummonItems.SUMMON_NETHERITE_SWORD_STAFF, 1,0.05f))
                .withPool(weightLootPool(singleItemIncrease(Items.BLAZE_POWDER, 1, 2), 1f))
                .withPool(weightLootPool(singleItemIncrease(TEYoyosItems.CASCADE, 1, 2), 0.003f))
        );


        Stream.of(TEMonsterEntities.HELL_BAT)
                .forEach(e-> {
                    this.add(e.get(), LootTable.lootTable()
                            .withPool(weightLootPool(singleItemIncrease(Items.BLAZE_POWDER, 1, 2), 1f))
                            .withPool(weightLootPool(singleItemIncrease(TEYoyosItems.CASCADE, 1, 2), 0.003f))
                    );
                });


        this.add(TEMonsterEntities.FIRE_IMP.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.IMP_STAFF, 1,0.05f))
                .withPool(weightLootPool(singleItemIncrease(Items.BLAZE_POWDER, 1, 2), 1f))
                .withPool(weightLootPool(singleItemIncrease(TEYoyosItems.CASCADE, 1, 2), 0.003f))
        );

        // 雪地怪
        Stream.of(TEMonsterEntities.ICE_BAT)
                .forEach(e-> {
                    this.add(e.get(), LootTable.lootTable()
                            .withPool(weightLootPool(singleItemIncrease(Items.SNOWBALL, 1, 2), 1f))
                    );
                });

        this.add(TEMonsterEntities.SNOW_FLINX.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.SNOW_FLINX_STAFF, 1,0.05f))
                .withPool(weightLootPool(singleItemIncrease(Items.SNOWBALL, 1, 2), 1f))
        );


        // 沙漠怪
        Stream.of(TEMonsterEntities.ANTLION_SWARMER, TEMonsterEntities.GIANT_ANTLION_SWARMER, TEMonsterEntities.TOMB_CRAWLER)
                .forEach(e-> {
                    this.add(e.get(), LootTable.lootTable()
                            .withPool(weightLootPool(singleItemIncrease(Items.ROTTEN_FLESH, 1, 2), 1f))
                            .withPool(weightLootPool(singleItemIncrease(Items.SAND, 1, 2), 1f))
                    );
                });


        // 雀杖
        this.add(TEAnimals.BIRD.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.FINCH_STAFF, 1,0.05f))
        );
        this.add(TEAnimals.CARDINAL.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.FINCH_STAFF, 1,0.05f))
        );
        this.add(TEAnimals.BLUE_JAY.get(), LootTable.lootTable()
                .withPool(singleItemPool(TESummonItems.FINCH_STAFF, 1,0.05f))
        );

        // 哥布林
        Stream.of(TEMonsterEntities.GOBLIN_SCOUT,TEMonsterEntities.GOBLIN_THIEF,TEMonsterEntities.GOBLIN_WARRIOR, TEMonsterEntities.GOBLIN_SORCERER,
                TEMonsterEntities.GOBLIN_PEON,TEMonsterEntities.ANGER_GOBLIN,TEMonsterEntities.DARK_CASTER,
                TEMonsterEntities.SPORE_ZOMBIE
        ).forEach(e->{
            this.add(e.get(), LootTable.lootTable()
                    .withPool(weightLootPool(singleItemIncrease(Items.ROTTEN_FLESH, 1, 2), 1f))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT))
                            .add(LootItem.lootTableItem(Items.CARROT))
                            .add(LootItem.lootTableItem(Items.POTATO)
                                    .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))));
        });

        // 巨大卷壳怪
        this.add(TEMonsterEntities.GIANT_SHELLY.get(), LootTable.lootTable()
                .withPool(singleItemPool(TEYoyosItems.RALLY, 1, 0.039F)));


        this.add(TEMonsterEntities.GOBLIN_ARCHER.get(), LootTable.lootTable()
                .withPool(weightLootPool(singleItemIncrease(Items.ROTTEN_FLESH, 1, 3), 1f))
                .withPool(weightLootPool(singleItemIncrease(Items.ARROW, 1, 2), 1f))
        );

        // 鸟妖
        this.add(TEMonsterEntities.HARPY.get(), LootTable.lootTable()
                .withPool(weightLootPool(singleItem(Items.CHICKEN, 1), 1f)
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                )
        );

        // 飞鱼
        this.add(TEMonsterEntities.FLYING_FISH.get(), LootTable.lootTable()
                .withPool(weightLootPool(singleItemIncrease(Items.FEATHER, 1, 2),1))
                .withPool(weightLootPool(singleItemIncrease(Items.COD, 1, 1),1)
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                )
        );

        this.add(TEMonsterEntities.PIRANHA.get(), LootTable.lootTable()
                .withPool(weightLootPool(singleItemIncrease(Items.SALMON, 1, 1),1)
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                )
        );

        // 动物
        Stream.of(TEAnimals.BUNNY, TEAnimals.BUNNY, TEAnimals.SQUIRREL).forEach(e->{
            this.add(e.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(Items.RABBIT_HIDE)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(Items.RABBIT).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                    .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(Items.RABBIT_FOOT)).when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1F, 0.03F))));
        });

        Stream.of(TEAnimals.JEWEL_BUNNY, TEAnimals.JEWEL_SQUIRREL).forEach(e->{
            this.add(e.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(singleItemIncrease(Items.COPPER_INGOT, 1, 2).when(VariantCondition.of(0)))
                            .add(singleItemIncrease(Items.AMETHYST_SHARD, 1, 2).when(VariantCondition.of(1)))
                            .add(singleItemIncrease(Items.DIAMOND, 1, 1).when(VariantCondition.of(2)))
                            .add(singleItemIncrease(Items.EMERALD, 1, 1).when(VariantCondition.of(3)))
                            .add(singleItemIncrease(Items.GOLD_INGOT, 1, 2).when(VariantCondition.of(4)))
                            .add(singleItemIncrease(Items.REDSTONE, 1, 2).when(VariantCondition.of(5)))
                            .add(singleItemIncrease(Items.LAPIS_LAZULI, 1, 2).when(VariantCondition.of(6)))
                            .add(singleItemIncrease(Items.RAW_GOLD, 1, 2).when(VariantCondition.of(7)))
                    )
            );
        });


    }


    public static LootPool.Builder singleItemPool(ItemLike item, int count, float chance){
        return weightLootPool(singleItem(item, count), chance);
    }

    public static LootPool.Builder singleItemPool(ItemLike item, float chance){
        return weightLootPool(singleItem(item, 1), chance);
    }

    public static LootPool.Builder singleItemPool(ItemLike item){
        return weightLootPool(singleItem(item, 1), 1);
    }

    public static LootPool.Builder weightLootPool(LootPoolSingletonContainer.Builder<?> builder, float chance){
        if(chance >= 1){
            return LootPool.lootPool().add(builder);
        }
        int weight = (int) (chance * 1000);
        int emptyWeight = 1000 - weight;
        return LootPool.lootPool().add(builder.setWeight(weight)).add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
    }


    public static LootPoolSingletonContainer.Builder<?> singleItem(ItemLike item, int count){
        if(count == 1)
            return LootItem.lootTableItem(item);
        return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)));
    }

    public static LootPoolSingletonContainer.Builder<?> singleItem(ItemLike item, int countMin, int countMax){
        return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(countMin, countMax)));
    }

    public LootPoolSingletonContainer.Builder<?> singleItem(ItemLike item, int count, float enchantmentChance){
        return singleItem(item, count).apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, Math.max(count * enchantmentChance, 1.0F))));
    }


    public LootPoolSingletonContainer.Builder<?> singleItemIncrease(ItemLike item, int countMin, int countMax){
        return singleItem(item, countMin, countMax).apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1)));
    }


    private Stream<EntityType<?>> getIterableFromRegister(DeferredRegister<EntityType<?>> register) {
        return new ArrayList<EntityType<?>>(
                register.getEntries().stream()
                        .map(DeferredHolder::get)
                        .filter(map::containsKey)
                        .toList()
        ).stream();
    }

//    protected void add(EntityType<?> entityType, LootTable.Builder builder) {
//        ResourceKey<LootTable> resourceKey = ResourceKey.create(Registries.LOOT_TABLE, BuiltInRegistries.ENTITY_TYPE.getKey(entityType).withPrefix("te"));
//        this.add(entityType, resourceKey, builder);
//    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return getIterableFromRegister(TEEntities.ENTITIES);
    }
}
