package org.confluence.mod.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
    public static final StreamCodec<RegistryFriendlyByteBuf, WholeItemParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, WholeItemParticleOptions::item,
            ByteBufCodecs.FLOAT, WholeItemParticleOptions::gravity,
            ByteBufCodecs.INT, WholeItemParticleOptions::life,
            WholeItemParticleOptions::new
    );

    @Override
    public ParticleType<WholeItemParticleOptions> getType() {
        return ModParticleTypes.WHOLE_ITEM.get();
    }
}
