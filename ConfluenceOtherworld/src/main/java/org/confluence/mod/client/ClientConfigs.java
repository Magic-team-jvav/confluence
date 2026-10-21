package org.confluence.mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.*;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ClientConfigs {
    public static BooleanValue ENABLE_NON_SPIDER_MODEL;
    public static int showWindParticles = 90;
    public static float minEctoMistEffectRadius = 10;

    /// 风是否让雨雪倾斜、并驱动云层速度（纯视觉）。
    public static boolean windSlantsRainClouds = true;
    /// 暴风雪是否播放环境风声（露天与室内为两种音效）。
    public static boolean blizzardAmbience = true;

    public static int rainbowCount = 3;
    public static boolean rainbowGradient = false;

    public static int soulcererBackgroundHue = 270;
    public static int soulcererBackgroundContrast = 255;
    public static boolean soulcererBackgroundTree = true;
    public static boolean soulcererBackgroundMagic = true;
    /// 背景总开关，默认开；关掉后总览界面不画任何背景层。
    public static boolean soulcererBackgroundEnabled = true;

    /// 背景是否绘制：**优先读实时配置**（界面里刚点完立即生效），配置尚未加载时才回落到缓存字段。
    ///
    /// 总览界面里的开关和配置界面的开关都写同一个 `ConfigValue`，这里读它而不是读缓存，
    /// 就不会出现"改了值但界面还按旧值画"的情况。
    public static boolean isSoulcererBackgroundEnabled() {
        return SOULCERER_BACKGROUND_ENABLED == null ? soulcererBackgroundEnabled : SOULCERER_BACKGROUND_ENABLED.get();
    }

    /// 写入背景总开关：同时更新配置项与缓存字段，界面与配置文件保持一致。
    public static void setSoulcererBackgroundEnabled(boolean enabled) {
        soulcererBackgroundEnabled = enabled;
        if (SOULCERER_BACKGROUND_ENABLED != null) {
            SOULCERER_BACKGROUND_ENABLED.set(enabled);
        }
    }

    public static boolean achievementToast = true;
    public static boolean weaponTooltipBaseStats = false;
    public static SellPriceDisplay sellPriceDisplay = SellPriceDisplay.EVERYWHERE;
    public static int customTitle = 71;
    public static CustomBossBarRenderer.Style bossBarStyle = CustomBossBarRenderer.Style.STATIC;
    public static boolean bossBarNumbersVisible = true;

    public static boolean terraStyleHealth = true;
    public static TerraStyleHealthHud.Health healthStyle = TerraStyleHealthHud.Health.OVERLAY;
    public static int healthOffsetX = 0;
    public static int healthOffsetY = 0;
    public static boolean terraStyleFood = true;
    public static TerraStyleFoodHud.Food foodStyle = TerraStyleFoodHud.Food.OVERLAY;
    public static TerraStyleManaHud.Mana manaStyle = TerraStyleManaHud.Mana.OVERLAY;
    public static TerraStyleManaHud.Soul soulStyle = TerraStyleManaHud.Soul.OVERLAY;
    public static int manaOffsetX = 0;
    public static int manaOffsetY = 0;
    public static ClientSoulHandler.Type soulQuickSkillStyle;
    public static boolean terraStyleArmor = true;
    public static TerraStyleArmorHud.Armor armorStyle = TerraStyleArmorHud.Armor.OVERLAY;
    public static boolean leftEffectIcon = true;
    public static int extraInventoryButtonOffsetX = 0;
    public static int extraInventoryButtonOffsetY = 0;

    public static boolean bloodyEffect = true;
    public static GoreEffect goreEffect = GoreEffect.CONFLUENCE_VANILLA;
    /// 伤害/治疗数值的显示方式，默认虚拟信息。
    public static IndicatorMode damageIndicator = IndicatorMode.VIRTUAL;
    public static IndicatorMode healIndicator = IndicatorMode.VIRTUAL;
    public static WeaponUseButton staffUseButton = WeaponUseButton.RIGHT;
    public static WeaponUseButton whipUseButton = WeaponUseButton.LEFT;
    public static WeaponUseButton yoyoUseButton = WeaponUseButton.LEFT;
    public static WeaponUseButton gunUseButton = WeaponUseButton.LEFT;
    public static WeaponUseButton flailUseButton = WeaponUseButton.LEFT;

    private static IntValue SHOW_WIND_PARTICLES;
    private static IntValue MIN_ECTO_MIST_EFFECT_RADIUS;

    private static IntValue RAINBOW_COUNT;
    private static BooleanValue RAINBOW_GRADIENT;

    public static IntValue SOULCERER_BACKGROUND_HUE;
    public static IntValue SOULCERER_BACKGROUND_CONTRAST;
    public static BooleanValue SOULCERER_BACKGROUND_TREE;
    public static BooleanValue SOULCERER_BACKGROUND_MAGIC;
    /// 魂师技能总览界面的背景总开关：关掉后整层背景不画。
    public static BooleanValue SOULCERER_BACKGROUND_ENABLED;

    private static BooleanValue ACHIEVEMENT_TOAST;
    private static BooleanValue WEAPON_TOOLTIP_BASE_STATS;
    private static EnumValue<SellPriceDisplay> SELL_PRICE_DISPLAY;
    private static IntValue CUSTOM_TITLE;
    private static EnumValue<CustomBossBarRenderer.Style> BOSS_BAR_STYLE;
    private static BooleanValue BOSS_BAR_NUMBERS_VISIBLE;

    private static BooleanValue TERRA_STYLE_HEALTH;
    private static EnumValue<TerraStyleHealthHud.Health> HEALTH_STYLE;
    private static IntValue HEALTH_OFFSET_X;
    private static IntValue HEALTH_OFFSET_Y;
    private static BooleanValue TERRA_STYLE_FOOD;
    private static EnumValue<TerraStyleFoodHud.Food> FOOD_STYLE;
    private static EnumValue<TerraStyleManaHud.Mana> MANA_STYLE;
    private static EnumValue<TerraStyleManaHud.Soul> SOUL_STYLE;
    private static IntValue MANA_OFFSET_X;
    private static IntValue MANA_OFFSET_Y;
    private static EnumValue<ClientSoulHandler.Type> SOUL_QUICK_SKILL_STYLE;
    private static BooleanValue TERRA_STYLE_ARMOR;
    private static EnumValue<TerraStyleArmorHud.Armor> ARMOR_STYLE;
    private static BooleanValue LEFT_EFFECT_ICON;
    private static IntValue EXTRA_INVENTORY_BUTTON_OFFSET_X;
    private static IntValue EXTRA_INVENTORY_BUTTON_OFFSET_Y;

    private static BooleanValue BLOODY_EFFECT;
    private static EnumValue<GoreEffect> GORE_EFFECT;
    private static EnumValue<IndicatorMode> DAMAGE_INDICATOR;
    private static EnumValue<IndicatorMode> HEAL_INDICATOR;
    private static EnumValue<WeaponUseButton> STAFF_USE_BUTTON;
    private static EnumValue<WeaponUseButton> WHIP_USE_BUTTON;
    private static EnumValue<WeaponUseButton> YOYO_USE_BUTTON;
    private static EnumValue<WeaponUseButton> GUN_USE_BUTTON;
    private static EnumValue<WeaponUseButton> FLAIL_USE_BUTTON;

    private static BooleanValue WIND_SLANTS_RAIN_CLOUDS;
    private static BooleanValue BLIZZARD_AMBIENCE;

    public static void onLoad() {
        showWindParticles = SHOW_WIND_PARTICLES.get();
        minEctoMistEffectRadius = MIN_ECTO_MIST_EFFECT_RADIUS.get();
        windSlantsRainClouds = WIND_SLANTS_RAIN_CLOUDS.get();
        blizzardAmbience = BLIZZARD_AMBIENCE.get();

        rainbowCount = RAINBOW_COUNT.get();
        rainbowGradient = RAINBOW_GRADIENT.get();

        soulcererBackgroundHue = SOULCERER_BACKGROUND_HUE.get();
        soulcererBackgroundContrast = SOULCERER_BACKGROUND_CONTRAST.get();
        soulcererBackgroundTree = SOULCERER_BACKGROUND_TREE.get();
        soulcererBackgroundMagic = SOULCERER_BACKGROUND_MAGIC.get();
        soulcererBackgroundEnabled = SOULCERER_BACKGROUND_ENABLED.get();

        achievementToast = ACHIEVEMENT_TOAST.get();
        weaponTooltipBaseStats = WEAPON_TOOLTIP_BASE_STATS.get();
        sellPriceDisplay = SELL_PRICE_DISPLAY.get();
        customTitle = CUSTOM_TITLE.get();
        bossBarStyle = BOSS_BAR_STYLE.get();
        bossBarNumbersVisible = BOSS_BAR_NUMBERS_VISIBLE.get();

        terraStyleHealth = TERRA_STYLE_HEALTH.get();
        healthStyle = HEALTH_STYLE.get();
        healthOffsetX = HEALTH_OFFSET_X.get();
        healthOffsetY = HEALTH_OFFSET_Y.get();
        foodStyle = FOOD_STYLE.get();
        manaStyle = MANA_STYLE.get();
        soulStyle = SOUL_STYLE.get();
        manaOffsetX = MANA_OFFSET_X.get();
        manaOffsetY = MANA_OFFSET_Y.get();
        soulQuickSkillStyle = SOUL_QUICK_SKILL_STYLE.get();
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
        staffUseButton = STAFF_USE_BUTTON.get();
        whipUseButton = WHIP_USE_BUTTON.get();
        yoyoUseButton = YOYO_USE_BUTTON.get();
        gunUseButton = GUN_USE_BUTTON.get();
        flailUseButton = FLAIL_USE_BUTTON.get();
    }

    public static void register(ModContainer container) {
        Builder builder = new Builder();

        {
            builder.push("GUI");
            {
                builder.push("ItemTooltips");
                WEAPON_TOOLTIP_BASE_STATS = builder.comment("Show weapon damage and critical strike chance with modifiers, excluding player equipment and effects. Disabled by default to show the final values with player bonuses.")
                        .define("weaponTooltipBaseStats", false);
                SELL_PRICE_DISPLAY = builder.defineEnum("sellPriceDisplay", SellPriceDisplay.EVERYWHERE);
                builder.pop();
            }
            ACHIEVEMENT_TOAST = builder.define("achievementToast", true);
            CUSTOM_TITLE = builder.defineInRange("customTitle", 71, 0, 1000);
            {
                builder.push("Soulcerer");
                /// 总开关放在同一分组的最前面，与色相 / 对比度 / 树木 / 魔法效果一起。
                SOULCERER_BACKGROUND_ENABLED = builder.comment("Draw the dynamic background of the Soul Skill Overview screen. Disable to keep only the widgets.")
                        .define("soulcererBackgroundEnabled", true);
                SOULCERER_BACKGROUND_HUE = builder.defineInRange("soulcererBackgroundHue", 270, 0, 360);
                SOULCERER_BACKGROUND_CONTRAST = builder.defineInRange("soulcererBackgroundContrast", 255, 0, 255);
                SOULCERER_BACKGROUND_TREE = builder.define("soulcererBackgroundTree", true);
                SOULCERER_BACKGROUND_MAGIC = builder.define("soulcererBackgroundMagic", true);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("HUD");
            {
                builder.push("Boss");
                BOSS_BAR_STYLE = builder.defineEnum("bossBarStyle", CustomBossBarRenderer.Style.STATIC);
                BOSS_BAR_NUMBERS_VISIBLE = builder.comment("Show exact health values on custom boss bars.")
                        .define("bossBarNumbersVisible", true);
                builder.pop();
            }
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
            {
                builder.push("Soulcerer");
                SOUL_STYLE = builder.defineEnum("soulStyle", TerraStyleManaHud.Soul.OVERLAY);
                SOUL_QUICK_SKILL_STYLE = builder.defineEnum("soulcererQuickSkillStyle", ClientSoulHandler.Type.ROULETTE_WHEEL_SMALL);
                builder.pop();
            }
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
            ENABLE_NON_SPIDER_MODEL = builder.comment("Replace spiders and sand poachers with non-spider models. Requires a game restart.")
                    .define("enableNonSpiderModel", false);
            BLOODY_EFFECT = builder.define("bloodyEffect", true);
            GORE_EFFECT = builder.defineEnum("goreEffect", GoreEffect.CONFLUENCE_VANILLA);
            DAMAGE_INDICATOR = builder.defineEnum("damageIndicator", IndicatorMode.VIRTUAL);
            HEAL_INDICATOR = builder.defineEnum("healIndicator", IndicatorMode.VIRTUAL);
            builder.pop();
        }
        {
            builder.push("Biome");
            MIN_ECTO_MIST_EFFECT_RADIUS = builder.defineInRange("minEctoMistEffectRadius", 10, 0, 100);
            {
                builder.push("The Hallow");
                RAINBOW_COUNT = builder.defineInRange("rainbowCount", 3, 0, 20);
                RAINBOW_GRADIENT = builder.define("rainbowGradient", false);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("WeaponInput");
            STAFF_USE_BUTTON = builder.defineEnum("staffUseButton", WeaponUseButton.RIGHT);
            WHIP_USE_BUTTON = builder.defineEnum("whipUseButton", WeaponUseButton.LEFT);
            YOYO_USE_BUTTON = builder.defineEnum("yoyoUseButton", WeaponUseButton.LEFT);
            GUN_USE_BUTTON = builder.defineEnum("gunUseButton", WeaponUseButton.LEFT);
            FLAIL_USE_BUTTON = builder.defineEnum("flailUseButton", WeaponUseButton.LEFT);
            builder.pop();
        }
        {
            // 风系统里**纯视听**的两项放这里；影响玩法的那几项（风速频率、局地风向偏差、
            // 沙尘暴/暴风雪的实体受力）属于服务端行为，留在 CommonConfigs。
            builder.push("Weather");
            SHOW_WIND_PARTICLES = builder.defineInRange("showWindParticles", 90, 0, 100);
            WIND_SLANTS_RAIN_CLOUDS = builder.define("windSlantsRainClouds", true);
            BLIZZARD_AMBIENCE = builder.define("blizzardAmbience", true);
            builder.pop();
        }

        container.registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    /// 返回当前物品配置的主要动作键；不属于这五类武器时返回 {@code null}。
    public static @Nullable WeaponUseButton weaponUseButton(ItemStack stack) {
        Holder<Item> holder = stack.getItem().builtInRegistryHolder();
        if (holder.is(ModTags.Items.MANA_WEAPON)) {
            return staffUseButton;
        } else if (holder.is(ModTags.Items.WHIP)) {
            return whipUseButton;
        } else if (holder.is(ModTags.Items.YOYO)) {
            return yoyoUseButton;
        } else if (holder.is(ModTags.Items.GUN)) {
            return gunUseButton;
        } else if (holder.is(ModTags.Items.FLAIL)) {
            return flailUseButton;
        }
        return null;
    }

    public static boolean usesLeftWeaponButton(ItemStack stack) {
        return weaponUseButton(stack) == WeaponUseButton.LEFT;
    }

    public enum WeaponUseButton implements TranslatableEnum {
        LEFT,
        RIGHT;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.weaponInputButton." + name().toLowerCase(Locale.ROOT));
        }
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
                return living != null && living.getType().is(ModTags.EntityTypes.GORE_EFFECT_BLACKLIST);
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
                return Minecraft.getInstance().player != null
                        && Minecraft.getInstance().player.containerMenu instanceof NPCTradeMenu;
            }
        };

        public abstract boolean test();

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.sellPriceDisplay." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum IndicatorMode implements TranslatableEnum {
        PARTICLE,
        VIRTUAL,
        /// 关闭：既不生成原版伤害指示粒子，也不显示虚拟信息
        OFF;

        public boolean isParticle() {
            return this == PARTICLE;
        }

        public boolean isVirtual() {
            return this == VIRTUAL;
        }

        /// 关闭时伤害 / 治疗数值完全不显示
        public boolean isOff() {
            return this == OFF;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.indicatorMode." + name().toLowerCase(Locale.ROOT));
        }
    }
}
