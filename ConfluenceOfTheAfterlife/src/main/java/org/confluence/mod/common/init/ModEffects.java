package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.effect.PublicMobEffect;
import org.confluence.mod.common.effect.beneficial.*;
import org.confluence.mod.common.effect.harmful.*;
import org.confluence.mod.common.effect.neutral.LoveEffect;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Confluence.MODID);

    public static final DeferredHolder<MobEffect, ExquisitelyStuffedEffect> EXQUISITELY_STUFFED = EFFECTS.register("exquisitely_stuffed", ExquisitelyStuffedEffect::new);
    public static final DeferredHolder<MobEffect, IronSkinEffect> IRON_SKIN = EFFECTS.register("iron_skin", IronSkinEffect::new);
    public static final DeferredHolder<MobEffect, EnduranceEffect> ENDURANCE = EFFECTS.register("endurance", EnduranceEffect::new);
    public static final DeferredHolder<MobEffect, InfernoEffect> INFERNO = EFFECTS.register("inferno", InfernoEffect::new);
    public static final DeferredHolder<MobEffect, LifeForceEffect> LIFE_FORCE = EFFECTS.register("life_force", LifeForceEffect::new);
    public static final DeferredHolder<MobEffect, RageEffect> RAGE = EFFECTS.register("rage", RageEffect::new);
    public static final DeferredHolder<MobEffect, WrathEffect> WRATH = EFFECTS.register("wrath", WrathEffect::new);
    public static final DeferredHolder<MobEffect, ThornsEffect> THORNS = EFFECTS.register("thorns", ThornsEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> MANA_REGENERATION = EFFECTS.register("mana_regeneration", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x6600CC));
    public static final DeferredHolder<MobEffect, TitanEffect> TITAN = EFFECTS.register("titan", TitanEffect::new);
    public static final DeferredHolder<MobEffect, BuilderEffect> BUILDER = EFFECTS.register("builder", BuilderEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> FISHING = EFFECTS.register("fishing", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF));
    public static final DeferredHolder<MobEffect, MagicPowerEffect> MAGIC_POWER = EFFECTS.register("magic_power", MagicPowerEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> OBSIDIAN_SKIN = EFFECTS.register("obsidian_skin", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x660066));
    public static final DeferredHolder<MobEffect, LuckEffect> LUCK_EFFECT = EFFECTS.register("luck", LuckEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> WATER_WALKING = EFFECTS.register("water_walking", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x0000BB));
    public static final DeferredHolder<MobEffect, HeartReachEffect> HEART_REACH = EFFECTS.register("heart_reach", HeartReachEffect::new);
    public static final DeferredHolder<MobEffect, ArcheryEffect> ARCHERY = EFFECTS.register("archery", ArcheryEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> FLIPPER = EFFECTS.register("flipper", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x000088));
    public static final DeferredHolder<MobEffect, ShineEffect> SHINE = EFFECTS.register("shine", ShineEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SPELUNKER = EFFECTS.register("spelunker", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF00));
    public static final DeferredHolder<MobEffect, MobEffect> HUNTER = EFFECTS.register("hunter", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFC800));
    public static final DeferredHolder<MobEffect, MobEffect> DANGER_SENSE = EFFECTS.register("danger_sense", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0xFFAFAF));
    public static final DeferredHolder<MobEffect, ManaSicknessEffect> MANA_SICKNESS = EFFECTS.register("mana_sickness", ManaSicknessEffect::new);
    public static final DeferredHolder<MobEffect, BleedingEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);
    public static final DeferredHolder<MobEffect, AcidVenomEffect> ACID_VENOM = EFFECTS.register("acid_venom", AcidVenomEffect::new);
    public static final DeferredHolder<MobEffect, FrostburnEffect> FROST_BURN = EFFECTS.register("frost_burn", FrostburnEffect::new);
    public static final DeferredHolder<MobEffect, CursedInfernoEffect> CURSED_INFERNO = EFFECTS.register("cursed_inferno", CursedInfernoEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> SILENCED = EFFECTS.register("silenced", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0xFFFAFA));
    public static final DeferredHolder<MobEffect, PublicMobEffect> CURSED = EFFECTS.register("cursed", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x4F4F4F));
    public static final DeferredHolder<MobEffect, WitheredArmorEffect> WITHERED_ARMOR = EFFECTS.register("withered_armor", WitheredArmorEffect::new);
    public static final DeferredHolder<MobEffect, IchorEffect> ICHOR = EFFECTS.register("ichor", IchorEffect::new);
    public static final DeferredHolder<MobEffect, PotionSicknessEffect> POTION_SICKNESS = EFFECTS.register("potion_sickness", PotionSicknessEffect::new);
    public static final DeferredHolder<MobEffect, BrokenArmorEffect> BROKEN_ARMOR = EFFECTS.register("broken_armor", BrokenArmorEffect::new);
    public static final DeferredHolder<MobEffect, StonedEffect> STONED = EFFECTS.register("stoned", StonedEffect::new);
    public static final DeferredHolder<MobEffect, BloodButcheredEffect> BLOOD_BUTCHERED = EFFECTS.register("blood_butchered", BloodButcheredEffect::new);
    public static final DeferredHolder<MobEffect, TentacleSpikesEffect> TENTACLE_SPIKES = EFFECTS.register("tentacle_spikes", TentacleSpikesEffect::new);
    public static final DeferredHolder<MobEffect, LoveEffect> LOVE = EFFECTS.register("love", LoveEffect::new);
    public static final DeferredHolder<MobEffect, ShimmerEffect> SHIMMER = EFFECTS.register("shimmer", ShimmerEffect::new);
    public static final DeferredHolder<MobEffect, PublicMobEffect> FROZEN = EFFECTS.register("frozen", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x66CCFF));
}
