package org.confluence.mod.common.init;

import PortLib.extensions.net.minecraft.world.effect.MobEffectInstance.PortMobEffectInstanceExtension;
import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
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
import org.mesdag.portlib.wrapper.common.PortEffectCure;

import java.util.function.Function;

import static org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.PortOperation.*;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Confluence.MODID);
    public static final PortEffectCure FLASK = PortEffectCure.get("confluence:flask");
    public static final PortEffectCure CANNOT_REMOVE_BY_NURSE = PortEffectCure.get("confluence:cannot_remove_by_nurse");

    public static final RegistryObject<MobEffect> EXQUISITELY_STUFFED = register("exquisitely_stuffed", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, ADD_VALUE, v -> exquisitelyStuffed(v, 0.04, 0.06, 0.08))
            .addAttributeModifier(Attributes.ARMOR, id, ADD_VALUE, v -> exquisitelyStuffed(v, 1, 2, 3))
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.02, 0.03, 0.04))
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getRangedDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(LibAttributes.getSummonDamage(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(PortAttributesExtension.blockBreakSpeed(), id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
            .addAttributeModifier(ConfluenceMagicLib.SUMMON_KNOCKBACK, id, ADD_MULTIPLIED_TOTAL, v -> exquisitelyStuffed(v, 0.05, 0.075, 0.10))
    );
    public static final RegistryObject<MobEffect> IRON_SKIN = register("iron_skin", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x184F5)
            .addAttributeModifier(Attributes.ARMOR, id, 4, ADD_VALUE));
    public static final RegistryObject<MobEffect> ENDURANCE = register("endurance", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x708090)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, id, 1, ADD_VALUE));
    public static final RegistryObject<MobEffect> INFERNO = register("inferno", InfernoEffect::new);
    public static final RegistryObject<MobEffect> LIFE_FORCE = register("life_force", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC0CB)
            .addAttributeModifier(Attributes.MAX_HEALTH, id, 0.2, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> RAGE = register("rage", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF4500)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.1, ADD_VALUE));
    public static final RegistryObject<MobEffect> WRATH = register("wrath", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFF8C00)
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, 0.1, ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getRangedDamage(), id, 0.1, ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.1, ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getSummonDamage(), id, 0.1, ADD_MULTIPLIED_TOTAL)
    );
    public static final RegistryObject<MobEffect> THORNS = EFFECTS.register("thorns", ThornsEffect::new);
    public static final RegistryObject<MobEffect> MANA_REGENERATION = EFFECTS.register("mana_regeneration", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6600CC));
    public static final RegistryObject<MobEffect> STAR_IN_A_BOTTLE = EFFECTS.register("star_in_a_bottle", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF66));
    public static final RegistryObject<MobEffect> TITAN = register("titan", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD2B48C)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, id, 0.5, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> BUILDER = register("builder", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8B6914)
            .addAttributeModifier(PortAttributesExtension.blockInteractionRange(), id, 3, ADD_VALUE));
    public static final RegistryObject<MobEffect> FISHING = EFFECTS.register("fishing", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF));
    public static final RegistryObject<MobEffect> MAGIC_POWER = register("magic_power", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xCC00CC)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.2, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> OBSIDIAN_SKIN = EFFECTS.register("obsidian_skin", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x660066));
    public static final RegistryObject<MobEffect> LUCK_EFFECT = register("luck", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x39C5BB)
            .addAttributeModifier(Attributes.LUCK, id, ADD_VALUE, amplifier -> (amplifier + 1) * 0.5));
    public static final RegistryObject<MobEffect> WATER_WALKING = EFFECTS.register("water_walking", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x0000BB));
    public static final RegistryObject<MobEffect> HEART_REACH = EFFECTS.register("heart_reach", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0099));
    public static final RegistryObject<MobEffect> ARCHERY = EFFECTS.register("archery", ArcheryEffect::new);
    public static final RegistryObject<MobEffect> FLIPPER = EFFECTS.register("flipper", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000088));
    public static final RegistryObject<MobEffect> SHINE = EFFECTS.register("shine", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF66));
    public static final RegistryObject<MobEffect> SPELUNKER = EFFECTS.register("spelunker", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00));
    public static final RegistryObject<MobEffect> HUNTER = EFFECTS.register("hunter", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC800));
    public static final RegistryObject<MobEffect> DANGER_SENSE = EFFECTS.register("danger_sense", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFAFAF));
    public static final RegistryObject<MobEffect> MANA_SICKNESS = EFFECTS.register("mana_sickness", ManaSicknessEffect::new);
    public static final RegistryObject<MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);
    public static final RegistryObject<MobEffect> ACID_VENOM = EFFECTS.register("acid_venom", AcidVenomEffect::new);
    public static final RegistryObject<MobEffect> CURSED_INFERNO = EFFECTS.register("cursed_inferno", CursedInfernoEffect::new);
    public static final RegistryObject<MobEffect> SILENCED = EFFECTS.register("silenced", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFFAFA));
    public static final RegistryObject<MobEffect> CURSED = EFFECTS.register("cursed", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x4F4F4F));
    public static final RegistryObject<MobEffect> WITHERED_ARMOR = register("withered_armor", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xE0EEE0)
            .addAttributeModifier(Attributes.ARMOR, id, -0.5, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> ICHOR = register("ichor", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFD700)
            .addAttributeModifier(Attributes.ARMOR, id, -15, ADD_VALUE));
    public static final RegistryObject<MobEffect> POTION_SICKNESS = EFFECTS.register("potion_sickness", PotionSicknessEffect::new);
    public static final RegistryObject<MobEffect> BROKEN_ARMOR = register("broken_armor", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x330088)
            .addAttributeModifier(Attributes.ARMOR, id, -0.5, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> STONED = register("stoned", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x999999)
            .addAttributeModifier(PortAttributesExtension.safeFallDistance(), id, -3.0, ADD_VALUE)
            .addAttributeModifier(PortAttributesExtension.gravity(), id, 0.02, ADD_VALUE));
    public static final RegistryObject<MobEffect> BLOOD_BUTCHERED = EFFECTS.register("blood_butchered", BloodButcheredEffect::new);
    public static final RegistryObject<MobEffect> TENTACLE_SPIKES = EFFECTS.register("tentacle_spikes", TentacleSpikesEffect::new);
    public static final RegistryObject<MobEffect> LOVE = EFFECTS.register("love", () -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xEE0000));
    public static final RegistryObject<MobEffect> SHIMMER = EFFECTS.register("shimmer", ShimmerEffect::new);
    public static final RegistryObject<MobEffect> FROZEN = EFFECTS.register("frozen", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x66CCFF));
    public static final RegistryObject<MobEffect> STINKY = register("stinky", StinkyEffect::new);
    public static final RegistryObject<MobEffect> CRATE = EFFECTS.register("crate", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xD88B3F));
    public static final RegistryObject<MobEffect> THE_BAST_DEFENSE = register("the_bast_defense", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.ARMOR, id, 5.0, ADD_VALUE));
    public static final RegistryObject<MobEffect> SHARPENED = EFFECTS.register("sharpened", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDDDDD)); // 改为事件
    public static final RegistryObject<MobEffect> BEWITCHED = register("bewitched", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xDDAA33)
            .addAttributeModifier(ConfluenceMagicLib.MINION_CAPACITY, id, 1.0, ADD_VALUE));
    public static final RegistryObject<MobEffect> AMMO_BOX = EFFECTS.register("ammo_box", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x119911));
    public static final RegistryObject<MobEffect> COZY_FIRE = EFFECTS.register("cozy_fire", CozyFireEffect::new);
    public static final RegistryObject<MobEffect> HEART_LANTERN = EFFECTS.register("heart_lantern", HeartLanternEffect::new);
    public static final RegistryObject<MobEffect> HUNGER_DELAYED = EFFECTS.register("hunger_delayed", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xdd6e20));
    public static final RegistryObject<MobEffect> DELICIOUS = EFFECTS.register("delicious", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xf25778));
    public static final RegistryObject<MobEffect> CHOKING = register("choking", id -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x708090)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, -0.30F, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> MIDAS = EFFECTS.register("midas", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFAA00));
    public static final RegistryObject<MobEffect> TIPSY = register("tipsy", id -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xBBBB00)
            .addAttributeModifier(Attributes.ARMOR, id, -2, ADD_VALUE)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.02, ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, 0.1, ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getAttackDamage(), id, 0.1, ADD_MULTIPLIED_TOTAL));
    public static final RegistryObject<MobEffect> CLAIRVOYANCE = register("clairvoyance", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9999FF)
            .addAttributeModifier(LibAttributes.getMagicDamage(), id, 0.05, ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(LibAttributes.getCriticalChance(), id, 0.02, ADD_VALUE));
    public static final RegistryObject<MobEffect> HOLY_PROTECTION = EFFECTS.register("holy_protection", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8888FF));
    public static final RegistryObject<MobEffect> TITANIUM_BARRIER = EFFECTS.register("titanium_barrier", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xb9bfbf));
    public static final RegistryObject<MobEffect> FROSTBITE = EFFECTS.register("frostbite", FrostbiteEffect::new);
    public static final RegistryObject<MobEffect> SHADOWFLAME = EFFECTS.register("shadowflame", ShadowflameEffect::new);
    public static final RegistryObject<MobEffect> SUMMONING = register("summoning", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x9cf257)
            .addAttributeModifier(ConfluenceMagicLib.MINION_CAPACITY, id, 1, ADD_VALUE));
    public static final RegistryObject<MobEffect> WATER_CANDLE = register("water_candle", WaterCandleEffect::new);
    public static final RegistryObject<MobEffect> PEACE_CANDLE = register("peace_candle", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xda9ae0)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, ADD_VALUE, (i) -> (i + 1) * -0.26)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, ADD_VALUE, (i) -> (i + 1) * -0.30));
    public static final RegistryObject<MobEffect> HAPPY = register("happy", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xe6ad25)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id.withSuffix("_doubling"), ADD_MULTIPLIED_BASE, (i) -> (i + 1) * 1.1)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, ADD_MULTIPLIED_TOTAL, (i) -> (i + 1) * 0.1)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, ADD_VALUE, (i) -> (i + 1) * -0.13)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, ADD_VALUE, (i) -> (i + 1) * -0.20));
    public static final RegistryObject<MobEffect> CALM = register("calm", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6469ca)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, ADD_VALUE, (i) -> (i + 1) * -0.49)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, ADD_VALUE, (i) -> (i + 1) * -0.5)); //对比初始值多-0.1来拟合效果，数值暂定
    public static final RegistryObject<MobEffect> BATTLE = register("battle", id -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x8b64ca)
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, ADD_MULTIPLIED_BASE, (i) -> (i + 1) * 0.5)  //0.5 就大概是翻倍了，数值暂定
            .addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, ADD_MULTIPLIED_BASE, (i) -> (i + 1) * 0.5));
    public static final RegistryObject<MobEffect> ENEMY_BANNER = EFFECTS.register("enemy_banner", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xAA0000));
    public static final RegistryObject<MobEffect> AROMATIC_SATIATION = EFFECTS.register("aromatic_satiation", AromaticSatiationEffect::new);
    public static final RegistryObject<MobEffect> DEMONIC_THOUGHTS = EFFECTS.register("demonic_toughts", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x5d478b));

    // 药剂
    public static final RegistryObject<FlaskEffect> WEAPON_IMBUE_FIRE = EFFECTS.register("weapon_imbue_fire", FlaskOfFireEffect::new);
    public static final RegistryObject<FlaskEffect> WEAPON_IMBUE_GOLD = EFFECTS.register("weapon_imbue_gold", FlaskOfGoldEffect::new);

    private static <T extends MobEffect> RegistryObject<T> register(String name, Function<ResourceLocation, T> function) {
        return PortDeferredRegisterExtension.register(EFFECTS, name, function);
    }

    private static double exquisitelyStuffed(int v, double v0, double v1, double v2) {
        if (v == 1) return v1;
        if (v == 2) return v2;
        return v0;
    }

    public static float getHeartReachRange(LivingEntity living) {
        return living.hasEffect(HEART_REACH.get()) ? 12.17F : 1.75F;
    }

    public static void onLuckEffectRemove(LivingEntity living, MobEffect mobEffect, int amplifier) {
        if (amplifier > 0 && mobEffect == LUCK_EFFECT.get()) {
            living.addEffect(new MobEffectInstance(mobEffect, 6000, amplifier - 1));
        }
    }

    public static void onLoveEffectAdd(MobEffectInstance instance, LivingEntity living, Entity entity) {
        if (PortMobEffectInstanceExtension.is(instance, LOVE) && living instanceof Animal animal && !animal.isBaby()) {
            animal.setInLove(entity instanceof Player player ? player : null);
        }
    }
}
