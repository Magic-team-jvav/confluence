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
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class LoomRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public LoomRecipe(ItemStack result, ShapedRecipePattern pattern) {
        super(result, pattern);
    }

    public LoomRecipe(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
    }

    public LoomRecipe(ItemStack result, Either<ShapedRecipePattern, NonNullList<Ingredient>> either) {
        super(result, either);
    }

    @Override
    public String getGroup() {
        return "loom";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.LOOM.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.LOOM_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.LOOM_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<LoomRecipe> {
        @Override
        protected MapCodec<LoomRecipe> getCodec() {
            return eitherSerializerMapCodec(LoomRecipe::new);
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, LoomRecipe> getStreamCodec() {
            return eitherSerializerStreamCodec(LoomRecipe::new);
        }
    }
}
