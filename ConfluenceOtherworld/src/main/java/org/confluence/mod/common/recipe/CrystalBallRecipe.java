package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.EnvironmentAmountRecipe;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class CrystalBallRecipe extends EnvironmentAmountRecipe {
    public CrystalBallRecipe(ItemStack result, NonNullList<Ingredient> ingredients, EnvironmentLevelAccess.Matcher environment) {
        super(result, ingredients, environment);
    }

    @Override
    protected int maxIngredientSize() {
        return 4;
    }

    @Override
    public String getGroup() {
        return "crystal_ball";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.CRYSTAL_BALL.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRYSTAL_BALL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRYSTAL_BALL_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<CrystalBallRecipe> {
        @Override
        protected MapCodec<CrystalBallRecipe> getCodec() {
            return environmentShapelessSerializerMapCodec(CrystalBallRecipe::new);
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, CrystalBallRecipe> getStreamCodec() {
            return environmentShapelessSerializerSteamCodec(CrystalBallRecipe::new);
        }
    }
}
