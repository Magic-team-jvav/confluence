package org.confluence.mod.common.summoner.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.confluence.mod.common.summoner.register.SummonerParticleTypes;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.core.particles.PortParticleOptions;

import java.util.Objects;

/**
 * 通用粒子参数。
 * <p>
 * 中心色块和边缘色块使用 RGB 颜色，粒子透明度由客户端根据生命周期自动计算。
 * </p>
 */
public final class GenericParticleOptions extends PortParticleOptions {

    public static final MapCodec<GenericParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("centerColor").forGetter(GenericParticleOptions::centerColor),
            com.mojang.serialization.Codec.INT.fieldOf("edgeColor").forGetter(GenericParticleOptions::edgeColor),
            com.mojang.serialization.Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::lifetime),
            com.mojang.serialization.Codec.FLOAT.fieldOf("spinSpeed").forGetter(GenericParticleOptions::spinSpeed),
            com.mojang.serialization.Codec.FLOAT.fieldOf("friction").forGetter(GenericParticleOptions::friction),
            com.mojang.serialization.Codec.FLOAT.fieldOf("scale").forGetter(GenericParticleOptions::scale)
    ).apply(instance, GenericParticleOptions::new));

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, GenericParticleOptions> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.INT, GenericParticleOptions::centerColor,
            PortByteBufCodecs.INT, GenericParticleOptions::edgeColor,
            PortByteBufCodecs.INT, GenericParticleOptions::lifetime,
            PortByteBufCodecs.FLOAT, GenericParticleOptions::spinSpeed,
            PortByteBufCodecs.FLOAT, GenericParticleOptions::friction,
            PortByteBufCodecs.FLOAT, GenericParticleOptions::scale,
            GenericParticleOptions::new
    );

    private final int centerColor;
    private final int edgeColor;
    private final int lifetime;
    private final float spinSpeed;
    private final float friction;
    private final float scale;

    public GenericParticleOptions(int centerColor, int edgeColor, int lifetime, float spinSpeed, float friction, float scale) {
        super(SummonerParticleTypes.GENERIC.get(), CODEC, STREAM_CODEC);
        this.centerColor = centerColor;
        this.edgeColor = edgeColor;
        this.lifetime = Math.max(1, lifetime);
        this.spinSpeed = spinSpeed;
        this.friction = friction;
        this.scale = scale;
    }

    public int centerColor() {
        return centerColor;
    }

    public int edgeColor() {
        return edgeColor;
    }

    public int lifetime() {
        return lifetime;
    }

    public float spinSpeed() {
        return spinSpeed;
    }

    public float friction() {
        return friction;
    }

    public float scale() {
        return scale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenericParticleOptions) obj;
        return this.centerColor == that.centerColor
                && this.edgeColor == that.edgeColor
                && this.lifetime == that.lifetime
                && Float.floatToIntBits(this.spinSpeed) == Float.floatToIntBits(that.spinSpeed)
                && Float.floatToIntBits(this.friction) == Float.floatToIntBits(that.friction)
                && Float.floatToIntBits(this.scale) == Float.floatToIntBits(that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(centerColor, edgeColor, lifetime, spinSpeed, friction, scale);
    }
}
