package org.confluence.mod.common.data.gen.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.common.loot.GamePhaseLootItemCondition;
import org.confluence.mod.common.loot.PercentageLootEntry;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.common.loot.BindSoulSkillFunction;

import java.util.Calendar;
import java.util.function.BiConsumer;

public record GiftSubProvider() implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        // VanillaChestLoot
        DateLootItemCondition.Builder halloweens = DateLootItemCondition.builder().from(Calendar.OCTOBER, 10).to(Calendar.NOVEMBER, 1);
        DateLootItemCondition.Builder christmas = DateLootItemCondition.builder().from(Calendar.DECEMBER, 15).to(Calendar.DECEMBER, 31);
        LootItemConditionalFunction.Builder<?> count2To4 = SetItemCountFunction.setCount(UniformGenerator.between(2, 4));
        LootItemConditionalFunction.Builder<?> count3To6 = SetItemCountFunction.setCount(UniformGenerator.between(3, 6));
        LootItemConditionalFunction.Builder<?> count4To15 = SetItemCountFunction.setCount(UniformGenerator.between(4, 15));
        LootItemConditionalFunction.Builder<?> count1To3 = SetItemCountFunction.setCount(UniformGenerator.between(1, 3));
        LootItemConditionalFunction.Builder<?> count4To7 = SetItemCountFunction.setCount(UniformGenerator.between(4, 7));
        LootItemConditionalFunction.Builder<?> count2To5 = SetItemCountFunction.setCount(UniformGenerator.between(2, 5));
        LootItemConditionalFunction.Builder<?> count25To34 = SetItemCountFunction.setCount(UniformGenerator.between(25, 34));
        LootItemConditionalFunction.Builder<?> count12To21 = SetItemCountFunction.setCount(UniformGenerator.between(12, 21));
        LootItemConditionalFunction.Builder<?> count1To4 = SetItemCountFunction.setCount(UniformGenerator.between(1, 4));
        LootItemConditionalFunction.Builder<?> count5To15 = SetItemCountFunction.setCount(UniformGenerator.between(5, 15));
        LootItemConditionalFunction.Builder<?> count8To11 = SetItemCountFunction.setCount(UniformGenerator.between(8, 11));
        GamePhaseLootItemCondition.Builder afterWallOfFlesh = GamePhaseLootItemCondition.builder().from(GamePhase.WALL_OF_FLESH).to(GamePhase.MOON_LORD);
        GamePhaseLootItemCondition.Builder beforeWallOfFlesh = GamePhaseLootItemCondition.builder().from(GamePhase.BEFORE_SKELETRON).to(GamePhase.WALL_OF_FLESH);


        output.accept(Confluence.asResource("gameplay/crate/obsidian_crate"), environmentLavaCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(19, LootItem.lootTableItem(MountItems.SUPERHEATED_BLOOD))
                        )))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.LAVA_CHARM).setWeight(5))
                        .add(LootItem.lootTableItem(AccessoryItems.LAVAPROOF_FISHING_HOOK).setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ConsumableItems.WET_BOMB).apply(SetItemCountFunction.setCount(UniformGenerator.between(7, 10))))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.OBSIDIAN_LOCK_BOX)))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(MinecartItems.DEMONIC_HELLCART))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(TFBlocks.HANGING_POT).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(FunctionalBlocks.HELLFORGE))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/obsidian_lock_box"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SpearItems.DARK_LANCE))
                        .add(LootItem.lootTableItem(ManaWeaponItems.FLOWER_OF_FIRE))
                        .add(LootItem.lootTableItem(ManaWeaponItems.FLAMELASH))
                        .add(LootItem.lootTableItem(BowItems.HELLWING_BOW))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(TCItems.TREASURE_MAGNET))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/dungeon_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(DecorativeBlocks.BLUE_BRICKS.FULL).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.GOLDEN_LOCK_BOX)))
        );
        output.accept(Confluence.asResource("gameplay/crate/golden_lock_box"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(PercentageLootEntry.exclusive(true,
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(SwordItems.MURAMASA)),
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(TCItems.COBALT_SHIELD)),
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(GunItems.HAND_GUN)),
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(ManaWeaponItems.AQUA_SCEPTER)),
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(ManaWeaponItems.MAGIC_MISSILE)),
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(YoyoItems.VALOR)),
                        PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(TCItems.RAM_RUNE))
                )))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ToolItems.SHADOW_KEY))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/hallowed_crate"), environmentCrateCommon()
        );
        output.accept(Confluence.asResource("gameplay/crate/oasis_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DUNERIDER_BOOTS))
                        .add(LootItem.lootTableItem(TCItems.ANCIENT_CHISEL))
                        .add(LootItem.lootTableItem(FishingPoleItems.SCARAB_FISHING_ROD))
                        .add(LootItem.lootTableItem(ManaWeaponItems.THUNDER_ZAPPER))
                        .add(LootItem.lootTableItem(SpearItems.STORM_SPEAR))
                        .add(LootItem.lootTableItem(ToolItems.MAGIC_CONCH))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 35, LootItem.lootTableItem(TCItems.SANDSTORM_IN_A_BOTTLE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL).apply(count2To5))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ConsumableItems.SCARAB_BOMB).apply(count1To3))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/corrupt_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.BAND_OF_STARPOWER))
                        .add(LootItem.lootTableItem(GunItems.MUSKET))
                        .add(LootItem.lootTableItem(ManaWeaponItems.VILETHRON))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/frozen_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS))
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE))
                        .add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG))
                        .add(LootItem.lootTableItem(GunItems.SNOWBALL_CANNON))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/jungle_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FLOWER_BOOTS).setWeight(5))
                        .add(LootItem.lootTableItem(TCItems.ANKLET_OF_THE_WIND).setWeight(19))
                        .add(LootItem.lootTableItem(TCItems.FERAL_CLAWS).setWeight(19))
                        .add(LootItem.lootTableItem(FishingPoleItems.FIBERGLASS_FISHING_POLE).setWeight(19))
                        .add(LootItem.lootTableItem(ToolItems.STAFF_OF_REGROWTH).setWeight(19))
                        .add(LootItem.lootTableItem(GunItems.BOOMSTICK).setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.BAMBOO).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 50))))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/ocean_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FLIPPER).setWeight(22))
                        .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS).setWeight(10))
                        .add(LootItem.lootTableItem(TCItems.INNER_TUBE).setWeight(22))
                        .add(LootItem.lootTableItem(SwordItems.BREATHING_REED).setWeight(22))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/savanna_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BowItems.HUNTING_BOW))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/sky_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.LUCKY_HORSESHOE))
                        .add(LootItem.lootTableItem(SwordItems.STARFURY))
                        .add(LootItem.lootTableItem(TCItems.SHINY_RED_BALLOON))
                        .add(LootItem.lootTableItem(AccessoryItems.CELESTIAL_MAGNET))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(NatureBlocks.CLOUD_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 40, LootItem.lootTableItem(TCItems.FLEDGLING_WINGS))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/crimson_crate"), environmentCrateCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.PANIC_NECKLACE))
                        .add(LootItem.lootTableItem(GunItems.THE_UNDERTAKER))
                        .add(LootItem.lootTableItem(SpearItems.THE_ROTTED_FORK))
                        .add(LootItem.lootTableItem(ManaWeaponItems.CRIMSON_ROD))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/wooden_crate"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 250 / 9814, LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)),
                                PercentageLootEntry.rule(100.0 * 244 / 9814, LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)),
                                PercentageLootEntry.rule(100.0 * 186 / 9814, LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.AGLET)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.RADAR)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.CLIMBING_CLAWS)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.STEP_STOOL)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 21, LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 90)))),
                                PercentageLootEntry.rule(100.0 / 21, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 28, LootItem.lootTableItem(Items.RAW_COPPER).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 28, LootItem.lootTableItem(Items.RAW_IRON).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 28, LootItem.lootTableItem(MaterialItems.RAW_LEAD).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 28, LootItem.lootTableItem(MaterialItems.RAW_TIN).apply(count3To6))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 3 / 124, LootItem.lootTableItem(MaterialItems.TIN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 * 3 / 124, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 * 3 / 124, LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 * 3 / 124, LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.SHINE_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.GILLS_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION.get()).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION.get()).apply(count1To3))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION).apply(count5To15)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.LESSER_MANA_POTION).apply(count5To15))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 9, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(count1To4)),
                                PercentageLootEntry.rule(100.0 * 2 / 9, LootItem.lootTableItem(BaitItems.APPRENTICE_BAIT).apply(count1To4))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/iron_crate"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 426 / 9814, LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)),
                                PercentageLootEntry.rule(100.0 * 404 / 9814, LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)),
                                PercentageLootEntry.rule(100.0 * 608 / 9814, LootItem.lootTableItem(SwordItems.FALCON_BLADE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 36, LootItem.lootTableItem(Items.RAW_COPPER).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 36, LootItem.lootTableItem(Items.RAW_IRON).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 36, LootItem.lootTableItem(MaterialItems.RAW_LEAD).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 36, LootItem.lootTableItem(MaterialItems.RAW_TIN).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 36, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 / 36, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(count3To6))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(3, LootItem.lootTableItem(MaterialItems.TIN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(3, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(3, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(3, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(3, LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(3, LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION.get()).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.CALMING_POTION.get()).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.FLIPPER_POTION.get()).apply(count2To4))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(count5To15)),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(count5To15))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 6, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(count2To4)),
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(count2To4))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/golden_crate"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 10 / 80, LootItem.lootTableItem(ConsumableItems.LIFE_CRYSTAL))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 4 / 96, LootItem.lootTableItem(Items.RAW_GOLD).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 * 4 / 96, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 * 4 / 96, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(count3To6)),
                                PercentageLootEntry.rule(100.0 * 4 / 96, LootItem.lootTableItem(MaterialItems.RAW_PLATINUM).apply(count3To6))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION.get()).apply(count2To5))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 20)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(SwordItems.ENCHANTED_SWORD))
                        ))
                )
        );

        output.accept(Confluence.asResource("gameplay/crate/hellstone_crate"), environmentLavaCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(19, LootItem.lootTableItem(MountItems.SUPERHEATED_BLOOD))
                        )))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.LAVA_CHARM).setWeight(5))
                        .add(LootItem.lootTableItem(AccessoryItems.LAVAPROOF_FISHING_HOOK).setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ConsumableItems.WET_BOMB).apply(SetItemCountFunction.setCount(UniformGenerator.between(7, 10))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(FunctionalBlocks.HELLFORGE))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.OBSIDIAN_LOCK_BOX)))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(MinecartItems.DEMONIC_HELLCART))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(TFBlocks.HANGING_POT).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/stockade_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(DecorativeBlocks.BLUE_BRICKS.FULL).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.GOLDEN_LOCK_BOX)))
        );
        output.accept(Confluence.asResource("gameplay/crate/divine_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.SOUL_OF_LIGHT).apply(count2To5))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.CRYSTAL_SHARDS).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 10))))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/mirage_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.DUNERIDER_BOOTS))
                        .add(LootItem.lootTableItem(TCItems.ANCIENT_CHISEL))
                        .add(LootItem.lootTableItem(FishingPoleItems.SCARAB_FISHING_ROD))
                        .add(LootItem.lootTableItem(ManaWeaponItems.THUNDER_ZAPPER))
                        .add(LootItem.lootTableItem(SpearItems.STORM_SPEAR))
                        .add(LootItem.lootTableItem(ToolItems.MAGIC_CONCH))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 35, LootItem.lootTableItem(TCItems.SANDSTORM_IN_A_BOTTLE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(MaterialItems.STURDY_FOSSIL).apply(count2To5))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ConsumableItems.SCARAB_BOMB).apply(count1To3))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/defiled_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(AccessoryItems.BAND_OF_STARPOWER))
                        .add(LootItem.lootTableItem(GunItems.MUSKET))
                        .add(LootItem.lootTableItem(ManaWeaponItems.VILETHRON))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.SOUL_OF_NIGHT).apply(count2To5))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/boreal_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS))
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE))
                        .add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG))
                        .add(LootItem.lootTableItem(GunItems.SNOWBALL_CANNON))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/bramble_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FLOWER_BOOTS).setWeight(5))
                        .add(LootItem.lootTableItem(TCItems.ANKLET_OF_THE_WIND).setWeight(19))
                        .add(LootItem.lootTableItem(TCItems.FERAL_CLAWS).setWeight(19))
                        .add(LootItem.lootTableItem(FishingPoleItems.FIBERGLASS_FISHING_POLE).setWeight(19))
                        .add(LootItem.lootTableItem(ToolItems.STAFF_OF_REGROWTH).setWeight(19))
                        .add(LootItem.lootTableItem(GunItems.BOOMSTICK).setWeight(19))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.BAMBOO).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 50))))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/beach_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FLIPPER).setWeight(22))
                        .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS).setWeight(10))
                        .add(LootItem.lootTableItem(TCItems.INNER_TUBE).setWeight(22))
                        .add(LootItem.lootTableItem(SwordItems.BREATHING_REED).setWeight(22))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/wild_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BowItems.HUNTING_BOW))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/azure_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.LUCKY_HORSESHOE))
                        .add(LootItem.lootTableItem(SwordItems.STARFURY))
                        .add(LootItem.lootTableItem(TCItems.SHINY_RED_BALLOON))
                        .add(LootItem.lootTableItem(AccessoryItems.CELESTIAL_MAGNET))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(NatureBlocks.CLOUD_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 40, LootItem.lootTableItem(TCItems.FLEDGLING_WINGS))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/hematic_crate"), environmentCrateHardModeCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.PANIC_NECKLACE))
                        .add(LootItem.lootTableItem(GunItems.THE_UNDERTAKER))
                        .add(LootItem.lootTableItem(SpearItems.THE_ROTTED_FORK))
                        .add(LootItem.lootTableItem(ManaWeaponItems.CRIMSON_ROD))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MaterialItems.SOUL_OF_NIGHT).apply(count2To5))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/pearlwood_crate"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 250 / 9814, LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)),
                                PercentageLootEntry.rule(100.0 * 244 / 9814, LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)),
                                PercentageLootEntry.rule(100.0 * 186 / 9814, LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.AGLET)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.RADAR)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.CLIMBING_CLAWS)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(TCItems.STEP_STOOL)),
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 21, LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 90)))),
                                PercentageLootEntry.rule(100.0 / 21, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 84, LootItem.lootTableItem(Items.RAW_COPPER).apply(count4To15)),
                                PercentageLootEntry.rule(100.0 / 84, LootItem.lootTableItem(Items.RAW_IRON).apply(count4To15)),
                                PercentageLootEntry.rule(100.0 / 84, LootItem.lootTableItem(MaterialItems.RAW_LEAD).apply(count4To15)),
                                PercentageLootEntry.rule(100.0 / 84, LootItem.lootTableItem(MaterialItems.RAW_TIN).apply(count4To15)),
                                PercentageLootEntry.rule(100.0 / 84, LootItem.lootTableItem(MaterialItems.RAW_COBALT).apply(count4To15)),
                                PercentageLootEntry.rule(100.0 / 84, LootItem.lootTableItem(MaterialItems.RAW_PALLADIUM).apply(count4To15))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 3 / 168, LootItem.lootTableItem(MaterialItems.TIN_INGOT).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 * 3 / 168, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 * 3 / 168, LootItem.lootTableItem(MaterialItems.COBALT_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))),
                                PercentageLootEntry.rule(100.0 * 3 / 168, LootItem.lootTableItem(MaterialItems.PALLADIUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))),
                                PercentageLootEntry.rule(100.0 * 3 / 168, LootItem.lootTableItem(Items.IRON_INGOT).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 * 3 / 168, LootItem.lootTableItem(Items.COPPER_INGOT).apply(count2To5))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.SHINE_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.GILLS_POTION).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION.get()).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION.get()).apply(count1To3))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION).apply(count5To15)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.LESSER_MANA_POTION).apply(count5To15))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 9, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(count1To4)),
                                PercentageLootEntry.rule(100.0 * 2 / 9, LootItem.lootTableItem(BaitItems.APPRENTICE_BAIT).apply(count1To4))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/mythril_crate"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 426 / 9814, LootItem.lootTableItem(TCItems.SAILFISH_BOOTS)),
                                PercentageLootEntry.rule(100.0 * 404 / 9814, LootItem.lootTableItem(TCItems.TSUNAMI_IN_A_BOTTLE)),
                                PercentageLootEntry.rule(100.0 * 608 / 9814, LootItem.lootTableItem(SwordItems.FALCON_BLADE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(Items.RAW_COPPER).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(Items.RAW_IRON).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_LEAD).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_TIN).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_COBALT).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_PALLADIUM).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_MYTHRIL).apply(count12To21)),
                                PercentageLootEntry.rule(100.0 / 120, LootItem.lootTableItem(MaterialItems.RAW_ORICHALCUM).apply(count12To21))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 5 / 720, LootItem.lootTableItem(MaterialItems.TIN_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 5 / 720, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 5 / 720, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 5 / 720, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 5 / 720, LootItem.lootTableItem(Items.IRON_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 5 / 720, LootItem.lootTableItem(Items.COPPER_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 10 / 720, LootItem.lootTableItem(MaterialItems.COBALT_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 10 / 720, LootItem.lootTableItem(MaterialItems.PALLADIUM_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 10 / 720, LootItem.lootTableItem(MaterialItems.MYTHRIL_INGOT).apply(count4To7)),
                                PercentageLootEntry.rule(100.0 * 10 / 720, LootItem.lootTableItem(MaterialItems.ORICHALCUM_INGOT).apply(count4To7))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION.get()).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.CALMING_POTION.get()).apply(count2To4)),
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(PotionItems.FLIPPER_POTION.get()).apply(count2To4))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(count5To15)),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(count5To15))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 6, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(count2To4)),
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(count2To4))
                        ))
                )
        );
        output.accept(Confluence.asResource("gameplay/crate/titanium_crate"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 10 / 80, LootItem.lootTableItem(ConsumableItems.LIFE_CRYSTAL))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(Items.RAW_GOLD).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_PLATINUM).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_MYTHRIL).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_ORICHALCUM).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_ADAMANTITE).apply(count25To34)),
                                PercentageLootEntry.rule(100.0 / 80, LootItem.lootTableItem(MaterialItems.RAW_TITANIUM).apply(count25To34))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 4 / 360, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 4 / 360, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 4 / 360, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 4 / 360, LootItem.lootTableItem(Items.GOLD_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 8 / 360, LootItem.lootTableItem(MaterialItems.MYTHRIL_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 8 / 360, LootItem.lootTableItem(MaterialItems.ORICHALCUM_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 8 / 360, LootItem.lootTableItem(MaterialItems.ADAMANTITE_INGOT).apply(count8To11)),
                                PercentageLootEntry.rule(100.0 * 8 / 360, LootItem.lootTableItem(MaterialItems.TITANIUM_INGOT).apply(count8To11))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(count2To5)),
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION.get()).apply(count2To5))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 20)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(SwordItems.ENCHANTED_SWORD))
                        ))
                )
        );

        // 暗影套噬魂怪掉落
        output.accept(Confluence.asResource("gameplay/corruption_carry"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArmorItems.SHADOW_HELMET))
                        .add(LootItem.lootTableItem(ArmorItems.SHADOW_CHESTPLATE))
                        .add(LootItem.lootTableItem(ArmorItems.SHADOW_LEGGINGS))
                        .add(LootItem.lootTableItem(ArmorItems.SHADOW_BOOTS)))
        );

        // 牡蛎
        output.accept(Confluence.asResource("gameplay/clam"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.SHUCKED_OYSTER)))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 30 / 202, LootItem.lootTableItem(MaterialItems.PEARL)),
                                PercentageLootEntry.rule(100.0 * 16 / 202, LootItem.lootTableItem(MaterialItems.BLACK_PEARL)),
                                PercentageLootEntry.rule(100.0 * 5 / 202, LootItem.lootTableItem(MaterialItems.PINK_PEARL))
                        ))
                )
        );
        // 蠕虫罐头
        output.accept(Confluence.asResource("gameplay/can_of_worms"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BaitItems.WORM))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 8)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(30, LootItem.lootTableItem(BaitItems.ENCHANTED_NIGHTCRAWLER)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(BaitItems.WORM))
                        ))
                )
        );
        // 圣诞礼物
        output.accept(Confluence.asResource("gameplay/christmas_gift"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(DecorativeBlocks.GREEN_CANDY_BLOCK).setWeight(1680)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 29)))
                        )
                        .add(LootItem.lootTableItem(DecorativeBlocks.RED_CANDY_BLOCK).setWeight(1680)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 29)))
                        )
                        .add(LootItem.lootTableItem(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET).setWeight(1680)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 29)))
                        )
                        .add(LootItem.lootTableItem(FoodItems.SUGAR_COOKIE).setWeight(360))
                        .add(LootItem.lootTableItem(FoodItems.GINGERBREAD_COOKIE).setWeight(360))
                        .add(LootItem.lootTableItem(FoodItems.CHRISTMAS_PUDDING).setWeight(360))
                        .add(LootItem.lootTableItem(TCItems.HAND_WARMER).setWeight(63))
                        .add(LootItem.lootTableItem(TCItems.TOOLBOX).setWeight(31))
                        .add(LootItem.lootTableItem(SwordItems.CANDY_CANE_SWORD).setWeight(63))
                        .add(LootItem.lootTableItem(Items.COAL).setWeight(333).setQuality(-2))
                        .add(LootItem.lootTableItem(PickaxeItems.CANDY_CANE_PICKAXE).setWeight(63))
                        .add(LootItem.lootTableItem(HookItems.CANDY_CANE_HOOK).setWeight(63))
                )
        );
        // 套餐
        output.accept(Confluence.asResource("gameplay/deluxe_package"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ROASTED_DUCK))
                        .add(LootItem.lootTableItem(Items.COOKED_CHICKEN))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.BURGER)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.FRIES)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.CHICKEN_NUGGET))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 6)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.ICE_CREAM))
                        .add(LootItem.lootTableItem(FoodItems.JOJA_COLA))
                )
        );
        // 草药袋内部池
        output.accept(Confluence.asResource("gameplay/herb_bag_inner"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MaterialItems.WATERLEAF))
                        .add(LootItem.lootTableItem(FoodItems.WATERLEAF_SEED))
                        .add(LootItem.lootTableItem(MaterialItems.FIREBLOSSOM))
                        .add(LootItem.lootTableItem(FoodItems.FIREBLOSSOM_SEED))
                        .add(LootItem.lootTableItem(MaterialItems.MOONGLOW))
                        .add(LootItem.lootTableItem(FoodItems.MOONGLOW_SEED))
                        .add(LootItem.lootTableItem(MaterialItems.BLINKROOT))
                        .add(LootItem.lootTableItem(FoodItems.BLINKROOT_SEED))
                        .add(LootItem.lootTableItem(MaterialItems.SHIVERTHORN))
                        .add(LootItem.lootTableItem(FoodItems.SHIVERTHORN_SEED))
                        .add(LootItem.lootTableItem(MaterialItems.DAYBLOOM))
                        .add(LootItem.lootTableItem(FoodItems.DAYBLOOM_SEED))
                        .add(LootItem.lootTableItem(MaterialItems.DEATHWEED))
                        .add(LootItem.lootTableItem(FoodItems.DEATHWEED_SEED))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))
                )
        );
        // 草药袋内部池
        output.accept(Confluence.asResource("gameplay/herb_bag"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.HERB_BAG_INNER))
                        .apply(count2To5)
                )
        );
        // 红包
        output.accept(Confluence.asResource("gameplay/red_envelope"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.COPPER_COIN).setWeight(888).setQuality(-3))
                        .add(LootItem.lootTableItem(ModItems.SILVER_COIN).setWeight(88).setQuality(1))
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(8).setQuality(2))
                        .add(LootItem.lootTableItem(ModItems.PLATINUM_COIN).setQuality(3))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(8)))
                )
        );
        // 生命木魔棒捆绑
        output.accept(Confluence.asResource("gameplay/living_mahogany_carry"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.LIVING_MAHOGANY_WAND)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.RICH_MAHOGANY_LEAF_WAND)))
        );
        // 砂糖橘
        output.accept(Confluence.asResource("gameplay/sugar_tangerine"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.PEELED_SUGAR_TANGERINE)))
        );
        // 松果
        output.accept(Confluence.asResource("gameplay/pine_cone"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FoodItems.PINE_NUT))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                )
        );
        // 史莱姆嵌套携带
        output.accept(Confluence.asResource("gameplay/slime_carry"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 42 / 628, LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10)))),
                                PercentageLootEntry.rule(100.0 * 42 / 628, LootItem.lootTableItem(ConsumableItems.BOMB).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(MaterialItems.RAW_TIN).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(MaterialItems.RAW_PLATINUM).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(Items.RAW_COPPER).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(Items.RAW_GOLD).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 21 / 628, LootItem.lootTableItem(Items.RAW_IRON).apply(count1To3)),
                                PercentageLootEntry.rule(100.0 * 56 / 628, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 56 / 628, LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 99)))),
                                PercentageLootEntry.rule(100.0 * 56 / 628, LootItem.lootTableItem(ModItems.COPPER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 99)))),
                                PercentageLootEntry.rule(100.0 * 42 / 628, LootItem.lootTableItem(ModBlocks.ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))),
                                PercentageLootEntry.rule(100.0 / 628, AlternativesEntry.alternatives(
                                LootItem.lootTableItem(ModItems.HEART).when(AllOfCondition.allOf(halloweens, christmas).invert()),
                                LootItem.lootTableItem(ModItems.CANDY_APPLE).when(halloweens),
                                LootItem.lootTableItem(ModItems.CANDY_CANE).when(christmas)
                                )),
                                PercentageLootEntry.rule(100.0 * 24 / 628, LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION)),
                                PercentageLootEntry.rule(100.0 * 24 / 628, LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION)),
                                PercentageLootEntry.rule(100.0 * 24 / 628, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION)),
                                PercentageLootEntry.rule(100.0 * 24 / 628, LootItem.lootTableItem(PotionItems.MINING_POTION)),
                                PercentageLootEntry.rule(100.0 * 24 / 628, LootItem.lootTableItem(PotionItems.RECALL_POTION)),
                                PercentageLootEntry.rule(100.0 * 24 / 628, LootItem.lootTableItem(PotionItems.WORMHOLE_POTION))
                        ))
                )
        );
        // 初始任务
        output.accept(ModLootTables.QUESTS_0, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 虫洞药水、传送药水（各1/6概率 = 400/2400 ≈ 16.7%）
                        .add(LootItem.lootTableItem(PotionItems.WORMHOLE_POTION).setWeight(400))
                        .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).setWeight(400))
                        // 宝匣药水、钓鱼药水（各1/3概率 = 800/2400 ≈ 33.3%）
                        .add(LootItem.lootTableItem(PotionItems.CRATE_POTION).setWeight(800).apply(count2To5))
                        .add(LootItem.lootTableItem(PotionItems.FISHING_POTION).setWeight(800).apply(count2To5))
                        // 钓鱼浮标（1/25概率 = 96/2400 = 4%）
                        .add(LootItem.lootTableItem(AccessoryItems.FISHING_BOBBER).setWeight(96))
                        // 六分仪、渔民宝典、天气收音机（各1/30概率 = 80/2400 ≈ 3.3%）
                        .add(LootItem.lootTableItem(TCItems.SEXTANT).setWeight(80))
                        .add(LootItem.lootTableItem(TCItems.FISHERMANS_POCKET_GUIDE).setWeight(80))
                        .add(LootItem.lootTableItem(TCItems.WEATHER_RADIO).setWeight(80))
                        // 钓具箱、优质钓鱼线、渔夫耳环（各1/40概率 = 60/2400 = 2.5%）
                        .add(LootItem.lootTableItem(AccessoryItems.TACKLE_BOX).setWeight(60))
                        .add(LootItem.lootTableItem(AccessoryItems.HIGH_TEST_FISHING_LINE).setWeight(60))
                        .add(LootItem.lootTableItem(TCItems.ANGLER_EARRING).setWeight(60))
                        // 鱼钩、鲤鱼矿车（各1/60概率 = 40/2400 ≈ 1.67%）
                        .add(LootItem.lootTableItem(HookItems.FISH_HOOK).setWeight(40))
                        .add(LootItem.lootTableItem(MinecartItems.MINECARP).setWeight(40))
                        // 金虫网（1/80概率 = 30/2400 = 1.25%）
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_BUG_NET).setWeight(30))
                        // 无底蜂蜜桶、蜂蜜吸收棉（各1/140概率 = 17.14/2400 ≈ 0.71%，取整为17）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_HONEY_BUCKET).setWeight(17))
                        .add(LootItem.lootTableItem(ToolItems.HONEY_ABSORBANT_SPONGE).setWeight(17))
                        // 服装暂时用金币代替（各1/80概率 = 30/2400 = 1.25%）
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(30))
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(30))
                )
        );
        output.accept(ModLootTables.QUESTS_AFTER_10, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.QUESTS_1)).when(beforeWallOfFlesh)
                )
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.QUESTS_2)).when(afterWallOfFlesh)
                )
        );
        output.accept(ModLootTables.QUESTS_AFTER_75, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.QUESTS_3)).when(beforeWallOfFlesh)
                )
                .withPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(ModLootTables.QUESTS_4)).when(afterWallOfFlesh)
                )
        );
        // 渔夫奖励列表其二  肉前，第十个任务后
        output.accept(ModLootTables.QUESTS_1, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 虫洞药水、传送药水（各1/6概率 = 1400/8400 ≈ 16.7%）
                        .add(LootItem.lootTableItem(PotionItems.WORMHOLE_POTION).setWeight(1400))
                        .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).setWeight(1400))
                        // 宝匣药水、钓鱼药水（各1/3概率 = 2800/8400 ≈ 33.3%）
                        .add(LootItem.lootTableItem(PotionItems.CRATE_POTION).setWeight(2800).apply(count2To5))
                        .add(LootItem.lootTableItem(PotionItems.FISHING_POTION).setWeight(2800).apply(count2To5))
                        // 无底水桶、超级吸收棉（各1/70概率 = 120/8400 ≈ 1.43%）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_WATER_BUCKET).setWeight(120))
                        .add(LootItem.lootTableItem(ToolItems.SUPER_ABSORBANT_SPONGE).setWeight(120))
                        // 钓鱼浮标（1/25概率 = 336/8400 = 4%）
                        .add(LootItem.lootTableItem(AccessoryItems.FISHING_BOBBER).setWeight(336))
                        // 六分仪、渔民宝典、天气收音机（各1/30概率 = 280/8400 ≈ 3.33%）
                        .add(LootItem.lootTableItem(TCItems.SEXTANT).setWeight(280))
                        .add(LootItem.lootTableItem(TCItems.FISHERMANS_POCKET_GUIDE).setWeight(280))
                        .add(LootItem.lootTableItem(TCItems.WEATHER_RADIO).setWeight(280))
                        // 钓具箱、优质钓鱼线、渔夫耳环（各1/40概率 = 210/8400 = 2.5%）
                        .add(LootItem.lootTableItem(AccessoryItems.TACKLE_BOX).setWeight(210))
                        .add(LootItem.lootTableItem(AccessoryItems.HIGH_TEST_FISHING_LINE).setWeight(210))
                        .add(LootItem.lootTableItem(TCItems.ANGLER_EARRING).setWeight(210))
                        // 鱼钩、鲤鱼矿车（各1/60概率 = 140/8400 ≈ 1.67%）
                        .add(LootItem.lootTableItem(HookItems.FISH_HOOK).setWeight(140))
                        .add(LootItem.lootTableItem(MinecartItems.MINECARP).setWeight(140))
                        // 金虫网（1/80概率 = 105/8400 = 1.25%）
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_BUG_NET).setWeight(105))
                        // 无底蜂蜜桶、蜂蜜吸收棉（各1/140概率 = 60/8400 ≈ 0.71%）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_HONEY_BUCKET).setWeight(60))
                        .add(LootItem.lootTableItem(ToolItems.HONEY_ABSORBANT_SPONGE).setWeight(60))
                        // 服装暂时用金币代替（各1/80概率 = 105/8400 = 1.25%）
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(105))
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(105))
                )
        );

        // 渔夫奖励列表其三  第十个任务后，且肉后
        output.accept(ModLootTables.QUESTS_2, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 虫洞药水、传送药水（各1/6概率 = 1400/8400 ≈ 16.7%）
                        .add(LootItem.lootTableItem(PotionItems.WORMHOLE_POTION).setWeight(1400))
                        .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).setWeight(1400))
                        // 宝匣药水、钓鱼药水（各1/3概率 = 2800/8400 ≈ 33.3%）
                        .add(LootItem.lootTableItem(PotionItems.CRATE_POTION).setWeight(2800).apply(count2To5))
                        .add(LootItem.lootTableItem(PotionItems.FISHING_POTION).setWeight(2800).apply(count2To5))
                        // 鳍翼、无底水桶、超级吸收棉（各1/70概率 = 120/8400 ≈ 1.43%）
                        .add(LootItem.lootTableItem(TCItems.FIN_WINGS).setWeight(120))
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_WATER_BUCKET).setWeight(120))
                        .add(LootItem.lootTableItem(ToolItems.SUPER_ABSORBANT_SPONGE).setWeight(120))
                        // 无底蜂蜜桶、蜂蜜吸收棉（各1/100概率 = 84/8400 = 1%）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_HONEY_BUCKET).setWeight(84))
                        .add(LootItem.lootTableItem(ToolItems.HONEY_ABSORBANT_SPONGE).setWeight(84))
                        // 钓鱼浮标（1/25概率 = 336/8400 = 4%）
                        .add(LootItem.lootTableItem(AccessoryItems.FISHING_BOBBER).setWeight(336))
                        // 六分仪、渔民宝典、天气收音机（各1/30概率 = 280/8400 ≈ 3.33%）
                        .add(LootItem.lootTableItem(TCItems.SEXTANT).setWeight(280))
                        .add(LootItem.lootTableItem(TCItems.FISHERMANS_POCKET_GUIDE).setWeight(280))
                        .add(LootItem.lootTableItem(TCItems.WEATHER_RADIO).setWeight(280))
                        // 钓具箱、优质钓鱼线、渔夫耳环（各1/40概率 = 210/8400 = 2.5%）
                        .add(LootItem.lootTableItem(AccessoryItems.TACKLE_BOX).setWeight(210))
                        .add(LootItem.lootTableItem(AccessoryItems.HIGH_TEST_FISHING_LINE).setWeight(210))
                        .add(LootItem.lootTableItem(TCItems.ANGLER_EARRING).setWeight(210))
                        // 鱼钩、鲤鱼矿车（各1/60概率 = 140/8400 ≈ 1.67%）
                        .add(LootItem.lootTableItem(HookItems.FISH_HOOK).setWeight(140))
                        .add(LootItem.lootTableItem(MinecartItems.MINECARP).setWeight(140))
                        // 金虫网（1/80概率 = 105/8400 = 1.25%）
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_BUG_NET).setWeight(105))
                        // 服装暂时用金币代替（各1/80概率 = 105/8400 = 1.25%）
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(105))
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(105))
                        // 熔线钓钩（1/100概率 = 84/8400 = 1%）
                        .add(LootItem.lootTableItem(FishingPoleItems.HOTLINE_FISHING_HOOK).setWeight(84))
                )
        );
        // 渔夫奖励列表其四  第七十五个任务后，且肉前
        output.accept(ModLootTables.QUESTS_3, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 虫洞药水、传送药水（各1/6概率 = 7000/42000 ≈ 16.7%）
                        .add(LootItem.lootTableItem(PotionItems.WORMHOLE_POTION).setWeight(7000))
                        .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).setWeight(7000))
                        // 宝匣药水、钓鱼药水（各1/3概率 = 14000/42000 ≈ 33.3%）
                        .add(LootItem.lootTableItem(PotionItems.CRATE_POTION).setWeight(14000).apply(count2To5))
                        .add(LootItem.lootTableItem(PotionItems.FISHING_POTION).setWeight(14000).apply(count2To5))
                        // 无底水桶、超级吸收棉（各1/70概率 = 600/42000 ≈ 1.43%）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_WATER_BUCKET).setWeight(600))
                        .add(LootItem.lootTableItem(ToolItems.SUPER_ABSORBANT_SPONGE).setWeight(600))
                        // 无底蜂蜜桶、蜂蜜吸收棉（各1/140概率 = 300/42000 ≈ 0.71%）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_HONEY_BUCKET).setWeight(300))
                        .add(LootItem.lootTableItem(ToolItems.HONEY_ABSORBANT_SPONGE).setWeight(300))
                        // 钓鱼浮标（1/25概率 = 1680/42000 = 4%）
                        .add(LootItem.lootTableItem(AccessoryItems.FISHING_BOBBER).setWeight(1680))
                        // 六分仪、渔民宝典、天气收音机（各1/30概率 = 1400/42000 ≈ 3.33%）
                        .add(LootItem.lootTableItem(TCItems.SEXTANT).setWeight(1400))
                        .add(LootItem.lootTableItem(TCItems.FISHERMANS_POCKET_GUIDE).setWeight(1400))
                        .add(LootItem.lootTableItem(TCItems.WEATHER_RADIO).setWeight(1400))
                        // 钓具箱、优质钓鱼线、渔夫耳环（各1/40概率 = 1050/42000 = 2.5%）
                        .add(LootItem.lootTableItem(AccessoryItems.TACKLE_BOX).setWeight(1050))
                        .add(LootItem.lootTableItem(AccessoryItems.HIGH_TEST_FISHING_LINE).setWeight(1050))
                        .add(LootItem.lootTableItem(TCItems.ANGLER_EARRING).setWeight(1050))
                        // 鱼钩、鲤鱼矿车（各1/60概率 = 700/42000 ≈ 1.67%）
                        .add(LootItem.lootTableItem(HookItems.FISH_HOOK).setWeight(700))
                        .add(LootItem.lootTableItem(MinecartItems.MINECARP).setWeight(700))
                        // 金虫网（1/80概率 = 525/42000 = 1.25%）
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_BUG_NET).setWeight(525))
                        // 金钓竿（1/250概率 = 168/42000 = 0.4%）
                        .add(LootItem.lootTableItem(FishingPoleItems.GOLDEN_FISHING_ROD).setWeight(168))
                        // 服装暂时用金币代替（各1/80概率 = 525/42000 = 1.25%）
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(525))
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(525))
                )
        );
        // 渔夫奖励列表其五  第七十五个任务后，且肉后
        output.accept(ModLootTables.QUESTS_4, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        // 虫洞药水、传送药水（各1/6概率 = 7000/42000 ≈ 16.7%）
                        .add(LootItem.lootTableItem(PotionItems.WORMHOLE_POTION).setWeight(7000))
                        .add(LootItem.lootTableItem(PotionItems.RECALL_POTION).setWeight(7000))
                        // 宝匣药水、钓鱼药水（各1/3概率 = 14000/42000 ≈ 33.3%）
                        .add(LootItem.lootTableItem(PotionItems.CRATE_POTION).setWeight(14000).apply(count2To5))
                        .add(LootItem.lootTableItem(PotionItems.FISHING_POTION).setWeight(14000).apply(count2To5))
                        // 鳍翼、无底水桶、超级吸收棉（各1/70概率 = 600/42000 ≈ 1.43%）
                        .add(LootItem.lootTableItem(TCItems.FIN_WINGS).setWeight(600))
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_WATER_BUCKET).setWeight(600))
                        .add(LootItem.lootTableItem(ToolItems.SUPER_ABSORBANT_SPONGE).setWeight(600))
                        // 无底蜂蜜桶、蜂蜜吸收棉（各1/100概率 = 420/42000 = 1%）
                        .add(LootItem.lootTableItem(ToolItems.BOTTOMLESS_HONEY_BUCKET).setWeight(420))
                        .add(LootItem.lootTableItem(ToolItems.HONEY_ABSORBANT_SPONGE).setWeight(420))
                        // 钓鱼浮标（1/25概率 = 1680/42000 = 4%）
                        .add(LootItem.lootTableItem(AccessoryItems.FISHING_BOBBER).setWeight(1680))
                        // 六分仪、渔民宝典、天气收音机（各1/30概率 = 1400/42000 ≈ 3.33%）
                        .add(LootItem.lootTableItem(TCItems.SEXTANT).setWeight(1400))
                        .add(LootItem.lootTableItem(TCItems.FISHERMANS_POCKET_GUIDE).setWeight(1400))
                        .add(LootItem.lootTableItem(TCItems.WEATHER_RADIO).setWeight(1400))
                        // 钓具箱、优质钓鱼线、渔夫耳环（各1/40概率 = 1050/42000 = 2.5%）
                        .add(LootItem.lootTableItem(AccessoryItems.TACKLE_BOX).setWeight(1050))
                        .add(LootItem.lootTableItem(AccessoryItems.HIGH_TEST_FISHING_LINE).setWeight(1050))
                        .add(LootItem.lootTableItem(TCItems.ANGLER_EARRING).setWeight(1050))
                        // 鱼钩、鲤鱼矿车（各1/60概率 = 700/42000 ≈ 1.67%）
                        .add(LootItem.lootTableItem(HookItems.FISH_HOOK).setWeight(700))
                        .add(LootItem.lootTableItem(MinecartItems.MINECARP).setWeight(700))
                        // 金虫网（1/80概率 = 525/42000 = 1.25%）
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_BUG_NET).setWeight(525))
                        // 熔线钓钩（1/100概率 = 420/42000 = 1%）
                        .add(LootItem.lootTableItem(FishingPoleItems.HOTLINE_FISHING_HOOK).setWeight(420))
                        // 服装暂时用金币代替（各1/80概率 = 525/42000 = 1.25%）
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(525))
                        .add(LootItem.lootTableItem(ModItems.GOLD_COIN).setWeight(525))
                        // 金钓竿（1/250概率 = 168/42000 = 0.4%）
                        .add(LootItem.lootTableItem(FishingPoleItems.GOLDEN_FISHING_ROD).setWeight(168))
                )
        );
        // 宝藏袋

        //双子魔眼
        output.accept(Confluence.asResource("treasure_bag/the_twins/classic"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_SIGHT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 30)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(12)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/the_twins/expert"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_SIGHT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(42)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.MECHANICAL_WHEEL_PIECE)))
        );
        output.accept(Confluence.asResource("treasure_bag/the_twins/master"), LootTable.lootTable()
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_SIGHT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(42)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.MECHANICAL_WHEEL_PIECE)))
                //.withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.THE_TWINS_RELIC)))
        );
        // 机械骷髅王
        output.accept(Confluence.asResource("treasure_bag/skeletron_prime/classic"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_FRIGHT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 30)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(12)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/skeletron_prime/expert"), LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_FRIGHT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(42)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.MECHANICAL_WAGON_PIECE)))
        );
        output.accept(Confluence.asResource("treasure_bag/skeletron_prime/master"), LootTable.lootTable()
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_FRIGHT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(42)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                        ))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.MECHANICAL_WAGON_PIECE)))
                //.withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.SKELETRON_PRIME_RELIC)))
        );

        // 肉墙
        // 毁灭者
        output.accept(Confluence.asResource("treasure_bag/the_destroyer/classic"), theDestroyerTreasureBag(15, 30, 12, null));
        output.accept(Confluence.asResource("treasure_bag/the_destroyer/expert"), theDestroyerTreasureBag(20, 35, 42, MaterialItems.MECHANICAL_WAGON_PIECE));
        output.accept(Confluence.asResource("treasure_bag/the_destroyer/master"), theDestroyerTreasureBag(20, 35, 42, MaterialItems.MECHANICAL_WAGON_PIECE));

        // 世纪之花
        output.accept(Confluence.asResource("treasure_bag/plantera/classic"), planteraTreasureBag(15));
        output.accept(Confluence.asResource("treasure_bag/plantera/expert"), planteraTreasureBag(45));
        output.accept(Confluence.asResource("treasure_bag/plantera/master"), planteraTreasureBag(45));

        // 拜月教邪教徒；远古操纵机尚未实现时，远古布作为当前进度材料。
        output.accept(Confluence.asResource("treasure_bag/lunatic_cultist/classic"), lunaticCultistTreasureBag(10, 15));
        output.accept(Confluence.asResource("treasure_bag/lunatic_cultist/expert"), lunaticCultistTreasureBag(30, 25));
        output.accept(Confluence.asResource("treasure_bag/lunatic_cultist/master"), lunaticCultistTreasureBag(30, 30));

        output.accept(Confluence.asResource("treasure_bag/wall_of_flesh/classic"), wallOfFleshTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(8)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/wall_of_flesh/expert"), wallOfFleshTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(28)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/wall_of_flesh/master"), wallOfFleshTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(28)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.WALL_OF_FLESH_RELIC)))
        );
        // 肉山
        output.accept(Confluence.asResource("treasure_bag/hill_of_flesh/classic"), wallOfFleshTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(8)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/hill_of_flesh/expert"), wallOfFleshTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(28)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/hill_of_flesh/master"), wallOfFleshTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(28)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.HILL_OF_FLESH_RELIC)))
        );
        // 骷髅王

        output.accept(Confluence.asResource("treasure_bag/skeletron/classic"), LootTable.lootTable()
                .withPool(memoryOfBonebrandLoot())
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(HookItems.SKELETRON_HAND)))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 105 / 1000, LootItem.lootTableItem(ManaWeaponItems.BOOK_OF_SKULLS))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/skeletron/expert"), LootTable.lootTable()
                .withPool(memoryOfBonebrandLoot())
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(HookItems.SKELETRON_HAND)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.BONE_GLOVE)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(17)))
                ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ManaWeaponItems.BOOK_OF_SKULLS))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/skeletron/master"), LootTable.lootTable()
                .withPool(memoryOfBonebrandLoot())
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(HookItems.SKELETRON_HAND)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.BONE_GLOVE)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(17)))
                ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ManaWeaponItems.BOOK_OF_SKULLS))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.SKELETRON_RELIC)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
        );
        // 蜂后

        output.accept(Confluence.asResource("treasure_bag/queen_bee/classic"), queenBeeTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ROYAL_WAX)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(16, 26)))
                ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(75, LootItem.lootTableItem(ConsumableItems.BEENADE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 30))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ModItems.HIVE_WAND))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(MountItems.HONEYED_GOGGLES))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/queen_bee/expert"), queenBeeTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.HIVE_PACK)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ROYAL_WAX)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(17, 29)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.BEENADE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 29)))
                ))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.HIVE_WAND))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(100.0 / 9, LootItem.lootTableItem(MountItems.HONEYED_GOGGLES))))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(22)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/queen_bee/master"), queenBeeTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.HIVE_PACK)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ROYAL_WAX)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(17, 29)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ConsumableItems.BEENADE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 29)))
                ))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.HIVE_WAND))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(100.0 / 9, LootItem.lootTableItem(MountItems.HONEYED_GOGGLES))))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(22)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.QUEEN_BEE_RELIC)))
        );
        // 巨鹿

        output.accept(Confluence.asResource("treasure_bag/deerclops/classic"), deerclopsTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/deerclops/expert"), deerclopsTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(22)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/deerclops/master"), deerclopsTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(22)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.DEERCLOPS_RELIC)))
        );
        //克脑

        output.accept(Confluence.asResource("treasure_bag/brain_of_cthulhu/classic"), HealingPotionCommon().withPool(soulBookLoot(SoulBooks.SOUL_SURGE))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(40, 90)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/brain_of_cthulhu/expert"), HealingPotionCommon().withPool(soulBookLoot(SoulBooks.SOUL_SURGE))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.BRAIN_OF_CONFUSION)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(80, 110)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.TISSUE_SAMPLE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 40)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(13)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/brain_of_cthulhu/master"), HealingPotionCommon().withPool(soulBookLoot(SoulBooks.SOUL_SURGE))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.BRAIN_OF_CONFUSION)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(110, 135)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.TISSUE_SAMPLE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30, 50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(13)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.BRAIN_OF_CTHULHU_RELIC)))
        );

        // 世吞
        output.accept(Confluence.asResource("treasure_bag/eater_of_worlds/classic"), eaterOfWorldsTreasureBagCommon()
        );
        output.accept(Confluence.asResource("treasure_bag/eater_of_worlds/expert"), eaterOfWorldsTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.WORM_SCARF)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
        );
        output.accept(Confluence.asResource("treasure_bag/eater_of_worlds/master"), eaterOfWorldsTreasureBagCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.WORM_SCARF)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.EATER_OF_WORLDS_RELIC)))
        );
        // 史王

        output.accept(Confluence.asResource("treasure_bag/king_slime/classic"), KingSlimeTreasureBagCommon()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(4))
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 12, LootItem.lootTableItem(ArmorItems.NINJA_HELMET)),
                                PercentageLootEntry.rule(100.0 / 12, LootItem.lootTableItem(ArmorItems.NINJA_CHESTPLATE)),
                                PercentageLootEntry.rule(100.0 / 12, LootItem.lootTableItem(ArmorItems.NINJA_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 / 12, LootItem.lootTableItem(ArmorItems.NINJA_BOOTS))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(MountItems.SLIMY_SADDLE))
                        ))
                )
        );
        output.accept(Confluence.asResource("treasure_bag/king_slime/expert"), KingSlimeTreasureBagCommon()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(4))
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_HELMET)),
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_CHESTPLATE)),
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_BOOTS))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.ROYAL_GEL)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                        ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MountItems.SLIMY_SADDLE))
                        ))
                )
        );
        output.accept(Confluence.asResource("treasure_bag/king_slime/master"), KingSlimeTreasureBagCommon()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(4))
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_HELMET)),
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_CHESTPLATE)),
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 * 2 / 12, LootItem.lootTableItem(ArmorItems.NINJA_BOOTS))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.ROYAL_GEL)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(MountItems.SLIMY_SADDLE))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.KING_SLIME_RELIC)))
        );
        // 克眼

        LootPool.Builder binoculars_1$40 = LootPool.lootPool()
                .add(PercentageLootEntry.independent(false,
                        PercentageLootEntry.rule(100.0 / 40, LootItem.lootTableItem(ToolItems.BINOCULARS))
                ));
        LootPool.Builder binoculars_1$30 = LootPool.lootPool()
                .add(PercentageLootEntry.independent(false,
                        PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(ToolItems.BINOCULARS))
                ));
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/classic_corruption"), eyeOfCthulhuTreasureBagCorruption()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(binoculars_1$40)
        );
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/expert_corruption"), eyeOfCthulhuTreasureBagCorruption()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.SHIELD_OF_CTHULHU)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(binoculars_1$30)
        );
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/master_corruption"), eyeOfCthulhuTreasureBagCorruption()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.SHIELD_OF_CTHULHU)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(binoculars_1$30)
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.EYE_OF_CTHULHU_RELIC)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.AVIATORS.get())))
        );


        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/classic_crimson"), eyeOfCthulhuTreasureBagCrimson()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(binoculars_1$40)
        );
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/expert_crimson"), eyeOfCthulhuTreasureBagCrimson()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.SHIELD_OF_CTHULHU)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(binoculars_1$30)
        );
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/master_crimson"), eyeOfCthulhuTreasureBagCrimson()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.SHIELD_OF_CTHULHU)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(binoculars_1$30)
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.EYE_OF_CTHULHU_RELIC)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.AVIATORS.get())))
        );


        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/classic_double_evil"), eyeOfCthulhuTreasureBagDoubleEvil()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                ))
                .withPool(binoculars_1$40)
        );
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/expert_double_evil"), eyeOfCthulhuTreasureBagDoubleEvil()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.SHIELD_OF_CTHULHU)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(binoculars_1$30)
        );
        output.accept(Confluence.asResource("treasure_bag/eye_of_cthulhu/master_double_evil"), eyeOfCthulhuTreasureBagDoubleEvil()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(TCItems.SHIELD_OF_CTHULHU)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(binoculars_1$30)
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(DecorativeBlocks.EYE_OF_CTHULHU_RELIC)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.AVIATORS.get())))
        );


    }

    private static LootTable.Builder eyeOfCthulhuTreasureBagCorruption() {
        return HealingPotionCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ArrowItems.UNHOLY_ARROW)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_DEMONITE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30, 90)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.CORRUPT_SEED)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                ));
    }

    private static LootTable.Builder eyeOfCthulhuTreasureBagCrimson() {
        return HealingPotionCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ArrowItems.UNHOLY_ARROW)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30, 90)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.CRIMSON_SEED)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                ));
    }

    private static LootTable.Builder eyeOfCthulhuTreasureBagDoubleEvil() {
        return HealingPotionCommon()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.SILVER_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ArrowItems.UNHOLY_ARROW)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 50)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_CRIMTANE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30, 90)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.CRIMSON_SEED)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_DEMONITE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30, 90)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.CORRUPT_SEED)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                ));
    }

    private static LootTable.Builder wallOfFleshTreasureBagCommon() {
        // tip 恶魔之心已通过代码给予
        return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(HammerItems.PWNHAMMER)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.WARRIOR_EMBLEM))
                        .add(LootItem.lootTableItem(TCItems.RANGER_EMBLEM))
                        .add(LootItem.lootTableItem(TCItems.SORCERER_EMBLEM))
                        .add(LootItem.lootTableItem(AccessoryItems.SUMMONER_EMBLEM))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(SwordItems.BREAKER_BLADE))
                                .add(LootItem.lootTableItem(WhipItems.FIRECRACKER))
                        // todo 另外三个
                )
                .withPool(soulBookLoot(SoulBooks.BLOOD_WORLD_WRATH));
    }

    // 克脑的也只有药
    private static LootTable.Builder HealingPotionCommon() {
        return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
        ));
    }

    private static LootTable.Builder KingSlimeTreasureBagCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(HookItems.SLIME_HOOK)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(FunctionalBlocks.SOLIDIFIER)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false, PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(SummonItems.SLIME_STAFF))))
                );
    }

    private static LootTable.Builder queenBeeTreasureBagCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BEE_KEEPER))
                        .add(LootItem.lootTableItem(BowItems.THE_BEES_KNEES))
                        .add(LootItem.lootTableItem(ManaWeaponItems.BEE_GUN))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(TCItems.HONEY_COMB))
                        ))
                )
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.HONEY_BOTTLE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ));
    }

    private static LootTable.Builder deerclopsTreasureBagCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(AccessoryItems.RADIO_THING))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PetItems.CHESTER_STAFF))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ManaWeaponItems.WEATHER_PAIN)),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(AxeItems.LUCY_THE_AXE)),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(SummonItems.EYE_LASER_TURRET_STAFF))
                        ))
                );
    }


    private static LootTable.Builder eaterOfWorldsTreasureBagCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.RAW_DEMONITE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30, 59)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SHADOW_SCALE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 19)))
                ))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))
                ))
                .withPool(soulBookLoot(SoulBooks.SOUL_SURGE));
    }

    // 毁灭者宝藏袋
    private static LootTable.Builder theDestroyerTreasureBag(int hallowedMin, int hallowedMax, int goldCoins, ItemLike difficultyBonus) {
        LootTable.Builder table = LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.SOUL_OF_MIGHT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 40)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.HALLOWED_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(hallowedMin, hallowedMax)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(goldCoins)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))));
        if (difficultyBonus != null) {
            table.withPool(LootPool.lootPool().add(LootItem.lootTableItem(difficultyBonus)));
        }
        return table;
    }

    private static LootTable.Builder planteraTreasureBag(int goldCoins) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ToolItems.TEMPLE_KEY)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(goldCoins)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))))
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(2, LootItem.lootTableItem(HamaxeItems.THE_AXE))
                        )));
    }

    private static LootTable.Builder lunaticCultistTreasureBag(int goldCoins, int ancientCloth) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MaterialItems.ANCIENT_CLOTH)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(ancientCloth)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.GOLD_COIN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(goldCoins)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(PotionItems.GREATER_HEALING_POTION)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 15)))));
    }

    private static LootTable.Builder environmentCrateCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 12))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(Items.RAW_GOLD).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))),
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(Items.RAW_COPPER).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))),
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(Items.RAW_IRON).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))),
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(MaterialItems.RAW_LEAD).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))),
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(MaterialItems.RAW_PLATINUM).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))),
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))),
                                PercentageLootEntry.rule(100.0 / 49, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(MaterialItems.TIN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4)))),
                                PercentageLootEntry.rule(100.0 / 24, LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.MINING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                        ))
                );
    }

    // 困难模式前岩浆匣子通用
    private static LootTable.Builder environmentLavaCrateCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 12))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(MaterialItems.RAW_HELLSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.HELLSTONE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.MINING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                        ))
                );
    }

    // 困难模式岩浆匣子通用
    private static LootTable.Builder environmentLavaCrateHardModeCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 12))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 7, LootItem.lootTableItem(MaterialItems.RAW_HELLSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.HELLSTONE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.MINING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                        ))
                );
    }

    /// 骷髅王宝藏袋通用掉落：刻骨的记忆，独立 66% 池。
    private static LootPool.Builder memoryOfBonebrandLoot() {
        return LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.66F))
                .add(LootItem.lootTableItem(AccessoryItems.MEMORY_OF_BONEBRAND));
    }

    /// 100% 抽取池：必定掉落一本绑定指定书籍的通用技能书。
    private static LootPool.Builder soulBookLoot(ResourceLocation bookId) {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(SoulItems.SOUL_SKILL_BOOK)
                        .apply(BindSoulSkillFunction.bind(bookId)));
    }

    // 困难模式匣子通用
    private static LootTable.Builder environmentCrateHardModeCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 12))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(Items.RAW_GOLD).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(Items.RAW_COPPER).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(Items.RAW_IRON).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_LEAD).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_PLATINUM).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_SILVER).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_TUNGSTEN).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_COBALT).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_PALLADIUM).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_MYTHRIL).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_ORICHALCUM).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_ADAMANTITE).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(100.0 / 96, LootItem.lootTableItem(MaterialItems.RAW_TITANIUM).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 108, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 16)))),
                                PercentageLootEntry.rule(100.0 / 108, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 16)))),
                                PercentageLootEntry.rule(100.0 / 108, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 16)))),
                                PercentageLootEntry.rule(100.0 / 108, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 16)))),
                                PercentageLootEntry.rule(100.0 / 108, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 16)))),
                                PercentageLootEntry.rule(100.0 / 108, LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 16)))),
                                PercentageLootEntry.rule(100.0 * 2 / 108, LootItem.lootTableItem(MaterialItems.COBALT_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 16)))),
                                PercentageLootEntry.rule(100.0 * 2 / 108, LootItem.lootTableItem(MaterialItems.PALLADIUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 16)))),
                                PercentageLootEntry.rule(100.0 * 2 / 108, LootItem.lootTableItem(MaterialItems.MYTHRIL_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 16)))),
                                PercentageLootEntry.rule(100.0 * 2 / 108, LootItem.lootTableItem(MaterialItems.ORICHALCUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 16)))),
                                PercentageLootEntry.rule(100.0 * 2 / 108, LootItem.lootTableItem(MaterialItems.ADAMANTITE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 16)))),
                                PercentageLootEntry.rule(100.0 * 2 / 108, LootItem.lootTableItem(MaterialItems.TITANIUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 16))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.MINING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))),
                                PercentageLootEntry.rule(100.0 / 22, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(PotionItems.MANA_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 17))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.JOURNEYMAN_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(BaitItems.MASTER_BAIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                        ))
                );
    }
}
