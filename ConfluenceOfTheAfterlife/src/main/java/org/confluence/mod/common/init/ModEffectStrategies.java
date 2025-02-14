package org.confluence.mod.common.init;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.terraentity.hit_effect.EffectStrategy;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.registries.EffectStrategies;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.confluence.terraentity.hit_effect.EffectStrategy.*;

/**
 * <h1>攻击时给敌人施加的效果或回调</h1>
 * <p> 适用于剑、弹射物、生物命中特效等 </p>
 *
 * @author coffee
 */
public final class ModEffectStrategies {
    public static final EffectStrategies EFFECT_STRATEGY = EffectStrategies.create(Confluence.MODID);

    public static DeferredHolder<EffectStrategy, EffectStrategy> createEffect(String name, BiConsumer<LivingEntity, LivingEntity> effect) {
        return EFFECT_STRATEGY.register(name, ()->new EffectStrategy(name, effect));
    }

    public static DeferredHolder<EffectStrategy, EffectStrategy> createEffect(String name, String en_us, String zh_cn, BiConsumer<LivingEntity, LivingEntity> effect) {
        return EFFECT_STRATEGY.register(name, ()->new EffectStrategy(name,en_us,zh_cn, effect));
    }


    /**未定义效果*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> UNDEFINED = createEffect("undefined", "undefined", "未定义效果",
            UNDEFINED_EFFECT);

    /**血腥屠刀*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> BLOOD_BUTCHERED_EFFECT = createEffect("blood_butchered", "blood_butchered", "血腥屠宰",
            TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(ModEffects.BLOOD_BUTCHERED, 180, 0, 4, 0.5f));

    /**蝙蝠棍*/
    public static final  DeferredHolder<EffectStrategy, EffectStrategy> BAT_FANG_EFFECT = createEffect("bat", "blood absorb +1 hp", "吸血 +1 hp",
            (owner, entity)-> owner.heal(1));

    /** 魔光剑*/
    public static final  DeferredHolder<EffectStrategy, EffectStrategy> LIGHTS_BANE_EFFECT = createEffect("lights_bane","summon lights bane", "召唤魔光剑",
            ON_HIT_PROJECTILE.apply((level)->ModEntities.LIGHTS_BANE_PROJECTILE.get().create(level).addAttackDamage(7f)));

    /**触手钉锤*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> TENTACLE_SPIKES_EFFECT =createEffect("tentacle_spikes","tentacle spikes", "触手钉锤",
            TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(ModEffects.TENTACLE_SPIKES, 180, 0, 4, 0.5f));

    /**猎弓*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> HUNTING_RIFLE_EFFECT = createEffect("hunting_4_sec","hunting 4 seconds","狩猎 4秒",
            EffectStrategy.TIME_EFFECT.apply(TEEffects.SUMMON_FOCUS, 80));

    /**火山*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> HELL_FIRE_EFFECT = createEffect("hell_fire_5_sec","hell fire 5 seconds", "烈火焚身 5秒",
            TIME_EFFECT.apply(ModEffects.HELL_FIRE,5 * 20));
    /**着火*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> SET_FIRE_EFFECT = createEffect("set_fire_5_sec","set fire 5 seconds", "着火啦 5秒",
            SET_FIRE.apply(5 * 20, 1f));

    /**霜冻*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> FROST_BURN_EFFECT = createEffect("frozen_burn_10_sec","frozen burn 10 seconds", "霜冻 10秒",
            EffectStrategy.TIME_EFFECT.apply(ModEffects.FROST_BURN,10*20));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> FROST_BURN_BOOMERANG_EFFECT = createEffect("frozen_burn_3_sec_50_chance","50% chance frozen burn 3 seconds", "50%几率 霜冻 3秒",
            TIME_POSSIBILITY_EFFECT.apply(ModEffects.FROST_BURN,3 * 20,0.5F));


    // 北斗飞镖
    public static final DeferredHolder<EffectStrategy, EffectStrategy> FROST_BURN_10_SEC_4_AMP = createEffect("frozen_burn_10_sec_4_amp",
            EffectStrategy.TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(ModEffects.FROST_BURN, 200,3,3,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> HELL_FIRE_10_SEC_4_AMP = createEffect("hell_fire_10_sec_4_amp",
            EffectStrategy.TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(ModEffects.HELL_FIRE,  200,3,3,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> WITHER_10_SEC_4_AMP = createEffect("wither_10_sec_4_amp",
            EffectStrategy.TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(MobEffects.WITHER, 200,3,3,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> POISON_10_SEC_4_AMP = createEffect("poison_10_sec_4_amp",
            EffectStrategy.TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(MobEffects.POISON, 200,3,3,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> HARM_1_SEC_7_AMP = createEffect("harm_1_sec_7_amp",
            EffectStrategy.TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(MobEffects.HARM, 1,6,6,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> BEI_DOU_EFFECT = createEffect("bei_dou", "random 5 effects", "随机5种效果",
            (owner, entity)->EffectStrategy.RANDOM_POSSIBILITY_EFFECT.apply(Map.of(
                    ModEffectStrategies.POISON_10_SEC_4_AMP, 6f,
                    ModEffectStrategies.HELL_FIRE_10_SEC_4_AMP, 5f,
                    ModEffectStrategies.WITHER_10_SEC_4_AMP, 5f,
                    ModEffectStrategies.HARM_1_SEC_7_AMP, 1f,
                    ModEffectStrategies.FROST_BURN_10_SEC_4_AMP, 5f
            )).get().getEffect().accept(owner, entity));

}
