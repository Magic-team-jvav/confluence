package org.confluence.terraentity.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static ModConfigSpec.BooleanValue BOSS_CLEAR_WHEN_NO_TARGET;
    public static ModConfigSpec.DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_HEALTH;
    public static ModConfigSpec.DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE;
    private static ModConfigSpec.BooleanValue BOSS_NO_PHYSICS;
    public static ModConfigSpec.BooleanValue BOSS_LEAVE_ON_DAY;
    public static ModConfigSpec.BooleanValue BOSS_KEEP_WANDERING;

//    public static ModConfigSpec.BooleanValue RESPAWN_PROTECT;


    public static ModConfigSpec.BooleanValue ENHANCE_ALL_MONSTER;
    public static ModConfigSpec.DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_HEALTH;
    public static ModConfigSpec.DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_DAMAGE;

    public static ModConfigSpec.BooleanValue SPAWN_WITHOUT_LIGHT;

    public static ModConfigSpec.DoubleValue CHANCE_TO_SPAWN_SLIME_ON_ZOMBIE_HEAD;
    public static ModConfigSpec.DoubleValue ENEMY_SPAWN_CHANCE;
    public static ModConfigSpec.BooleanValue ENEMY_SPAWN_CHANCE_APPLY_ALL;

    public static ModConfigSpec.IntValue BEHAVIOR_TREE_WEB_VIEWER_SERVER_PORT;


    private static ModConfigSpec spec;

    public static ModConfigSpec init(){
        final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BOSS_CLEAR_WHEN_NO_TARGET = BUILDER
                .comment("When a boss has no target, should it be cleared?")
                .define("boss_clear_when_no_target", true);
        BOSS_ATTRIBUTES_MULTIPLIER_HEALTH = BUILDER
                .comment("Multiplier for boss attributes health.")
                .defineInRange("boss_attributes_multiplier_health", 1F, 0.0625f, 10f);
        BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE = BUILDER
                .comment("Multiplier for boss attributes damage.")
                .defineInRange("boss_attributes_multiplier_damage", 1F, 0.0625f, 10f);

        BOSS_NO_PHYSICS = BUILDER
                .comment("Should the boss have no physics? Only for some bosses.")
                .define("boss_no_physics", true);

        BOSS_LEAVE_ON_DAY = BUILDER
                .comment("Should the boss leave on day? Only for some bosses.")
                .define("boss_leave_on_day", false);

        BOSS_KEEP_WANDERING = BUILDER
                .comment("Should bosses random stroll when have no target? ")
                .define("boss_keep_wandering", true);

//        RESPAWN_PROTECT = BUILDER
//                .comment("Should players be protected from respawning?.If true, nearby bosses will be discard")
//                .define("respawn_protect", true);

        ENHANCE_ALL_MONSTER = BUILDER
                .comment("Should all monsters be enhanced?\nIf false, only specific monsters in this mod.")
                .define("enhance_all_monster", false);

        MONSTER_ATTRIBUTES_MULTIPLIER_HEALTH = BUILDER
                .comment("Multiplier for monster attributes health.")
                .defineInRange("monster_attributes_multiplier_health", 1F, 0.0625f, 100f);
        MONSTER_ATTRIBUTES_MULTIPLIER_DAMAGE = BUILDER
                .comment("Multiplier for monster attributes damage.")
                .defineInRange("monster_attributes_multiplier_damage", 1F, 0.0625f, 100f);

        SPAWN_WITHOUT_LIGHT = BUILDER
                .comment("Should monsters spawn without light?")
                .define("spawn_without_light", true);

        CHANCE_TO_SPAWN_SLIME_ON_ZOMBIE_HEAD = BUILDER
                .comment("Chance to spawn slime on zombie head.")
                .defineInRange("chance_to_spawn_slime_on_zombie_head", 0.05, 0, 1);
        ENEMY_SPAWN_CHANCE = BUILDER
                .comment("Chance to spawn a monster.")
                .defineInRange("enemy_spawn_chance", 1D, 0, 1);
        ENEMY_SPAWN_CHANCE_APPLY_ALL = BUILDER
                .comment("Should the chance to spawn a monster apply to all monsters?")
                .define("enemy_spawn_chance_apply_all", false);

        BEHAVIOR_TREE_WEB_VIEWER_SERVER_PORT = BUILDER
                .comment("Port for behavior web viewer.")
                .defineInRange("behavior_tree_web_viewer_server_port", 59160, 1024, 65535);
        return spec = BUILDER.build();
    }

    public static boolean bossNoPhysics() {
        if (spec == null) return true;
        if (spec.isLoaded()) return BOSS_NO_PHYSICS.get();
        return true;
    }
}
