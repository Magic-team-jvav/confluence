package org.confluence.mod.common.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;
import org.confluence.terra_curio.common.recipe.AmountIngredient;
import org.jetbrains.annotations.NotNull;

public class AltarRecipe extends AbstractAmountRecipe {
    public AltarRecipe(ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pResult, pIngredients);
    }

    @Override
    protected int maxIngredientSize() {
        return 4;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALTAR_SERIALIZER.get();
    }

    @Override
    public @NotNull String getGroup() {
        return "altar";
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.ALTAR_TYPE.get();
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(FunctionalBlocks.DEMON_ALTAR.get());
    }

    public static class Serializer extends AbstractAmountRecipe.Serializer<AltarRecipe> {
        public static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                    Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                    if (ingredients.length == 0) {
                        return DataResult.error(() -> "No ingredients for workshop recipe");
                    } else {
                        return ingredients.length > 12 ? DataResult.error(() -> "Too many ingredients for workshop recipe. The maximum is: 12") : DataResult.success(NonNullList.of(AmountIngredient.EMPTY, ingredients));
                    }
                }, DataResult::success).forGetter(recipe -> recipe.ingredients)
        ).apply(instance, AltarRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC = StreamCodec.of(AltarRecipe.Serializer::toNetwork, AltarRecipe.Serializer::fromNetwork);

        @Override
        protected AltarRecipe newInstance(ItemStack pResult, NonNullList<Ingredient> pIngredients) {
            return new AltarRecipe(pResult, pIngredients);
        }

        @Override
        public @NotNull MapCodec<AltarRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static AltarRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
            nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new AltarRecipe(itemstack, nonnulllist);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, AltarRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }

    public static class Type implements RecipeType<AltarRecipe> {
        @Override
        public String toString() {
            return "confluence:altar_type";
        }
    }
}
