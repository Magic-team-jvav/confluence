package org.confluence.mod;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class StartupConfigs {
    public static ModConfigSpec.BooleanValue PAINTS_REPLACE_TEXTURE;
    public static ModConfigSpec.ConfigValue<List<? extends String>> BANNED_MOD_FOR_PAINTS;

    public static void register(ModContainer container) {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.push("Paints");
        PAINTS_REPLACE_TEXTURE = BUILDER.define("paintsReplaceTexture", false);
        BANNED_MOD_FOR_PAINTS = BUILDER.defineListAllowEmpty("bannedModForPaints", () -> List.of(
                "integrateddynamics", "ae2", "refinedstorage", "create", "mekanism", "immersiveengineering", "enderio"
        ), () -> "modid", o -> o instanceof String s && !s.contains(":"));
        BUILDER.pop();

        container.registerConfig(ModConfig.Type.STARTUP, BUILDER.build());
    }
}
