package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.effect.PublicMobEffect;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.effect.beneficial.*;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.flask.FlaskOfFireEffect;
import org.confluence.mod.common.effect.flask.FlaskOfGoldEffect;
import org.confluence.mod.common.effect.harmful.*;
import org.confluence.mod.common.effect.neutral.LoveEffect;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;
import org.mesdag.particlestorm.api.MolangParticleMobEffect;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Confluence.MODID);
    public static final EffectCure FLASK = EffectCure.get("confluence:flask");
    public static final EffectCure CANNOT_REMOVE_BY_NURSE = EffectCure.get("confluence:cannot_remove_by_nurse");

    public static final DeferredHolder<MobEffect, MobEffect> EXQUISITELY_STUFFED = EFFECTS.register("exquisitely_stuffed", ExquisitelyStuffedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> IRON_SKIN = EFFECTS.register("iron_skin", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x184F5)
            .addAttributeModifier(Attributes.ARMOR, Confluence.asResource("iron_skin"), 4, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> ENDURANCE = EFFECTS.register("endurance", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x708090)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Confluence.asResource("endurance"), 1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> INFERNO = EFFECTS.register("inferno", InfernoEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LIFE_FORCE = EFFECTS.register("life_force", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC0CB)
            .addAttributeModifier(Attributes.MAX_HEALTH, Confluence.asResource("life_force"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> RAGE = EFFECTS.register("rage", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF4500)
            .addAttributeModifier(TCAttributes.getCriticalChance(), Confluence.asResource("rage"), 0.1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> WRATH = EFFECTS.register("wrath", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF8C00)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, Confluence.asResource("wrath"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(TCAttributes.getRangedDamage(), Confluence.asResource("wrath"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(TCAttributes.getMagicDamage(), Confluence.asResource("wrath"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(TEAttributes.SUMMON_DAMAGE, Confluence.asResource("wrath"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredHolder<MobEffect, MobEffect> THORNS = EFFECTS.register("thorns", ThornsEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> MANA_REGENERATION = EFFECTS.register("mana_regeneration", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6600CC));
    public static final DeferredHolder<MobEffect, MobEffect> TITAN = EFFECTS.register("titan", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD2B48C)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, Confluence.asResource("titan"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> BUILDER = EFFECTS.register("builder", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8B6914)
            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, Confluence.asResource("builder"), 3, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> FISHING = EFFECTS.register("fishing", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF));
    public static final DeferredHolder<MobEffect, MobEffect> MAGIC_POWER = EFFECTS.register("magic_power", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xCC00CC)
            .addAttributeModifier(TCAttributes.getMagicDamage(), Confluence.asResource("magic_power"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> OBSIDIAN_SKIN = EFFECTS.register("obsidian_skin", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x660066));
    public static final DeferredHolder<MobEffect, MobEffect> LUCK_EFFECT = EFFECTS.register("luck", LuckEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> WATER_WALKING = EFFECTS.register("water_walking", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x0000BB));
    public static final DeferredHolder<MobEffect, MobEffect> HEART_REACH = EFFECTS.register("heart_reach", HeartReachEffect::new);
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
    public static final DeferredHolder<MobEffect, MobEffect> WITHERED_ARMOR = EFFECTS.register("withered_armor", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xE0EEE0)
            .addAttributeModifier(Attributes.ARMOR, Confluence.asResource("withered_armor"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> ICHOR = EFFECTS.register("ichor", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFD700)
            .addAttributeModifier(Attributes.ARMOR, Confluence.asResource("ichor"), -15, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> POTION_SICKNESS = EFFECTS.register("potion_sickness", PotionSicknessEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BROKEN_ARMOR = EFFECTS.register("broken_armor", BrokenArmorEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> STONED = EFFECTS.register("stoned", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x999999)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Confluence.asResource("stoned"), -3.0, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.GRAVITY, Confluence.asResource("stoned"), 0.02, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> BLOOD_BUTCHERED = EFFECTS.register("blood_butchered", BloodButcheredEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> TENTACLE_SPIKES = EFFECTS.register("tentacle_spikes", TentacleSpikesEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LOVE = EFFECTS.register("love", LoveEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SHIMMER = EFFECTS.register("shimmer", ShimmerEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FROZEN = EFFECTS.register("frozen", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x66CCFF));
    public static final DeferredHolder<MobEffect, MobEffect> STINKY = EFFECTS.register("stinky", () -> new MolangParticleMobEffect(MobEffectCategory.HARMFUL, 0x99FF00, Confluence.asResource("stinky")));
    public static final DeferredHolder<MobEffect, MobEffect> CRATE = EFFECTS.register("crate", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD88B3F));
    public static final DeferredHolder<MobEffect, MobEffect> THE_BAST_DEFENSE = EFFECTS.register("the_bast_defense", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.ARMOR, Confluence.asResource("the_bast_defense"), 5.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> SHARPENED = EFFECTS.register("sharpened", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDDDDD)
            .addAttributeModifier(TCAttributes.getArmorPenetration(), Confluence.asResource("sharpened"), 12.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> BEWITCHED = EFFECTS.register("bewitched", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDAA33)
            .addAttributeModifier(TEAttributes.MINION_CAPACITY, Confluence.asResource("bewitched"), 1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> AMMO_BOX = EFFECTS.register("ammo_box", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x119911));
    public static final DeferredHolder<MobEffect, MobEffect> COZY_FIRE = EFFECTS.register("cozy_fire", CozyFireEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HUNGER_DELAYED = EFFECTS.register("hunger_delayed", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0099));
    public static final DeferredHolder<MobEffect, MobEffect> DELICIOUS = EFFECTS.register("delicious", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0099));
    public static final DeferredHolder<MobEffect, MobEffect> CHOKING = EFFECTS.register("choking", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x708090)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, Confluence.asResource("choking"), -0.30F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> MIDAS = EFFECTS.register("midas", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFAA00));
    public static final DeferredHolder<MobEffect, MobEffect> TIPSY = EFFECTS.register("tipsy", () -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xBBBB00)
            .addAttributeModifier(Attributes.ARMOR, Confluence.asResource("tipsy"), -2, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(TCAttributes.getCriticalChance(), Confluence.asResource("tipsy"), 0.02, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, Confluence.asResource("tipsy"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, Confluence.asResource("tipsy"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> CLAIRVOYANCE = EFFECTS.register("clairvoyance", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9999FF)
            .addAttributeModifier(TCAttributes.getMagicDamage(), Confluence.asResource("clairvoyance"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(TCAttributes.getCriticalChance(), Confluence.asResource("clairvoyance"), 0.02, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> HOLY_PROTECTION = EFFECTS.register("holy_protection", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8888FF));
    public static final DeferredHolder<MobEffect, MobEffect> TITANIUM_BARRIER = EFFECTS.register("titanium_barrier", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x777777));
    public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE = EFFECTS.register("frostbite", FrostbiteEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SHADOWFLAME = EFFECTS.register("shadowflame", ShadowflameEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SUMMONING = EFFECTS.register("summoning", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x888888)
            .addAttributeModifier(TEAttributes.MINION_CAPACITY, Confluence.asResource("summoning"), 1, AttributeModifier.Operation.ADD_VALUE));

    // TODO 玩家在 y260-320（太空）中将 33 替换为 167
    public static final DeferredHolder<MobEffect, MobEffect> WATER_CANDLE = EFFECTS.register("water_candle", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFF0000)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_SPEED_FACTOR, Confluence.asResource("water_candle"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * 0.33)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_COUNT_FACTOR, Confluence.asResource("water_candle"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * 0.50));
    public static final DeferredHolder<MobEffect, MobEffect> PEACE_CANDLE = EFFECTS.register("peace_candle", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_SPEED_FACTOR, Confluence.asResource("peace_candle"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * -0.23)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_COUNT_FACTOR, Confluence.asResource("peace_candle"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * -0.30));
    public static final DeferredHolder<MobEffect, MobEffect> HAPPY = EFFECTS.register("happy", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, Confluence.asResource("happy_doubling"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, (i) -> i * 1.1)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, Confluence.asResource("happy"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, (i) -> i * 0.1)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_SPEED_FACTOR, Confluence.asResource("happy"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * -0.17)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_COUNT_FACTOR, Confluence.asResource("happy"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * -0.20));
    public static final DeferredHolder<MobEffect, MobEffect> CALM = EFFECTS.register("calm", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_SPEED_FACTOR, Confluence.asResource("calm"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * -0.39)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_COUNT_FACTOR, Confluence.asResource("calm"), AttributeModifier.Operation.ADD_VALUE, (i) -> i * -0.40));
    public static final DeferredHolder<MobEffect, MobEffect> BATTLE = EFFECTS.register("battle", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_SPEED_FACTOR, Confluence.asResource("battle"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, (i) -> i * 2)
            .addAttributeModifier(ConfluenceMagicLib.PLAYER_MONSTER_SPAWN_COUNT_FACTOR, Confluence.asResource("battle"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, (i) -> i * 2));

    // 药剂
    public static final DeferredHolder<MobEffect, FlaskEffect> WEAPON_IMBUE_FIRE = EFFECTS.register("weapon_imbue_fire", FlaskOfFireEffect::new);
    public static final DeferredHolder<MobEffect, FlaskEffect> WEAPON_IMBUE_GOLD = EFFECTS.register("weapon_imbue_gold", FlaskOfGoldEffect::new);
}
