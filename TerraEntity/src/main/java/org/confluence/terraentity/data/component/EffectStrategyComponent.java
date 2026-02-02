package org.confluence.terraentity.data.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.registries.hit_effect.variant.PrefabEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 命中效果的数据生成器组件
 *
 * @param effects 命中效果
 */
public record EffectStrategyComponent(List<IEffectStrategy> effects) implements DataComponentType<EffectStrategyComponent> {
    public static final Codec<EffectStrategyComponent> CODEC = IEffectStrategy.TYPED_CODEC.listOf().xmap(EffectStrategyComponent::new, EffectStrategyComponent::effects);
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectStrategyComponent> STREAM_CODEC = IEffectStrategy.STREAM_CODEC.apply(ByteBufCodecs.list()).map(EffectStrategyComponent::new, EffectStrategyComponent::effects);

    public void applyAll(LivingEntity owner, LivingEntity target) {
        for (IEffectStrategy effect : effects) {
            effect.getEffect().accept(owner, target);
        }
    }

    public static EffectStrategyComponent of(IEffectStrategy effect) {
        return new EffectStrategyComponent(List.of(effect));
    }

    public static EffectStrategyComponent ofPrefab(String name, DeferredHolder<EffectStrategy, EffectStrategy> effect) {
        return of(PrefabEffect.of(name, effect));
    }

    @Override
    public @Nullable Codec<EffectStrategyComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, EffectStrategyComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof EffectStrategyComponent(IEffectStrategy effect1) && effect1 == effects;
    }
}
