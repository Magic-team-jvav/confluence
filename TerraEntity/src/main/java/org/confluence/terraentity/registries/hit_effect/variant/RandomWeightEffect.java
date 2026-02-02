package org.confluence.terraentity.registries.hit_effect.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProvider;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProviderTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.utils.TEUtils;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RandomWeightEffect implements IEffectStrategy {
    Supplier<Map<EffectStrategy, Float>> effectMap;
    Map<EffectStrategy, Float> cache;
    String name;
    public Map<EffectStrategy, Float> getEffectMap(){
        return effectMap.get();
    }
    public static MapCodec<RandomWeightEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.unboundedMap(TERegistries.EFFECT_STRATEGIES.byNameCodec(), Codec.FLOAT).fieldOf("effect_map").forGetter(RandomWeightEffect::getEffectMap),
            Codec.STRING.fieldOf("name").forGetter(RandomWeightEffect::getName)
    ).apply(instance, (map,name)->new RandomWeightEffect(name, () -> map)));

    public RandomWeightEffect(String name, Supplier<Map<EffectStrategy, Float>> effectMap){
        this.effectMap = effectMap;
        this.name = name;
    }

    @Override
    public BiConsumer<LivingEntity, LivingEntity> getEffect() {
        if(cache == null){
            cache = effectMap.get();
        }
        return TEUtils.getRandomByWeight(cache).getEffect();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EffectStrategyProvider codec() {
        return EffectStrategyProviderTypes.RANDOM_EFFECT_PROVIDER.get();
    }
}
