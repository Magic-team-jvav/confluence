package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.mod.mixin.accessor.ShapedRecipePatternAccessor;

import java.util.Optional;
import java.util.function.Function;

public class AmountShapedRecipePattern extends ShapedRecipePattern {
    public static final Function<ShapedRecipePattern, AmountShapedRecipePattern> TO_AMOUNT = pattern -> new AmountShapedRecipePattern(pattern.width(), pattern.height(), pattern.ingredients(), ((ShapedRecipePatternAccessor) pattern).getData());
    public static final Function<AmountShapedRecipePattern, ShapedRecipePattern> NO_AMOUNT = pattern -> new ShapedRecipePattern(pattern.width(), pattern.height(), pattern.ingredients(), ((ShapedRecipePatternAccessor) pattern).getData());
    public static final MapCodec<AmountShapedRecipePattern> MAP_CODEC = ShapedRecipePattern.MAP_CODEC.xmap(TO_AMOUNT, NO_AMOUNT);
    public static final StreamCodec<RegistryFriendlyByteBuf, AmountShapedRecipePattern> STREAM_CODEC = ShapedRecipePattern.STREAM_CODEC.map(TO_AMOUNT, NO_AMOUNT);

    public AmountShapedRecipePattern(int width, int height, NonNullList<Ingredient> ingredients, Optional<Data> data) {
        super(width, height, ingredients, data);
    }

    public boolean matches(RecipeInput input, int width, int height) {
        if (input.size() == ingredients().size()) {
            if (width == width() && height == height()) {
                if (!((ShapedRecipePatternAccessor) this).getSymmetrical() && matches(input, true)) {
                    return true;
                }
                return matches(input, false);
            }
        }
        return false;
    }

    private boolean matches(RecipeInput input, boolean symmetrical) {
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                Ingredient ingredient;
                if (symmetrical) {
                    ingredient = ingredients().get(width() - j - 1 + i * width());
                } else {
                    ingredient = ingredients().get(j + i * width());
                }
                ItemStack itemstack = getItem(input, j, i);
                if (!ingredient.test(itemstack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static ItemStack getItem(RecipeInput input, int row, int column) {
        return input.getItem(row + column * 4);
    }
}
