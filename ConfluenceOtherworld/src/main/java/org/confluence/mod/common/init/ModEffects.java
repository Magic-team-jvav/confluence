package org.confluence.mod.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.effect.PublicMobEffect;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.effect.beneficial.*;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.flask.FlaskOfFireEffect;
import org.confluence.mod.common.effect.flask.FlaskOfGoldEffect;
import org.confluence.mod.common.effect.harmful.*;
import org.confluence.mod.common.effect.neutral.DimensionalOverlapEffect;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;
import org.confluence.mod.common.effect.neutral.SparkleSlimeEffect;
import org.confluence.mod.common.effect.soulcerer.EnhancedSoulEffect;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Confluence.MODID);
    public static final EffectCure FLASK = EffectCure.get("confluence:flask");
    public static final EffectCure CANNOT_REMOVE_BY_NURSE = EffectCure.get("confluence:cannot_remove_by_nurse");
    public static final DeferredHolder<MobEffect, EnhancedSoulEffect> ENHANCED_SOUL = registerEffectWithEnglishName("enhanced_soul", "强力附魂", "Enhanced Soul", EnhancedSoulEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> EXQUISITELY_STUFFED = registerEffect("exquisitely_stuffed", "酒足饭饱", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, ADD_VALUE, v -> exquisitelyStuffed(v, 0.04, 0.06, 0.08))
            .addAttributeModifier(Attributes.ARMOR, id, ADD_VALUE, v -> exquisitelyStuffed(v, 1, 2, 3))
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.02, 0.03, 0.04))
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getRangedDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getSummonDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(ConfluenceMagicLib.SUMMON_KNOCKBACK, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10)));
    public static final DeferredHolder<MobEffect, MobEffect> IRON_SKIN = registerEffectWithEnglishName("iron_skin", "铁皮", "Ironskin", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x184F5)
            .addAttributeModifier(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> ENDURANCE = registerEffect("endurance", "耐力", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x708090)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, id, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> INFERNO = registerEffect("inferno", "狱火", InfernoEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LIFE_FORCE = registerEffectWithEnglishName("life_force", "生命力", "Lifeforce", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC0CB)
            .addAttributeModifier(Attributes.MAX_HEALTH, id, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> RAGE = registerEffect("rage", "暴怒", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF4500)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> WRATH = registerEffect("wrath", "怒气", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF8C00)
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getRangedDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getSummonDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> THORNS = registerEffect("thorns", "荆棘", ThornsEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> MANA_REGENERATION = registerEffect("mana_regeneration", "魔力再生", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6600CC));
    public static final DeferredHolder<MobEffect, MobEffect> STAR_IN_A_BOTTLE = registerEffectWithEnglishName("star_in_a_bottle", "星星瓶", "Star in a Bottle", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF66));
    public static final DeferredHolder<MobEffect, MobEffect> TITAN = registerEffect("titan", "泰坦", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD2B48C)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> BUILDER = registerEffect("builder", "建筑工", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8B6914)
            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, id, 3, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> FISHING = registerEffect("fishing", "钓鱼", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF));
    public static final DeferredHolder<MobEffect, MobEffect> MAGIC_POWER = registerEffect("magic_power", "魔能", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xCC00CC)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> OBSIDIAN_SKIN = registerEffect("obsidian_skin", "黑曜石皮", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x660066));
    public static final DeferredHolder<MobEffect, MobEffect> LUCK_EFFECT = registerEffect("luck", "幸运", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x39C5BB)
            .addAttributeModifier(Attributes.LUCK, id, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.5));
    public static final DeferredHolder<MobEffect, MobEffect> WATER_WALKING = registerEffect("water_walking", "水上漂", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x0000BB));
    public static final DeferredHolder<MobEffect, MobEffect> HEART_REACH = registerEffectWithEnglishName("heart_reach", "拾心", "Heartreach", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0099));
    public static final DeferredHolder<MobEffect, MobEffect> ARCHERY = registerEffect("archery", "箭术", ArcheryEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FLIPPER = registerEffect("flipper", "脚蹼", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000088));
    public static final DeferredHolder<MobEffect, MobEffect> SHINE = registerEffect("shine", "光芒", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF66));
    public static final DeferredHolder<MobEffect, MobEffect> SPELUNKER = registerEffect("spelunker", "洞穴探险", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00));
    public static final DeferredHolder<MobEffect, MobEffect> HUNTER = registerEffect("hunter", "猎人", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC800));
    public static final DeferredHolder<MobEffect, MobEffect> DANGER_SENSE = registerEffectWithEnglishName("danger_sense", "危险感", "Dangersense", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFAFAF));
    public static final DeferredHolder<MobEffect, MobEffect> MANA_SICKNESS = registerEffect("mana_sickness", "耐魔性", ManaSicknessEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING = registerEffect("bleeding", "流血", BleedingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ACID_VENOM = registerEffect("acid_venom", "酸性毒液", AcidVenomEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CURSED_INFERNO = registerEffect("cursed_inferno", "诅咒狱火", CursedInfernoEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SILENCED = registerEffect("silenced", "沉默", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFFAFA));
    public static final DeferredHolder<MobEffect, MobEffect> CURSED = registerEffect("cursed", "诅咒", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x4F4F4F));
    public static final DeferredHolder<MobEffect, MobEffect> WITHERED_ARMOR = registerEffect("withered_armor", "枯萎盔甲", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xE0EEE0)
            .addAttributeModifier(Attributes.ARMOR, id, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> ICHOR = registerEffect("ichor", "灵液", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFD700)
            .addAttributeModifier(Attributes.ARMOR, id, -15, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> POTION_SICKNESS = registerEffect("potion_sickness", "耐药性", PotionSicknessEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BROKEN_ARMOR = registerEffect("broken_armor", "破损盔甲", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x330088)
            .addAttributeModifier(Attributes.ARMOR, id, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> STONED = registerEffect("stoned", "石化", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x999999)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, id, -3.0, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.GRAVITY, id, 0.02, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> BLOOD_BUTCHERED = registerEffect("blood_butchered", "血腥屠宰", BloodButcheredEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> TENTACLE_SPIKES = registerEffect("tentacle_spikes", "触手钉刺", TentacleSpikesEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LOVE = registerEffect("love", "爱情", () -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xEE0000));
    public static final DeferredHolder<MobEffect, MobEffect> SHIMMER = registerEffect("shimmer", "闪烁", ShimmerEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FROZEN = registerEffect("frozen", "冰冻", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x66CCFF));
    public static final DeferredHolder<MobEffect, MobEffect> STINKY = registerEffect("stinky", "恶臭", StinkyEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CRATE = registerEffect("crate", "宝匣", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD88B3F));
    public static final DeferredHolder<MobEffect, MobEffect> THE_BAST_DEFENSE = registerEffect("the_bast_defense", "巴斯特防御", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.ARMOR, id, 5.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> SHARPENED = registerEffect("sharpened", "锋利", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDDDDD)); // 改为事件
    public static final DeferredHolder<MobEffect, MobEffect> BEWITCHED = registerEffect("bewitched", "着魔", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDAA33)
            .addAttributeModifier(ConfluenceMagicLib.MINION_CAPACITY, id, 1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> AMMO_BOX = registerEffect("ammo_box", "弹药箱", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x119911));
    public static final DeferredHolder<MobEffect, MobEffect> COZY_FIRE = registerEffect("cozy_fire", "温馨之火", CozyFireEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HEART_LANTERN = registerEffect("heart_lantern", "心灯", HeartLanternEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HUNGER_DELAYED = registerEffect("hunger_delayed", "饥饿延缓", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xdd6e20));
    public static final DeferredHolder<MobEffect, MobEffect> DELICIOUS = registerEffect("delicious", "美味", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xf25778));
    public static final DeferredHolder<MobEffect, MobEffect> CHOKING = registerEffect("choking", "干噎", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x708090)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, -0.30F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> MIDAS = registerEffect("midas", "迈达斯", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFAA00));
    public static final DeferredHolder<MobEffect, MobEffect> TIPSY = registerEffect("tipsy", "踉跄", id -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xBBBB00)
            .addAttributeModifier(Attributes.ARMOR, id, -2, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.02, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> CLAIRVOYANCE = registerEffect("clairvoyance", "灵视", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9999FF)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.02, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> HOLY_PROTECTION = registerEffect("holy_protection", "神圣防护", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8888FF));
    public static final DeferredHolder<MobEffect, MobEffect> TITANIUM_BARRIER = registerEffect("titanium_barrier", "钛金屏障", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xb9bfbf));
    public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE = registerEffect("frostbite", "冻伤", FrostbiteEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SHADOWFLAME = registerEffect("shadowflame", "暗影焰", ShadowflameEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SUMMONING = registerEffect("summoning", "召唤", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9cf257)
            .addAttributeModifier(ConfluenceMagicLib.MINION_CAPACITY, id, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> WATER_CANDLE = registerEffect("water_candle", "水蜡烛", WaterCandleEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SHADOW_CANDLE = registerEffect("shadow_candle", "暗影蜡烛", () -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0x9FEFFF));
    public static final DeferredHolder<MobEffect, MobEffect> PEACE_CANDLE = registerEffect("peace_candle", "和平蜡烛", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xda9ae0)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, i -> (i + 1) * -0.26)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, i -> (i + 1) * -0.30));
    public static final DeferredHolder<MobEffect, MobEffect> HAPPY = registerEffectWithEnglishName("happy", "快乐！", "Happy!", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xe6ad25)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id.withSuffix("_doubling"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, i -> (i + 1) * 1.1)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, i -> (i + 1) * 0.1)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, i -> (i + 1) * -0.13)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, i -> (i + 1) * -0.20));
    public static final DeferredHolder<MobEffect, MobEffect> CALM = registerEffect("calm", "冷静", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6469ca)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, i -> (i + 1) * -0.49)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, i -> (i + 1) * -0.5)); //对比初始值多-0.1来拟合效果，数值暂定
    public static final DeferredHolder<MobEffect, MobEffect> BATTLE = registerEffect("battle", "战斗", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8b64ca)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, i -> (i + 1) * 0.5)  //0.5 就大概是翻倍了，数值暂定
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, i -> (i + 1) * 0.5));
    public static final DeferredHolder<MobEffect, MobEffect> ENEMY_BANNER = registerEffect("enemy_banner", "敌怪旗", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0000));
    public static final DeferredHolder<MobEffect, MobEffect> AROMATIC_SATIATION = registerEffect("aromatic_satiation", "芳息饱腹", AromaticSatiationEffect::new);

    // 药剂
    public static final DeferredHolder<MobEffect, FlaskEffect> WEAPON_IMBUE_FIRE = registerEffectWithEnglishName("weapon_imbue_fire", "武器灌注：火焰", "Weapon Imbue: Fire", FlaskOfFireEffect::new);
    public static final DeferredHolder<MobEffect, FlaskEffect> WEAPON_IMBUE_GOLD = registerEffectWithEnglishName("weapon_imbue_gold", "武器灌注：金", "Weapon Imbue: Gold", FlaskOfGoldEffect::new);

    // TODO 需要细调
    public static final DeferredHolder<MobEffect, MobEffect> DIMENSIONAL_OVERLAP = registerEffect("dimensional_overlap", "维度重叠", id -> new DimensionalOverlapEffect()
            .addAttributeModifier(ConfluenceMagicLib.VOID_EROSION_DELTA, id, AttributeModifier.Operation.ADD_VALUE, i -> i <= 0 ? -128.0f : -220.0f));

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------
    public static final DeferredHolder<MobEffect, MobEffect> JUNGLES_FURY = registerEffectWithEnglishName("jungles_fury", "丛林之怒", "Jungle's Fury", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x669933));
    public static final DeferredHolder<MobEffect, MobEffect> ELECTRIFIED = registerEffect("electrified", "带电", ElectrifiedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> GARDEN_GNOME_LUCK = registerEffect("garden_gnome_luck", "花园侏儒", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8B8B8B)
            .addAttributeModifier(Attributes.LUCK, id, 0.2, ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> SPARKLE_SLIME = registerEffect("sparkle_slime", "闪耀史莱姆", SparkleSlimeEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> WEBBED = registerEffect("webbed", "被网住", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xDDDDCC)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, -1.0, ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.JUMP_STRENGTH, id, -1.0, ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> DEMONIC_THOUGHTS = registerEffect("demonic_toughts", "邪念", DemonicThoughtsEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FROST_BURN = registerEffectWithEnglishName("frost_burn", "霜冻", "Frostburn", FrostburnEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HELLFIRE = registerEffect("hellfire", "狱炎", HellFireEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> DRYADS_BANE = registerEffectWithEnglishName("dryads_bane", "树妖祸害", "Dryad's Bane", DryadsBaneEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> DRYADS_BLESSING = registerEffectWithEnglishName("dryads_blessing", "树妖祝福", "Dryad's Blessing", DryadsBlessingEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> THE_TONGUE = registerEffect("the_tongue", "狂卷之舌", TheTongueEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HORRIFIED = registerEffect("horrified", "惊恐", HorrifiedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CRIMSON_STORM = registerEffect("crimson_storm", "猩红风暴", CrimsonStormEffect::new);
    public static final DeferredHolder<MobEffect, DriveAwayEffect> SCARED = registerEffect("scared", "惊慌", () -> new DriveAwayEffect(0.3, 200.0, 0.8, 1.2, 1.5, 0.0));

    private static double exquisitelyStuffed(int v, double v0, double v1, double v2) {
        if (v == 1) return v1;
        if (v == 2) return v2;
        return v0;
    }

    public static float getHeartReachRange(LivingEntity living) {
        return living.hasEffect(HEART_REACH) ? 12.17F : 1.75F;
    }

    public static void onLuckEffectRemove(LivingEntity living, Holder<MobEffect> mobEffect, int amplifier) {
        if (amplifier > 0 && mobEffect == LUCK_EFFECT) {
            living.addEffect(new MobEffectInstance(mobEffect, 6000, amplifier - 1));
        }
    }

    public static void onLoveEffectAdd(MobEffectInstance mobEffectInstance, LivingEntity living, Entity entity) {
        if (mobEffectInstance.is(LOVE) && living instanceof Animal animal && !animal.isBaby()) {
            animal.setInLove(entity instanceof Player player ? player : null);
        }
    }

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> registerEffect(String registryId, String chineseName, Supplier<T> factory) {
        DeferredHolder<MobEffect, T> entry = EFFECTS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> registerEffect(String registryId, String chineseName, Function<ResourceLocation, T> factory) {
        DeferredHolder<MobEffect, T> entry = EFFECTS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> registerEffectWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredHolder<MobEffect, T> entry = EFFECTS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> registerEffectWithEnglishName(String registryId, String chineseName, String englishName, Function<ResourceLocation, T> factory) {
        DeferredHolder<MobEffect, T> entry = EFFECTS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
