package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.GamePhase2AttributeModifiers;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.mesdag.portlib.datamap.PortDataMapProvider;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("deprecation")
public final class GamePhase2AttributeModifiersSubProvider {
    private static final ResourceLocation id = Confluence.asResource("game_phase_modifier");

    private static final AttributeModifiersValue VANILLA_MINECRAFT_MONSTER_ENHANCEMENT = AttributeModifiersValue.builder()
            .add(LibAttributes.getAttackDamage().value(), id, 2.8, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.MAX_HEALTH, id, 2.8, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ARMOR, id, 2.8, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue INCREASE_FRIENDLY_CREATURE_HEALTH = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue NORMAL_CHANGE_1 = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 0.1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(LibAttributes.getAttackDamage().value(), id, -0.1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue NORMAL_CHANGE_2 = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 1.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(LibAttributes.getAttackDamage().value(), id, 0.8, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .build();

    private static final AttributeModifiersValue NORMAL_CHANGE_3 = AttributeModifiersValue.builder()
            .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(LibAttributes.getAttackDamage().value(), id, 1.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
            .build();

    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create()
                // 初始数值基础，目标阶段数值除初始数值-1 = 填参值
                // 泰拉怪物
                .add(MonsterEntities.BLOOD_ZOMBIE, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.SHORT_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.BIG_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.BIG_ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.BIG_HELMET_ANGER_BONES, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.ANTLION_SWARMER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.GIANT_ANTLION_SWARMER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.LITTLE_HORNET, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.BLACK_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.BLUE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 5.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 4.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.GREEN_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 7.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 6.1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 8.8, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 11, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.ICE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 2.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.PURPLE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.RED_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.YELLOW_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.DESERT_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.BLOOD_CRAWLER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.PINK_JELLYFISH, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.BLUE_JELLYFISH, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.CAVE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 2.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEAnimals.CRAB, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.PIRANHA, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.CRIMERA, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.CURSED_SKULL, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.EATER_OF_SOULS, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.DARK_CASTER, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 1.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 0.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.DEMON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.DEMON_EYE, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.DEVOURER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.DUNGEON_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.FACE_MONSTER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.FIRE_IMP, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.GHOST, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.CRAWDAD, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.GIANT_SHELLY, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.GIANT_WORM, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.GOBLIN_SCOUT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.GRANITE_ELEMENTAL, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                /*.add(MonsterEntities.METEOR_HEAD, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))*/
                .add(MonsterEntities.HARPY, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.HELL_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.HORNET, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.ICE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.JUNGLE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.JUNGLE_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.LAVA_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.SNATCHER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.SNATCHER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                // todo 史莱姆之母↓
//                .add(MonsterEntities., Map.of(
//                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
//                        GamePhase.PLANTERA, NORMAL_CHANGE_2
//                ))
                .add(MonsterEntities.PINK_SLIME, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.SPORE_BAT, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 2.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.SPORE_SKELETON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.SPORE_ZOMBIE, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.TOMB_CRAWLER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.UNDEAD_VIKING, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.VOODOO_DEMON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.DRIPPLER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.FLYING_FISH, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 3.3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 2.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 4.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(LibAttributes.getAttackDamage().value(), id, 3.4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 4, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(MonsterEntities.GOBLIN_ARCHER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                ))
                .add(MonsterEntities.GOBLIN_PEON, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.GOBLIN_SORCERER, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_2,
                        GamePhase.PLANTERA, NORMAL_CHANGE_3
                ))
                .add(MonsterEntities.GOBLIN_THIEF, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1,
                        GamePhase.PLANTERA, NORMAL_CHANGE_2
                )).add(MonsterEntities.GOBLIN_WARRIOR, Map.of(
                        GamePhase.WALL_OF_FLESH, NORMAL_CHANGE_1
                ))

                .add(MonsterEntities.MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.DARK_MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.BLOOD_MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))
                .add(MonsterEntities.LIGHT_MUMMY, Map.of(
                        GamePhase.PLANTERA, NORMAL_CHANGE_1
                ))


                // MC原版敌对怪物
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 1.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
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
                                .add(Attributes.MAX_HEALTH, id, 3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
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
                                .add(LibAttributes.getAttackDamage().value(), id, 1.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
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
                                .add(LibAttributes.getAttackDamage().value(), id, 1.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.ZOMBIE_VILLAGER.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 1.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(EntityType.DROWNED.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 1.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
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
                                .add(LibAttributes.getAttackDamage().value(), id, 1.7, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                // NPC
                .add(NpcEntities.GUIDE, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.DEMOLITIONIST, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.GOBLIN_TINKERER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.ARMS_DEALER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.NURSE, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.MERCHANT, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.PAINTER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.ANGLER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.FEMALE_ANGLER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.DRYAD, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.DYE_TRADER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.OLD_MAN, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.MECHANIC, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.TRAVELING_MERCHANT, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.WITCH_DOCTOR, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.PARTY_GIRL, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.CLOTHIER, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.ZOOLOGIST, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.TRUFFLE, Map.of(
                        GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH
                ))
                .add(NpcEntities.WIZARD, Map.of(
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
//                                .add(LibAttributes.getAttackDamage().value(), id, 2.6, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
//                                .add(Attributes.MAX_HEALTH, id, 3, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
//                                .build()
//                ))
//                .add(EntityType.IRON_GOLEM.builtInRegistryHolder(), Map.of(
//                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
//                                .add(LibAttributes.getAttackDamage().value(), id, 2, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL)
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

    @SuppressWarnings("unchecked")
    public static class Builder extends PortDataMapProvider.Builder<GamePhase2AttributeModifiers, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS);
        }

        public final Builder add(Holder<EntityType<?>> holder, Map<GamePhase, AttributeModifiersValue> map) {
            super.add(Objects.requireNonNull(holder.getKey()), new GamePhase2AttributeModifiers(map), false);
            return this;
        }

        public final Builder add(TagKey<EntityType<?>> tagKey, Map<GamePhase, AttributeModifiersValue> map) {
            super.add(tagKey, new GamePhase2AttributeModifiers(map), false);
            return this;
        }

        public final Builder add(RegistryObject<? extends EntityType<?>> object, Map<GamePhase, AttributeModifiersValue> map) {
            super.add((ResourceKey<EntityType<?>>) object.getKey(), new GamePhase2AttributeModifiers(map), false);
            return this;
        }
    }
}
