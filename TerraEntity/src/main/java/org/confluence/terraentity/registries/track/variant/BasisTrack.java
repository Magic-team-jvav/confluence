package org.confluence.terraentity.registries.track.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.TrackTypeProvider;
import org.confluence.terraentity.registries.track.TrackTypeProviderTypes;
import org.confluence.terraentity.utils.TEUtils;

/**
 * 变换方向的追踪类型
 * @param power 变换方向的力度
 */
public record BasisTrack(double trackAngle, double power) implements ITrackType {

    public static MapCodec<BasisTrack> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.DOUBLE.fieldOf("trackAngle").forGetter(BasisTrack::trackAngle),
            Codec.DOUBLE.fieldOf("power").forGetter(BasisTrack::power)
            ).apply(instance, BasisTrack::new));


    @Override
    public Vec3 calDeltaMovement(Vec3 currentDir, Vec3 targetDir, double trackAngle) {
        if(trackAngle < this.trackAngle)
            return TEUtils.interpolateBasis(currentDir, targetDir, d->d*power, d->0);
        return currentDir;
    }

    @Override
    public String getName() {
        return TerraEntity.toLang(TerraEntity.space("track_type/basis"));
    }

    @Override
    public TrackTypeProvider getCodec() {
        return TrackTypeProviderTypes.BASIS_TRACK_TYPE.get();
    }

}
