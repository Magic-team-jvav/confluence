package org.confluence.mod.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModParticleTypes;
import org.jetbrains.annotations.NotNull;

public record WholeItemParticleOptions(ItemStack item) implements ParticleOptions {
    private static final Codec<ItemStack> ITEM_CODEC = Codec.withAlternative(ItemStack.SINGLE_ITEM_CODEC, ItemStack.ITEM_NON_AIR_CODEC, ItemStack::new);

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticleTypes.WHOLE_ITEM.get();
    }

    public static MapCodec<WholeItemParticleOptions> CODEC = ITEM_CODEC.xmap(WholeItemParticleOptions::new, p_123709_ -> p_123709_.item).fieldOf("item");

    public static StreamCodec<? super RegistryFriendlyByteBuf, WholeItemParticleOptions> STREAM_CODEC = ItemStack.STREAM_CODEC.map(WholeItemParticleOptions::new, p_319433_ -> p_319433_.item);

    public ItemStack getItem() {
        return item;
    }
}
