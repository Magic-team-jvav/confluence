package org.confluence.mod;

import org.mesdag.portlib.config.PortConfigSpec;

import java.util.List;

public final class StartupConfigs {
    public static final List<String> DEFAULT_BANNED_MOD = List.of();
    private static PortConfigSpec.BooleanValue PAINTS_REPLACE_TEXTURE;
    private static PortConfigSpec.ListValue<String> BANNED_MOD_FOR_PAINTS;
    private static PortConfigSpec.BooleanValue FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB;
    private static PortConfigSpec.BooleanValue BREWING_STAND_RECIPE;

    public static void register() {
        PortConfigSpec.Builder builder = PortConfigSpec.builder(Confluence.MODID);

        builder.push("Paints");
        PAINTS_REPLACE_TEXTURE = builder.define("paintsReplaceTexture", false);
        BANNED_MOD_FOR_PAINTS = builder.defineList("bannedModForPaints", DEFAULT_BANNED_MOD, o -> o instanceof String s && !s.contains(":"));
        builder.pop();

        FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB = builder.define("forceAllowWipItemsDisplayInCreativeModeTab", false);
        BREWING_STAND_RECIPE = builder.define("brewingStandRecipe", true);

        PortConfigSpec spec = builder.build();
        spec.load();
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
