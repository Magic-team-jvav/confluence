package org.confluence.mod.client;

import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.client.gui.hud.TerraStyleArmorHud;
import org.confluence.mod.client.gui.hud.TerraStyleFoodHud;
import org.confluence.mod.client.gui.hud.TerraStyleHealthHud;
import org.confluence.mod.client.gui.hud.TerraStyleManaHud;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ClientConfigs {
    public static final List<String> DEFAULT_BANNED_MOD_FOR_PAINTS = List.of(
            "integrateddynamics", "ae2", "refinedstorage", "create", "mekanism", "immersiveengineering", "enderio"
    );
    public static int showWindParticles = 90;
    public static Set<String> bannedModForPaints = new HashSet<>(DEFAULT_BANNED_MOD_FOR_PAINTS);
    public static boolean achievementToast = true;

    public static boolean terraStyleHealth = true;
    public static TerraStyleHealthHud.Health healthStyle = TerraStyleHealthHud.Health.OVERLAY;
    public static boolean terraStyleFood = true;
    public static TerraStyleFoodHud.Food foodStyle = TerraStyleFoodHud.Food.OVERLAY;
    public static TerraStyleManaHud.Mana manaStyle = TerraStyleManaHud.Mana.OVERLAY;
    public static boolean terraStyleArmor = true;
    public static TerraStyleArmorHud.Armor armorStyle = TerraStyleArmorHud.Armor.OVERLAY;
    public static boolean leftEffectIcon = true;

    public static boolean bloodyEffect = true;
    public static GoreEffect goreEffect = GoreEffect.CONFLUENCE_VANILLA;
    public static boolean damageIndicator = true;

    private static IntValue SHOW_WIND_PARTICLES;
    private static ConfigValue<List<? extends String>> BANNED_MOD_FOR_PAINTS;
    private static BooleanValue ACHIEVEMENT_TOAST;

    private static BooleanValue TERRA_STYLE_HEALTH;
    private static EnumValue<TerraStyleHealthHud.Health> HEALTH_STYLE;
    private static BooleanValue TERRA_STYLE_FOOD;
    private static EnumValue<TerraStyleFoodHud.Food> FOOD_STYLE;
    private static EnumValue<TerraStyleManaHud.Mana> MANA_STYLE;
    private static BooleanValue TERRA_STYLE_ARMOR;
    private static EnumValue<TerraStyleArmorHud.Armor> ARMOR_STYLE;
    private static BooleanValue LEFT_EFFECT_ICON;

    private static BooleanValue BLOODY_EFFECT;
    private static EnumValue<GoreEffect> GORE_EFFECT;
    private static BooleanValue DAMAGE_INDICATOR;

    public static void onLoad() {
        showWindParticles = SHOW_WIND_PARTICLES.get();
        bannedModForPaints = new HashSet<>(BANNED_MOD_FOR_PAINTS.get());
        achievementToast = ACHIEVEMENT_TOAST.get();

        terraStyleHealth = TERRA_STYLE_HEALTH.get();
        healthStyle = HEALTH_STYLE.get();
        leftEffectIcon = LEFT_EFFECT_ICON.get();
        foodStyle = FOOD_STYLE.get();
        manaStyle = MANA_STYLE.get();
        terraStyleArmor = TERRA_STYLE_ARMOR.get();
        armorStyle = ARMOR_STYLE.get();
        terraStyleFood = TERRA_STYLE_FOOD.get();

        bloodyEffect = BLOODY_EFFECT.get();
        goreEffect = GORE_EFFECT.get();
        damageIndicator = DAMAGE_INDICATOR.get();
    }

    public static void register(ModContainer container) {
        Builder BUILDER = new Builder();

        SHOW_WIND_PARTICLES = BUILDER.defineInRange("showWindParticles", 90, 0, 100);
        BANNED_MOD_FOR_PAINTS = BUILDER.defineListAllowEmpty("bannedModForPaints", () -> DEFAULT_BANNED_MOD_FOR_PAINTS, () -> "modid", o -> o instanceof String s && !s.contains(":"));
        ACHIEVEMENT_TOAST = BUILDER.define("achievementToast", true);


        BUILDER.push("HUD");
        BUILDER.push("Health");
        TERRA_STYLE_HEALTH = BUILDER.define("terraStyleHealth", true);
        HEALTH_STYLE = BUILDER.defineEnum("healthStyle", TerraStyleHealthHud.Health.OVERLAY);
        BUILDER.pop();

        BUILDER.push("Food");
        TERRA_STYLE_FOOD = BUILDER.define("terraStyleFood", true);
        FOOD_STYLE = BUILDER.defineEnum("foodStyle", TerraStyleFoodHud.Food.OVERLAY);
        BUILDER.pop();

        BUILDER.push("Mana");
        MANA_STYLE = BUILDER.defineEnum("manaStyle", TerraStyleManaHud.Mana.OVERLAY);
        BUILDER.pop();

        BUILDER.push("Armor");
        TERRA_STYLE_ARMOR = BUILDER.define("terraStyleArmor", true);
        ARMOR_STYLE = BUILDER.defineEnum("armorStyle", TerraStyleArmorHud.Armor.OVERLAY);
        BUILDER.pop();

        LEFT_EFFECT_ICON = BUILDER.define("leftEffectIcon", true);
        BUILDER.pop();


        BUILDER.push("Entity");
        BLOODY_EFFECT = BUILDER.define("bloodyEffect", true);
        GORE_EFFECT = BUILDER.defineEnum("goreEffect", GoreEffect.CONFLUENCE_VANILLA);
        DAMAGE_INDICATOR = BUILDER.define("damageIndicator", true);
        BUILDER.pop();


        container.registerConfig(ModConfig.Type.CLIENT, BUILDER.build());
    }

    public enum GoreEffect implements TranslatableEnum {
        OFF, CONFLUENCE, CONFLUENCE_VANILLA, ALL;

        @Override
        @NotNull
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.goreEffect." + name().toLowerCase(Locale.ROOT));
        }
    }
}
