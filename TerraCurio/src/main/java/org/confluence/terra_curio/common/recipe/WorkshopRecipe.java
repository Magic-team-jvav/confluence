package org.confluence.terra_curio.common.recipe;

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
import org.confluence.terra_curio.common.init.TCBlocks;
import org.confluence.terra_curio.common.init.TCRecipes;

public class WorkshopRecipe extends EnvironmentAmountRecipe {
    public WorkshopRecipe(ItemStack result, NonNullList<Ingredient> ingredients, EnvironmentLevelAccess.Matcher environment) {
        super(result, ingredients, environment);
    }

    @Override
    protected int maxIngredientSize() {
        return 12;
    }

    @Override
    public String getGroup() {
        return "workshop";
    }

    @Override
    public ItemStack getToastSymbol() {
        return TCBlocks.WORKSHOP.toStack();
    }

    @Override
    public RecipeSerializer<WorkshopRecipe> getSerializer() {
        return TCRecipes.WORKSHOP_SERIALIZER.get();
    }

    @Override
    public RecipeType<WorkshopRecipe> getType() {
        return TCRecipes.WORKSHOP_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<WorkshopRecipe> {
        @Override
        protected MapCodec<WorkshopRecipe> getCodec() {
            return environmentShapelessSerializerMapCodec(WorkshopRecipe::new);
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, WorkshopRecipe> getStreamCodec() {
            return environmentShapelessSerializerSteamCodec(WorkshopRecipe::new);
        }
    }
}
