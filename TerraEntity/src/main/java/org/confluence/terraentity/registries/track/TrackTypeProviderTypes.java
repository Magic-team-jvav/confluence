package org.confluence.terraentity.registries.track;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;

import java.util.function.Supplier;

/**
 * 注册追踪编解码器的类型
 */
public class TrackTypeProviderTypes {
    public static final DeferredRegister<TrackTypeProvider> TYPES = DeferredRegister.create(TERegistries.TRACK_TYPE_PROVIDERS, TerraEntity.MODID);

    public static final Supplier<TrackTypeProvider> SIMPLE_TRACK_TYPE = register("simple_track_type", SimpleTrack.CODEC);
    public static final Supplier<TrackTypeProvider> BASIS_TRACK_TYPE = register("basis_track_type", BasisTrack.CODEC);


    private static Supplier<TrackTypeProvider> register(String name, MapCodec<? extends ITrackType> codec) {
        return TYPES.register(name, ()->new TrackTypeProvider(codec));
    }
}
