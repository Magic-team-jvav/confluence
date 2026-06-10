package org.confluence.mod.common.track.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.init.ModTrackTypeProviderTypes;
import org.confluence.mod.common.track.TrackTypeProvider;

/// 变换方向的追踪类型
///
/// @param power 变换方向的力度
public record BasisTrack(double trackAngle, double power) implements ITrackType {
    public static final MapCodec<BasisTrack> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("trackAngle").forGetter(BasisTrack::trackAngle),
            Codec.DOUBLE.fieldOf("power").forGetter(BasisTrack::power)
    ).apply(instance, BasisTrack::new));


    @Override
    public Vec3 calDeltaMovement(Vec3 currentDir, Vec3 targetDir, double trackAngle) {
        if (trackAngle < trackAngle) {
            return VectorUtils.interpolateBasis(currentDir, targetDir, d -> d * power, d -> 0);
        }
        return currentDir;
    }

    @Override
    public String getName() {
        return Confluence.MODID + ".track_type.basis";
    }

    @Override
    public TrackTypeProvider getCodec() {
        return ModTrackTypeProviderTypes.BASIS_TRACK_TYPE.get();
    }

}
