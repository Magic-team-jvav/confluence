package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.GamePhase2AttributeModifiers;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.Map;
import java.util.Objects;

public final class GamePhase2AttributeModifiersSubProvider {
    private static final ResourceLocation id = Confluence.asResource("game_phase_modifier");

    private static final AttributeModifiersValue VANILLA_MINECRAFT_MONSTER_ENHANCEMENT = AttributeModifiersValue.builder()
            .add(Attributes.ATTACK_DAMAGE, id, 2.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.MAX_HEALTH, id, 2.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ARMOR, id, 2.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue INCREASE_FRIENDLY_CREATURE_HEALTH = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue NORMAL_CHANGE_1 = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ATTACK_DAMAGE, id, -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue NORMAL_CHANGE_2 = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 1.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ATTACK_DAMAGE, id, 0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue NORMAL_CHANGE_3 = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ATTACK_DAMAGE, id, 1.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .build();

    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create()
                // 初始数值基础，目标阶段数值除初始数值-1 = 填参值
                // 泰拉怪物
                .add(TEMonsterEntities.BLOOD_ZOMBIE, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.SHORT_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.BIG_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.BIG_ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.ANTLION_SWARMER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.GIANT_ANTLION_SWARMER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.LITTLE_HORNET, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.BLACK_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.BLUE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 5.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 4.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.GREEN_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 7.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 6.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 8.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.ICE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 2.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.PURPLE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.RED_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.YELLOW_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.DESERT_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.BLOOD_CRAWLER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.PINK_JELLYFISH, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.BLUE_JELLYFISH, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.CAVE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 2.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEAnimals.CRAB, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.PIRANHA, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.CRIMERA, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.CURSED_SKULL, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.EATER_OF_SOULS, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.DARK_CASTER, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 1.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.DEMON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.DEMON_EYE, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.DEVOURER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.DUNGEON_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.FACE_MONSTER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.FIRE_IMP, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.GHOST, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.CRAWDAD, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.GIANT_SHELLY, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.GIANT_WORM, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.GOBLIN_SCOUT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.GRANITE_ELEMENTAL, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                /*.add(TEMonsterEntities.METEOR_HEAD, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))*/
                .add(TEMonsterEntities.HARPY, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.HELL_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.HORNET, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.ICE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.JUNGLE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.JUNGLE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.LAVA_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.SNATCHER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.SNATCHER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                // todo 史莱姆之母↓
//                .add(TEMonsterEntities., Map.of(
//                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
//                        GamePhase.PLANTERA, NORMAL_CHANGE_2
//                ))
                .add(TEMonsterEntities.PINK_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.SPORE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 2.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.SPORE_SKELETON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.SPORE_ZOMBIE, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.TOMB_CRAWLER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.UNDEAD_VIKING, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.VOODOO_DEMON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.DRIPPLER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.FLYING_FISH, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 2.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 3.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.GOBLIN_ARCHER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(TEMonsterEntities.GOBLIN_PEON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.GOBLIN_SORCERER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(TEMonsterEntities.GOBLIN_THIEF, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                )).add(TEMonsterEntities.GOBLIN_WARRIOR, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))

                .add(TEMonsterEntities.MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.DARK_MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.BLOOD_MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(TEMonsterEntities.LIGHT_MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))



                // MC原版敌对怪物
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.VINDICATOR.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.BOGGED.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.BREEZE.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.CREEPER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.ELDER_GUARDIAN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.EVOKER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.GHAST.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.GUARDIAN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.HOGLIN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.HUSK.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.MAGMA_CUBE.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.PHANTOM.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.PIGLIN_BRUTE.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.PILLAGER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.RAVAGER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.SHULKER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.SILVERFISH.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.SKELETON.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.SLIME.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.STRAY.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.VEX.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.WARDEN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.WITCH.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.WITHER_SKELETON.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.ZOGLIN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.ZOMBIE_VILLAGER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.DROWNED.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.ENDERMAN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.PIGLIN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.SPIDER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.CAVE_SPIDER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, VANILLA_MINECRAFT_MONSTER_ENHANCEMENT
                ))
                .add(EntityType.ZOMBIFIED_PIGLIN.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                // NPC
                .add(TENpcEntities.GUIDE, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.DEMOLITIONIST, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.GOBLIN_TINKERER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.ARMS_DEALER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.NURSE, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.MERCHANT, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.PAINTER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.ANGLER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.FEMALE_ANGLER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.DRYAD, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.DYE_TRADER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.OLD_MAN, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.MECHANIC, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.TRAVELING_MERCHANT, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.WITCH_DOCTOR, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.PARTY_GIRL, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.CLOTHIER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.ZOOLOGIST, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.TRUFFLE, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(TENpcEntities.WIZARD, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
        // MC原版友好生物
//                .add(EntityType.ALLAY.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
//                ))
//                .add(EntityType.VILLAGER.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
//                ))
//                .add(EntityType.WANDERING_TRADER.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
//                ))
//                .add(EntityType.WOLF.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
//                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
//                                .add(Attributes.MAX_HEALTH, id, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
//                                .build()
//                ))
//                .add(EntityType.IRON_GOLEM.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
//                                .add(Attributes.ATTACK_DAMAGE, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
//                                .build()
//                ))
//                .add(EntityType.HORSE.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
//                ))
//                .add(EntityType.SNIFFER.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
//                ))
        ;
    }

    public static class Builder extends DataMapProvider.Builder<GamePhase2AttributeModifiers, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS);
        }

        public final Builder add(IHolderExtension<EntityType<?>> holder, Map<GamePhase, AttributeModifiersValue> map) {
            super.add(Objects.requireNonNull(holder.getKey()), new GamePhase2AttributeModifiers(map), false);
            return this;
        }

        public final Builder add(TagKey<EntityType<?>> tagKey, Map<GamePhase, AttributeModifiersValue> map) {
            super.add(tagKey, new GamePhase2AttributeModifiers(map), false);
            return this;
        }
    }
}
