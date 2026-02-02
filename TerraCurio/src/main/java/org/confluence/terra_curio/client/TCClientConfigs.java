package org.confluence.terra_curio.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class TCClientConfigs {
    private static ModConfigSpec.BooleanValue PLAY_SHOES_SOUND;
    private static ModConfigSpec.DoubleValue SHOES_SOUND_VOLUME;
    private static ModConfigSpec.BooleanValue SHOW_SHOES_PARTICLE;
    private static ModConfigSpec.BooleanValue SPEED_UP;

    private static ModConfigSpec.BooleanValue AUTO_ATTACK;
    private static ModConfigSpec.BooleanValue RIGHT_CLICK_DELAY;

    private static ModConfigSpec.DoubleValue INFORMATION_HUD_TOP;
    private static ModConfigSpec.BooleanValue INFORMATION_HUD_LEFT;

    public static boolean playShoesSound = true;
    public static float shoesSoundVolume = 1.0F;
    public static boolean showShoesParticle = true;
    public static boolean speedUp = true;

    public static boolean autoAttack = true;
    public static boolean rightClickDelay = true;

    public static float informationHudTop = 0.5F;
    public static boolean informationIsLeft = false;

    public static void onLoad() {
        playShoesSound = PLAY_SHOES_SOUND.get();
        shoesSoundVolume = SHOES_SOUND_VOLUME.get().floatValue();
        showShoesParticle = SHOW_SHOES_PARTICLE.get();
        speedUp = SPEED_UP.get();

        autoAttack = AUTO_ATTACK.get();
        rightClickDelay = RIGHT_CLICK_DELAY.get();

        informationHudTop = INFORMATION_HUD_TOP.get().floatValue();
        informationIsLeft = INFORMATION_HUD_LEFT.get();
    }

    public static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        PLAY_SHOES_SOUND = builder.push("Speed Shoes").define("playSound", true);
        SHOES_SOUND_VOLUME = builder.defineInRange("shoesSoundVolume", 1.0, 0.0, 1.0);
        SHOW_SHOES_PARTICLE = builder.define("showParticle", true);
        SPEED_UP = builder.define("speedUp", true);

        AUTO_ATTACK = builder.pop().push("Functional").define("autoAttack", true);
        RIGHT_CLICK_DELAY = builder.define("rightClickDelay", true);

        INFORMATION_HUD_TOP = builder.pop().push("Information HUD").comment("finalTop = screenHeight * top").defineInRange("top", 0.5, 0.0, 1.0);
        INFORMATION_HUD_LEFT = builder.comment("left or right").define("isLeft", false);
        container.registerConfig(ModConfig.Type.CLIENT, builder.build());
    }
}
