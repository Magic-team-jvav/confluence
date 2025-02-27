package org.confluence.mod.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.mod.common.init.ModParticleTypes;
import org.jetbrains.annotations.NotNull;

// 除了显示数字还能显示“美味...” “致命失误！”
public record DamageIndicatorOptions(Component text, boolean big) implements ParticleOptions {
    @Override
    @NotNull
    public ParticleType<?> getType(){
        return ModParticleTypes.DAMAGE_INDICATOR.get();
    }

    public static final MapCodec<DamageIndicatorOptions> CODEC = RecordCodecBuilder.mapCodec(
        (thisOptionsInstance) -> thisOptionsInstance.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter((thisOptions) -> thisOptions.text),
            Codec.BOOL.fieldOf("big").forGetter(options -> options.big)
        ).apply(thisOptionsInstance, DamageIndicatorOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DamageIndicatorOptions> STREAM_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC, opt -> opt.text,
        ByteBufCodecs.BOOL, opt -> opt.big,
        DamageIndicatorOptions::new
    );

}
