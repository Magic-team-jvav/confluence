package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;

import java.util.ArrayList;
import java.util.List;

public class HeavyWorkBenchRecipe extends AbstractAmountRecipe {
    public final ShapedRecipePattern pattern;

    protected HeavyWorkBenchRecipe(ItemStack pResult, ShapedRecipePattern pattern) {
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
    public boolean matches(RecipeInput input, Level pLevel) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack itemStack = input.getItem(i);
            itemStacks.add(itemStack);
        }
        return pattern.matches(CraftingInput.of(4, 4, itemStacks));
    }

    @Override
    public ItemStack assembleAndExtract(RecipeInput input, HolderLookup.Provider registries) {
        extractInput(input, ingredients, true);
        return assemble(input, registries);
    }

    @Override
    public boolean isIncomplete() {
        return false;
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
