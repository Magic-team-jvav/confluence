package org.confluence.terraentity;

import net.neoforged.neoforge.common.ModConfigSpec;


public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec.ConfigValue<Boolean> BOSS_CLEAR_WHEN_NO_TARGET  = BUILDER.comment("When a boss has no target, should it be cleared?").define("boss_clear_when_no_target", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

}
