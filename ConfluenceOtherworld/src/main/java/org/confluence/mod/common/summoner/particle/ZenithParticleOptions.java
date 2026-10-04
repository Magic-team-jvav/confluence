package org.confluence.mod.common.summoner.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import org.confluence.mod.common.summoner.register.SummonerParticleTypes;
import org.jetbrains.annotations.NotNull;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.core.particles.PortParticleOptions;

import java.util.Objects;

/**
 * 天顶剑粒子选项：颜色（RGB）、寿命、阻力、大小由服务端下发，速度随生成时的速度传入。
 */
public final class ZenithParticleOptions extends PortParticleOptions {

    public static final MapCodec<ZenithParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("color").forGetter(ZenithParticleOptions::color),
                    Codec.INT.fieldOf("lifetime").forGetter(ZenithParticleOptions::lifetime),
                    Codec.FLOAT.fieldOf("friction").forGetter(ZenithParticleOptions::friction),
                    Codec.FLOAT.fieldOf("scale").forGetter(ZenithParticleOptions::scale)
            ).apply(instance, ZenithParticleOptions::new)
    );

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, ZenithParticleOptions> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public ZenithParticleOptions decode(PortRegistryFriendlyByteBuf buffer) {
            return new ZenithParticleOptions(
                    buffer.readInt(),
                    buffer.readInt(),
                    buffer.readFloat(),
                    buffer.readFloat()
            );
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, ZenithParticleOptions options) {
            buffer.writeInt(options.color);
            buffer.writeInt(options.lifetime);
            buffer.writeFloat(options.friction);
            buffer.writeFloat(options.scale);
        }
    };

    private final int color;
    private final int lifetime;
    private final float friction;
    private final float scale;

    public ZenithParticleOptions(int color, int lifetime, float friction, float scale) {
        super(SummonerParticleTypes.ZENITH.get(), CODEC, STREAM_CODEC);
        this.color = color;
        this.lifetime = lifetime;
        this.friction = friction;
        this.scale = scale;
    }

    public int color() {
        return color;
    }

    public int lifetime() {
        return lifetime;
    }

    public float friction() {
        return friction;
    }

    public float scale() {
        return scale;
    }

    @Override
    public @NotNull ParticleType<ZenithParticleOptions> getType() {
        return SummonerParticleTypes.ZENITH.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ZenithParticleOptions) obj;
        return this.color == that.color
                && this.lifetime == that.lifetime
                && Float.floatToIntBits(this.friction) == Float.floatToIntBits(that.friction)
                && Float.floatToIntBits(this.scale) == Float.floatToIntBits(that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, lifetime, friction, scale);
    }
}
