package org.confluence.mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.client.gui.hud.TerraStyleArmorHud;
import org.confluence.mod.client.gui.hud.TerraStyleFoodHud;
import org.confluence.mod.client.gui.hud.TerraStyleHealthHud;
import org.confluence.mod.client.gui.hud.TerraStyleManaHud;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.client.gui.container.TETradeScreen;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ClientConfigs {
    public static int showWindParticles = 90;
    public static float minEctoMistEffectRadius = 10;

    public static boolean achievementToast = true;
    public static SellPriceDisplay sellPriceDisplay = SellPriceDisplay.EVERYWHERE;
    public static int customTitle = 71;

    public static boolean terraStyleHealth = true;
    public static TerraStyleHealthHud.Health healthStyle = TerraStyleHealthHud.Health.OVERLAY;
    public static int healthOffsetX = 0;
    public static int healthOffsetY = 0;
    public static boolean terraStyleFood = true;
    public static TerraStyleFoodHud.Food foodStyle = TerraStyleFoodHud.Food.OVERLAY;
    public static TerraStyleManaHud.Mana manaStyle = TerraStyleManaHud.Mana.OVERLAY;
    public static int manaOffsetX = 0;
    public static int manaOffsetY = 0;
    //    public static TerraStyleSoulHud.Soul soulStyle = TerraStyleSoulHud.Soul.OVERLAY;
//    public static int soulOffsetX = 0;
//    public static int soulOffsetY = 0;
    public static boolean terraStyleArmor = true;
    public static TerraStyleArmorHud.Armor armorStyle = TerraStyleArmorHud.Armor.OVERLAY;
    public static boolean leftEffectIcon = true;
    public static int extraInventoryButtonOffsetX = 0;
    public static int extraInventoryButtonOffsetY = 0;

    public static boolean bloodyEffect = true; // todo
    public static GoreEffect goreEffect = GoreEffect.CONFLUENCE_VANILLA;
    public static boolean damageIndicator = true;
    public static boolean healIndicator = true;

    private static IntValue SHOW_WIND_PARTICLES;
    private static IntValue MIN_ECTO_MIST_EFFECT_RADIUS;

    private static BooleanValue ACHIEVEMENT_TOAST;
    private static EnumValue<SellPriceDisplay> SELL_PRICE_DISPLAY;
    private static IntValue CUSTOM_TITLE;

    private static BooleanValue TERRA_STYLE_HEALTH;
    private static EnumValue<TerraStyleHealthHud.Health> HEALTH_STYLE;
    private static IntValue HEALTH_OFFSET_X;
    private static IntValue HEALTH_OFFSET_Y;
    private static BooleanValue TERRA_STYLE_FOOD;
    private static EnumValue<TerraStyleFoodHud.Food> FOOD_STYLE;
    private static EnumValue<TerraStyleManaHud.Mana> MANA_STYLE;
    private static IntValue MANA_OFFSET_X;
    private static IntValue MANA_OFFSET_Y;
    //    private static EnumValue<TerraStyleSoulHud.Soul> SOUL_STYLE;
//    private static IntValue SOUL_OFFSET_X;
//    private static IntValue SOUL_OFFSET_Y;
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
        minEctoMistEffectRadius = MIN_ECTO_MIST_EFFECT_RADIUS.get();

        achievementToast = ACHIEVEMENT_TOAST.get();
        sellPriceDisplay = SELL_PRICE_DISPLAY.get();
        customTitle = CUSTOM_TITLE.get();

        terraStyleHealth = TERRA_STYLE_HEALTH.get();
        healthStyle = HEALTH_STYLE.get();
        healthOffsetX = HEALTH_OFFSET_X.get();
        healthOffsetY = HEALTH_OFFSET_Y.get();
        foodStyle = FOOD_STYLE.get();
        manaStyle = MANA_STYLE.get();
        manaOffsetX = MANA_OFFSET_X.get();
        manaOffsetY = MANA_OFFSET_Y.get();
//        soulStyle = SOUL_STYLE.get();
//        soulOffsetX = SOUL_OFFSET_X.get();
//        soulOffsetY = SOUL_OFFSET_Y.get();
        terraStyleArmor = TERRA_STYLE_ARMOR.get();
        armorStyle = ARMOR_STYLE.get();
        terraStyleFood = TERRA_STYLE_FOOD.get();
        leftEffectIcon = LEFT_EFFECT_ICON.get();
        extraInventoryButtonOffsetX = EXTRA_INVENTORY_BUTTON_OFFSET_X.get();
        extraInventoryButtonOffsetY = EXTRA_INVENTORY_BUTTON_OFFSET_Y.get();

        bloodyEffect = BLOODY_EFFECT.get();
        goreEffect = GORE_EFFECT == null ? GoreEffect.OFF : GORE_EFFECT.get();
        damageIndicator = DAMAGE_INDICATOR.get();
        healIndicator = HEAL_INDICATOR.get();
    }

    public static void register(ModContainer container) {
        Builder builder = new Builder();

        SHOW_WIND_PARTICLES = builder.defineInRange("showWindParticles", 90, 0, 100);
        MIN_ECTO_MIST_EFFECT_RADIUS = builder.defineInRange("minEctoMistEffectRadius", 10, 0, 100);
        {
            builder.push("GUI");
            ACHIEVEMENT_TOAST = builder.define("achievementToast", true);
            SELL_PRICE_DISPLAY = builder.defineEnum("sellPriceDisplay", SellPriceDisplay.EVERYWHERE);
            CUSTOM_TITLE = builder.defineInRange("customTitle", 71, 0, 1000);
            builder.pop();
        }
        {
            builder.push("HUD");
            {
                builder.push("Health");
                TERRA_STYLE_HEALTH = builder.define("terraStyleHealth", true);
                HEALTH_STYLE = builder.defineEnum("healthStyle", TerraStyleHealthHud.Health.OVERLAY);
                HEALTH_OFFSET_X = builder.defineInRange("healthOffsetX", 0, -256, 256);
                HEALTH_OFFSET_Y = builder.defineInRange("healthOffsetY", 0, -256, 256);
                builder.pop();
            }
            {
                builder.push("Food");
                TERRA_STYLE_FOOD = builder.define("terraStyleFood", true);
                FOOD_STYLE = builder.defineEnum("foodStyle", TerraStyleFoodHud.Food.OVERLAY);
                builder.pop();
            }
            {
                builder.push("Mana");
                MANA_STYLE = builder.defineEnum("manaStyle", TerraStyleManaHud.Mana.OVERLAY);
                MANA_OFFSET_X = builder.defineInRange("manaOffsetX", 0, -256, 256);
                MANA_OFFSET_Y = builder.defineInRange("manaOffsetY", 0, -256, 256);
                builder.pop();
            }
//            {
//                builder.push("Soul");
//                SOUL_STYLE = builder.defineEnum("soulStyle", TerraStyleSoulHud.Soul.OVERLAY);
//                SOUL_OFFSET_X = builder.defineInRange("soulOffsetX", 0, -256, 256);
//                SOUL_OFFSET_Y = builder.defineInRange("soulOffsetY", 0, -256, 256);
//                builder.pop();
//            }
            {
                builder.push("Armor");
                TERRA_STYLE_ARMOR = builder.define("terraStyleArmor", true);
                ARMOR_STYLE = builder.defineEnum("armorStyle", TerraStyleArmorHud.Armor.OVERLAY);
                builder.pop();
            }
            LEFT_EFFECT_ICON = builder.define("leftEffectIcon", true);
            EXTRA_INVENTORY_BUTTON_OFFSET_X = builder.defineInRange("extraInventoryButtonOffsetX", 0, -256, 256);
            EXTRA_INVENTORY_BUTTON_OFFSET_Y = builder.defineInRange("extraInventoryButtonOffsetY", 0, -256, 256);
            builder.pop();
        }
        {
            builder.push("Entity");
            BLOODY_EFFECT = builder.define("bloodyEffect", true);
            if (!ModList.get().isLoaded("yes_steve_model")) {
                GORE_EFFECT = builder.defineEnum("goreEffect", GoreEffect.CONFLUENCE_VANILLA);
            }
            DAMAGE_INDICATOR = builder.define("damageIndicator", true);
            HEAL_INDICATOR = builder.define("healIndicator", true);
            builder.pop();
        }

        container.registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    public enum GoreEffect implements TranslatableEnum {
        OFF {
            @Override
            public boolean isInvalidFor(@Nullable LivingEntity living, @Nullable Item item) {
                return true;
            }
        },
        CONFLUENCE {
            @Override
            public boolean isInvalidFor(@Nullable LivingEntity living, @Nullable Item item) {
                if (living != null) {
                    EntityType<?> type = living.getType();
                    if (type.is(ModTags.EntityTypes.GORE_EFFECT_BLACKLIST)) {
                        return true;
                    }
                    return !ModUtils.isFromConfluence(BuiltInRegistries.ENTITY_TYPE, type);
                }
                if (item != null) {
                    return !ModUtils.isFromConfluence(BuiltInRegistries.ITEM, item);
                }
                return true;
            }
        },
        CONFLUENCE_VANILLA {
            @Override
            public boolean isInvalidFor(@Nullable LivingEntity living, @Nullable Item item) {
                String namespace;
                if (living != null) {
                    EntityType<?> type = living.getType();
                    if (type.is(ModTags.EntityTypes.GORE_EFFECT_BLACKLIST)) {
                        return true;
                    }
                    namespace = BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace();
                } else if (item != null) {
                    namespace = BuiltInRegistries.ITEM.getKey(item).getNamespace();
                } else {
                    return true;
                }
                return !ResourceLocation.DEFAULT_NAMESPACE.equals(namespace) && !ModUtils.CONFLUENCE_NAMESPACES.contains(namespace);
            }
        },
        ALL {
            @Override
            public boolean isInvalidFor(@Nullable LivingEntity living, @Nullable Item item) {
                return false;
            }
        };

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.goreEffect." + name().toLowerCase(Locale.ROOT));
        }

        public abstract boolean isInvalidFor(@Nullable LivingEntity living, @Nullable Item item);
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
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.sellPriceDisplay." + name().toLowerCase(Locale.ROOT));
        }
    }
}
