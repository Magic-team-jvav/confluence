package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PortRegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.ItemStackHandlerRecipeInput;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class AltarRecipe extends AbstractAmountRecipe<ItemStackHandlerRecipeInput> {
    public AltarRecipe(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
    }

    @Override
    protected int maxIngredientSize() {
        return 5;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALTAR_SERIALIZER.get();
    }

    @Override
    public String getGroup() {
        return "altar";
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALTAR_TYPE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.DEMON_ALTAR.toStack();
    }

    public static class Serializer extends SimpleRecipeSerializer<AltarRecipe> {
        @Override
        protected MapCodec<AltarRecipe> getCodec() {
            return shapelessSerializerMapCodec(AltarRecipe::new);
        }

        @Override
        protected StreamCodec<PortRegistryFriendlyByteBuf, AltarRecipe> getStreamCodec() {
            return shapelessSerializerSteamCodec(AltarRecipe::new);
        }
    }
}
