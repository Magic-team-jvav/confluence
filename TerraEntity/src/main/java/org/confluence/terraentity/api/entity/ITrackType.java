package org.confluence.terraentity.api.entity;

import com.mojang.serialization.Codec;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.track.TrackTypeProvider;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;

/**
 * <h1>跟踪方式</h1>
 */
public interface ITrackType {

    /**
     * 获取计算后的速度
     * @return 速度
     */
    Vec3 calDeltaMovement(Vec3 currentDir, Vec3 targetDir, double trackAngle);

    double trackAngle();

    String getName();

    /**
     * 获取编解码器
     * @return 编解码器
     */
    TrackTypeProvider getCodec();

    Codec<ITrackType> TYPED_CODEC = TERegistries.TRACK_TYPE_PROVIDERS
            .byNameCodec()
            .dispatch(ITrackType::getCodec, TrackTypeProvider::codec);

    Codec<ITrackType> CODEC = Codec.lazyInitialized(
            () -> {
                Codec<ITrackType> codec = Codec.withAlternative(TYPED_CODEC, SimpleTrack.CODEC.codec());
                return codec;
//                return Codec.either(WeightSelectedZombie.MAP_CODEC.codec(), codec)
//                        .xmap(Either::unwrap, group -> {
//                            if(group instanceof SingleZombie constantvalue){
//                                return Either.right(constantvalue);
//                            }
//                            else if(group instanceof WeightSelectedZombie weightselectedzombie){
//                                return  Either.left(weightselectedzombie);
//                            }
//                            return null;
//                        });
            }
    );
}
