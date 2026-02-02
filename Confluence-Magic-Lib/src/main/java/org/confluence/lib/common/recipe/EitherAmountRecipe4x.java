package org.confluence.lib.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.confluence.lib.mixed.LibShapedRecipePattern;
import org.confluence.lib.util.LibStreamCodecUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EitherAmountRecipe4x<I extends MenuRecipeInput> extends AbstractAmountRecipe<I> {
    public static final StreamCodec<RegistryFriendlyByteBuf, Either<ShapedRecipePattern, NonNullList<Ingredient>>> EITHER_CODEC = ByteBufCodecs.either(ShapedRecipePattern.STREAM_CODEC, LibStreamCodecUtils.INGREDIENTS);
    public final Either<ShapedRecipePattern, NonNullList<Ingredient>> either;

    public EitherAmountRecipe4x(ItemStack result, ShapedRecipePattern pattern) {
        super(result, pattern.ingredients());
        this.either = Either.left(pattern);
        LibShapedRecipePattern.setNonSymmetricalMatching(pattern);
    }

    public EitherAmountRecipe4x(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
        this.either = Either.right(ingredients);
    }

    public EitherAmountRecipe4x(ItemStack result, Either<ShapedRecipePattern, NonNullList<Ingredient>> either) {
        super(result, either.map(ShapedRecipePattern::ingredients, Function.identity()));
        this.either = either;
        either.ifLeft(LibShapedRecipePattern::setNonSymmetricalMatching);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return either.map(shaped -> width >= shaped.width() && height >= shaped.height(), shapeless -> true);
    }

    @Override
    public boolean matches(I input, Level level) {
        return either.map(
                shaped -> shaped.matches(input.asCraftingInput(false)),
                shapeless -> matches(input.size(), input::getItem, shapeless)
        );
    }

    @Override
    public ItemStack assembleAndExtract(I input, HolderLookup.Provider registries) {
        either
                .ifLeft(shaped -> consumeShaped(input, 4, 4, shaped))
                .ifRight(shapeless -> consumeShapeless(input, shapeless));
        return assemble(input, registries);
    }

    @Override
    public boolean isIncomplete() {
        return either.map(
                shaped -> shaped.ingredients().isEmpty() || shaped.ingredients().stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(Ingredient::hasNoItems),
                shapeless -> shapeless.isEmpty() || shapeless.stream().anyMatch(Ingredient::hasNoItems)
        );
    }

    @Override
    protected int maxIngredientSize() {
        return 16;
    }

    public static <R extends EitherAmountRecipe4x<?>> MapCodec<R> shapedSerializerMapCodec(BiFunction<ItemStack, ShapedRecipePattern, R> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.either.left().orElseThrow())
        ).apply(instance, factory));
    }

    public static <R extends EitherAmountRecipe4x<?>> StreamCodec<RegistryFriendlyByteBuf, R> shapedSerializerSteamCodec(BiFunction<ItemStack, ShapedRecipePattern, R> factory) {
        return StreamCodec.composite(
                ItemStack.STREAM_CODEC, r -> r.result,
                ShapedRecipePattern.STREAM_CODEC, r -> r.either.left().orElseThrow(),
                factory
        );
    }

    public static <R extends EitherAmountRecipe4x<?>> MapCodec<R> eitherSerializerMapCodec(BiFunction<ItemStack, Either<ShapedRecipePattern, NonNullList<Ingredient>>, R> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.mapEither(ShapedRecipePattern.MAP_CODEC, INGREDIENTS_CODEC).forGetter(recipe -> recipe.either)
        ).apply(instance, factory));
    }

    public static <R extends EitherAmountRecipe4x<?>> StreamCodec<RegistryFriendlyByteBuf, R> eitherSerializerStreamCodec(BiFunction<ItemStack, Either<ShapedRecipePattern, NonNullList<Ingredient>>, R> factory) {
        return StreamCodec.composite(
                ItemStack.STREAM_CODEC, r -> r.result,
                EITHER_CODEC, r -> r.either,
                factory
        );
    }
}
