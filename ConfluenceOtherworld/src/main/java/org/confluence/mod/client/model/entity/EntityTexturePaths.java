package org.confluence.mod.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

public final class EntityTexturePaths {
    private static final String ROOT = "textures/entity/";

    private EntityTexturePaths() {}

    public static ResourceLocation resolve(ResourceLocation texture) {
        String path = texture.getPath();
        if (!Confluence.MODID.equals(texture.getNamespace()) || !path.startsWith(ROOT) || !path.endsWith(".png"))
            return texture;
        String name = path.substring(ROOT.length(), path.length() - ".png".length());
        String directory = switch (name) {
            case "blood_crawler" -> "monster/evil";
            case "eye_of_cthulhu_gears_2", "servant_of_cthulhu", "the_hungry", "visual_neuron" ->
                    "boss";
            case "enemy_banner", "life_crystal_entity", "step_stool", "target_dummy",
                 "waffle" -> "model";
            case "angry_tumbler", "basilisk", "dark_lamia", "desert_spirit", "light_lamia" ->
                    "monster/desert";
            case "gastropod", "pixie", "unicorn" -> "monster/hallow";
            case "derpling", "giant_tortoise" -> "monster/jungle";
            case "ice_tortoise" -> "monster/snow";
            case "nymph", "nymph_dark", "nymph_dark_blood" -> "monster/cavern";
            case "gnome", "werewolf" -> "monster/surface";
            case "rune_wizard", "tim" -> "monster/wizard";
            case "goblin_warlock", "shadowflame_apparition" -> "goblin";
            case "harpy", "wyvern" -> "monster/sky";
            case "angler_fish", "arapaima", "blood_feeder", "blood_jelly",
                 "crystal_thresher", "flying_fish", "fungo_fish", "piranha",
                 "wandering_eye_fish" -> "monster/aquatic";
            case "cave_bat", "giant_bat", "giant_flying_fox", "hell_bat",
                 "ice_bat", "illuminant_bat", "jungle_bat", "lava_bat",
                 "spore_bat" -> "monster/bat";
            case "possess_armor_void_vessel", "possessed_armor" -> "monster/construct";
            case "demon", "fire_imp", "red_devil", "voodoo_demon" -> "monster/demon";
            case "angry_nimbus", "chaos_elemental", "granite_elemental", "granite_golem",
                 "ice_elemental", "ice_golem", "meteor_head", "rock_golem",
                 "snow_flinx" -> "monster/elemental";
            case "blood_tumors", "bone_biter", "corrupt_goldfish", "corrupt_penguin",
                 "corruptor", "crimera", "decayeder", "drippler",
                 "eater_of_souls", "evil_bunny", "face_monster", "flesh_reaver",
                 "herpling", "slimer", "vicious_goldfish", "vicious_penguin" -> "monster/evil";
            case "antlion", "antlion_charger", "antlion_larva", "antlion_swarmer",
                 "hornet", "little_hornet", "moss_hornet", "sand_poacher" -> "monster/insect";
            case "brain_scrambler", "gigazapper", "gigazapper_af2", "gray_grunt",
                 "martian_engineer", "martian_officer", "martian_probe", "martian_probe_alert",
                 "martian_walker", "ray_gunner", "scutlix", "tesla_turret" -> "monster/martian";
            case "corrupt_mimic", "crimson_mimic", "golden_mimic", "hallowed_mimic",
                 "ice_mimic", "jungle_mimic", "shadow_mimic", "water_bolt_mimic",
                 "wooden_mimic" -> "monster/mimic";
            case "pirate_captain", "pirate_corsair", "pirate_crossbower", "pirate_deadeye",
                 "pirate_deckhand", "pirate_parrot", "pirates_curse" -> "monster/pirate";
            case "angry_dandelion", "clinger", "clinger_segment", "fungi_bulb",
                 "fungi_bulb_segment", "giant_fungi_bulb", "giant_fungi_bulb_segment", "man_eater",
                 "snatcher" -> "monster/plant";
            case "anger_bones", "armored_skeleton", "armored_viking", "base_bones",
                 "big_anger_bones", "big_helmet_anger_bones", "big_muscle_anger_bones",
                 "blood_mummy",
                 "blood_zombie", "cursed_skull", "dark_caster", "dark_mummy",
                 "doctor_bones", "dreamer_ghoul", "dungeon_spirit", "ghost",
                 "ghoul", "hat_spore_zombie", "hoplite", "icy_merman",
                 "light_mummy", "mummy", "short_bones", "spore_skeleton",
                 "spore_zombie", "tainted_ghoul", "the_bride", "the_groom",
                 "undead_viking", "vile_ghoul", "zombie_merman" -> "monster/undead";
            case "bone_serpent", "bone_serpent_segment", "bone_serpent_tail", "devourer",
                 "devourer_segment", "devourer_tail", "digger", "digger_segment",
                 "digger_tail", "giant_worm", "giant_worm_segment", "giant_worm_tail",
                 "leech", "leech_segment", "leech_tail", "tomb_crawler",
                 "tomb_crawler_segment", "tomb_crawler_tail", "wither_bone_serpent",
                 "wither_bone_serpent_segment",
                 "wither_bone_serpent_tail", "world_feeder", "world_feeder_segment",
                 "world_feeder_tail" -> "monster/worm";
            case "bee_projectile", "blood_cloud_projectile", "blood_rain_projectile",
                 "bloody_spore",
                 "bone_thrown_knive_projectile", "crystal_vile_shard_projectile",
                 "demon_scythe_projectile", "dungeon_demon_bone_projectile",
                 "enchanted_sword_projectile", "forbidden_sign", "frost_daggerfish_projectile",
                 "hallow_star_projectile",
                 "hurtnado_projectile", "ice_blade_sword_projectile", "lights_bane",
                 "magic_dagger_projectile",
                 "normal_boulder", "rain_cloud_projectile", "rain_projectile",
                 "rolling_cactus_spike",
                 "shuriken_projectile", "skull_projectile", "spiky_ball_projectile",
                 "star_fury_projectile",
                 "storm_spear_shot_projectile", "super_spiky_ball_projectile",
                 "thrown_knive_projectile", "vilethron_projectile" -> "projectile";
            case "sand_shark", "shark" -> "shark";
            case "wall_creeper" -> "spider";
            case "windy_balloon" -> "windy_balloon";
            default -> "";
        };
        return directory.isEmpty() ? texture : texture.withPath(ROOT + directory + "/" + name + ".png");
    }
}
