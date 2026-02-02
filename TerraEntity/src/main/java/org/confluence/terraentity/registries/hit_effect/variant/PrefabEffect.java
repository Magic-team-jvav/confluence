package org.confluence.terraentity.registries.hit_effect.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProvider;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProviderTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 用于引用预制的效果策略的数据生成器
 * <p>由于有时注册需要使用懒加载，使用这个可以包装EffectStrategy</p>
 */
public class PrefabEffect implements IEffectStrategy {

    private final Supplier<EffectStrategy> effect;
    String name;
    BiConsumer<LivingEntity, LivingEntity> effectConsumer;

    public PrefabEffect(String name, Supplier<EffectStrategy> effect) {
        this.effect = effect;
//        this.effectConsumer = effect.getEffect();
        this.name = name;
    }

    public static PrefabEffect of(String name, Supplier<EffectStrategy> effect){
        return new PrefabEffect(name, effect);
    }

    public static MapCodec<PrefabEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(PrefabEffect::getName),
            EffectStrategy.CODEC.fieldOf("effect").forGetter(PrefabEffect::getEffectStrategy)
    ).apply(instance, (name, effect)-> new PrefabEffect(name, ()->effect)));


    @Override
    public BiConsumer<LivingEntity, LivingEntity> getEffect() {
        if(effectConsumer == null){
            effectConsumer = effect.get().getEffect();
        }
        return effectConsumer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EffectStrategyProvider codec() {
        return EffectStrategyProviderTypes.PREFAB_EFFECT_PROVIDER.get();
    }

    public EffectStrategy getEffectStrategy() {
        return effect.get();
    }
}
