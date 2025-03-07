package org.confluence.mod.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public final class CommonConfigs {
    public static BooleanValue DROP_MONEY;
    public static BooleanValue AUTO_STACK_GELS_COLOR;
    public static BooleanValue FLETCHING_MENU;
    public static BooleanValue SHIMMER_DECOMPOSE;
    private static IntValue FALLING_STAR_FREQUENCY;
    public static BooleanValue RETURN_POTION_GLASS_BOTTLE;

    public static BooleanValue SHOW_MONEY_DROPS;
    public static IntValue DEFAULT_RESPAWN_TIME_MIN;
    public static IntValue DEFAULT_RESPAWN_TIME_MAX;
    public static IntValue BOSS_RESPAWN_TIME_MIN;
    public static IntValue BOSS_RESPAWN_TIME_MAX;

    public static long fallingStarFrequency = 2400;

    public static void onLoad() {
        fallingStarFrequency = FALLING_STAR_FREQUENCY.get();
    }

    public static void register(ModContainer container) {
        Builder BUILDER = new Builder();

        BUILDER.push("Gameplay");
        DROP_MONEY = BUILDER.comment("Determines if entity drops money after death").define("dropsMoney", true);
        AUTO_STACK_GELS_COLOR = BUILDER.comment("Auto stack when pickup colorful gels").define("autoStackGelsColor", true);
        FLETCHING_MENU = BUILDER.comment("Allows you to open menu through right click the Fletching Table").define("fletchingMenu", true);
        SHIMMER_DECOMPOSE = BUILDER.comment("Allows Shimmer fluid to decomposing items").define("shimmer_decompose", true);
        FALLING_STAR_FREQUENCY = BUILDER.comment("Determines how long the falling star spawning next time").defineInRange("fallingStarFrequency", 2400, 20, 20000);
        RETURN_POTION_GLASS_BOTTLE = BUILDER.define("returnPotionGlassBottle", true);
        BUILDER.pop();

        BUILDER.push("PlayerDeath");
        SHOW_MONEY_DROPS = BUILDER.define("showMoneyDrops", true);
        DEFAULT_RESPAWN_TIME_MIN = BUILDER.comment("The min value of the default respawn time").defineInRange("defaultRespawnTimeMin", 3, 0, Integer.MAX_VALUE);
        DEFAULT_RESPAWN_TIME_MAX = BUILDER.comment("The max value of the default respawn time").defineInRange("defaultRespawnTimeMax", 8, 0, Integer.MAX_VALUE);
        BOSS_RESPAWN_TIME_MIN = BUILDER.comment("The min respawn time when the boss is present").defineInRange("bossRespawnTimeMin", 9, 0, Integer.MAX_VALUE);
        BOSS_RESPAWN_TIME_MAX = BUILDER.comment("The max respawn time when the boss is present").defineInRange("bossRespawnTimeMax", 18, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        container.registerConfig(ModConfig.Type.COMMON, BUILDER.build());
    }
}
