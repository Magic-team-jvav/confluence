package org.confluence.mod.common.recipe;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import PortLib.extensions.net.minecraft.world.item.crafting.Ingredient.PortIngredientExtension;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.menu.FletchingTableMenu;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.item.crafting.PortRecipe;
import org.mesdag.portlib.wrapper.world.item.crafting.PortRecipeInput;

public class FletchingTableRecipe implements PortRecipe<FletchingTableRecipe.Input> {
    private final ItemStack result;
    private final Ingredient tail;
    private final Ingredient body;
    private final Ingredient head;
    private final NonNullList<Ingredient> ingredients;
    private ResourceLocation id;

    public FletchingTableRecipe(ItemStack result, Ingredient tail, Ingredient body, Ingredient head) {
        this.result = result;
        this.tail = tail;
        this.body = body;
        this.head = head;
        this.ingredients = NonNullList.of(Ingredient.EMPTY, tail, body, head);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void setId(ResourceLocation id) {
        this.id = id;
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
    public ItemStack assemble(Input input, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
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
                    PortItemStackExtension.strictCodec().fieldOf("result").forGetter(recipe -> recipe.result),
                    PortCodecExtension.lenientOptionalFieldOf(PortIngredientExtension.codecNonempty(), "tail", Ingredient.EMPTY).forGetter(recipe -> recipe.tail),
                    PortCodecExtension.lenientOptionalFieldOf(PortIngredientExtension.codecNonempty(), "body", Ingredient.EMPTY).forGetter(recipe -> recipe.body),
                    PortCodecExtension.lenientOptionalFieldOf(PortIngredientExtension.codecNonempty(), "head", Ingredient.EMPTY).forGetter(recipe -> recipe.head)
            ).apply(instance, FletchingTableRecipe::new));
        }

        @Override
        protected PortStreamCodec<PortRegistryFriendlyByteBuf, FletchingTableRecipe> getStreamCodec() {
            return PortStreamCodec.composite(
                    PortItemStackExtension.streamCodec(), recipe -> recipe.result,
                    PortIngredientExtension.contentsStreamCodec(), recipe -> recipe.tail,
                    PortIngredientExtension.contentsStreamCodec(), recipe -> recipe.body,
                    PortIngredientExtension.contentsStreamCodec(), recipe -> recipe.head,
                    FletchingTableRecipe::new
            );
        }
    }

    public static class Input extends SimpleContainer implements PortRecipeInput {
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
