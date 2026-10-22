package org.confluence.mod.common.data.gen.loot;

import net.minecraft.ChatFormatting;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.common.loot.BindSoulSkillFunction;
import org.confluence.mod.common.loot.PercentageLootEntry;
import org.confluence.terra_curio.common.init.TCItems;

import java.util.List;
import java.util.function.BiConsumer;

public final class ChestSubProvider implements LootTableSubProvider {
    private static final List<net.minecraft.world.item.enchantment.Enchantment> MANA_ENCHANTMENTS = List.of(
            ModEnchantments.MANA_REGENERATION.get(),
            ModEnchantments.EFFICIENT_MAGIC.get(),
            ModEnchantments.MANA_MENDING.get(),
            ModEnchantments.CELESTIAL_ABSORPTION.get(),
            ModEnchantments.SOOTHED_MANA.get(),
            ModEnchantments.ARCANE_PROTECTION.get(),
            ModEnchantments.SPELL_DESPERATION.get(),
            ModEnchantments.MYSTIC_SURGE.get()
    );

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        output.accept(Confluence.asResource("chests/mysterious_note"), mysteriousTextLoot(ModItems.MYSTERIOUS_NOTE.get(), "mysterious_note", 5, 100.0 / 7));
        output.accept(Confluence.asResource("chests/mysterious_slate"), mysteriousTextLoot(ModItems.MYSTERIOUS_SLATE.get(), "mysterious_slate", 2, 6.25));
        // VanillaChestLoot
        LootPoolSingletonContainer.Builder<?> manaEnchantedBookBuilder = LootItem.lootTableItem(Items.BOOK)
                .apply(manaEnchantment());

        output.accept(Confluence.asResource("chests/surface_chests"), initialWorldSurfaceCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.UMBRELLA))
                        .add(LootItem.lootTableItem(SpearItems.SPEAR))
                        .add(LootItem.lootTableItem(BoomerangItems.WOOD_BOOMERANG))
                        .add(LootItem.lootTableItem(GunItems.BLOWGUN))
                        .add(LootItem.lootTableItem(TCItems.AGLET))
                        .add(LootItem.lootTableItem(TCItems.CLIMBING_CLAWS))
                        .add(LootItem.lootTableItem(ManaWeaponItems.WAND_OF_SPARKING))
                        .add(LootItem.lootTableItem(TCItems.RADAR))
                        .add(LootItem.lootTableItem(TCItems.STEP_STOOL))
                        .add(LootItem.lootTableItem(AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE))
                )
        );

        output.accept(Confluence.asResource("chests/living_wood_chests"), initialWorldSurfaceCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.UMBRELLA))
                        .add(LootItem.lootTableItem(BoomerangItems.WOOD_BOOMERANG))
                        .add(LootItem.lootTableItem(GunItems.BLOWGUN))
                        .add(LootItem.lootTableItem(TCItems.AGLET))
                        .add(LootItem.lootTableItem(TCItems.CLIMBING_CLAWS))
                        .add(LootItem.lootTableItem(ManaWeaponItems.WAND_OF_SPARKING))
                        .add(LootItem.lootTableItem(TCItems.RADAR))
                        .add(LootItem.lootTableItem(TCItems.STEP_STOOL))
                        .add(LootItem.lootTableItem(AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 2 / 3, LootItem.lootTableItem(ModItems.LIVING_WOOD_WAND))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(SummonItems.FINCH_STAFF))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 6, LootItem.lootTableItem(ModItems.LEAF_WAND)),
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(MinecartItems.SUNFLOWER_MINECART)),
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(MinecartItems.LADYBUG_MINECART))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/water_chests"), initialWorldUndergroundCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.BREATHING_REED).setWeight(3))
                        .add(LootItem.lootTableItem(TCItems.FLIPPER).setWeight(3))
                        .add(LootItem.lootTableItem(TCItems.INNER_TUBE).setWeight(3))
                        .add(LootItem.lootTableItem(TCItems.WATER_WALKING_BOOTS).setWeight(3))
                        .add(LootItem.lootTableItem(Items.TRIDENT).setWeight(3))
                )
        );


        output.accept(Confluence.asResource("chests/underground_chests"), initialWorldUndergroundCommon()
                .withPool(undergroundCabinsGoldenChestPool())
        );

        // 发光蘑菇地下小屋的金箱：底子与普通地下小屋的金箱完全一致（共用 undergroundCabinsGoldenChestPool），
        // 额外追加「蘑菇矿车 / 蘑菇套装 二选一」池子。
        // 额外追加「蘑菇矿车 / 蘑菇套装 二选一」和「100% 松露灯」两个池子。
        output.accept(Confluence.asResource("chests/glowing_mushroom_chests"), initialWorldUndergroundCommon()
                .withPool(undergroundCabinsGoldenChestPool())
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MinecartItems.SHROOM_MINECART))
                        .add(LootTableReference.lootTableReference(ModLootTables.MUSHROOM_SET))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SoulItems.TRUFFLE_LANTERN))
                )
        );

        // 蘑菇套装（Mushroom set）四件套：整包发放的嵌套子表，被发光蘑菇地下小屋金箱引用。
        // TODO 该套装为新增注册的时装（见 VanityArmorItems），已加入 wip 物品标签，模型与贴图待补。
        output.accept(ModLootTables.MUSHROOM_SET, LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.MUSHROOM_HELMET)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.MUSHROOM_CHESTPLATE)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.MUSHROOM_LEGGINGS)))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(VanityArmorItems.MUSHROOM_BOOTS)))
        );

        output.accept(Confluence.asResource("chests/frozen_chests"), initialWorldUndergroundCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE).setWeight(15))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS).setWeight(15))
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES).setWeight(15))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE).setWeight(15))
                        .add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG).setWeight(15))
                        .add(LootItem.lootTableItem(GunItems.SNOWBALL_CANNON).setWeight(15))
                        .add(LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR).setWeight(5))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ToolItems.ICE_MIRROR))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/cave_frozen_chests"), initialWorldIceCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BLIZZARD_IN_A_BOTTLE).setWeight(15))
                        .add(LootItem.lootTableItem(TCItems.FLURRY_BOOTS).setWeight(15))
                        .add(LootItem.lootTableItem(TCItems.ICE_SKATES).setWeight(15))
                        .add(LootItem.lootTableItem(SwordItems.ICE_BLADE).setWeight(15))
                        .add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG).setWeight(15))
                        .add(LootItem.lootTableItem(GunItems.SNOWBALL_CANNON).setWeight(15))
                        .add(LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR).setWeight(5))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ToolItems.ICE_MIRROR))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/sandstone_chests"), initialWorldUndergroundCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.ANCIENT_CHISEL))
                        .add(LootItem.lootTableItem(TCItems.DUNERIDER_BOOTS))
                        .add(LootItem.lootTableItem(ToolItems.MAGIC_CONCH))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.THUNDER_ZAPPER))
                        .add(LootItem.lootTableItem(SpearItems.STORM_SPEAR))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MinecartItems.DESERT_MINECART)),
                                PercentageLootEntry.rule(100.0 * 2 / 15, LootItem.lootTableItem(ToolItems.ENCUMBERING_STONE))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/cave_sandstone_chests"), initialWorldCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.ANCIENT_CHISEL))
                        .add(LootItem.lootTableItem(TCItems.DUNERIDER_BOOTS))
                        .add(LootItem.lootTableItem(ToolItems.MAGIC_CONCH))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.THUNDER_ZAPPER))
                        .add(LootItem.lootTableItem(SpearItems.STORM_SPEAR))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(MinecartItems.DESERT_MINECART)),
                                PercentageLootEntry.rule(100.0 * 2 / 15, LootItem.lootTableItem(ToolItems.ENCUMBERING_STONE))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/heaven_islands_chests_0"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.LUCKY_HORSESHOE))
                        .add(LootItem.lootTableItem(TCItems.SHINY_RED_BALLOON))
                        .add(LootItem.lootTableItem(AccessoryItems.CELESTIAL_MAGNET))
                        .add(LootItem.lootTableItem(SwordItems.STARFURY))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 40, LootItem.lootTableItem(TCItems.FLEDGLING_WINGS))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/heaven_islands_chests_1"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.LUCKY_HORSESHOE))
                        .add(LootItem.lootTableItem(TCItems.SHINY_RED_BALLOON))
                        .add(LootItem.lootTableItem(AccessoryItems.CELESTIAL_MAGNET))
                        .add(LootItem.lootTableItem(SwordItems.STARFURY))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 101, LootItem.lootTableItem(TCItems.FLEDGLING_WINGS).setQuality(2))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(NatureBlocks.CLOUD_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(FunctionalBlocks.SKY_MILL))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/ivy_chests"), initialWorldUndergroundCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FLOWER_BOOTS).setWeight(4))
                        .add(LootItem.lootTableItem(TCItems.ANKLET_OF_THE_WIND).setWeight(21))
                        .add(LootItem.lootTableItem(TCItems.FERAL_CLAWS).setWeight(21))
                        .add(LootItem.lootTableItem(ToolItems.STAFF_OF_REGROWTH).setWeight(21))
                        .add(LootItem.lootTableItem(GunItems.BOOMSTICK).setWeight(21))
                        .add(LootItem.lootTableItem(FishingPoleItems.FIBERGLASS_FISHING_POLE).setWeight(7))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(MinecartItems.BEE_MINECART))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/living_ivy_chests"), initialWorldUndergroundCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.FLOWER_BOOTS).setWeight(4))
                        .add(LootItem.lootTableItem(TCItems.ANKLET_OF_THE_WIND).setWeight(21))
                        .add(LootItem.lootTableItem(TCItems.FERAL_CLAWS).setWeight(21))
                        .add(LootItem.lootTableItem(ToolItems.STAFF_OF_REGROWTH).setWeight(21))
                        .add(LootItem.lootTableItem(GunItems.BOOMSTICK).setWeight(21))
                        .add(LootItem.lootTableItem(FishingPoleItems.FIBERGLASS_FISHING_POLE).setWeight(7))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(20, LootTableReference.lootTableReference(ModLootTables.LIVING_MAHOGANY_CARRY)),
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(MinecartItems.BEE_MINECART))
                        ))
                )
        );

        output.accept(Confluence.asResource("chests/cave_chests"), initialWorldCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.BAND_OF_REGENERATION).setWeight(9))
                        .add(LootItem.lootTableItem(TCItems.MAGIC_MIRROR).setWeight(9))
                        .add(LootItem.lootTableItem(TCItems.CLOUD_IN_A_BOTTLE).setWeight(9))
                        .add(LootItem.lootTableItem(TCItems.SHOE_SPIKES).setWeight(9))
                        .add(LootItem.lootTableItem(TCItems.HERMES_BOOTS).setWeight(9))
                        .add(LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR).setWeight(3))
                        .add(LootItem.lootTableItem(TCItems.LAVA_CHARM))
                )
        );
        // 地牢箱系列
        output.accept(Confluence.asResource("chests/dungeon_chest_armor"), dungeonCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.DIAMOND_HORSE_ARMOR)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.SADDLE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_HELMET)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_HELMET))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_BOOTS)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_BOOTS))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_CHESTPLATE)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_CHESTPLATE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_LEGGINGS))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_LEGGINGS))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.LEATHER)).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 19)))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_chest_catch"), dungeonCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.STINKY_FISH)).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BaitItems.APPRENTICE_BAIT)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(FishingPoleItems.REINFORCED_FISHING_POLE))
                        ))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_chest_restaurant"), dungeonCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_LEGGINGS))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(FoodItems.COOK_FISH)).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BREAD)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.CHARCOAL)).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 12)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(FishingPoleItems.REINFORCED_FISHING_POLE))
                        ))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_chest_storage"), dungeonCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.SILVER_LEGGINGS)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ArmorItems.TUNGSTEN_LEGGINGS))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.ARROW)).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GunItems.MUSKET_BULLET)).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(FishingPoleItems.REINFORCED_FISHING_POLE))
                        ))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_chest_weapons"), dungeonCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.SHIELD))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(SwordItems.SILVER_BROADSWORD)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(SwordItems.TUNGSTEN_BROADSWORD))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(SwordItems.SILVER_SHORT_SWORD)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(SwordItems.TUNGSTEN_SHORT_SWORD))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BowItems.SILVER_SHORT_BOW)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BowItems.TUNGSTEN_SHORT_BOW))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BowItems.SILVER_BOW)),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(BowItems.TUNGSTEN_BOW))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.ARROW)).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 25)))
                )
                .withPool(LootPool.lootPool()
                        .add(manaEnchantedBookBuilder)
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_golden_chest"), initialWorldCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(TCItems.COBALT_SHIELD))
                        .add(LootItem.lootTableItem(SwordItems.MURAMASA))
                        .add(LootItem.lootTableItem(ManaWeaponItems.AQUA_SCEPTER))
                        .add(LootItem.lootTableItem(ManaWeaponItems.MAGIC_MISSILE))
                        .add(LootItem.lootTableItem(GunItems.HAND_GUN))
                        .add(LootItem.lootTableItem(YoyoItems.VALOR))
                        .add(LootItem.lootTableItem(TCItems.RAM_RUNE))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ToolItems.SHADOW_KEY))
                        ))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_golden_chest_muramasa"), initialWorldCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SwordItems.MURAMASA))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.RUST_IRON_KEY))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ToolItems.SHADOW_KEY))
                        ))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_golden_chest_residence"), initialWorldCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.ANVIL))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.DIAMOND).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 8))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.GOLDEN_APPLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                        ))
                )
        );
        output.accept(Confluence.asResource("chests/dungeon_wooden_chest"), initialWorldCaveCommon()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ToolItems.GOLDEN_DUNGEON_KEY))
                )
        );
        // 下界
        output.accept(Confluence.asResource("chests/nether_tower_barrel"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.OBSIDIAN)).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 10)))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(Items.GOLDEN_APPLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(TCItems.LAVA_CHARM))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArrowItems.HELLFIRE_ARROW)).apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 30)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.COOKED_PORKCHOP)).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))
                )
                .withPool(LootPool.lootPool()
                        .add(manaEnchantedBookBuilder)
                )
        );

        output.accept(Confluence.asResource("chests/shadow_chests"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ManaWeaponItems.FLOWER_OF_FIRE))
                        .add(LootItem.lootTableItem(ManaWeaponItems.FLAMELASH))
                        .add(LootItem.lootTableItem(SpearItems.DARK_LANCE))
                        .add(LootItem.lootTableItem(BowItems.HELLWING_BOW))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(MinecartItems.DEMONIC_HELLCART))
                        .add(LootItem.lootTableItem(TCItems.TREASURE_MAGNET).setWeight(2))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))),
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))),
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(MaterialItems.METEORITE_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 29))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ArrowItems.HELLFIRE_ARROW)).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 74)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GunItems.MUSKET_BULLET)).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 74)))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.TORCH)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))
                                .setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ConsumableItems.DYNAMITE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 31, LootItem.lootTableItem(PotionItems.FEATHERFALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.MANA_REGENERATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.MAGIC_POWER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.HEART_REACH_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.INVISIBILITY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 3 / 31, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.THORNS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.RANDOM_TELEPORT_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.OBSIDIAN_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.INFERNO_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.TORCH)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 29)))
                        )
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 2 / 3, LootItem.lootTableItem(PotionItems.INFERNO_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(manaEnchantedBookBuilder)
                )
        );
        // 空岛村
        output.accept(Confluence.asResource("chests/sky_village_chests"), LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(MaterialItems.WEAVING_CLOUD_COTTON).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(MaterialItems.FALLING_STAR).apply(SetItemCountFunction.setCount(UniformGenerator.between(12, 24))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.CLOUD_BREAD).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 8))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.BOOK).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(13, 15))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.APPLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 7))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(NatureBlocks.CLOUD_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 45))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(PercentageLootEntry.independent(false,
                                        PercentageLootEntry.rule(50, LootItem.lootTableItem(ToolItems.METEOR_COMPASS))
                                ))
                        )
                        .withPool(LootPool.lootPool()
                                .add(PercentageLootEntry.independent(false,
                                        PercentageLootEntry.rule(100.0 / 3, manaEnchantedBookBuilder)
                                ))
                        )
                        //星钢剑
                        .withPool(LootPool.lootPool()
                                .add(PercentageLootEntry.independent(false,
                                        PercentageLootEntry.rule(25, LootItem.lootTableItem(SwordItems.STAR_STEEL_SWORD))
                                ))
                        )
                        //稻草人弓
                        .withPool(LootPool.lootPool()
                                .add(PercentageLootEntry.independent(false,
                                        PercentageLootEntry.rule(15, LootItem.lootTableItem(BowItems.SCAREBOW.get()))
                                ))
                        )
                        //灵魂缓冲：独立池子 33% 概率
                        .withPool(LootPool.lootPool()
                                .when(LootItemRandomChanceCondition.randomChance(0.33F))
                                .add(LootItem.lootTableItem(SoulItems.SOUL_SKILL_BOOK)
                                        .apply(BindSoulSkillFunction.bind(SoulBooks.SOUL_BUFFER)))
                        )
                //先暂时使用空岛村箱子
        );
    }

    // percent 为该物品的总出现百分比，各文本变体平均分配，剩余概率自动不生成物品。
    private static LootTable.Builder mysteriousTextLoot(net.minecraft.world.item.Item item, String key, int variants, double percent) {
        PercentageLootEntry.Rule[] rules = new PercentageLootEntry.Rule[variants];
        for (int index = 0; index < variants; index++) {
            rules[index] = PercentageLootEntry.rule(percent / variants, LootItem.lootTableItem(item)
                    .apply(SetLoreFunction.setLore().setReplace(true).addLine(Component.translatable("lore.confluence." + key + "." + index).withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false))))
                    .apply(SetNameFunction.setName(Component.translatable("item.confluence." + key + ".name_" + index).withStyle(style -> style.withItalic(false)))));
        }
        return LootTable.lootTable().withPool(LootPool.lootPool().add(PercentageLootEntry.exclusive(false, rules)));
    }

    // 地下小屋金箱的专属池：普通地下小屋与发光蘑菇地下小屋共用，保证两张表的这一部分完全一致
    private static LootPool.Builder undergroundCabinsGoldenChestPool() {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(TCItems.BAND_OF_REGENERATION).setWeight(3))
                .add(LootItem.lootTableItem(TCItems.MAGIC_MIRROR).setWeight(3))
                .add(LootItem.lootTableItem(TCItems.CLOUD_IN_A_BOTTLE).setWeight(3))
                .add(LootItem.lootTableItem(TCItems.SHOE_SPIKES).setWeight(3))
                .add(LootItem.lootTableItem(TCItems.HERMES_BOOTS).setWeight(3))
                .add(LootItem.lootTableItem(FunctionalBlocks.EXTRACTINATOR).setWeight(1));
    }

    // 困难模式前箱子地下通用
    private LootTable.Builder initialWorldUndergroundCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ConsumableItems.BOMB).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 19))))
                        ))
                )
                // 缺天使雕像
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ModBlocks.ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 100))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ConsumableItems.SHURIKEN).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 49)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 49))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(PotionItems.LESSER_HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.REGENERATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.SHINE_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.ARCHERY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.GILLS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.MINING_POTION.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 27, LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 2 / 3, LootItem.lootTableItem(PotionItems.RECALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(Items.BOOK).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(15.0F))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(Items.BOOK).apply(manaEnchantment()))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(50, 89))))
                        ))
                );
    }

    // 困难模式前箱子地表通用
    private LootTable.Builder initialWorldSurfaceCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(ConsumableItems.THROWING_KNIVE).apply(SetItemCountFunction.setCount(UniformGenerator.between(75, 150))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(ConsumableItems.HERB_BAG).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(ConsumableItems.CAN_OF_WORMS).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ConsumableItems.GRENADE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(MaterialItems.LEAD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(MaterialItems.TIN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 7))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModBlocks.ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 50))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 50)))),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ConsumableItems.SHURIKEN).apply(SetItemCountFunction.setCount(UniformGenerator.between(25, 50))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20)))),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.BOTTLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 2 / 3, LootItem.lootTableItem(PotionItems.RECALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 29))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.OAK_LOG).apply(SetItemCountFunction.setCount(UniformGenerator.between(12, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 30, LootItem.lootTableItem(Items.BOOK)
                                        .apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(10.0F))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(5, LootItem.lootTableItem(Items.BOOK).apply(manaEnchantment()))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.SHINE_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.BUILDER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.MINING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                );

    }

    // 困难模式前箱子洞穴通用
    private LootTable.Builder initialWorldCaveCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ArrowItems.FLAMING_ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ConsumableItems.THROWING_KNIVE).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ArrowItems.STAR_ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ConsumableItems.DYNAMITE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ConsumableItems.SUSPICIOUS_LOOKING_EYE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.FEATHERFALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.ARCHERY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.THORNS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.RANDOM_TELEPORT_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.INVISIBILITY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 29))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(PotionItems.RECALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(Items.BOOK)
                                        .apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(20.0F))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(Items.BOOK).apply(manaEnchantment()))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                );
    }


    // 困难模式前冰雪洞穴箱
    private LootTable.Builder initialWorldIceCaveCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.SILVER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.TUNGSTEN_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))),
                                PercentageLootEntry.rule(100.0 / 8, LootItem.lootTableItem(MaterialItems.PLATINUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ArrowItems.FLAMING_ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35)))),
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ConsumableItems.THROWING_KNIVE).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(25, LootItem.lootTableItem(ArrowItems.STAR_ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 35))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(ConsumableItems.DYNAMITE))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(20, LootItem.lootTableItem(ConsumableItems.DEER_THING))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(PotionItems.HEALING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.SPELUNKER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.FEATHERFALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.ARCHERY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 18, LootItem.lootTableItem(PotionItems.GRAVITATION_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.DANGERSENSE_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.HUNTER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.THORNS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.WATER_WALKING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.RANDOM_TELEPORT_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 / 18, LootItem.lootTableItem(PotionItems.INVISIBILITY_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(15, 29))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(PotionItems.RECALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 15, LootItem.lootTableItem(Items.BOOK)
                                        .apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(20.0F))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(10, LootItem.lootTableItem(Items.BOOK).apply(manaEnchantment()))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                );
    }

    // 地牢箱子附带通用
    private LootTable.Builder dungeonCommon() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20)))),
                                PercentageLootEntry.rule(100.0 / 3, LootItem.lootTableItem(PotionItems.BOTTLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 * 2 / 3, LootItem.lootTableItem(PotionItems.RECALL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(50, LootItem.lootTableItem(ModItems.SILVER_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(20, 40))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.independent(false,
                                PercentageLootEntry.rule(100.0 / 6, LootItem.lootTableItem(ModItems.GOLD_COIN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.COBWEB).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                )
                .withPool(LootPool.lootPool()
                        .add(PercentageLootEntry.exclusive(false,
                                PercentageLootEntry.rule(100.0 * 2 / 24, LootItem.lootTableItem(PotionItems.IRON_SKIN_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 24, LootItem.lootTableItem(PotionItems.SHINE_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 24, LootItem.lootTableItem(PotionItems.NIGHT_OWL_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 24, LootItem.lootTableItem(PotionItems.BUILDER_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 24, LootItem.lootTableItem(PotionItems.MINING_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))),
                                PercentageLootEntry.rule(100.0 * 2 / 24, LootItem.lootTableItem(PotionItems.SWIFTNESS_POTION).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        ))
                )
                .withPool(LootPool.lootPool()
                        .add(
                                LootItem.lootTableItem(Items.BOOK)
                                        .apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)))
                        )
                );
    }

    /// 使用原版构建器创建只会抽取本模组魔力附魔的随机附魔函数。
    private static LootItemFunction.Builder manaEnchantment() {
        EnchantRandomlyFunction.Builder builder = EnchantRandomlyFunction.randomEnchantment();
        MANA_ENCHANTMENTS.forEach(builder::withEnchantment);
        return builder;
    }
}
