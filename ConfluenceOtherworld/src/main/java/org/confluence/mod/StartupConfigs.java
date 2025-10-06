package org.confluence.mod;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class StartupConfigs {
    public static final List<String> DEFAULT_BANNED_MOD = List.of();
    private static ModConfigSpec.BooleanValue PAINTS_REPLACE_TEXTURE;
    private static ModConfigSpec.ConfigValue<List<? extends String>> BANNED_MOD_FOR_PAINTS;
    private static ModConfigSpec.BooleanValue FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB;
    private static ModConfigSpec.BooleanValue BREWING_STAND_RECIPE;

    public static void register(ModContainer container) {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.push("Paints");
        PAINTS_REPLACE_TEXTURE = BUILDER.define("paintsReplaceTexture", false);
        BANNED_MOD_FOR_PAINTS = BUILDER.defineListAllowEmpty("bannedModForPaints", () -> DEFAULT_BANNED_MOD, () -> "modid", o -> o instanceof String s && !s.contains(":"));
        BUILDER.pop();

        FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB = BUILDER.define("forceAllowWipItemsDisplayInCreativeModeTab", false);
        BREWING_STAND_RECIPE = BUILDER.define("brewingStandRecipe", true);

        container.registerConfig(ModConfig.Type.STARTUP, BUILDER.build());
    }

    public static boolean paintsReplaceTexture() {
        return PAINTS_REPLACE_TEXTURE != null && PAINTS_REPLACE_TEXTURE.get();
    }

    public static List<? extends String> bannedModForPaints() {
        return BANNED_MOD_FOR_PAINTS == null ? DEFAULT_BANNED_MOD : BANNED_MOD_FOR_PAINTS.get();
    }

    public static boolean forceAllowWipItemsDisplayInCreativeModeTab() {
        return FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB != null && FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB.get();
    }

    public static boolean brewingStandRecipe() {
        return BREWING_STAND_RECIPE == null || BREWING_STAND_RECIPE.get();
    }
}
