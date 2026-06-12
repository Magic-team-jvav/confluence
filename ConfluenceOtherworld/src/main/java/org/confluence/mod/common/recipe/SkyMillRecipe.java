package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PortRegistryFriendlyByteBuf;
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

public class SkyMillRecipe extends EnvironmentAmountRecipe {
    public SkyMillRecipe(ItemStack result, NonNullList<Ingredient> ingredients, EnvironmentLevelAccess.Matcher environment) {
        super(result, ingredients, environment);
    }

    @Override
    protected int maxIngredientSize() {
        return 3;
    }

    @Override
    public String getGroup() {
        return "sky_mill";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.SKY_MILL.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SKY_MILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SKY_MILL_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<SkyMillRecipe> {
        @Override
        protected MapCodec<SkyMillRecipe> getCodec() {
            return environmentShapelessSerializerMapCodec(SkyMillRecipe::new);
        }

        @Override
        protected StreamCodec<PortRegistryFriendlyByteBuf, SkyMillRecipe> getStreamCodec() {
            return environmentShapelessSerializerSteamCodec(SkyMillRecipe::new);
        }
    }
}
