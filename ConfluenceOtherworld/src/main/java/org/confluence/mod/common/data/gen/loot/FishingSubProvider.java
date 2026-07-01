package org.confluence.mod.common.data.gen.loot;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.CrateBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.List;
import java.util.function.BiConsumer;

public record FishingSubProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        HolderLookup.RegistryLookup<Biome> registrylookup = this.registries.lookupOrThrow(Registries.BIOME);
        HolderLookup.RegistryLookup<Structure> registrystrcturelookup = this.registries.lookupOrThrow(Registries.STRUCTURE);

        HolderSet<Biome> isSavana = registrylookup.getOrThrow(PortTags.Biomes.IS_SAVANNA);
        HolderSet<Biome> isJungle = registrylookup.getOrThrow(PortTags.Biomes.IS_JUNGLE);
        HolderSet<Biome> isCorruption = registrylookup.getOrThrow(ModTags.Biomes.THE_CORRUPTION);
        HolderSet<Biome> isCrimson = registrylookup.getOrThrow(ModTags.Biomes.THE_CRIMSON);
        HolderSet<Biome> isHallow = registrylookup.getOrThrow(ModTags.Biomes.THE_HALLOW);
        HolderSet<Biome> isMushroom = registrylookup.getOrThrow(PortTags.Biomes.IS_MUSHROOM);
        HolderSet<Biome> isSnowyOrIcy = new OrHolderSet<>(List.of(
                registrylookup.getOrThrow(PortTags.Biomes.IS_SNOWY),
                registrylookup.getOrThrow(PortTags.Biomes.IS_ICY)
        ));
        HolderSet<Biome> isOceanOrBeach = new OrHolderSet<>(List.of(
                registrylookup.getOrThrow(PortTags.Biomes.IS_OCEAN),
                registrylookup.getOrThrow(PortTags.Biomes.IS_BEACH)
        ));
        HolderSet<Biome> isJungleOrLush = new OrHolderSet<>(List.of(
                isJungle,
                registrylookup.getOrThrow(PortTags.Biomes.IS_LUSH)
        ));
        HolderSet<Biome> isDesertOrBadlands = new OrHolderSet<>(List.of(
                registrylookup.getOrThrow(PortTags.Biomes.IS_DESERT),
                registrylookup.getOrThrow(PortTags.Biomes.IS_BADLANDS)
        ));
        MinMaxBounds.Doubles spaceThroughUltra = MinMaxBounds.Doubles.between(OverworldUtils.getSpaceY(), OverworldUtils.getUltraY());
        MinMaxBounds.Doubles caveThroughSpace = MinMaxBounds.Doubles.between(OverworldUtils.getCaveY(), OverworldUtils.getSpaceY());
        MinMaxBounds.Doubles caveThroughSurface = MinMaxBounds.Doubles.between(OverworldUtils.getCaveY(), OverworldUtils.getSurfaceY());
        MinMaxBounds.Doubles surfaceThroughSpace = MinMaxBounds.Doubles.between(OverworldUtils.getSurfaceY(), OverworldUtils.getSpaceY());
        HolderSet<Structure> isDungeon = HolderSet.direct(registrystrcturelookup.getOrThrow(ModStructures.Keys.DUNGEON));
        // 基础鱼
        output.accept(ModLootTables.FISH, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 地表通用鱼
                        .add(LootItem.lootTableItem(Items.COD).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(10))
                        .add(LootItem.lootTableItem(Items.SALMON).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.SALMON).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.SEA_BASS).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(10))
                        .add(LootItem.lootTableItem(Items.PUFFERFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(2))
                        .add(LootItem.lootTableItem(Items.TROPICAL_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(surfaceThroughSpace)
                        )).setWeight(2))
                        // 地下鱼
                        .add(LootItem.lootTableItem(BaitItems.GREEN_JELLYFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(100))
                        .add(LootItem.lootTableItem(BaitItems.BLUE_JELLYFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(100))
                        .add(LootItem.lootTableItem(FoodItems.ARMORED_CAVE_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(2100))
                        .add(LootItem.lootTableItem(FoodItems.STINKY_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(2100))
                        .add(LootItem.lootTableItem(FoodItems.MIRROR_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(2100))
                        // 空岛的鱼
                        .add(LootItem.lootTableItem(FoodItems.DAMSEL_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(spaceThroughUltra)
                        )).setWeight(2100))
                        // 丛林鱼
                        .add(LootItem.lootTableItem(FoodItems.PISCES_FIN_COD).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.NEON_GREASE_CARP).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSpace)
                        )).setWeight(60))
                        // 地下丛林鱼
                        .add(LootItem.lootTableItem(FoodItems.MOTTLED_OILFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSurface)
                        )).setWeight(2100))
                        // 沙漠鱼
                        .add(LootItem.lootTableItem(FoodItems.PARTIAL_MOUTH_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isDesertOrBadlands).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.ROCK_LOBSTER).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isDesertOrBadlands).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(ConsumableItems.CLAM).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isDesertOrBadlands).setY(caveThroughSpace)
                        )).setWeight(33))
                        // 海洋
                        .add(LootItem.lootTableItem(BaitItems.PINK_JELLYFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(5))
                        .add(LootItem.lootTableItem(FoodItems.TUNA).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(20))
                        .add(LootItem.lootTableItem(FoodItems.TROUT).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(20))
                        .add(LootItem.lootTableItem(FoodItems.SHRIMP).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(20))
                        .add(LootItem.lootTableItem(FoodItems.RED_SNAPPER).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(20))
                        .add(LootItem.lootTableItem(Items.PUFFERFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(5))
                        // 雪原
                        .add(LootItem.lootTableItem(FoodItems.ATLANTIC_COD).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSnowyOrIcy).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.FROSTY_MINNOW).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSnowyOrIcy).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(Items.COD).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSnowyOrIcy).setY(caveThroughSpace)
                        )).setWeight(5))
                        .add(LootItem.lootTableItem(ConsumableItems.FROST_DAGGERFISH).apply(SetItemCountFunction.setCount(UniformGenerator.between(7, 64))).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSnowyOrIcy).setY(caveThroughSpace)
                        )).setWeight(33))
                        // 热带草原
                        .add(LootItem.lootTableItem(FoodItems.YELLOW_EEL).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSavana).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.TILAPIA).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSavana).setY(caveThroughSpace)
                        )).setWeight(60))
                        // 腐化之地
                        .add(LootItem.lootTableItem(FoodItems.EBONY_KOI).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCorruption).setY(caveThroughSpace)
                        )).setWeight(2400))
                        // 猩红之地
                        .add(LootItem.lootTableItem(FoodItems.BLOODY_PIRANHAS).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCrimson).setY(caveThroughSpace)
                        )).setWeight(1200))
                        .add(LootItem.lootTableItem(FoodItems.SCARLET_TIGER_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCrimson).setY(caveThroughSpace)
                        )).setWeight(1200))
                        // 神圣之地
                        .add(LootItem.lootTableItem(FoodItems.COLORFUL_MINERAL_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isHallow).setY(caveThroughSpace)
                        )).setWeight(1200))
                        .add(LootItem.lootTableItem(FoodItems.PRINCESS_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isHallow).setY(caveThroughSpace)
                        )).setWeight(1200))
                        // 神圣之地地下
                        .add(LootItem.lootTableItem(FoodItems.CHAOS_FISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isHallow).setY(caveThroughSurface)
                        )).setWeight(2100))
                        // 蘑菇岛
                        .add(LootItem.lootTableItem(FoodItems.RED_PLEATFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isMushroom).setY(caveThroughSpace)
                        )).setWeight(60))
                        .add(LootItem.lootTableItem(FoodItems.BROWN_STALKSPINE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isMushroom).setY(caveThroughSpace)
                        )).setWeight(60))
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
                        .add(LootItem.lootTableItem(Blocks.BAMBOO).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungle)
                        )).setWeight(10))
                )
        );
        // 宝藏池
        output.accept(ModLootTables.TREASURE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FROG_LEG).setWeight(7))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(4).apply(
                                EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F))
                        ))
                        .add(LootItem.lootTableItem(TCItems.BALLOON_PUFFERFISH).setWeight(7))
                        .add(LootItem.lootTableItem(ConsumableItems.BOMB_FISH).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7))).setWeight(33))
                        .add(LootItem.lootTableItem(FoodItems.GOLDEN_CARP).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface))
                        ).setWeight(7))
                        .add(LootItem.lootTableItem(AccessoryItems.NATURES_GIFT).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSurface))
                        ).setWeight(25))
                        .add(LootItem.lootTableItem(MaterialItems.JUNGLE_SPORE).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 6))).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSurface))
                        ).setWeight(25))
                        .add(LootItem.lootTableItem(SwordItems.PURPLE_CLUBBERFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCorruption).setY(caveThroughSurface)
                        )).setWeight(25))
                        .add(LootItem.lootTableItem(PickaxeItems.REAVER_SHARK_PICKAXE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSurface)
                        )).setWeight(7))
                        .add(LootItem.lootTableItem(HammerItems.ROCKFISH).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(caveThroughSurface)
                        )).setWeight(7))
                        .add(LootItem.lootTableItem(FunctionalBlocks.ALCHEMY_TABLE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setStructures(isDungeon).setY(caveThroughSurface)
                        )).setWeight(7))
                        .add(LootItem.lootTableItem(ManaWeaponItems.CRYSTAL_SERPENT).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isHallow).setY(caveThroughSurface)
                        )).setWeight(7))
                )
        );
        // 钓鱼总战利品表
        output.accept(ModLootTables.FISHING, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(DynamicLoot.dynamicEntry(ModLootTables.JUNK).setWeight(5).setQuality(-1))
                        .add(DynamicLoot.dynamicEntry(ModLootTables.TREASURE)
                                .setWeight(10)
                                .setQuality(1)
                                .when(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(FishingHookPredicate.inOpenWater(true))
                                ))
                        )
                        .add(DynamicLoot.dynamicEntry(ModLootTables.FISH).setWeight(85).setQuality(-1))
                )
        );
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
                        .add(LootItem.lootTableItem(CrateBlocks.JUNGLE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 沙漠
                        .add(LootItem.lootTableItem(CrateBlocks.OASIS_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isDesertOrBadlands).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 海洋
                        .add(LootItem.lootTableItem(CrateBlocks.OCEAN_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 雪原
                        .add(LootItem.lootTableItem(CrateBlocks.FROZEN_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSnowyOrIcy).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 热带草原
                        .add(LootItem.lootTableItem(CrateBlocks.SAVANNA_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSavana).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 腐化之地
                        .add(LootItem.lootTableItem(CrateBlocks.CORRUPT_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCorruption).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 猩红之地
                        .add(LootItem.lootTableItem(CrateBlocks.CRIMSON_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCrimson).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 神圣之地
                        .add(LootItem.lootTableItem(CrateBlocks.HALLOWED_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isHallow).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 地牢
                        .add(LootItem.lootTableItem(CrateBlocks.DUNGEON_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setStructures(isDungeon).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                )
        );
        // 匣子-困难模式
        output.accept(ModLootTables.CRATE_HARDMODE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 世界通用匣
                        .add(LootItem.lootTableItem(CrateBlocks.PEARLWOOD_CRATE).setWeight(100))
                        .add(LootItem.lootTableItem(CrateBlocks.MYTHRIL_CRATE).setWeight(95).setQuality(1))
                        .add(LootItem.lootTableItem(CrateBlocks.TITANIUM_CRATE).setWeight(12).setQuality(2))
                        // 天空匣
                        .add(LootItem.lootTableItem(CrateBlocks.AZURE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setY(spaceThroughUltra)
                        )).setWeight(72).setQuality(1))
                        // 丛林
                        .add(LootItem.lootTableItem(CrateBlocks.BRAMBLE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isJungleOrLush).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 沙漠
                        .add(LootItem.lootTableItem(CrateBlocks.MIRAGE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isDesertOrBadlands).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 海洋
                        .add(LootItem.lootTableItem(CrateBlocks.SEASIDE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isOceanOrBeach).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 雪原
                        .add(LootItem.lootTableItem(CrateBlocks.BOREAL_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSnowyOrIcy).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 热带草原
                        .add(LootItem.lootTableItem(CrateBlocks.WILD_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isSavana).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 腐化之地
                        .add(LootItem.lootTableItem(CrateBlocks.DEFILED_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCorruption).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 猩红之地
                        .add(LootItem.lootTableItem(CrateBlocks.HEMATIC_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isCrimson).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 神圣之地
                        .add(LootItem.lootTableItem(CrateBlocks.DIVINE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setBiomes(isHallow).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                        // 地牢
                        .add(LootItem.lootTableItem(CrateBlocks.STOCKADE_CRATE).when(LocationCheck.checkLocation(
                                LocationPredicate.Builder.location().setStructures(isDungeon).setY(caveThroughSpace)
                        )).setWeight(72).setQuality(1))
                )
        );
        // 岩浆
        output.accept(ModLootTables.FISHING_LAVA, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.OBSIDIFISH).setWeight(630))
                        .add(LootItem.lootTableItem(FoodItems.FLASHFIN_KOI).setWeight(630))
                        .add(LootItem.lootTableItem(ToolItems.DEMON_CONCH).setWeight(3).setQuality(1))
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_LAVA_BUCKET).setWeight(3).setQuality(1))
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_LAVA_BUCKET).setWeight(3).setQuality(1))
                        .add(LootItem.lootTableItem(CrateBlocks.OBSIDIAN_CRATE).setWeight(100))
                )
        );
        // 蜂蜜
        output.accept(ModLootTables.FISHING_HONEY, LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.HONEYFIN).setWeight(10000)))
        );
    }
}
