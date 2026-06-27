package org.confluence.mod.api;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModCustomRegistries;
import org.confluence.mod.util.track.TrackTypeProvider;
import org.confluence.mod.util.track.variant.SimpleTrack;

/// # 跟踪方式
public interface ITrackType {
    /// 获取计算后的速度
    ///
    /// @return 速度
    Vec3 calDeltaMovement(Vec3 currentDir, Vec3 targetDir, double trackAngle);

    double trackAngle();

    String getName();

    /// 获取编解码器
    ///
    /// @return 编解码器
    TrackTypeProvider getCodec();

    Codec<ITrackType> TYPED_CODEC = ModCustomRegistries.TRACK_TYPE_PROVIDERS.byNameCodec().dispatch(ITrackType::getCodec, p -> p.codec().codec());
    Codec<ITrackType> CODEC = PortCodecExtension.lazyInitialized(() -> PortCodecExtension.withAlternative(TYPED_CODEC, SimpleTrack.CODEC.codec()));
}
