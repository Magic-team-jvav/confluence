package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class HeavyWorkBenchRecipe extends AbstractAmountRecipe<EnvironmentRecipeInput> {
    public final ShapedRecipePattern pattern;

    public HeavyWorkBenchRecipe(ItemStack pResult, ShapedRecipePattern pattern) {
        super(pResult, pattern.ingredients());
        this.pattern = pattern;
    }

    public static HeavyWorkBenchRecipe of(ShapedRecipePattern pattern, ItemStack result) {
        return new HeavyWorkBenchRecipe(result, pattern);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= pattern.width() && height >= pattern.height();
    }

    @Override
    public boolean matches(EnvironmentRecipeInput input, Level pLevel) {
        return input.getAccess().matches(this) && pattern.matches(input.asCraftingInput(false));
    }

    @Override
    public ItemStack assembleAndExtract(EnvironmentRecipeInput input, HolderLookup.Provider registries) {
        consumeShaped(input, 4, 4, pattern);
        return assemble(input, registries);
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(Ingredient::hasNoItems);
    }

    @Override
    protected int maxIngredientSize() {
        return 16;
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
        public static final MapCodec<HeavyWorkBenchRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern)
        ).apply(instance, HeavyWorkBenchRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, HeavyWorkBenchRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<HeavyWorkBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeavyWorkBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static HeavyWorkBenchRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            return new HeavyWorkBenchRecipe(itemstack, shapedrecipepattern);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, HeavyWorkBenchRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
        }
    }
}
