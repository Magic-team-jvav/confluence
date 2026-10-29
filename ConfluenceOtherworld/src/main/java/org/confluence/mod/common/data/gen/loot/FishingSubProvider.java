package org.confluence.mod.common.data.gen.loot;

import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.CrateBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.loot.GamePhaseLootItemCondition;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_curio.common.init.TCItems;

public record FishingSubProvider() implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        // 1.20 原生位置谓词按群系键匹配，集中列出本体及汇流群系。
        List<ResourceKey<Biome>> isDesertOrBadlands = List.of(
                Biomes.DESERT, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS
        );
        List<ResourceKey<Biome>> isOceanOrBeach = List.of(
                Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE
        );
        List<ResourceKey<Biome>> isSnowyOrIcy = List.of(
                Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.SNOWY_BEACH, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.GROVE
        );
        List<ResourceKey<Biome>> isJungleOrLush = List.of(
                Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.LUSH_CAVES
        );
        List<ResourceKey<Biome>> isSavanna = List.of(
                Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA
        );
        List<ResourceKey<Biome>> isCrimson = List.of(
                ModBiomes.THE_CRIMSON, ModBiomes.THE_CRIMSON_DESERT, ModBiomes.THE_CRIMSON_TUNDRA
        );
        List<ResourceKey<Biome>> isMushroom = List.of(
                Biomes.MUSHROOM_FIELDS, ModBiomes.GLOWING_MUSHROOM
        );
        List<ResourceKey<Biome>> isCorruption = List.of(
                ModBiomes.THE_CORRUPTION, ModBiomes.THE_CORRUPTION_DESERT, ModBiomes.THE_CORRUPTION_TUNDRA
        );
        List<ResourceKey<Biome>> isHallow = List.of(
                ModBiomes.THE_HALLOW, ModBiomes.THE_HALLOW_DESERT, ModBiomes.THE_HALLOW_TUNDRA
        );
        List<ResourceKey<Biome>> isJungle = List.of(
                Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE
        );
        MinMaxBounds.Doubles spaceThroughUltra = MinMaxBounds.Doubles.between(OverworldUtils.getSpaceY(), OverworldUtils.getUltraY());
        MinMaxBounds.Doubles caveThroughSpace = MinMaxBounds.Doubles.between(OverworldUtils.getCaveY(), OverworldUtils.getSpaceY());
        MinMaxBounds.Doubles caveThroughSurface = MinMaxBounds.Doubles.between(OverworldUtils.getCaveY(), OverworldUtils.getSurfaceY());
        MinMaxBounds.Doubles surfaceThroughSpace = MinMaxBounds.Doubles.between(OverworldUtils.getSurfaceY(), OverworldUtils.getSpaceY());
        // 六档按 Terraria 1.4.5.8 FishDropRules 归类；跨档鱼分别列出，扩展鱼沿用相对权重。
        // 丰富（兜底）
        output.accept(ModLootTables.FISH_PLENTIFUL, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.COD).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(10))
                        .add(LootItem.lootTableItem(FoodItems.SEA_BASS).setWeight(10))
                        .add(LootItem.lootTableItem(FoodItems.PARTIAL_MOUTH_FISH).when(biomeAt(caveThroughSpace, isDesertOrBadlands)).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.ROCK_LOBSTER).when(biomeAt(caveThroughSpace, isDesertOrBadlands)).setWeight(30))
                        .add(LootItem.lootTableItem(FoodItems.TROUT).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(20))
                        .add(LootItem.lootTableItem(Items.COD).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(5))
                )
        );
        // 普通
        output.accept(ModLootTables.FISH_COMMON, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.SALMON).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.SALMON).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(5))
                        .add(LootItem.lootTableItem(Items.PUFFERFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(2))
                        .add(LootItem.lootTableItem(Items.TROPICAL_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(2))
                        .add(LootItem.lootTableItem(FoodItems.ARMORED_CAVE_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(100))
                        .add(LootItem.lootTableItem(FoodItems.MIRROR_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(300))
                        .add(LootItem.lootTableItem(FoodItems.NEON_GREASE_CARP).when(biomeAt(caveThroughSpace, isJungleOrLush)).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.TUNA).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(20))
                        .add(LootItem.lootTableItem(FoodItems.RED_SNAPPER).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(20))
                        .add(LootItem.lootTableItem(Items.PUFFERFISH).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.ATLANTIC_COD).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(60))
                        .add(LootItem.lootTableItem(ConsumableItems.FROST_DAGGERFISH).apply(SetItemCountFunction.setCount(UniformGenerator.between(7, 64))).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.YELLOW_EEL).when(biomeAt(caveThroughSpace, isSavanna)).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.TILAPIA).when(biomeAt(caveThroughSpace, isSavanna)).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.SCARLET_TIGER_FISH).when(biomeAt(caveThroughSpace, isCrimson)).setWeight(1200))
                        .add(LootItem.lootTableItem(FoodItems.RED_PLEATFISH).when(biomeAt(caveThroughSpace, isMushroom)).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.BROWN_STALKSPINE).when(biomeAt(caveThroughSpace, isMushroom)).setWeight(60))
                )
        );
        // 不常见
        output.accept(ModLootTables.FISH_UNCOMMON, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ARMORED_CAVE_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(1300))
                        .add(LootItem.lootTableItem(FoodItems.MIRROR_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(300))
                        .add(LootItem.lootTableItem(FoodItems.DAMSEL_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(spaceThroughUltra)
                        )).setWeight(2100))
                        .add(LootItem.lootTableItem(FoodItems.PISCES_FIN_COD).when(biomeAt(caveThroughSpace, isJungleOrLush)).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.MOTTLED_OILFISH).when(biomeAt(caveThroughSurface, isJungleOrLush)).setWeight(2100))
                        .add(LootItem.lootTableItem(ConsumableItems.CLAM).when(biomeAt(caveThroughSpace, isDesertOrBadlands)).setWeight(33))
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(20))
                        .add(LootItem.lootTableItem(FoodItems.FROSTY_MINNOW).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(60))
                        .add(LootItem.lootTableItem(ConsumableItems.FROST_DAGGERFISH).apply(SetItemCountFunction.setCount(UniformGenerator.between(7, 64))).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(10))
                        .add(LootItem.lootTableItem(FoodItems.EBONY_KOI).when(biomeAt(caveThroughSpace, isCorruption)).setWeight(2400))
                        .add(LootItem.lootTableItem(FoodItems.BLOODY_PIRANHAS).when(biomeAt(caveThroughSpace, isCrimson)).setWeight(1200))
                        .add(LootItem.lootTableItem(FoodItems.PRINCESS_FISH).when(biomeAt(caveThroughSpace, isHallow)).setWeight(1200))
                        .add(LootItem.lootTableItem(ConsumableItems.BOMB_FISH).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7))).setWeight(33))
                )
        );
        // 稀有
        output.accept(ModLootTables.FISH_RARE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BaitItems.GREEN_JELLYFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(100).when(GamePhaseLootItemCondition.builder().from(GamePhase.WALL_OF_FLESH)))
                        .add(LootItem.lootTableItem(BaitItems.BLUE_JELLYFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(100))
                        .add(LootItem.lootTableItem(FoodItems.STINKY_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(2100))
                        .add(LootItem.lootTableItem(BaitItems.PINK_JELLYFISH).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.COLORFUL_MINERAL_FISH).when(biomeAt(caveThroughSpace, isHallow)).setWeight(1200))
                        .add(LootItem.lootTableItem(AccessoryItems.NATURES_GIFT).when(biomeAt(caveThroughSurface, isJungleOrLush)
                        ).setWeight(25))
                        .add(LootItem.lootTableItem(MaterialItems.JUNGLE_SPORE).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 6))).when(biomeAt(caveThroughSurface, isJungleOrLush)
                        ).setWeight(25))
                        .add(LootItem.lootTableItem(SwordItems.PURPLE_CLUBBERFISH).when(biomeAt(caveThroughSurface, isCorruption)).setWeight(25))
                )
        );
        // 非常稀有
        output.accept(ModLootTables.FISH_VERY_RARE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CHAOS_FISH).when(biomeAt(caveThroughSurface, isHallow)).setWeight(2100))
                        .add(LootItem.lootTableItem(PickaxeItems.REAVER_SHARK_PICKAXE).when(biomeAt(caveThroughSurface, isOceanOrBeach)).setWeight(7))
                        .add(LootItem.lootTableItem(HammerItems.ROCKFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(7))
                        .add(LootItem.lootTableItem(FunctionalBlocks.ALCHEMY_TABLE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setStructure(ModStructures.Keys.DUNGEON).setY(caveThroughSurface)
                        )).setWeight(7))
                )
        );
        // 极其稀有
        output.accept(ModLootTables.FISH_LEGENDARY, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FROG_LEG).setWeight(7))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(4).apply(
                                EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F))
                        ))
                        .add(LootItem.lootTableItem(TCItems.BALLOON_PUFFERFISH).setWeight(7))
                        .add(LootItem.lootTableItem(FoodItems.GOLDEN_CARP).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface))
                        ).setWeight(7))
                        .add(LootItem.lootTableItem(ManaWeaponItems.CRYSTAL_SERPENT).when(biomeAt(caveThroughSurface, isHallow)).setWeight(7).when(GamePhaseLootItemCondition.builder().from(GamePhase.WALL_OF_FLESH)))
                )
        );
        // 钓鱼垃圾池
        output.accept(ModLootTables.JUNK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Blocks.LILY_PAD).setWeight(17))
                        .add(LootItem.lootTableItem(Items.LEATHER_BOOTS).setWeight(10).apply(
                                SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.9F))
                        ))
                        .add(LootItem.lootTableItem(Items.LEATHER).setWeight(10))
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(10))
                        .add(LootItem.lootTableItem(Items.POTION).setWeight(10).apply(SetPotionFunction.setPotion(Potions.WATER)))
                        .add(LootItem.lootTableItem(Items.STRING).setWeight(5))
                        .add(LootItem.lootTableItem(Items.FISHING_ROD).setWeight(2).apply(
                                SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.9F))
                        ))
                        .add(LootItem.lootTableItem(Items.BOWL).setWeight(10))
                        .add(LootItem.lootTableItem(Items.STICK).setWeight(5))
                        .add(LootItem.lootTableItem(Items.INK_SAC).apply(SetItemCountFunction.setCount(ConstantValue.exactly(10.0F))))
                        .add(LootItem.lootTableItem(Blocks.TRIPWIRE_HOOK).setWeight(10))
                        .add(LootItem.lootTableItem(Blocks.SEAGRASS).setWeight(10))
                        .add(LootItem.lootTableItem(FoodItems.JOJA_COLA).setWeight(10))
                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10))
                        .add(LootItem.lootTableItem(Blocks.BAMBOO).when(biomeAt(MinMaxBounds.Doubles.ANY, isJungle)).setWeight(10))
                )
        );
        // 旧入口保留为引用，兼容数据包；普通钓鱼入口不再单独抽宝藏池。
        output.accept(ModLootTables.TREASURE, LootTable.lootTable().withPool(LootPool.lootPool()
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_LEGENDARY))));
        // 渔力直接作为本表的 luck 参数；以下整数参数拟合泰拉 10～900 渔力的六档选择概率。
        // 仅抽一次。兜底档保留正截距，其占比逐渐降低，避免归零后其余档位比例固定。
        output.accept(ModLootTables.FISH, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_PLENTIFUL).setWeight(4500).setQuality(11))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_COMMON).setWeight(0).setQuality(34))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_UNCOMMON).setWeight(0).setQuality(30))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_RARE).setWeight(0).setQuality(16))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_VERY_RARE).setWeight(0).setQuality(10))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH_LEGENDARY).setWeight(0).setQuality(6))));
        // 垃圾单独处理；达到 50 渔力后入口权重归零。
        output.accept(ModLootTables.FISHING, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootTableReference.lootTableReference(ModLootTables.JUNK).setWeight(50).setQuality(-1))
                .add(LootTableReference.lootTableReference(ModLootTables.FISH).setWeight(450))));
        // 匣子
        output.accept(ModLootTables.CRATE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 世界通用匣
                        .add(LootItem.lootTableItem(CrateBlocks.WOODEN_CRATE).setWeight(100))
                        .add(LootItem.lootTableItem(CrateBlocks.IRON_CRATE).setWeight(95).setQuality(1))
                        .add(LootItem.lootTableItem(CrateBlocks.GOLDEN_CRATE).setWeight(19).setQuality(2))
                        // 天空匣
                        .add(LootItem.lootTableItem(CrateBlocks.SKY_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(spaceThroughUltra)
                        )).setWeight(72).setQuality(1))
                        // 丛林
                        .add(LootItem.lootTableItem(CrateBlocks.JUNGLE_CRATE).when(biomeAt(caveThroughSpace, isJungleOrLush)).setWeight(72).setQuality(1))
                        // 沙漠
                        .add(LootItem.lootTableItem(CrateBlocks.OASIS_CRATE).when(biomeAt(caveThroughSpace, isDesertOrBadlands)).setWeight(72).setQuality(1))
                        // 海洋
                        .add(LootItem.lootTableItem(CrateBlocks.OCEAN_CRATE).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(72).setQuality(1))
                        // 雪原
                        .add(LootItem.lootTableItem(CrateBlocks.FROZEN_CRATE).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(72).setQuality(1))
                        // 热带草原
                        .add(LootItem.lootTableItem(CrateBlocks.SAVANNA_CRATE).when(biomeAt(caveThroughSpace, isSavanna)).setWeight(72).setQuality(1))
                        // 腐化之地
                        .add(LootItem.lootTableItem(CrateBlocks.CORRUPT_CRATE).when(biomeAt(caveThroughSpace, isCorruption)).setWeight(72).setQuality(1))
                        // 猩红之地
                        .add(LootItem.lootTableItem(CrateBlocks.CRIMSON_CRATE).when(biomeAt(caveThroughSpace, isCrimson)).setWeight(72).setQuality(1))
                        // 神圣之地
                        .add(LootItem.lootTableItem(CrateBlocks.HALLOWED_CRATE).when(biomeAt(caveThroughSpace, isHallow)).setWeight(72).setQuality(1))
                        // 地牢
                        .add(LootItem.lootTableItem(CrateBlocks.DUNGEON_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setStructure(ModStructures.Keys.DUNGEON).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                )
        );
        // 匣子-困难模式
        output.accept(ModLootTables.CRATE_HARDMODE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 世界通用匣
                        .add(LootItem.lootTableItem(CrateBlocks.PEARLWOOD_CRATE).setWeight(100))
                        .add(LootItem.lootTableItem(CrateBlocks.MYTHRIL_CRATE).setWeight(95).setQuality(1))
                        .add(LootItem.lootTableItem(CrateBlocks.TITANIUM_CRATE).setWeight(19).setQuality(2))
                        // 天空匣
                        .add(LootItem.lootTableItem(CrateBlocks.AZURE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(spaceThroughUltra)
                        )).setWeight(72).setQuality(1))
                        // 丛林
                        .add(LootItem.lootTableItem(CrateBlocks.BRAMBLE_CRATE).when(biomeAt(caveThroughSpace, isJungleOrLush)).setWeight(72).setQuality(1))
                        // 沙漠
                        .add(LootItem.lootTableItem(CrateBlocks.MIRAGE_CRATE).when(biomeAt(caveThroughSpace, isDesertOrBadlands)).setWeight(72).setQuality(1))
                        // 海洋
                        .add(LootItem.lootTableItem(CrateBlocks.SEASIDE_CRATE).when(biomeAt(caveThroughSpace, isOceanOrBeach)).setWeight(72).setQuality(1))
                        // 雪原
                        .add(LootItem.lootTableItem(CrateBlocks.BOREAL_CRATE).when(biomeAt(caveThroughSpace, isSnowyOrIcy)).setWeight(72).setQuality(1))
                        // 热带草原
                        .add(LootItem.lootTableItem(CrateBlocks.WILD_CRATE).when(biomeAt(caveThroughSpace, isSavanna)).setWeight(72).setQuality(1))
                        // 腐化之地
                        .add(LootItem.lootTableItem(CrateBlocks.DEFILED_CRATE).when(biomeAt(caveThroughSpace, isCorruption)).setWeight(72).setQuality(1))
                        // 猩红之地
                        .add(LootItem.lootTableItem(CrateBlocks.HEMATIC_CRATE).when(biomeAt(caveThroughSpace, isCrimson)).setWeight(72).setQuality(1))
                        // 神圣之地
                        .add(LootItem.lootTableItem(CrateBlocks.DIVINE_CRATE).when(biomeAt(caveThroughSpace, isHallow)).setWeight(72).setQuality(1))
                        // 地牢
                        .add(LootItem.lootTableItem(CrateBlocks.STOCKADE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setStructure(ModStructures.Keys.DUNGEON).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                )
        );
        // 岩浆
        output.accept(ModLootTables.FISHING_LAVA, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.OBSIDIFISH).setWeight(630))
                        .add(LootItem.lootTableItem(FoodItems.FLASHFIN_KOI).setWeight(315))
                        .add(LootItem.lootTableItem(ToolItems.DEMON_CONCH).setWeight(3).setQuality(1))
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_LAVA_BUCKET).setWeight(3).setQuality(1))
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_LAVA_BUCKET).setWeight(3).setQuality(1))
                        .add(LootItem.lootTableItem(CrateBlocks.OBSIDIAN_CRATE).setWeight(100)
                                .when(GamePhaseLootItemCondition.builder().to(GamePhase.WALL_OF_FLESH, false)))
                        .add(LootItem.lootTableItem(CrateBlocks.HELLSTONE_CRATE).setWeight(100)
                                .when(GamePhaseLootItemCondition.builder().from(GamePhase.WALL_OF_FLESH)))
                )
        );
        // 蜂蜜
        output.accept(ModLootTables.FISHING_HONEY, LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.HONEYFIN).setWeight(10000)))
        );
    }

    /// 1.20 的位置谓词不支持群系集合，生成等价的原生 OR 条件，并让每个分支包含高度限制。
    private static LootItemCondition.Builder biomeAt(MinMaxBounds.Doubles height, List<ResourceKey<Biome>> biomes) {
        return AnyOfCondition.anyOf(biomes.stream()
                .map(biome -> LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(biome).setY(height)))
                .toArray(LootItemCondition.Builder[]::new));
    }
}
