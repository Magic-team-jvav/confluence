package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class SkyMillRecipe extends AbstractAmountRecipe<EnvironmentRecipeInput> {
    public SkyMillRecipe(ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pResult, pIngredients);
    }

    @Override
    public boolean matches(EnvironmentRecipeInput input, Level pLevel) {
        return input.getAccess().matches(this) && super.matches(input, pLevel);
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

    public static class Serializer implements RecipeSerializer<SkyMillRecipe> {
        public static final MapCodec<SkyMillRecipe> CODEC = shapelessSerializerMapCodec(SkyMillRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, SkyMillRecipe> STREAM_CODEC = shapelessSerializerSteamCodec(SkyMillRecipe::new);

        @Override
        public MapCodec<SkyMillRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SkyMillRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
