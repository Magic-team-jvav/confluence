package org.confluence.terraentity.registries.hit_effect.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProvider;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProviderTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;

import java.util.function.BiConsumer;

/**
 * <h1>最通用的内置效果
 * @param effect 效果
 * @param duration 持续时间
 * @param amplifierMin 最小增幅
 * @param amplifierMax 最大增幅
 * @param possibility 几率
 */
public record TimePossibilityAmplifierEffect(String name, Holder<MobEffect> effect, int duration, int amplifierMin,int amplifierMax, float possibility) implements IEffectStrategy {
    public static MapCodec<TimePossibilityAmplifierEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(TimePossibilityAmplifierEffect::name),
            MobEffect.CODEC.fieldOf("effect").forGetter(TimePossibilityAmplifierEffect::effect),
            Codec.INT.fieldOf("duration").forGetter(TimePossibilityAmplifierEffect::duration),
            Codec.INT.fieldOf("amplifier_min").forGetter(TimePossibilityAmplifierEffect::amplifierMin),
            Codec.INT.fieldOf("amplifier_max").forGetter(TimePossibilityAmplifierEffect::amplifierMax),
            Codec.FLOAT.fieldOf("possibility").forGetter(TimePossibilityAmplifierEffect::possibility)
    ).apply(instance, TimePossibilityAmplifierEffect::new));

    public static TimePossibilityAmplifierEffect of(String name, Holder<MobEffect> effect, int duration, int amplifierMin, int amplifierMax, float possibility){
        return new TimePossibilityAmplifierEffect(name, effect, duration, amplifierMin, amplifierMax, possibility);
    }

    public static TimePossibilityAmplifierEffect of(String name, Holder<MobEffect> effect, int duration, int amplifier, float possibility){
        return of(name, effect, duration, amplifier, amplifier, possibility);
    }

    public static TimePossibilityAmplifierEffect of(String name, Holder<MobEffect> effect, int duration, int amplifier){
        return of(name, effect, duration, amplifier, 1.0f);
    }

    public static TimePossibilityAmplifierEffect of(String name, Holder<MobEffect> effect, int duration){
        return of(name, effect, duration, 0);
    }

    @Override
    public BiConsumer<LivingEntity, LivingEntity> getEffect() {
        return EffectStrategy.TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(effect, duration, amplifierMin, amplifierMax, possibility);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EffectStrategyProvider codec() {
        return EffectStrategyProviderTypes.TIME_POSSIBILITY_AMPLIFIER_EFFECT_PROVIDER.get();
    }
}
