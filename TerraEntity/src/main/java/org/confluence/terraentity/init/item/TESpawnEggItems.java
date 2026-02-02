package org.confluence.terraentity.init.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.function.Supplier;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TESpawnEggItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<SpawnEggItem> BLUE_SLIME_SPAWN_EGG = registerEgg("blue_slime_spawn_egg", TEMonsterEntities.BLUE_SLIME, 0x73bcf4, 0x466CBE);
    public static final DeferredItem<SpawnEggItem> PURPLE_SLIME_SPAWN_EGG = registerEgg("purple_slime_spawn_egg", TEMonsterEntities.PURPLE_SLIME, 0xf334f8, 0xA246BE);
    public static final DeferredItem<SpawnEggItem> GREEN_SLIME_SPAWN_EGG = registerEgg("green_slime_spawn_egg", TEMonsterEntities.GREEN_SLIME, 0xa2f89f, 0x3de838);
    public static final DeferredItem<SpawnEggItem> RED_SLIME_SPAWN_EGG = registerEgg("red_slime_spawn_egg", TEMonsterEntities.RED_SLIME, 0xf83434, 0xA51E1E);
    public static final DeferredItem<SpawnEggItem> YELLOW_SLIME_SPAWN_EGG = registerEgg("yellow_slime_spawn_egg", TEMonsterEntities.YELLOW_SLIME, 0xf8e234, 0xd19519);
    public static final DeferredItem<SpawnEggItem> HONEY_SLIME_SPAWN_EGG = registerEgg("honey_slime_spawn_egg", TEMonsterEntities.HONEY_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BLACK_SLIME_SPAWN_EGG = registerEgg("black_slime_spawn_egg", TEMonsterEntities.BLACK_SLIME, 0x7E7E7E, 0x373535);
    public static final DeferredItem<SpawnEggItem> PINK_SLIME_SPAWN_EGG = registerEgg("pink_slime_spawn_egg", TEMonsterEntities.PINK_SLIME, 0xFF87B3, 0xf89fe3);
    public static final DeferredItem<SpawnEggItem> DUNGEON_SLIME_SPAWN_EGG = registerEgg("dungeon_slime_spawn_egg", TEMonsterEntities.DUNGEON_SLIME, 0x6d697b, 0x6d697b);
    public static final DeferredItem<SpawnEggItem> DESERT_SLIME_SPAWN_EGG = registerEgg("desert_slime_spawn_egg", TEMonsterEntities.DESERT_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GREEN_DUMPLING_SLIME_SPAWN_EGG = registerEgg("green_dumpling_slime_spawn_egg", TEMonsterEntities.GREEN_DUMPLING_SLIME, 0xa2f89f, 0x3de838);
    public static final DeferredItem<SpawnEggItem> SWAMP_SLIME_SPAWN_EGG = registerEgg("swamp_slime_spawn_egg", TEMonsterEntities.SWAMP_SLIME, 0xa2f89f, 0x3de838);
    public static final DeferredItem<SpawnEggItem> JUNGLE_SLIME_SPAWN_EGG = registerEgg("jungle_slime_spawn_egg", TEMonsterEntities.JUNGLE_SLIME, 0x9ae920, 0xC7AB5E);
    public static final DeferredItem<SpawnEggItem> GOLDEN_SLIME_SPAWN_EGG = registerEgg("golden_slime_spawn_egg", TEMonsterEntities.GOLDEN_SLIME, 0xfcf8bd, 0xf8e234);
    public static final DeferredItem<SpawnEggItem> SPIKED_SLIME_SPAWN_EGG = registerEgg("spiked_slime_spawn_egg", TEMonsterEntities.SPIKED_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SPIKED_JUNGLE_SLIME_SPAWN_EGG = registerEgg("spiked_jungle_slime_spawn_egg", TEMonsterEntities.SPIKED_JUNGLE_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SPIKED_ICE_SLIME_SPAWN_EGG = registerEgg("spiked_ice_slime_spawn_egg", TEMonsterEntities.SPIKED_ICE_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> JUNGLE_BAT_SPAWN_EGG = registerEgg("jungle_bat_spawn_egg", TEMonsterEntities.JUNGLE_BAT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SNATCHER_SPAWN_EGG = registerEgg("snatcher_spawn_egg", TEMonsterEntities.SNATCHER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> MAN_EATER_SPAWN_EGG = registerEgg("man_eater_spawn_egg", TEMonsterEntities.MAN_EATER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HORNET_SPAWN_EGG = registerEgg("hornet_spawn_egg", TEMonsterEntities.HORNET, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ICE_SLIME_SPAWN_EGG = registerEgg("ice_slime_spawn_egg", TEMonsterEntities.ICE_SLIME, 0xB3F0EA, 0x7FDEDF);
    public static final DeferredItem<SpawnEggItem> ICE_BAT_SPAWN_EGG = registerEgg("ice_bat_spawn_egg", TEMonsterEntities.ICE_BAT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> LAVA_SLIME_SPAWN_EGG = registerEgg("lava_slime_spawn_egg", TEMonsterEntities.LAVA_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HELL_BAT_SPAWN_EGG = registerEgg("hell_bat_spawn_egg", TEMonsterEntities.HELL_BAT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CRIMSLIME_SPAWN_EGG = registerEgg("crimslime_spawn_egg", TEMonsterEntities.CRIMSLIME, 0x8B4949, 0x7D1D1D);
    public static final DeferredItem<SpawnEggItem> CORRUPT_SLIME_SPAWN_EGG = registerEgg("corrupt_slime_spawn_egg", TEMonsterEntities.CORRUPT_SLIME, 0x3f3885, 0x625b9f);
    public static final DeferredItem<SpawnEggItem> TROPIC_SLIME_SPAWN_EGG = registerEgg("tropic_slime_spawn_egg", TEMonsterEntities.TROPIC_SLIME, 0x73bcf4, 0x7374f4);
    public static final DeferredItem<SpawnEggItem> LUMINOUS_SLIME_SPAWN_EGG = registerEgg("evil_slime_spawn_egg", TEMonsterEntities.LUMINOUS_SLIME, 0xFF00FF, 0xEDFFFA);
    public static final DeferredItem<SpawnEggItem> DEMON_EYE_SPAWN_EGG = registerEgg("demon_eye_spawn_egg", TEMonsterEntities.DEMON_EYE, 0xffffff, 0xab0d0d);
    public static final DeferredItem<SpawnEggItem> BLOOD_CRAWLER_SPAWN_EGG = registerEgg("blood_crawler_spawn_egg", TEMonsterEntities.BLOOD_CRAWLER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BLOODY_SPORE_SPAWN_EGG = registerEgg("bloody_spore_spawn_egg", TEMonsterEntities.BLOODY_SPORE, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SPORE_SKELETON_SPAWN_EGG = registerEgg("spore_skeleton_spawn_egg", TEMonsterEntities.SPORE_SKELETON, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SPORE_ZOMBIE_SPAWN_EGG = registerEgg("spore_zombie_spawn_egg", TEMonsterEntities.SPORE_ZOMBIE, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HAT_SPORE_ZOMBIE_SPAWN_EGG = registerEgg("hat_spore_zombie_spawn_egg", TEMonsterEntities.HAT_SPORE_ZOMBIE, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DECAYEDER_SPAWN_EGG = registerEgg("decayeder_spawn_egg", TEMonsterEntities.DECAYEDER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DEVOURER_SPAWN_EGG = registerEgg("devourer_spawn_egg", TEMonsterEntities.DEVOURER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GIANT_SHELLY_SPAWN_EGG = registerEgg("giant_shelly_spawn_egg", TEMonsterEntities.GIANT_SHELLY, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CRAWDAD_SPAWN_EGG = registerEgg("crawdad_spawn_egg", TEMonsterEntities.CRAWDAD, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GIANT_WORM_SPAWN_EGG = registerEgg("giant_worm_spawn_egg", TEMonsterEntities.GIANT_WORM, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> NYMPH_SPAWN_EGG = registerEgg("nymph_spawn_egg", TEMonsterEntities.NYMPH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CAVE_BAT_SPAWN_EGG = registerEgg("cave_bat_spawn_egg", TEMonsterEntities.CAVE_BAT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SPORE_BAT_SPAWN_EGG = registerEgg("spore_bat_spawn_egg", TEMonsterEntities.SPORE_BAT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> TOMB_CRAWLER_SPAWN_EGG = registerEgg("tomb_crawler_spawn_egg", TEMonsterEntities.TOMB_CRAWLER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BONE_SERPENT_SPAWN_EGG = registerEgg("bone_serpent_spawn_egg", TEMonsterEntities.BONE_SERPENT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> WITHER_BONE_SERPENT_SPAWN_EGG = registerEgg("wither_bone_serpent_spawn_egg", TEMonsterEntities.WITHER_BONE_SERPENT, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ANTLION_SWARMER_SPAWN_EGG = registerEgg("antlion_swarmer_spawn_egg", TEMonsterEntities.ANTLION_SWARMER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GIANT_ANTLION_SWARMER_SPAWN_EGG = registerEgg("giant_antlion_spawn_egg", TEMonsterEntities.GIANT_ANTLION_SWARMER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GRANTITE_ELEMENTAL_SPAWN_EGG = registerEgg("grantite_elemental_spawn_egg", TEMonsterEntities.GRANITE_ELEMENTAL, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> FLYING_FISH_SPAWN_EGG = registerEgg("flying_fish_spawn_egg", TEMonsterEntities.FLYING_FISH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HARPY_SPAWN_EGG = registerEgg("harpy_spawn_egg", TEMonsterEntities.HARPY, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DEMON_SPAWN_EGG = registerEgg("demon_spawn_egg", TEMonsterEntities.DEMON, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> VOODOO_DEMON_SPAWN_EGG = registerEgg("voodoo_demon_spawn_egg", TEMonsterEntities.VOODOO_DEMON, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DRIPPLER_SPAWN_EGG = registerEgg("drippler_spawn_egg", TEMonsterEntities.DRIPPLER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BLOOD_ZOMBIE_SPAWN_EGG = registerEgg("blood_zombie_spawn_egg", TEMonsterEntities.BLOOD_ZOMBIE, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> WANDERING_EYE_FISH_SPAWN_EGG = registerEgg("wandering_eye_fish_spawn_egg", TEMonsterEntities.WANDERING_EYE_FISH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GHOST_SPAWN_EGG = registerEgg("ghost_spawn_egg", TEMonsterEntities.GHOST, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CRIMERA_SPAWN_EGG = registerEgg("crimera_spawn_egg", TEMonsterEntities.CRIMERA, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> EATER_OF_SOULS_SPAWN_EGG = registerEgg("eater_of_souls_spawn_egg", TEMonsterEntities.EATER_OF_SOULS, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> FACE_MONSTER_SPAWN_EGG = registerEgg("face_monster_spawn_egg", TEMonsterEntities.FACE_MONSTER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> FIRE_IMG_SPAWN_EGG = registerEgg("fire_imp_spawn_egg", TEMonsterEntities.FIRE_IMP, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SNOW_FLINX_SPAWN_EGG = registerEgg("snow_flinx_spawn_egg", TEMonsterEntities.SNOW_FLINX, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> METEOR_HEAD_SPAWN_EGG = registerEgg("meteor_head_spawn_egg", TEMonsterEntities.METEOR_HEAD, 0xffffff, 0xffffff);

    public static final DeferredItem<SpawnEggItem> PIRANHA_SPAWN_EGG = registerEgg("piranha_spawn_egg", TEMonsterEntities.PIRANHA, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SHARK_SPAWN_EGG = registerEgg("shark_spawn_egg", TEMonsterEntities.SHARK, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ARAPAIMA_SPAWN_EGG = registerEgg("arapaima_spawn_egg", TEMonsterEntities.ARAPAIMA, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BLUE_JELLYFISH_SPAWN_EGG = registerEgg("blue_jellyfish_spawn_egg", TEMonsterEntities.BLUE_JELLYFISH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> PINK_JELLYFISH_SPAWN_EGG = registerEgg("pink_jellyfish_spawn_egg", TEMonsterEntities.PINK_JELLYFISH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GREEN_JELLYFISH_SPAWN_EGG = registerEgg("green_jellyfish_spawn_egg", TEMonsterEntities.GREEN_JELLYFISH, 0xffffff, 0xffffff);

    // BOSS
    public static final DeferredItem<SpawnEggItem> KING_SLIME_SPAWN_EGG = registerEgg("king_slime_spawn_egg", TEBossEntities.KING_SLIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> EYE_OF_CTHULHU_SPAWN_EGG = registerEgg("cthulhu_eye_spawn_egg", TEBossEntities.EYE_OF_CTHULHU, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> EATER_OF_WORLD_SPAWN_EGG = registerEgg("eater_of_world_spawn_egg", TEBossEntities.EATER_OF_WORLDS, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BRAIN_OF_CTHULHU_SPAWN_EGG = registerEgg("brain_of_cthulhu_spawn_egg", TEBossEntities.BRAIN_OF_CTHULHU, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> QUEEN_BEE_SPAWN_EGG = registerEgg("queen_bee_spawn_egg", TEBossEntities.QUEEN_BEE, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SKELETRON_SPAWN_EGG = registerEgg("skeletron_spawn_egg", TEBossEntities.SKELETRON, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DUNGEON_GUARDIAN_SPAWN_EGG = registerEgg("dungeon_guardian_spawn_egg", TEBossEntities.DUNGEON_GUARDIAN, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DEERCLOPS_SPAWN_EGG = registerEgg("deerclops_spawn_egg", TEBossEntities.DEERCLOPS, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> WALL_OF_FLESH_SPAWN_EGG = registerEgg("wall_of_flesh_spawn_egg", TEBossEntities.WALL_OF_FLESH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HILL_OF_FLESH_SPAWN_EGG = registerEgg("hill_of_flesh_spawn_egg", TEBossEntities.HILL_OF_FLESH, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> RETINAZER_SPAWN_EGG = registerEgg("retinazer_spawn_egg", TEBossEntities.RETINAZER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SPAZMATISM_SPAWN_EGG = registerEgg("spazmatism_spawn_egg", TEBossEntities.SPAZMATISM, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> THE_TWINS_SPAWN_EGG = registerEgg("the_twins_spawn_egg", TEBossEntities.THE_TWINS, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SKELETRON_PRIME_SPAWN_EGG = registerEgg("skeletron_prime_spawn_egg", TEBossEntities.SKELETRON_PRIME, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> THE_DESTROYER_SPAWN_EGG = registerEgg("the_destroyer_spawn_egg", TEBossEntities.THE_DESTROYER, 0xffffff, 0xffffff);
    public static final DeferredItem<SpawnEggItem> PLANTERA_SPAWN_EGG = registerEgg("plantera_spawn_egg", TEBossEntities.PLANTERA, 0xffffff, 0xffffff);

    // 地牢骷髅
    public static final DeferredItem<SpawnEggItem> ANGER_BONES_SPAWN_EGG = registerEgg("anger_bones_spawn_egg", TEMonsterEntities.ANGER_BONES, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SHORT_BONES_SPAWN_EGG = registerEgg("short_bones_spawn_egg", TEMonsterEntities.SHORT_BONES, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BIG_BONES_SPAWN_EGG = registerEgg("big_bones_spawn_egg", TEMonsterEntities.BIG_BONES, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BIG_ANGER_BONES_SPAWN_EGG = registerEgg("big_anger_bones_spawn_egg", TEMonsterEntities.BIG_ANGER_BONES, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BIG_MUSCLE_ANGER_BONES_SPAWN_EGG = registerEgg("big_muscle_anger_bones_spawn_egg", TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BIG_HELMET_ANGER_BONES_SPAWN_EGG = registerEgg("big_helmet_anger_bones_spawn_egg", TEMonsterEntities.BIG_HELMET_ANGER_BONES, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CURSED_SKULL_SPAWN_EGG = registerEgg("cursed_skull_spawn_egg", TEMonsterEntities.CURSED_SKULL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DARK_CASTER_SPAWN_EGG = registerEgg("dark_caster_spawn_egg", TEMonsterEntities.DARK_CASTER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> UNDEAD_VIKING_SPAWN_EGG = registerEgg("undead_viking_spawn_egg", TEMonsterEntities.UNDEAD_VIKING, 0xffffff);

    // 哥布林军队
    public static final DeferredItem<SpawnEggItem> GOBLIN_SORCERER_SPAWN_EGG = registerEgg("goblin_sorcerer_spawn_egg", TEMonsterEntities.GOBLIN_SORCERER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOBLIN_ARCHER_SPAWN_EGG = registerEgg("goblin_archer_spawn_egg", TEMonsterEntities.GOBLIN_ARCHER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOBLIN_PEON_SPAWN_EGG = registerEgg("goblin_peon_spawn_egg", TEMonsterEntities.GOBLIN_PEON, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOBLIN_WARRIOR_SPAWN_EGG = registerEgg("goblin_warrior_spawn_egg", TEMonsterEntities.GOBLIN_WARRIOR, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOBLIN_THIEF_SPAWN_EGG = registerEgg("goblin_thief_spawn_egg", TEMonsterEntities.GOBLIN_THIEF, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOBLIN_SCOUT_SPAWN_EGG = registerEgg("goblin_scout_spawn_egg", TEMonsterEntities.GOBLIN_SCOUT, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ANGER_GOBLIN_SPAWN_EGG = registerEgg("anger_goblin_spawn_egg", TEMonsterEntities.ANGER_GOBLIN, 0xffffff);

    /* ********肉后怪物********* */
    public static final DeferredItem<SpawnEggItem> WYVERN_SPAWN_EGG = registerEgg("wyvern_spawn_egg", TEMonsterEntities.WYVERN, 0xffffff);
    public static final DeferredItem<SpawnEggItem> PIXIE_SPAWN_EGG = registerEgg("pixie_spawn_egg", TEMonsterEntities.PIXIE, 0xffffff);
    public static final DeferredItem<SpawnEggItem> POSSESS_ARMOR_SPAWN_EGG = registerEgg("possess_armor_spawn_egg", TEMonsterEntities.POSSESS_ARMOR, 0xffffff);
    public static final DeferredItem<SpawnEggItem> WRAITH_SPAWN_EGG = registerEgg("wraith_spawn_egg", TEMonsterEntities.WRAITH, 0xffffff);

    // 宝箱怪
    public static final DeferredItem<SpawnEggItem> WOODEN_MIMIC_SPAWN_EGG = registerEgg("wooden_mimic_spawn_egg", TEMonsterEntities.WOODEN_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOLDEN_MIMIC_SPAWN_EGG = registerEgg("golden_mimic_spawn_egg", TEMonsterEntities.GOLDEN_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SHADOW_MIMIC_SPAWN_EGG = registerEgg("shadow_mimic_spawn_egg", TEMonsterEntities.SHADOW_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ICE_MIMIC_SPAWN_EGG = registerEgg("ice_mimic_spawn_egg", TEMonsterEntities.ICE_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CRIMSON_MIMIC_SPAWN_EGG = registerEgg("crimson_mimic_spawn_egg", TEMonsterEntities.CRIMSON_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CORRUPT_MIMIC_SPAWN_EGG = registerEgg("corrupt_mimic_spawn_egg", TEMonsterEntities.CORRUPT_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HALLOWED_MIMIC_SPAWN_EGG = registerEgg("hallowed_mimic_spawn_egg", TEMonsterEntities.HALLOWED_MIMIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> JUNGLE_MIMIC_SPAWN_EGG = registerEgg("jungle_mimic_spawn_egg", TEMonsterEntities.JUNGLE_MIMIC, 0xffffff);

    public static final DeferredItem<SpawnEggItem> MUMMY_SPAWN_EGG = registerEgg("mummy_spawn_egg", TEMonsterEntities.MUMMY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DARK_MUMMY_SPAWN_EGG = registerEgg("dark_mummy_spawn_egg", TEMonsterEntities.DARK_MUMMY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BLOOD_MUMMY_SPAWN_EGG = registerEgg("blood_mummy_spawn_egg", TEMonsterEntities.BLOOD_MUMMY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> LIGHT_MUMMY_SPAWN_EGG = registerEgg("light_mummy_spawn_egg", TEMonsterEntities.LIGHT_MUMMY, 0xffffff);

    public static final DeferredItem<SpawnEggItem> DARK_LAMIA_SPAWN_EGG = registerEgg("dark_lamia_spawn_egg", TEMonsterEntities.DARK_LAMIA, 0xffffff);
    public static final DeferredItem<SpawnEggItem> LIGHT_LAMIA_SPAWN_EGG = registerEgg("light_lamia_spawn_egg", TEMonsterEntities.LIGHT_LAMIA, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GHOUL_SPAWN_EGG = registerEgg("ghoul_spawn_egg", TEMonsterEntities.GHOUL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> TAINTED_GHOUL_SPAWN_EGG = registerEgg("tainted_ghoul_spawn_egg", TEMonsterEntities.TAINTED_GHOUL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> VILE_GHOUL_SPAWN_EGG = registerEgg("vile_ghoul_spawn_egg", TEMonsterEntities.VILE_GHOUL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DREAMER_GHOUL_SPAWN_EGG = registerEgg("dreamer_ghoul_spawn_egg", TEMonsterEntities.DREAMER_GHOUL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SAND_POACHER_SPAWN_EGG = registerEgg("sand_poacher_spawn_egg", TEMonsterEntities.SAND_POACHER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DERPLING_SPAWN_EGG = registerEgg("derpling_spawn_egg", TEMonsterEntities.DERPLING, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HERPLING_SPAWN_EGG = registerEgg("herpling_spawn_egg", TEMonsterEntities.HERPLING, 0xffffff);


    // NPC
    public static final DeferredItem<SpawnEggItem> GUIDE_SPAWN_EGG = registerEgg("guide_spawn_egg", TENpcEntities.GUIDE, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DEMOLITIONIST_SPAWN_EGG = registerEgg("demolitionist_spawn_egg", TENpcEntities.DEMOLITIONIST, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GOBLIN_TINKERER_SPAWN_EGG = registerEgg("goblin_tinkerer_spawn_egg", TENpcEntities.GOBLIN_TINKERER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ARMS_DEALER_SPAWN_EGG = registerEgg("arms_dealer_spawn_egg", TENpcEntities.ARMS_DEALER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> NURSE_SPAWN_EGG = registerEgg("nurse_spawn_egg", TENpcEntities.NURSE, 0xffffff);
    public static final DeferredItem<SpawnEggItem> MERCHANT_SPAWN_EGG = registerEgg("merchant_spawn_egg", TENpcEntities.MERCHANT, 0xffffff);
    public static final DeferredItem<SpawnEggItem> PAINTER_SPAWN_EGG = registerEgg("painter_spawn_egg", TENpcEntities.PAINTER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DRYAD_SPAWN_EGG = registerEgg("dryad_spawn_egg", TENpcEntities.DRYAD, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DYE_TRADER_SPAWN_EGG = registerEgg("dye_trader_spawn_egg", TENpcEntities.DYE_TRADER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ANGLER_SPAWN_EGG = registerEgg("angler_spawn_egg", TENpcEntities.ANGLER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> OLD_MAN_SPAWN_EGG = registerEgg("old_man_spawn_egg", TENpcEntities.OLD_MAN, 0xffffff);
    public static final DeferredItem<SpawnEggItem> MECHANIC_SPAWN_EGG = registerEgg("mechanic_spawn_egg", TENpcEntities.MECHANIC, 0xffffff);
    public static final DeferredItem<SpawnEggItem> TRAVELING_MERCHANT_SPAWN_EGG = registerEgg("traveling_merchant_spawn_egg", TENpcEntities.TRAVELING_MERCHANT, 0xffffff);
    public static final DeferredItem<SpawnEggItem> WITCH_DOCTOR_SPAWN_EGG = registerEgg("witch_doctor_spawn_egg", TENpcEntities.WITCH_DOCTOR, 0xffffff);
    public static final DeferredItem<SpawnEggItem> PARTY_GIRL_SPAWN_EGG = registerEgg("party_girl_spawn_egg", TENpcEntities.PARTY_GIRL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CLOTHIER_SPAWN_EGG = registerEgg("clothier_spawn_egg", TENpcEntities.CLOTHIER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> ZOOLOGIST_SPAWN_EGG = registerEgg("zoologist_spawn_egg", TENpcEntities.ZOOLOGIST, 0xffffff);
    public static final DeferredItem<SpawnEggItem> TRUFFLE_SPAWN_EGG = registerEgg("truffle_spawn_egg", TENpcEntities.TRUFFLE, 0xffffff);


    // 动物
    public static final DeferredItem<SpawnEggItem> SQUIRREL_SPAWN_EGG = registerEgg("squirrel_spawn_egg", TEAnimals.SQUIRREL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> JEWEL_SQUIRREL_SPAWN_EGG = registerEgg("jewel_squirrel_spawn_egg", TEAnimals.JEWEL_SQUIRREL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BUNNY_SPAWN_EGG = registerEgg("bunny_spawn_egg", TEAnimals.BUNNY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> JEWEL_BUNNY_SPAWN_EGG = registerEgg("jewel_bunny_spawn_egg", TEAnimals.JEWEL_BUNNY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> EXPLOSIVE_BUNNY_SPAWN_EGG = registerEgg("explosive_bunny_spawn_egg", TEAnimals.EXPLOSIVE_BUNNY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DUCK_SPAWN_EGG = registerEgg("duck_spawn_egg", TEAnimals.DUCK, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BIRD_SPAWN_EGG = registerEgg("bird_spawn_egg", TEAnimals.BIRD, 0xffffff);
    public static final DeferredItem<SpawnEggItem> BLUE_JAY_SPAWN_EGG = registerEgg("blue_jay_spawn_egg", TEAnimals.BLUE_JAY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CARDINAL_SPAWN_EGG = registerEgg("cardinal_spawn_egg", TEAnimals.CARDINAL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> CRAB_SPAWN_EGG = registerEgg("crap_spawn_egg", TEAnimals.CRAB, 0xffffff);

    // 昆虫
    public static final DeferredItem<SpawnEggItem> GLOWING_SNAIL_SPAWN_EGG = registerEgg("glowing_snail_spawn_egg", TEAnimals.GLOWING_SNAIL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GRUBBY_SPAWN_EGG = registerEgg("grubby_spawn_egg", TEAnimals.GRUBBY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> MAGGOT_SPAWN_EGG = registerEgg("maggot_spawn_egg", TEAnimals.MAGGOT, 0xffffff);
    public static final DeferredItem<SpawnEggItem> MAGMA_SNAIL_SPAWN_EGG = registerEgg("magma_snail_spawn_egg", TEAnimals.MAGMA_SNAIL, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SLUGGY_SPAWN_EGG = registerEgg("sluggy_spawn_egg", TEAnimals.SLUGGY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SNAIL_SPAWN_EGG = registerEgg("snail_spawn_egg", TEAnimals.SNAIL, 0xffffff);

    public static final DeferredItem<SpawnEggItem> BUTTERFLY_SPAWN_EGG = registerEgg("butterfly_spawn_egg", TEAnimals.BUTTERFLY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> HELL_BUTTERFLY_SPAWN_EGG = registerEgg("hell_butterfly_spawn_egg", TEAnimals.HELL_BUTTERFLY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> PRISMATIC_LACEWING_SPAWN_EGG = registerEgg("prismatic_lacewing_spawn_egg", TEAnimals.PRISMATIC_LACEWING, 0xffffff);
    public static final DeferredItem<SpawnEggItem> DRAGONFLY_SPAWN_EGG = registerEgg("dragonfly_spawn_egg", TEAnimals.DRAGONFLY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> FAIRY_SPAWN_EGG = registerEgg("fairy_spawn_egg", TEAnimals.FAIRY, 0xffffff);
    public static final DeferredItem<SpawnEggItem> FEALING_SPAWN_EGG = registerEgg("fealing_spawn_egg", TEAnimals.FEALING, 0xffffff);
    public static final DeferredItem<SpawnEggItem> GRASSHOPPER_SPAWN_EGG = registerEgg("grasshopper_spawn_egg", TEAnimals.GRASSHOPPER, 0xffffff);
    public static final DeferredItem<SpawnEggItem> LADYBUG_SPAWN_EGG = registerEgg("ladybug_spawn_egg", TEAnimals.LADYBUG, 0xffffff);
    public static final DeferredItem<SpawnEggItem> SCORPION_SPAWN_EGG = registerEgg("scorpion_spawn_egg", TEAnimals.SCORPION, 0xffffff);
    public static final DeferredItem<SpawnEggItem> WORM_SPAWN_EGG = registerEgg("worm_spawn_egg", TEAnimals.WORM, 0xffffff);


    public static DeferredItem<SpawnEggItem> registerEgg(String name, Supplier<? extends EntityType<? extends Mob>> entityType, int primaryColor, int secondaryColor) {
        return ITEMS.register(name, () -> new DeferredSpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties()));
    }

    public static DeferredItem<SpawnEggItem> registerEgg(String name, Supplier<? extends EntityType<? extends Mob>> entityType, int primaryColor) {
        return ITEMS.register(name, () -> new DeferredSpawnEggItem(entityType, primaryColor, 0xffffff, new Item.Properties()));
    }
}
