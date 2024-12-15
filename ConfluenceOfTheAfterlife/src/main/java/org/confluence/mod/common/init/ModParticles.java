package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.particle.options.DamageIndicatorOptions;
import org.jetbrains.annotations.NotNull;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Confluence.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<DamageIndicatorOptions>> DAMAGE_INDICATOR = register("damage_indicator", true, DamageIndicatorOptions.CODEC, DamageIndicatorOptions.STREAM_CODEC);

    // 原版用了Function获取codec，现在自己的用不到，要用了再改
    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(String id,boolean overrideLimiter,MapCodec<T> mapCodec, StreamCodec<? super RegistryFriendlyByteBuf, T>streamCodec){
        return PARTICLES.register(id, () -> new ParticleType<>(overrideLimiter) {
            @Override
            @NotNull
            public MapCodec<T> codec(){
                return mapCodec;
            }

            @Override
            @NotNull
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec(){
                return streamCodec;
            }
        });
    }

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String id,boolean overrideLimiter){
        return PARTICLES.register(id, () -> new SimpleParticleType(overrideLimiter));
    }
}
