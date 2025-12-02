package org.confluence.mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.client.gui.hud.*;
import org.confluence.mod.client.handler.StarPhaseHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.terraentity.client.gui.container.TETradeScreen;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class ClientConfigs {
    public static int showWindParticles = 90;
    public static boolean achievementToast = true;
    public static SellPriceDisplay sellPriceDisplay = SellPriceDisplay.EVERYWHERE;
    public static float minEctoMistEffectRadius = 10;

    public static boolean terraStyleHealth = true;
    public static TerraStyleHealthHud.Health healthStyle = TerraStyleHealthHud.Health.OVERLAY;
    public static int healthOffsetX = 0;
    public static int healthOffsetY = 0;
    public static boolean terraStyleFood = true;
    public static TerraStyleFoodHud.Food foodStyle = TerraStyleFoodHud.Food.OVERLAY;
    public static TerraStyleManaHud.Mana manaStyle = TerraStyleManaHud.Mana.OVERLAY;
    public static int manaOffsetX = 0;
    public static int manaOffsetY = 0;
    public static TerraStyleSoulHud.Soul soulStyle = TerraStyleSoulHud.Soul.OVERLAY;
    public static int soulOffsetX = 0;
    public static int soulOffsetY = 0;
    public static boolean terraStyleArmor = true;
    public static TerraStyleArmorHud.Armor armorStyle = TerraStyleArmorHud.Armor.OVERLAY;
    public static boolean leftEffectIcon = true;
    public static int extraInventoryButtonOffsetX = 0;
    public static int extraInventoryButtonOffsetY = 0;

    public static boolean bloodyEffect = true;
    public static GoreEffect goreEffect = GoreEffect.CONFLUENCE_VANILLA;
    public static boolean damageIndicator = true;
    public static boolean healIndicator = true;

    private static IntValue SHOW_WIND_PARTICLES;
    private static BooleanValue ACHIEVEMENT_TOAST;
    private static EnumValue<SellPriceDisplay> SELL_PRICE_DISPLAY;
    private static IntValue MIN_ECTO_MIST_EFFECT_RADIUS;

    private static BooleanValue TERRA_STYLE_HEALTH;
    private static EnumValue<TerraStyleHealthHud.Health> HEALTH_STYLE;
    private static IntValue HEALTH_OFFSET_X;
    private static IntValue HEALTH_OFFSET_Y;
    private static BooleanValue TERRA_STYLE_FOOD;
    private static EnumValue<TerraStyleFoodHud.Food> FOOD_STYLE;
    private static EnumValue<TerraStyleManaHud.Mana> MANA_STYLE;
    private static IntValue MANA_OFFSET_X;
    private static IntValue MANA_OFFSET_Y;
    private static EnumValue<TerraStyleSoulHud.Soul> SOUL_STYLE;
    private static IntValue SOUL_OFFSET_X;
    private static IntValue SOUL_OFFSET_Y;
    private static BooleanValue TERRA_STYLE_ARMOR;
    private static EnumValue<TerraStyleArmorHud.Armor> ARMOR_STYLE;
    private static BooleanValue LEFT_EFFECT_ICON;
    private static IntValue EXTRA_INVENTORY_BUTTON_OFFSET_X;
    private static IntValue EXTRA_INVENTORY_BUTTON_OFFSET_Y;

    private static BooleanValue BLOODY_EFFECT;
    private static EnumValue<GoreEffect> GORE_EFFECT;
    private static BooleanValue DAMAGE_INDICATOR;
    private static BooleanValue HEAL_INDICATOR;

    public static void onLoad() {
        showWindParticles = SHOW_WIND_PARTICLES.get();
        achievementToast = ACHIEVEMENT_TOAST.get();
        sellPriceDisplay = SELL_PRICE_DISPLAY.get();
        minEctoMistEffectRadius = MIN_ECTO_MIST_EFFECT_RADIUS.get();

        terraStyleHealth = TERRA_STYLE_HEALTH.get();
        healthStyle = HEALTH_STYLE.get();
        healthOffsetX = HEALTH_OFFSET_X.get();
        healthOffsetY = HEALTH_OFFSET_Y.get();
        foodStyle = FOOD_STYLE.get();
        manaStyle = MANA_STYLE.get();
        manaOffsetX = MANA_OFFSET_X.get();
        manaOffsetY = MANA_OFFSET_Y.get();
        soulStyle = SOUL_STYLE.get();
        soulOffsetX = SOUL_OFFSET_X.get();
        soulOffsetY = SOUL_OFFSET_Y.get();
        terraStyleArmor = TERRA_STYLE_ARMOR.get();
        armorStyle = ARMOR_STYLE.get();
        terraStyleFood = TERRA_STYLE_FOOD.get();
        leftEffectIcon = LEFT_EFFECT_ICON.get();
        extraInventoryButtonOffsetX = EXTRA_INVENTORY_BUTTON_OFFSET_X.get();
        extraInventoryButtonOffsetY = EXTRA_INVENTORY_BUTTON_OFFSET_Y.get();

        bloodyEffect = BLOODY_EFFECT.get();
        goreEffect = GORE_EFFECT != null ? GORE_EFFECT.get() : GoreEffect.OFF;
        damageIndicator = DAMAGE_INDICATOR.get();
        healIndicator = HEAL_INDICATOR.get();
        StarPhaseHandler.enabled = CommonConfigs.STAR_PHASE.get();
    }

    public static void register(ModContainer container) {
        Builder BUILDER = new Builder();

        SHOW_WIND_PARTICLES = BUILDER.defineInRange("showWindParticles", 90, 0, 100);
        ACHIEVEMENT_TOAST = BUILDER.define("achievementToast", true);
        SELL_PRICE_DISPLAY = BUILDER.defineEnum("sellPriceDisplay", SellPriceDisplay.EVERYWHERE);
        MIN_ECTO_MIST_EFFECT_RADIUS = BUILDER.defineInRange("minEctoMistEffectRadius", 10, 0, 100);
        {
            BUILDER.push("HUD");
            {
                BUILDER.push("Health");
                TERRA_STYLE_HEALTH = BUILDER.define("terraStyleHealth", true);
                HEALTH_STYLE = BUILDER.defineEnum("healthStyle", TerraStyleHealthHud.Health.OVERLAY);
                HEALTH_OFFSET_X = BUILDER.defineInRange("healthOffsetX", 0, -256, 256);
                HEALTH_OFFSET_Y = BUILDER.defineInRange("healthOffsetY", 0, -256, 256);
                BUILDER.pop();
            }
            {
                BUILDER.push("Food");
                TERRA_STYLE_FOOD = BUILDER.define("terraStyleFood", true);
                FOOD_STYLE = BUILDER.defineEnum("foodStyle", TerraStyleFoodHud.Food.OVERLAY);
                BUILDER.pop();
            }
            {
                BUILDER.push("Mana");
                MANA_STYLE = BUILDER.defineEnum("manaStyle", TerraStyleManaHud.Mana.OVERLAY);
                MANA_OFFSET_X = BUILDER.defineInRange("manaOffsetX", 0, -256, 256);
                MANA_OFFSET_Y = BUILDER.defineInRange("manaOffsetY", 0, -256, 256);
                BUILDER.pop();
            }
            {
                BUILDER.push("Soul");
                SOUL_STYLE = BUILDER.defineEnum("soulStyle", TerraStyleSoulHud.Soul.OVERLAY);
                SOUL_OFFSET_X = BUILDER.defineInRange("soulOffsetX", 0, -256, 256);
                SOUL_OFFSET_Y = BUILDER.defineInRange("soulOffsetY", 0, -256, 256);
                BUILDER.pop();
            }
            {
                BUILDER.push("Armor");
                TERRA_STYLE_ARMOR = BUILDER.define("terraStyleArmor", true);
                ARMOR_STYLE = BUILDER.defineEnum("armorStyle", TerraStyleArmorHud.Armor.OVERLAY);
                BUILDER.pop();
            }
            LEFT_EFFECT_ICON = BUILDER.define("leftEffectIcon", true);
            EXTRA_INVENTORY_BUTTON_OFFSET_X = BUILDER.defineInRange("extraInventoryButtonOffsetX", 0, -256, 256);
            EXTRA_INVENTORY_BUTTON_OFFSET_Y = BUILDER.defineInRange("extraInventoryButtonOffsetY", 0, -256, 256);
            BUILDER.pop();
        }
        {
            BUILDER.push("Entity");
            BLOODY_EFFECT = BUILDER.define("bloodyEffect", true);
            if (!ModList.get().isLoaded("yes_steve_model")) {
                GORE_EFFECT = BUILDER.defineEnum("goreEffect", GoreEffect.CONFLUENCE_VANILLA);
            }
            DAMAGE_INDICATOR = BUILDER.define("damageIndicator", true);
            HEAL_INDICATOR = BUILDER.define("healIndicator", true);
            BUILDER.pop();
        }

        container.registerConfig(ModConfig.Type.CLIENT, BUILDER.build());
    }

    public enum GoreEffect implements TranslatableEnum {
        OFF,
        CONFLUENCE,
        CONFLUENCE_VANILLA,
        ALL;

        @Override
        public @NotNull Component getTranslatedName() {
            return Component.translatable("confluence.configuration.goreEffect." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum SellPriceDisplay implements TranslatableEnum {
        NEVER {
            @Override
            public boolean test() {
                return false;
            }
        },
        EVERYWHERE {
            @Override
            public boolean test() {
                return true;
            }
        },
        TRADE_SCREEN {
            @Override
            public boolean test() {
                return Minecraft.getInstance().screen instanceof TETradeScreen<?>;
            }
        };

        public abstract boolean test();

        @Override
        public @NotNull Component getTranslatedName() {
            return Component.translatable("confluence.configuration.sellPriceDisplay." + name().toLowerCase(Locale.ROOT));
        }
    }
}
