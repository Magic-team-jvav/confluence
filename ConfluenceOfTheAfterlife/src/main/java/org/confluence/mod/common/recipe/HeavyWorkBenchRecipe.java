package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;
import org.jetbrains.annotations.NotNull;

public class HeavyWorkBenchRecipe extends AbstractAmountRecipe {
    public final ShapedRecipePattern pattern;

    protected HeavyWorkBenchRecipe(ShapedRecipePattern pattern, ItemStack pResult) {
        super(pResult, pattern.ingredients());
        this.pattern = pattern;
    }

    @Override
    protected int maxIngredientSize() {
        return 16;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.HEAVY_WORK_BENCH_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.HEAVY_WORK_BENCH_TYPE.get();
    }

    @Override
    public @NotNull String getGroup() {
        return "heavy_work_bench";
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return FunctionalBlocks.HEAVY_WORK_BENCH.toStack();
    }

    public static class Serializer implements RecipeSerializer<HeavyWorkBenchRecipe> {
        public static final MapCodec<HeavyWorkBenchRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, HeavyWorkBenchRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, HeavyWorkBenchRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public @NotNull MapCodec<HeavyWorkBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, HeavyWorkBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static HeavyWorkBenchRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new HeavyWorkBenchRecipe(shapedrecipepattern, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, HeavyWorkBenchRecipe recipe) {
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}
