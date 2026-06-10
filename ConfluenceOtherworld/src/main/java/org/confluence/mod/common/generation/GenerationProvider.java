package org.confluence.mod.common.generation;

import com.mojang.serialization.MapCodec;
import org.confluence.mod.api.IGeneration;

/// 用于提供轨迹类型编解码器
/// @param codec
public record GenerationProvider(MapCodec<? extends IGeneration> codec) {}
