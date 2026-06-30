package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.util.track.TrackTypeProvider;
import org.confluence.mod.util.track.variant.BasisTrack;
import org.confluence.mod.util.track.variant.SimpleTrack;

import java.util.function.Supplier;

/// 注册追踪编解码器的类型
public final class ModTrackTypeProviderTypes {
    public static final DeferredRegister<TrackTypeProvider> TYPES = DeferredRegister.create(ModCustomRegistries.Keys.TRACK_TYPE_PROVIDER, Confluence.MODID);

    public static final Supplier<TrackTypeProvider> SIMPLE_TRACK_TYPE = register("simple_track_type", SimpleTrack.CODEC);
    public static final Supplier<TrackTypeProvider> BASIS_TRACK_TYPE = register("basis_track_type", BasisTrack.CODEC);

    private static Supplier<TrackTypeProvider> register(String name, MapCodec<? extends ITrackType> codec) {
        return TYPES.register(name, () -> new TrackTypeProvider(codec));
    }
}
