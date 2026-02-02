package org.confluence.terra_curio;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class TCStartupConfigs {
    public static ModConfigSpec.ConfigValue<List<? extends String>> ATTRIBUTE_REPLACE;
    private static ModConfigSpec.BooleanValue SHOES_EXTRA_STEP_HEIGHT;

    public static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ATTRIBUTE_REPLACE = builder.defineListAllowEmpty("attributeReplacements", () -> List.of(
                "crit_chance = terra_curio:generic.crit_chance",
                "ranged_damage = terra_curio:generic.ranged_damage",
                "dodge_chance = terra_curio:generic.dodge_chance",
                "magic_damage = terra_curio:generic.magic_damage",
                "armor_penetration = terra_curio:generic.armor_penetration"
        ), () -> "", o -> true);
        SHOES_EXTRA_STEP_HEIGHT = builder.define("shoesExtraStepHeight", true);
        container.registerConfig(ModConfig.Type.STARTUP, builder.build());
    }

    public static boolean shoesExtraStepHeight() {
        return SHOES_EXTRA_STEP_HEIGHT == null || SHOES_EXTRA_STEP_HEIGHT.get();
    }
}
