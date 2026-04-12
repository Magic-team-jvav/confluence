package org.confluence.mod.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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
import org.confluence.mod.common.effect.neutral.ShimmerEffect;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Confluence.MODID);
    public static final EffectCure FLASK = EffectCure.get("confluence:flask");
    public static final EffectCure CANNOT_REMOVE_BY_NURSE = EffectCure.get("confluence:cannot_remove_by_nurse");

    public static final DeferredHolder<MobEffect, MobEffect> EXQUISITELY_STUFFED = EFFECTS.register("exquisitely_stuffed", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, ADD_VALUE, v -> exquisitelyStuffed(v, 0.04, 0.06, 0.08))
            .addAttributeModifier(Attributes.ARMOR, id, ADD_VALUE, v -> exquisitelyStuffed(v, 1, 2, 3))
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.02, 0.03, 0.04))
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getRangedDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getSummonDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(ConfluenceMagicLib.SUMMON_KNOCKBACK, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
    );
    public static final DeferredHolder<MobEffect, MobEffect> IRON_SKIN = EFFECTS.register("iron_skin", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x184F5)
            .addAttributeModifier(Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> ENDURANCE = EFFECTS.register("endurance", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x708090)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, id, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> INFERNO = EFFECTS.register("inferno", InfernoEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LIFE_FORCE = EFFECTS.register("life_force", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC0CB)
            .addAttributeModifier(Attributes.MAX_HEALTH, id, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> RAGE = EFFECTS.register("rage", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF4500)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> WRATH = EFFECTS.register("wrath", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF8C00)
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getRangedDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getSummonDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredHolder<MobEffect, MobEffect> THORNS = EFFECTS.register("thorns", ThornsEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> MANA_REGENERATION = EFFECTS.register("mana_regeneration", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6600CC));
    public static final DeferredHolder<MobEffect, MobEffect> STAR_IN_A_BOTTLE = EFFECTS.register("star_in_a_bottle", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF66));
    public static final DeferredHolder<MobEffect, MobEffect> TITAN = EFFECTS.register("titan", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD2B48C)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> BUILDER = EFFECTS.register("builder", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8B6914)
            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, id, 3, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> FISHING = EFFECTS.register("fishing", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF));
    public static final DeferredHolder<MobEffect, MobEffect> MAGIC_POWER = EFFECTS.register("magic_power", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xCC00CC)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> OBSIDIAN_SKIN = EFFECTS.register("obsidian_skin", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x660066));
    public static final DeferredHolder<MobEffect, MobEffect> LUCK_EFFECT = EFFECTS.register("luck", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x39C5BB)
            .addAttributeModifier(Attributes.LUCK, id, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.5));
    public static final DeferredHolder<MobEffect, MobEffect> WATER_WALKING = EFFECTS.register("water_walking", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x0000BB));
    public static final DeferredHolder<MobEffect, MobEffect> HEART_REACH = EFFECTS.register("heart_reach", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0099));
    public static final DeferredHolder<MobEffect, MobEffect> ARCHERY = EFFECTS.register("archery", ArcheryEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FLIPPER = EFFECTS.register("flipper", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000088));
    public static final DeferredHolder<MobEffect, MobEffect> SHINE = EFFECTS.register("shine", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF66));
    public static final DeferredHolder<MobEffect, MobEffect> SPELUNKER = EFFECTS.register("spelunker", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00));
    public static final DeferredHolder<MobEffect, MobEffect> HUNTER = EFFECTS.register("hunter", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC800));
    public static final DeferredHolder<MobEffect, MobEffect> DANGER_SENSE = EFFECTS.register("danger_sense", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFAFAF));
    public static final DeferredHolder<MobEffect, MobEffect> MANA_SICKNESS = EFFECTS.register("mana_sickness", ManaSicknessEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ACID_VENOM = EFFECTS.register("acid_venom", AcidVenomEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CURSED_INFERNO = EFFECTS.register("cursed_inferno", CursedInfernoEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SILENCED = EFFECTS.register("silenced", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFFAFA));
    public static final DeferredHolder<MobEffect, MobEffect> CURSED = EFFECTS.register("cursed", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x4F4F4F));
    public static final DeferredHolder<MobEffect, MobEffect> WITHERED_ARMOR = EFFECTS.register("withered_armor", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xE0EEE0)
            .addAttributeModifier(Attributes.ARMOR, id, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> ICHOR = EFFECTS.register("ichor", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFD700)
            .addAttributeModifier(Attributes.ARMOR, id, -15, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> POTION_SICKNESS = EFFECTS.register("potion_sickness", PotionSicknessEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BROKEN_ARMOR = EFFECTS.register("broken_armor", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x330088)
            .addAttributeModifier(Attributes.ARMOR, id, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> STONED = EFFECTS.register("stoned", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x999999)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, id, -3.0, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.GRAVITY, id, 0.02, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> BLOOD_BUTCHERED = EFFECTS.register("blood_butchered", BloodButcheredEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> TENTACLE_SPIKES = EFFECTS.register("tentacle_spikes", TentacleSpikesEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LOVE = EFFECTS.register("love", () -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xEE0000));
    public static final DeferredHolder<MobEffect, MobEffect> SHIMMER = EFFECTS.register("shimmer", ShimmerEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FROZEN = EFFECTS.register("frozen", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x66CCFF));
    public static final DeferredHolder<MobEffect, MobEffect> STINKY = EFFECTS.register("stinky", StinkyEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CRATE = EFFECTS.register("crate", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD88B3F));
    public static final DeferredHolder<MobEffect, MobEffect> THE_BAST_DEFENSE = EFFECTS.register("the_bast_defense", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.ARMOR, id, 5.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> SHARPENED = EFFECTS.register("sharpened", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDDDDD)
            .addAttributeModifier(LibAttributes.getArmorPenetration(), id, 12.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> BEWITCHED = EFFECTS.register("bewitched", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDAA33)
            .addAttributeModifier(ConfluenceMagicLib.MINION_CAPACITY, id, 1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> AMMO_BOX = EFFECTS.register("ammo_box", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x119911));
    public static final DeferredHolder<MobEffect, MobEffect> COZY_FIRE = EFFECTS.register("cozy_fire", CozyFireEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HEART_LANTERN = EFFECTS.register("heart_lantern", HeartLanternEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HUNGER_DELAYED = EFFECTS.register("hunger_delayed", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xdd6e20));
    public static final DeferredHolder<MobEffect, MobEffect> DELICIOUS = EFFECTS.register("delicious", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xf25778));
    public static final DeferredHolder<MobEffect, MobEffect> CHOKING = EFFECTS.register("choking", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x708090)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, -0.30F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> MIDAS = EFFECTS.register("midas", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFAA00));
    public static final DeferredHolder<MobEffect, MobEffect> TIPSY = EFFECTS.register("tipsy", id -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xBBBB00)
            .addAttributeModifier(Attributes.ARMOR, id, -2, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.02, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> CLAIRVOYANCE = EFFECTS.register("clairvoyance", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9999FF)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.02, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> HOLY_PROTECTION = EFFECTS.register("holy_protection", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8888FF));
    public static final DeferredHolder<MobEffect, MobEffect> TITANIUM_BARRIER = EFFECTS.register("titanium_barrier", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xb9bfbf));
    public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE = EFFECTS.register("frostbite", FrostbiteEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SHADOWFLAME = EFFECTS.register("shadowflame", ShadowflameEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SUMMONING = EFFECTS.register("summoning", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9cf257)
            .addAttributeModifier(ConfluenceMagicLib.MINION_CAPACITY, id, 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> WATER_CANDLE = EFFECTS.register("water_candle", WaterCandleEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PEACE_CANDLE = EFFECTS.register("peace_candle", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xda9ae0)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, (i) -> (i + 1) * -0.26)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, (i) -> (i + 1) * -0.30));
    public static final DeferredHolder<MobEffect, MobEffect> HAPPY = EFFECTS.register("happy", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xe6ad25)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id.withSuffix("_doubling"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, (i) -> (i + 1) * 1.1)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, (i) -> (i + 1) * 0.1)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, (i) -> (i + 1) * -0.13)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, (i) -> (i + 1) * -0.20));
    public static final DeferredHolder<MobEffect, MobEffect> CALM = EFFECTS.register("calm", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6469ca)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, (i) -> (i + 1) * -0.49)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, (i) -> (i + 1) * -0.5)); //对比初始值多-0.1来拟合效果，数值暂定
    public static final DeferredHolder<MobEffect, MobEffect> BATTLE = EFFECTS.register("battle", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8b64ca)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, (i) -> (i + 1) * 0.5)  //0.5 就大概是翻倍了，数值暂定
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, (i) -> (i + 1) * 0.5));
    public static final DeferredHolder<MobEffect, MobEffect> ENEMY_BANNER = EFFECTS.register("enemy_banner", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0000));
    public static final DeferredHolder<MobEffect, MobEffect> AROMATIC_SATIATION = EFFECTS.register("aromatic_satiation", AromaticSatiationEffect::new);

    // 药剂
    public static final DeferredHolder<MobEffect, FlaskEffect> WEAPON_IMBUE_FIRE = EFFECTS.register("weapon_imbue_fire", FlaskOfFireEffect::new);
    public static final DeferredHolder<MobEffect, FlaskEffect> WEAPON_IMBUE_GOLD = EFFECTS.register("weapon_imbue_gold", FlaskOfGoldEffect::new);

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
}
