package org.confluence.mod.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.EnvironmentEitherAmountRecipe4x;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.ApiStatus;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

public class HeavyWorkBenchRecipe extends EnvironmentEitherAmountRecipe4x {
    public HeavyWorkBenchRecipe(ItemStack result, Either<PortShapedRecipePattern, NonNullList<Ingredient>> either, EnvironmentLevelAccess.Matcher environment) {
        super(result, either, environment);
    }

    @ApiStatus.Internal
    public HeavyWorkBenchRecipe(ItemStack result, Either<PortShapedRecipePattern, NonNullList<Ingredient>> either) {
        this(result, either, EnvironmentLevelAccess.Matcher.EMPTY);
    }

    public HeavyWorkBenchRecipe(ItemStack result, NonNullList<Ingredient> ingredients, EnvironmentLevelAccess.Matcher environment) {
        super(result, ingredients, environment);
    }

    public HeavyWorkBenchRecipe(ItemStack result, PortShapedRecipePattern pattern, EnvironmentLevelAccess.Matcher environment) {
        super(result, pattern, environment);
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

    public static class Serializer extends SimpleRecipeSerializer<HeavyWorkBenchRecipe> {
        @Override
        protected MapCodec<HeavyWorkBenchRecipe> getCodec() {
            return environmentEitherSerializerMapCodec(HeavyWorkBenchRecipe::new);
        }

        @Override
        protected PortStreamCodec<PortRegistryFriendlyByteBuf, HeavyWorkBenchRecipe> getStreamCodec() {
            return environmentEitherSerializerStreamCodec(HeavyWorkBenchRecipe::new);
        }
    }
}
