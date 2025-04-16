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

public class CrystalBallRecipe extends AbstractAmountRecipe<EnvironmentRecipeInput> {
    public CrystalBallRecipe(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
    }

    @Override
    public boolean matches(EnvironmentRecipeInput input, Level pLevel) {
        return input.getAccess().matches(this) && super.matches(input, pLevel);
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

    public static class Serializer implements RecipeSerializer<CrystalBallRecipe> {
        public static final MapCodec<CrystalBallRecipe> CODEC = shapelessSerializerMapCodec(CrystalBallRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, CrystalBallRecipe> STREAM_CODEC = shapelessSerializerSteamCodec(CrystalBallRecipe::new);

        @Override
        public MapCodec<CrystalBallRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrystalBallRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
