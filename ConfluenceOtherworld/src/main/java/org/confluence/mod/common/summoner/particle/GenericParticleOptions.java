package org.confluence.mod.common.summoner.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.mod.common.summoner.register.SummonerParticleTypes;

import java.util.Objects;

/// 通用粒子参数。
///
/// 中心色块和边缘色块使用 RGB 颜色，粒子透明度由客户端根据生命周期自动计算。
///
/// 1.20 侧它 `extends PortParticleOptions`（PortLib 把 `type`/`codec`/`streamCodec` 塞进父类）；
/// 1.21.1 的写法是**自己实现 `ParticleOptions` 并覆写 `getType()`** —— 与 1.21 侧既有的
/// `common/particle/DamageIndicatorOptions` 完全同构（那一个也是 record + `getType()` 指回
/// `ModParticleTypes`）。本类的字段/构造/取值方法逐字照 1.20，只换掉那层 PortLib 父类。
public final class GenericParticleOptions implements ParticleOptions {
    public static final MapCodec<GenericParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("centerColor").forGetter(GenericParticleOptions::centerColor),
            Codec.INT.fieldOf("edgeColor").forGetter(GenericParticleOptions::edgeColor),
            Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::lifetime),
            Codec.FLOAT.fieldOf("spinSpeed").forGetter(GenericParticleOptions::spinSpeed),
            Codec.FLOAT.fieldOf("friction").forGetter(GenericParticleOptions::friction),
            Codec.FLOAT.fieldOf("scale").forGetter(GenericParticleOptions::scale)
    ).apply(instance, GenericParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GenericParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GenericParticleOptions::centerColor,
            ByteBufCodecs.INT, GenericParticleOptions::edgeColor,
            ByteBufCodecs.INT, GenericParticleOptions::lifetime,
            ByteBufCodecs.FLOAT, GenericParticleOptions::spinSpeed,
            ByteBufCodecs.FLOAT, GenericParticleOptions::friction,
            ByteBufCodecs.FLOAT, GenericParticleOptions::scale,
            GenericParticleOptions::new
    );

    private final int centerColor;
    private final int edgeColor;
    private final int lifetime;
    private final float spinSpeed;
    private final float friction;
    private final float scale;

    public GenericParticleOptions(int centerColor, int edgeColor, int lifetime, float spinSpeed, float friction, float scale) {
        this.centerColor = centerColor;
        this.edgeColor = edgeColor;
        this.lifetime = Math.max(1, lifetime);
        this.spinSpeed = spinSpeed;
        this.friction = friction;
        this.scale = scale;
    }

    @Override
    public ParticleType<?> getType() {
        return SummonerParticleTypes.GENERIC.get();
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
