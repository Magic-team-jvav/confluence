package org.confluence.mod.common.track.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.init.ModTrackTypeProviderTypes;
import org.confluence.mod.common.track.TrackTypeProvider;

import java.util.Optional;

/// # 简单跟踪
///
/// @param currDirScaleFactor 跟踪方向的缩放因子
/// @param homingPower        跟踪的吸引力
/// @param maxSpeed           最大速度
/// @param minSpeed           最小速度
public record SimpleTrack(
        double trackAngle,
        double currDirScaleFactor,
        double homingPower,
        Optional<Double> maxSpeed,
        double minSpeed
) implements ITrackType {
    public static final MapCodec<SimpleTrack> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.DOUBLE.fieldOf("trackAngle").forGetter(SimpleTrack::trackAngle),
            Codec.DOUBLE.fieldOf("currDirScaleFactor").forGetter(SimpleTrack::currDirScaleFactor),
            Codec.DOUBLE.fieldOf("homingPower").forGetter(SimpleTrack::homingPower),
            Codec.DOUBLE.optionalFieldOf("maxSpeed").forGetter(SimpleTrack::maxSpeed),
            Codec.DOUBLE.fieldOf("minSpeed").forGetter(SimpleTrack::minSpeed)
    ).apply(instance, SimpleTrack::new));

    @Override
    public Vec3 calDeltaMovement(Vec3 currentDir, Vec3 targetDir, double trackAngle) {
        if (trackAngle < this.trackAngle) {
            if (maxSpeed.isPresent()) {
                return TEUtils.interpolateSimple(currentDir, targetDir, currDirScaleFactor, homingPower, maxSpeed.get(), minSpeed, currentDir);
            }
            return TEUtils.interpolateSimple(currentDir, targetDir, currDirScaleFactor, homingPower, currentDir.length(), minSpeed, currentDir);
        }
        return currentDir;
    }

    @Override
    public String getName() {
        return Confluence.MODID + ".track_type.simple";
    }

    @Override
    public TrackTypeProvider getCodec() {
        return ModTrackTypeProviderTypes.SIMPLE_TRACK_TYPE.get();
    }
}
