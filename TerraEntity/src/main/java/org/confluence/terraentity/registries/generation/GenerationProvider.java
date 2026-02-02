package org.confluence.terraentity.registries.generation;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.entity.IGeneration;

/**
 * 用于提供轨迹类型编解码器
 * @param codec
 */
public record GenerationProvider(MapCodec<? extends IGeneration> codec) {


}
