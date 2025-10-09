package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class SimpleRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
    private MapCodec<T> codec;
    private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    @Override
    public final MapCodec<T> codec() {
        if (codec == null) this.codec = getCodec();
        return codec;
    }

    protected abstract MapCodec<T> getCodec();

    @Override
    public final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        if (streamCodec == null) this.streamCodec = getStreamCodec();
        return streamCodec;
    }

    protected abstract StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
}
