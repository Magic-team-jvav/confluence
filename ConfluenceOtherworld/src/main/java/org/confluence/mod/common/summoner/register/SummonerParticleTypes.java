package org.confluence.mod.common.summoner.register;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.particle.GenericParticleOptions;

import java.util.function.Supplier;

///
///
public final class SummonerParticleTypes {
    public static final DeferredRegister<ParticleType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Confluence.MODID);

    public static final Supplier<ParticleType<GenericParticleOptions>> GENERIC = register("generic", true, GenericParticleOptions.CODEC, GenericParticleOptions.STREAM_CODEC);

    /// 与 `ModParticleTypes` 里的同名辅助方法同形：1.21 的 `ParticleType` 是抽象类，
    /// `codec()` / `streamCodec()` 必须逐个实例覆写。
    private static <T extends ParticleOptions> Supplier<ParticleType<T>> register(String id, boolean overrideLimiter, MapCodec<T> mapCodec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, () -> new ParticleType<>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return mapCodec;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        });
    }

    private SummonerParticleTypes() {}
}
