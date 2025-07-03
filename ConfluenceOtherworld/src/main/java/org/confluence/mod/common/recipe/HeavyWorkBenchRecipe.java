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
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class HeavyWorkBenchRecipe extends EitherAmountRecipe4x<EnvironmentRecipeInput> {
    public HeavyWorkBenchRecipe(ItemStack pResult, Either<ShapedRecipePattern, NonNullList<Ingredient>> either) {
        super(pResult, either);
    }

    public HeavyWorkBenchRecipe(ItemStack pResult, ShapedRecipePattern pattern) {
        super(pResult, pattern);
    }

    @Override
    public boolean matches(EnvironmentRecipeInput input, Level level) {
        return input.getAccess().matches(this) && super.matches(input, level);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HEAVY_WORK_BENCH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HEAVY_WORK_BENCH_TYPE.get();
    }

    @Override
    public String getGroup() {
        return "heavy_work_bench";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.HEAVY_WORK_BENCH.toStack();
    }

    public static class Serializer implements RecipeSerializer<HeavyWorkBenchRecipe> {
        public static final MapCodec<HeavyWorkBenchRecipe> CODEC = EitherAmountRecipe4x.shapedSerializerMapCodec(HeavyWorkBenchRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, HeavyWorkBenchRecipe> STREAM_CODEC = EitherAmountRecipe4x.eitherSerializerSteamCodec(HeavyWorkBenchRecipe::new);

        @Override
        public MapCodec<HeavyWorkBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeavyWorkBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
