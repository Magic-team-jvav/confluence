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
    private static ModConfigSpec.BooleanValue ITEM_GROUPS;

    public static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Paints");
        PAINTS_REPLACE_TEXTURE = builder.define("paintsReplaceTexture", false);
        BANNED_MOD_FOR_PAINTS = builder.defineListAllowEmpty("bannedModForPaints", () -> DEFAULT_BANNED_MOD, () -> "modid", o -> o instanceof String s && !s.contains(":"));
        builder.pop();

        FORCE_ALLOW_WIP_ITEMS_DISPLAY_IN_CREATIVE_MODE_TAB = builder.define("forceAllowWipItemsDisplayInCreativeModeTab", false);
        BREWING_STAND_RECIPE = builder.define("brewingStandRecipe", true);
        ITEM_GROUPS = builder.define("itemGroups", true);

        container.registerConfig(ModConfig.Type.STARTUP, builder.build());
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

    public static boolean itemGroups() {
        return ITEM_GROUPS != null && ITEM_GROUPS.get();
    }
}
