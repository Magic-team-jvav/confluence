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
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.menu.AlchemyTableMenu;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableRecipe implements Recipe<AlchemyTableRecipe.Input> {
    private final ItemStack result;
    private final Ingredient base;
    private final NonNullList<Ingredient> ingredients;

    public AlchemyTableRecipe(ItemStack result, Ingredient base, NonNullList<Ingredient> ingredients) {
        if (ingredients.size() > 6) {
            throw new RuntimeException("Too many ingredients for '" + getGroup() + "' recipe. The maximum is: 6");
        }
        this.result = result;
        this.base = base;
        this.ingredients = ingredients;
    }

    public Ingredient getBase() {
        return base;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return base.test(input.getItem(0)) && AbstractAmountRecipe.matches(input.materials.getContainerSize(), input.materials::getItem, ingredients);
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return getResultItem(null).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMY_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALCHEMY_TABLE_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<AlchemyTableRecipe> {
        @Override
        protected MapCodec<AlchemyTableRecipe> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                    Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(recipe -> recipe.base),
                    AbstractAmountRecipe.INGREDIENTS_CODEC.forGetter(recipe -> recipe.ingredients)
            ).apply(instance, AlchemyTableRecipe::new));
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, AlchemyTableRecipe> getStreamCodec() {
            return new StreamCodec<>() {
                @Override
                public AlchemyTableRecipe decode(RegistryFriendlyByteBuf buffer) {
                    int size = buffer.readVarInt();
                    NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
                    nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                    return new AlchemyTableRecipe(itemstack, input, nonnulllist);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, AlchemyTableRecipe recipe) {
                    buffer.writeVarInt(recipe.ingredients.size());
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    }
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base);
                }
            };
        }
    }

    public static class Input extends SimpleContainer implements RecipeInput {
        private final AlchemyTableMenu menu;
        private final SimpleContainer materials = new SimpleContainer(6);

        public Input(AlchemyTableMenu menu) {
            super(1);
            this.menu = menu;
        }

        public SimpleContainer getMaterials() {
            return materials;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            if (index == 0) {
                super.setItem(0, stack);
            } else {
                materials.setItem(index - 1, stack);
            }
        }

        @Override
        public ItemStack getItem(int index) {
            if (index == 0) {
                return super.getItem(0);
            }
            return materials.getItem(index - 1);
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            if (index == 0) {
                return super.removeItem(0, count);
            }
            return materials.removeItem(index - 1, count);
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            if (index == 0) {
                return super.removeItemNoUpdate(0);
            }
            return materials.removeItemNoUpdate(index - 1);
        }

        @Override
        public void clearContent() {
            super.clearContent();
            materials.clearContent();
        }

        @Override
        public boolean isEmpty() {
            return super.isEmpty() && materials.isEmpty();
        }

        @Override
        public void setChanged() {
            super.setChanged();
            materials.setChanged();
            menu.slotsChanged(this);
        }

        @Override
        public int size() {
            return getContainerSize();
        }
    }
}
