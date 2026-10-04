package org.confluence.mod.util.track;

import com.mojang.serialization.MapCodec;
import org.confluence.mod.api.ITrackType;

/// 用于提供轨迹类型编解码器
///
/// @param codec
public record TrackTypeProvider(MapCodec<? extends ITrackType> codec) {}
