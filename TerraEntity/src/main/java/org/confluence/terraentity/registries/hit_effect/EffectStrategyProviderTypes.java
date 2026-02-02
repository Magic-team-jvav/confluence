package org.confluence.terraentity.registries.hit_effect;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.hit_effect.variant.PrefabEffect;
import org.confluence.terraentity.registries.hit_effect.variant.RandomWeightEffect;
import org.confluence.terraentity.registries.hit_effect.variant.TimePossibilityAmplifierEffect;

/**
 * 注册追踪编解码器的类型
 */
public class EffectStrategyProviderTypes {
    public static final DeferredRegister<EffectStrategyProvider> TYPES = DeferredRegister.create(TERegistries.EFFECT_STRATEGY_PROVIDERS, TerraEntity.MODID);

    public static final DeferredHolder<EffectStrategyProvider,EffectStrategyProvider> TIME_POSSIBILITY_AMPLIFIER_EFFECT_PROVIDER = register("time_possibility_amplifier_effect", TimePossibilityAmplifierEffect.CODEC);

    public static final DeferredHolder<EffectStrategyProvider,EffectStrategyProvider> PREFAB_EFFECT_PROVIDER = register("prefab_effect", PrefabEffect.CODEC);
    public static final DeferredHolder<EffectStrategyProvider,EffectStrategyProvider> RANDOM_EFFECT_PROVIDER = register("random_weight_effect", RandomWeightEffect.CODEC);


    private static DeferredHolder<EffectStrategyProvider,EffectStrategyProvider> register(String name, MapCodec<? extends IEffectStrategy> codec) {
        return TYPES.register(name, ()->new EffectStrategyProvider(codec));
    }
}
