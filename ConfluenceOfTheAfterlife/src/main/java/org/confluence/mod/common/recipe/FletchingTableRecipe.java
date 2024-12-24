package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModRecipes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FletchingTableRecipe implements Recipe<FletchingTableRecipeInput> {
    private final ItemStack result;
    private final Ingredient tail;
    private final Ingredient body;
    private final Ingredient head;

    protected FletchingTableRecipe(ItemStack pResult, Ingredient tail, Ingredient body, Ingredient head) {
        this.result = pResult;
        this.tail = tail;
        this.body = body;
        this.head = head;
    }

    @Override
    public boolean matches(@NotNull FletchingTableRecipeInput input, @NotNull Level level) {
        return tail.test(input.getTail()) && body.test(input.getBody()) && head.test(input.getHead());
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(tail, body, head);
    }

    public ItemStack getResult() {
        return result;
    }

    public Ingredient getTail() {
        return tail;
    }

    public Ingredient getBody() {
        return body;
    }

    public Ingredient getHead() {
        return head;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull FletchingTableRecipeInput input, HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@Nullable HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLETCHING_TABLE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.FLETCHING_TABLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FletchingTableRecipe> {
        public static final MapCodec<FletchingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.lenientOptionalFieldOf("tail", Ingredient.EMPTY).forGetter(recipe -> recipe.tail),
                Ingredient.CODEC_NONEMPTY.lenientOptionalFieldOf("body", Ingredient.EMPTY).forGetter(recipe -> recipe.body),
                Ingredient.CODEC_NONEMPTY.lenientOptionalFieldOf("head", Ingredient.EMPTY).forGetter(recipe -> recipe.head)
        ).apply(instance, FletchingTableRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, FletchingTableRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public @NotNull MapCodec<FletchingTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FletchingTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FletchingTableRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            Ingredient tail = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient body = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient head = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new FletchingTableRecipe(itemstack, tail, body, head);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FletchingTableRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.tail);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.body);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.head);
        }
    }
}
