package org.confluence.terraentity.init;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.registries.hit_effect.variant.PrefabEffect;
import org.confluence.terraentity.registries.hit_effect.variant.RandomWeightEffect;
import org.confluence.terraentity.registries.hit_effect.variant.TimePossibilityAmplifierEffect;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.confluence.terraentity.registries.hit_effect.EffectStrategy.*;

/**
 * <h1> 攻击时给敌人施加的效果或回调 </h1>
 * <p> 适用于剑、弹射物、生物命中特效等 </p>
 * <p> 为使翻译键统一，建议注册 EffectStrategy 使用 private, 复用Components  </p>
 * @author coffee
 */
public final class TEEffectStrategies {
    public static final DeferredRegister<EffectStrategy> EFFECT_STRATEGY = DeferredRegister.create(TERegistries.EFFECT_STRATEGIES, TerraEntity.MODID);

    /**
     * 复杂的回调效果
     * @param name id
     * @param effect 效果回调
     */
    public static DeferredHolder<EffectStrategy, EffectStrategy> createEffect(String name, BiConsumer<LivingEntity, LivingEntity> effect) {
        return EFFECT_STRATEGY.register(name, ()->new EffectStrategy(name, effect));
    }

    /**
     * 预定义类型效果
     * @param name id
     * @param effect 效果类型
     */
    public static DeferredHolder<EffectStrategy, EffectStrategy> createEffect(String name, IEffectStrategy effect) {
        return EFFECT_STRATEGY.register(name, ()->new EffectStrategy(effect));
    }

    /**未定义效果*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> UNDEFINED = createEffect("undefined",
            UNDEFINED_EFFECT);

    /**蝙蝠棍*/
    public static final  DeferredHolder<EffectStrategy, EffectStrategy> BAT_FANG_EFFECT = createEffect("bat",
            (owner, entity)-> {
                if(owner instanceof OwnableEntity o && o.getOwner() != null){
                    o.getOwner().heal(1);
                }else{
                    owner.heal(1);
                }
            });

    public static final  DeferredHolder<EffectStrategy, EffectStrategy> HEAL_0_5_EFFECT = createEffect("heal_0_5",
            (owner, entity)-> {
                if(owner instanceof OwnableEntity o && o.getOwner() != null){
                    o.getOwner().heal(0.5f);
                }else{
                    owner.heal(0.5f);
                }
            });

    /**着火*/
    public static final DeferredHolder<EffectStrategy, EffectStrategy> SET_FIRE_EFFECT = createEffect("set_fire_5_sec",
            SET_FIRE.apply(5 * 20, 1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> POISON_5_SEC_2_AMP = createEffect("poison_5_sec_2_amp",
            TimePossibilityAmplifierEffect.of("poison_5_sec_2_amp", MobEffects.POISON, 100,2,2,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> SLOW_5_SEC_2_AMP = createEffect("slow_5_sec_2_amp",
            TimePossibilityAmplifierEffect.of("slow_5_sec_2_amp", MobEffects.MOVEMENT_SLOWDOWN, 100,2,2,1f));


    public static final DeferredHolder<EffectStrategy, EffectStrategy> FROZEN_EFFECT = createEffect("frozen_burn_5_sec_2_amp",
            TimePossibilityAmplifierEffect.of("frozen_burn_5_sec_2_amp", TEEffects.FROST_BURN,100,2,2,1f));

    public static final DeferredHolder<EffectStrategy, EffectStrategy> HELL_FIRE_EFFECT = createEffect("hell_fire_5_sec_2_amp",
            TimePossibilityAmplifierEffect.of("hell_fire_5_sec_2_amp", TEEffects.HELLFIRE,5 * 20,2,2,1f));



//    /** 魔光剑*/
//    private static final  DeferredHolder<EffectStrategy, EffectStrategy> LIGHTS_BANE_EFFECT = createEffect("lights_bane",
//            ON_HIT_PROJECTILE.apply((level)->ModEntities.LIGHTS_BANE_PROJECTILE.get().create(level).addAttackDamage(7f)));
//

    public static final  DeferredHolder<EffectStrategy, EffectStrategy> YOYO_BEE_PROJ_EFFECT = createEffect("yoyo_bee_proj",
            (owner, entity)->{
                if(owner.getRandom().nextFloat() < 0.33f) {
                    ON_HIT_PROJECTILE.apply((level) -> TEProjectileEntities.BEE_PROJ.get().create(level)).accept(owner, entity);
                }
            });

    // 北斗飞镖
    private static final DeferredHolder<EffectStrategy, EffectStrategy> FROST_BURN_10_SEC_4_AMP = createEffect("frozen_burn_10_sec_4_amp",
            TimePossibilityAmplifierEffect.of("frozen_burn_10_sec_4_amp", TEEffects.FROST_BURN, 200,3,3,1f));

    private static final DeferredHolder<EffectStrategy, EffectStrategy> HELL_FIRE_10_SEC_4_AMP = createEffect("hell_fire_10_sec_4_amp",
            TimePossibilityAmplifierEffect.of("hell_fire_10_sec_4_amp", TEEffects.HELLFIRE,  200,3,3,1f));

    private static final DeferredHolder<EffectStrategy, EffectStrategy> WITHER_10_SEC_4_AMP = createEffect("wither_10_sec_4_amp",
            TimePossibilityAmplifierEffect.of("wither_10_sec_4_amp", MobEffects.WITHER, 200,3,3,1f));

    private static final DeferredHolder<EffectStrategy, EffectStrategy> POISON_10_SEC_4_AMP = createEffect("poison_10_sec_4_amp",
            TimePossibilityAmplifierEffect.of("poison_10_sec_4_amp", MobEffects.POISON, 200,3,3,1f));

    private static final DeferredHolder<EffectStrategy, EffectStrategy> INSTANT_HARM_1_SEC_7_AMP = createEffect("instant_harm_1_sec_7_amp",
            TimePossibilityAmplifierEffect.of("instant_harm_1_sec_7_amp", MobEffects.HARM, 1,6,6,1f));


    /**
     * 预定义效果的数据组件
     */
    public static class Components{

/* effect效果*/

        /**霜冻*/
        public static final Supplier<EffectStrategyComponent> FROST_BURN_EFFECT = ()->EffectStrategyComponent.of(
                TimePossibilityAmplifierEffect.of("frozen_burn_5_sec", TEEffects.FROST_BURN,10*20));

        public static final Supplier<EffectStrategyComponent> FROST_BURN_BOOMERANG_EFFECT = ()->EffectStrategyComponent.of(
                TimePossibilityAmplifierEffect.of("frozen_burn_3_sec_50_chance", TEEffects.FROST_BURN,3 * 20,0,0.5F));

//        /**触手钉锤*/
//        public static final Supplier<EffectStrategyComponent>  TENTACLE_SPIKES_EFFECT =()->EffectStrategyComponent.of(
//                TimePossibilityAmplifierEffect.of("tentacle_spikes", ModEffects.TENTACLE_SPIKES, 180, 0, 4, 0.5f));

        /**猎弓*/
        public static final Supplier<EffectStrategyComponent>  HUNTING_RIFLE_EFFECT = ()->EffectStrategyComponent.of(
                TimePossibilityAmplifierEffect.of("hunting_4_sec", TEEffects.SUMMON_FOCUS, 160));

        /**火山*/
        public static final Supplier<EffectStrategyComponent> HELL_FIRE_EFFECT = ()->new EffectStrategyComponent(List.of(
                TimePossibilityAmplifierEffect.of("hell_fire_5_sec", TEEffects.HELLFIRE,5 * 20),
                PrefabEffect.of("set_fire_5_sec", SET_FIRE_EFFECT)
        ));

//        /**血腥屠刀*/
//        public static final Supplier<EffectStrategyComponent>  BLOOD_BUTCHERED_EFFECT = ()->EffectStrategyComponent.of(
//                TimePossibilityAmplifierEffect.of("blood_butchered",ModEffects.BLOOD_BUTCHERED.getDelegate(), 180, 0, 4, 0.5f));

/* prefab效果*/

//        /**蝙蝠棍*/
//        public static final Supplier<EffectStrategyComponent> BAT_FANG_EFFECT = ()->EffectStrategyComponent.ofPrefab("bat",
//                ModEffectStrategies.BAT_FANG_EFFECT);
//
//        /** 魔光剑*/
//        public static final Supplier<EffectStrategyComponent> LIGHTS_BANE_EFFECT = ()->EffectStrategyComponent.ofPrefab("lights_bane",
//                ModEffectStrategies.LIGHTS_BANE_EFFECT);
//
//        /** 养蜂人*/
//        public static final Supplier<EffectStrategyComponent> BEE_KEEPER_EFFECT = ()->EffectStrategyComponent.ofPrefab("bee_keeper",
//                ModEffectStrategies.BEE_KEEPER_EFFECT);

/* random效果*/

        /**北斗飞镖*/
        public static final Supplier<EffectStrategyComponent> BEI_DOU_EFFECT = ()->EffectStrategyComponent.of(
                new RandomWeightEffect("bei_dou", ()->Map.of(
                        POISON_10_SEC_4_AMP.get(), 6f,
                        HELL_FIRE_10_SEC_4_AMP.get(), 5f,
                        WITHER_10_SEC_4_AMP.get(), 5f,
                        INSTANT_HARM_1_SEC_7_AMP.get(), 1f,
                        FROST_BURN_10_SEC_4_AMP.get(), 5f
                )));
    }
}
