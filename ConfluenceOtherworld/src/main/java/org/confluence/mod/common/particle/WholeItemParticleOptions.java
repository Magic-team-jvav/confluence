package org.confluence.mod.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.mod.common.init.ModParticleTypes;

@ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
public record WholeItemParticleOptions(ItemStack item, float gravity,
                                       int life) implements ParticleOptions {
    public static final MapCodec<WholeItemParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(WholeItemParticleOptions::item),
            Codec.FLOAT.fieldOf("gravity").forGetter(WholeItemParticleOptions::gravity),
            Codec.INT.fieldOf("life").forGetter(WholeItemParticleOptions::life)
    ).apply(instance, WholeItemParticleOptions::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, WholeItemParticleOptions> STREAM_CODEC = PortStreamCodec.composite(
            ItemStack.STREAM_CODEC, WholeItemParticleOptions::item,
            PortByteBufCodecs.FLOAT, WholeItemParticleOptions::gravity,
            PortByteBufCodecs.INT, WholeItemParticleOptions::life,
            WholeItemParticleOptions::new
    );

    @Override
    public ParticleType<WholeItemParticleOptions> getType() {
        return ModParticleTypes.WHOLE_ITEM.get();
    }
}
