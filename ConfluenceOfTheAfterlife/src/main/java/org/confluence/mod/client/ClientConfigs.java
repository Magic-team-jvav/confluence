package org.confluence.mod.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfigs {
    public static boolean showWindParticles = true;
    public static boolean terraStyleHealth = true;
    public static boolean leftEffectIcon = true;
    public static boolean hurtRedOverlay = true;
    public static boolean bloodyEffect = true;
    public static boolean damageIndicator = true;

    private static ModConfigSpec.BooleanValue SHOW_WIND_PARTICLES;
    private static ModConfigSpec.BooleanValue TERRA_STYLE_HEALTH;
    private static ModConfigSpec.BooleanValue LEFT_EFFECT_ICON;
    private static ModConfigSpec.BooleanValue HURT_RED_OVERLAY;

    public static void onLoad() {
        showWindParticles = SHOW_WIND_PARTICLES.get();
        terraStyleHealth = TERRA_STYLE_HEALTH.get();
        leftEffectIcon = LEFT_EFFECT_ICON.get();
        hurtRedOverlay = HURT_RED_OVERLAY.get();
    }

    public static void register(ModContainer container) {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        SHOW_WIND_PARTICLES = BUILDER.define("showWindParticles", true);
        TERRA_STYLE_HEALTH = BUILDER.push("HUD").define("drawVanillaHealth", true);
        LEFT_EFFECT_ICON = BUILDER.define("leftEffectIcon", true);
        HURT_RED_OVERLAY = BUILDER.pop().push("Entity").define("hurtRedOverlay", true);
        container.registerConfig(ModConfig.Type.CLIENT, BUILDER.build());
    }
}
