package org.confluence.terraentity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec.ConfigValue<Boolean> BOSS_CLEAR_WHEN_NO_TARGET  = BUILDER.comment("When a boss has no target, should it be cleared?").define("boss_clear_when_no_target", false);
    public static ModConfigSpec.ConfigValue<Float> BOSS_ATTRIBUTES_MULTIPLIER_HEALTH  = BUILDER.comment("Multiplier for boss attributes health. > 0").define("boss_attributes_multiplier_health", 0.5F);
    public static ModConfigSpec.ConfigValue<Float> BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE  = BUILDER.comment("Multiplier for boss attributes damage. > 0").define("boss_attributes_multiplier_damage", 0.7F);

    public static boolean bossClearWhenNoTarget;
    public static float boss_attributes_multiplier_health;
    public static float boss_attributes_multiplier_damage;

    public static void init() {
        bossClearWhenNoTarget = BOSS_CLEAR_WHEN_NO_TARGET.get();
        boss_attributes_multiplier_health = BOSS_ATTRIBUTES_MULTIPLIER_HEALTH.get();
        boss_attributes_multiplier_damage = BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE.get();
    }
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        init();
    }
    public static final ModConfigSpec SPEC = BUILDER.build();

}
