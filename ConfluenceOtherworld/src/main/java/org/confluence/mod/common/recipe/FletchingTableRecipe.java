package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.menu.FletchingTableMenu;
import org.jetbrains.annotations.Nullable;

public class FletchingTableRecipe implements Recipe<FletchingTableRecipe.Input> {
    private final ItemStack result;
    private final Ingredient tail;
    private final Ingredient body;
    private final Ingredient head;
    private final NonNullList<Ingredient> ingredients;

    public FletchingTableRecipe(ItemStack result, Ingredient tail, Ingredient body, Ingredient head) {
        this.result = result;
        this.tail = tail;
        this.body = body;
        this.head = head;
        this.ingredients = NonNullList.of(Ingredient.EMPTY, tail, body, head);
    }

    @Override
    public boolean matches(Input input, Level level) {
        return tail.test(input.getTail()) && body.test(input.getBody()) && head.test(input.getHead());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
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
    public final ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return getResult().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public final ItemStack getResultItem(@Nullable HolderLookup.Provider registries) {
        return getResult();
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLETCHING_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FLETCHING_TABLE_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<FletchingTableRecipe> {
        @Override
        protected MapCodec<FletchingTableRecipe> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FletchingTableRecipe::getResult),
                    Ingredient.CODEC_NONEMPTY.lenientOptionalFieldOf("tail", Ingredient.EMPTY).forGetter(FletchingTableRecipe::getTail),
                    Ingredient.CODEC_NONEMPTY.lenientOptionalFieldOf("body", Ingredient.EMPTY).forGetter(FletchingTableRecipe::getBody),
                    Ingredient.CODEC_NONEMPTY.lenientOptionalFieldOf("head", Ingredient.EMPTY).forGetter(FletchingTableRecipe::getHead)
            ).apply(instance, FletchingTableRecipe::new));
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, FletchingTableRecipe> getStreamCodec() {
            return StreamCodec.composite(
                    ItemStack.STREAM_CODEC, FletchingTableRecipe::getResult,
                    Ingredient.CONTENTS_STREAM_CODEC, FletchingTableRecipe::getTail,
                    Ingredient.CONTENTS_STREAM_CODEC, FletchingTableRecipe::getBody,
                    Ingredient.CONTENTS_STREAM_CODEC, FletchingTableRecipe::getHead,
                    FletchingTableRecipe::new
            );
        }
    }

    public static class Input extends SimpleContainer implements RecipeInput {
        private final FletchingTableMenu menu;

        public Input(FletchingTableMenu menu) {
            super(3);
            this.menu = menu;
        }

        public void setTail(ItemStack tail) {
            setItem(0, tail);
        }

        public ItemStack getTail() {
            return getItem(0);
        }

        public void setBody(ItemStack body) {
            setItem(1, body);
        }

        public ItemStack getBody() {
            return getItem(1);
        }

        public void setHead(ItemStack head) {
            setItem(2, head);
        }

        public ItemStack getHead() {
            return getItem(2);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            menu.slotsChanged(this);
        }

        @Override
        public int size() {
            return getContainerSize();
        }
    }
}
