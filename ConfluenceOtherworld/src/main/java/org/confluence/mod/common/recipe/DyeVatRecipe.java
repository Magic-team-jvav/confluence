package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class DyeVatRecipe extends AbstractAmountRecipe<MenuRecipeInput> {
    public DyeVatRecipe(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
    }

    @Override
    protected int maxIngredientSize() {
        return 4;
    }

    @Override
    public String getGroup() {
        return "dye_vat";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.DYE_VAT.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DYE_VAT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DYE_VAT_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<DyeVatRecipe> {
        @Override
        protected MapCodec<DyeVatRecipe> getCodec() {
            return AbstractAmountRecipe.shapelessSerializerMapCodec(DyeVatRecipe::new);
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, DyeVatRecipe> getStreamCodec() {
            return AbstractAmountRecipe.shapelessSerializerSteamCodec(DyeVatRecipe::new);
        }
    }
}
