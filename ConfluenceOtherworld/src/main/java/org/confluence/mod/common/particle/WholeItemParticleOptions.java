package org.confluence.mod.common.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModParticleTypes;
import org.jetbrains.annotations.NotNull;

public record WholeItemParticleOptions(ItemStack item) implements ParticleOptions {
    public static final MapCodec<WholeItemParticleOptions> CODEC = ItemStack.SINGLE_ITEM_CODEC.xmap(WholeItemParticleOptions::new, WholeItemParticleOptions::item).fieldOf("item");
    public static final StreamCodec<RegistryFriendlyByteBuf, WholeItemParticleOptions> STREAM_CODEC = ItemStack.STREAM_CODEC.map(WholeItemParticleOptions::new, WholeItemParticleOptions::item);

    @Override
    public @NotNull ParticleType<WholeItemParticleOptions> getType() {
        return ModParticleTypes.WHOLE_ITEM.get();
    }
}
