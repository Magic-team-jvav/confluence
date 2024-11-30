package org.confluence.mod.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class CommonConfigs {
    public static ModConfigSpec.BooleanValue DROP_MONEY;
    public static ModConfigSpec.IntValue DEFAULT_RESPAWN_TIME_MIN;
    public static ModConfigSpec.IntValue DEFAULT_RESPAWN_TIME_MAX;
    public static ModConfigSpec.IntValue BOSS_RESPAWN_TIME_MIN;
    public static ModConfigSpec.IntValue BOSS_RESPAWN_TIME_MAX;

    public static void onLoad() {

    }

    public static void register(ModContainer container) {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        DROP_MONEY = BUILDER
                .comment("Determines if entity drops money after death")
                .define("dropsMoney", true);
        DEFAULT_RESPAWN_TIME_MIN = BUILDER.comment(
                "The min value of the default respawn time"
        ).defineInRange("defaultRespawnTimeMin", 3, 0, Integer.MAX_VALUE);
        DEFAULT_RESPAWN_TIME_MAX = BUILDER.comment(
                "The max value of the default respawn time"
        ).defineInRange("defaultRespawnTimeMax", 8, 0, Integer.MAX_VALUE);
        BOSS_RESPAWN_TIME_MIN = BUILDER.comment(
                "The min respawn time when the boss is present"
        ).defineInRange("bossRespawnTimeMin", 9, 0, Integer.MAX_VALUE);
        BOSS_RESPAWN_TIME_MAX = BUILDER.comment(
                "The max respawn time when the boss is present"
        ).defineInRange("bossRespawnTimeMax", 18, 0, Integer.MAX_VALUE);
        container.registerConfig(ModConfig.Type.COMMON, BUILDER.build());
    }
}
