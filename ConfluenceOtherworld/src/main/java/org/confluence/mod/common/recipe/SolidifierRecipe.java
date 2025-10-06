package org.confluence.mod.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class SolidifierRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public SolidifierRecipe(ItemStack result, ShapedRecipePattern pattern) {
        super(result, pattern);
    }

    public SolidifierRecipe(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
    }

    public SolidifierRecipe(ItemStack result, Either<ShapedRecipePattern, NonNullList<Ingredient>> either) {
        super(result, either);
    }

    @Override
    public String getGroup() {
        return "solidifier";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.SOLIDIFIER.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SOLIDIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SOLIDIFIER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SolidifierRecipe> {
        public static final MapCodec<SolidifierRecipe> CODEC = EitherAmountRecipe4x.shapedSerializerMapCodec(SolidifierRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, SolidifierRecipe> STREAM_CODEC = EitherAmountRecipe4x.shapedSerializerSteamCodec(SolidifierRecipe::new);

        @Override
        public MapCodec<SolidifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SolidifierRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
