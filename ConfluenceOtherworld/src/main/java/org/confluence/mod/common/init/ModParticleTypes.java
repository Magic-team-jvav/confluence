package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.common.particle.WholeItemParticleOptions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Confluence.MODID);

    public static final Supplier<ParticleType<DamageIndicatorOptions>> DAMAGE_INDICATOR = register("damage_indicator", true, DamageIndicatorOptions.CODEC, DamageIndicatorOptions.STREAM_CODEC);
    public static final Supplier<ParticleType<WholeItemParticleOptions>> WHOLE_ITEM = register("whole_item", true, WholeItemParticleOptions.CODEC, WholeItemParticleOptions.STREAM_CODEC);
    public static final Supplier<SimpleParticleType> LEAVES = register("leaves", true);
    public static final Supplier<SimpleParticleType> RED_SAND = register("red_sand", true);
    public static final Supplier<SimpleParticleType> SAND = register("sand", true);
    public static final Supplier<SimpleParticleType> SNOW = register("snow", true);
    public static final Supplier<SimpleParticleType> YELLOW_WILLOW = register("yellow_willow", true);
    public static final Supplier<SimpleParticleType> LIGHT_BANE = register("lights_bane", true);
    public static final Supplier<SimpleParticleType> LIGHT_BANE_DUST = register("lights_bane_dust", true);
    public static final Supplier<SimpleParticleType> LIGHT_BANE_FADE = register("lights_bane_fade", true);
    public static final Supplier<SimpleParticleType> ECTO_MIST = register("ecto_mist", true);

    // 原版用了Function获取codec，现在自己的用不到，要用了再改
    private static <T extends ParticleOptions> Supplier<ParticleType<T>> register(String id, boolean overrideLimiter, MapCodec<T> mapCodec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, () -> new ParticleType<>(overrideLimiter) {
            @Override
            @NotNull
            public MapCodec<T> codec() {
                return mapCodec;
            }

            @Override
            @NotNull
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        });
    }

    private static Supplier<SimpleParticleType> register(String id, boolean overrideLimiter) {
        return TYPES.register(id, () -> new SimpleParticleType(overrideLimiter));
    }

    private static <T extends ParticleOptions> Supplier<ParticleType<T>> register(String name, boolean overrideLimitter, final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return TYPES.register(name, () -> new ParticleType<T>(overrideLimitter) {
            @Override
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }
}
