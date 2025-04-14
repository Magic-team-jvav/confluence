package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.lib.common.recipe.ShapedAmountRecipe4x;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class SawmillRecipe extends ShapedAmountRecipe4x<MenuRecipeInput> {
    public SawmillRecipe(ItemStack result, ShapedRecipePattern pattern) {
        super(result, pattern);
    }

    @Override
    public String getGroup() {
        return "sawmill";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.SAWMILL.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAWMILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SAWMILL_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SawmillRecipe> {
        public static final MapCodec<SawmillRecipe> CODEC = ShapedAmountRecipe4x.serializerMapCodec(SawmillRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> STREAM_CODEC = ShapedAmountRecipe4x.serializerSteamCodec(SawmillRecipe::new);

        @Override
        public MapCodec<SawmillRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
