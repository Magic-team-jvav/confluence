package org.confluence.terraentity.registries.hit_effect;

import com.mojang.serialization.MapCodec;

/**
 * 用于提供轨迹类型编解码器
 *
 * @param codec
 */
public record EffectStrategyProvider(MapCodec<? extends IEffectStrategy> codec) {}
