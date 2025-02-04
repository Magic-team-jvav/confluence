package org.confluence.mod.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.confluence.mod.client.gui.hud.TerraStyleHud;

public final class ClientConfigs {
    public static int showWindParticles = 90;

    public static boolean terraStyleHealth = true;
    public static TerraStyleHud.Health healthStyle = TerraStyleHud.Health.LEGACY;
    public static TerraStyleHud.Mana manaStyle = TerraStyleHud.Mana.LEGACY;
    public static boolean terraStyleArmor = true;
    public static TerraStyleHud.Armor armorStyle = TerraStyleHud.Armor.LEGACY_HORIZONTAL;
    public static boolean leftEffectIcon = true;

    public static boolean hurtRedOverlay = true;
    public static boolean bloodyEffect = true;
    public static boolean goreEffect = true;
    public static boolean damageIndicator = true;

    private static IntValue SHOW_WIND_PARTICLES;

    private static BooleanValue TERRA_STYLE_HEALTH;
    private static EnumValue<TerraStyleHud.Health> HEALTH_STYLE;
    private static EnumValue<TerraStyleHud.Mana> MANA_STYLE;
    private static BooleanValue TERRA_STYLE_ARMOR;
    private static EnumValue<TerraStyleHud.Armor> ARMOR_STYLE;
    private static BooleanValue LEFT_EFFECT_ICON;

    private static BooleanValue HURT_RED_OVERLAY;
    private static BooleanValue BLOODY_EFFECT;
    private static BooleanValue GORE_EFFECT;
    private static BooleanValue DAMAGE_INDICATOR;

    public static void onLoad() {
        showWindParticles = SHOW_WIND_PARTICLES.get();

        terraStyleHealth = TERRA_STYLE_HEALTH.get();
        healthStyle = HEALTH_STYLE.get();
        manaStyle = MANA_STYLE.get();
        terraStyleArmor = TERRA_STYLE_ARMOR.get();
        armorStyle = ARMOR_STYLE.get();
        leftEffectIcon = LEFT_EFFECT_ICON.get();

        hurtRedOverlay = HURT_RED_OVERLAY.get();
        bloodyEffect = BLOODY_EFFECT.get();
        goreEffect = GORE_EFFECT.get();
        damageIndicator = DAMAGE_INDICATOR.get();
    }

    public static void register(ModContainer container) {
        Builder BUILDER = new Builder();

        SHOW_WIND_PARTICLES = BUILDER.defineInRange("showWindParticles", 90, 0, 100);


        BUILDER.push("HUD");
        BUILDER.push("Health");
        TERRA_STYLE_HEALTH = BUILDER.define("terraStyleHealth", true);
        HEALTH_STYLE = BUILDER.defineEnum("healthStyle", TerraStyleHud.Health.LEGACY);
        BUILDER.pop();

        BUILDER.push("Mana");
        MANA_STYLE = BUILDER.defineEnum("manaStyle", TerraStyleHud.Mana.LEGACY);
        BUILDER.pop();

        BUILDER.push("Armor");
        TERRA_STYLE_ARMOR = BUILDER.define("terraStyleArmor", true);
        ARMOR_STYLE = BUILDER.defineEnum("armorStyle", TerraStyleHud.Armor.LEGACY_DIAGONAL);
        BUILDER.pop();

        LEFT_EFFECT_ICON = BUILDER.define("leftEffectIcon", true);
        BUILDER.pop();


        BUILDER.push("Entity");
        HURT_RED_OVERLAY = BUILDER.define("hurtRedOverlay", true);
        BLOODY_EFFECT = BUILDER.define("bloodyEffect", true);
        GORE_EFFECT = BUILDER.define("goreEffect", true);
        DAMAGE_INDICATOR = BUILDER.define("damageIndicator", true);
        BUILDER.pop();


        container.registerConfig(ModConfig.Type.CLIENT, BUILDER.build());
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
