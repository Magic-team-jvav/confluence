package org.confluence.mod.common.particle;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.mod.common.init.ModParticleTypes;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.core.particles.PortParticleOptions;

import java.util.Objects;

@ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
public final class WholeItemParticleOptions extends PortParticleOptions {
    public static final MapCodec<WholeItemParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PortItemStackExtension.singleItemCodec().fieldOf("item").forGetter(WholeItemParticleOptions::item),
        Codec.FLOAT.fieldOf("gravity").forGetter(WholeItemParticleOptions::gravity),
        Codec.INT.fieldOf("life").forGetter(WholeItemParticleOptions::life)
    ).apply(instance, WholeItemParticleOptions::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, WholeItemParticleOptions> STREAM_CODEC = PortStreamCodec.composite(
        PortItemStackExtension.streamCodec(), WholeItemParticleOptions::item,
        PortByteBufCodecs.FLOAT, WholeItemParticleOptions::gravity,
        PortByteBufCodecs.INT, WholeItemParticleOptions::life,
        WholeItemParticleOptions::new
    );
    private final ItemStack item;
    private final float gravity;
    private final int life;

    public WholeItemParticleOptions(ItemStack item, float gravity,
                                    int life) {
        super(ModParticleTypes.WHOLE_ITEM.get(), CODEC, STREAM_CODEC);
        this.item = item;
        this.gravity = gravity;
        this.life = life;
    }

    public ItemStack item() {
        return item;
    }

    public float gravity() {
        return gravity;
    }

    public int life() {
        return life;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WholeItemParticleOptions) obj;
        return Objects.equals(this.item, that.item) &&
               Float.floatToIntBits(this.gravity) == Float.floatToIntBits(that.gravity) &&
               this.life == that.life;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, gravity, life);
    }

    @Override
    public String toString() {
        return "WholeItemParticleOptions[" +
               "item=" + item + ", " +
               "gravity=" + gravity + ", " +
               "life=" + life + ']';
    }

}
