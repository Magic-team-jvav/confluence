package org.confluence.mod.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

public class SawmillRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public SawmillRecipe(ItemStack result, PortShapedRecipePattern pattern) {
        super(result, pattern);
    }

    public SawmillRecipe(ItemStack result, NonNullList<Ingredient> ingredients) {
        super(result, ingredients);
    }

    public SawmillRecipe(ItemStack result, Either<PortShapedRecipePattern, NonNullList<Ingredient>> either) {
        super(result, either);
    }

    @Override
    public String getGroup() {
        return "sawmill";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.SAWMILL.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAWMILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SAWMILL_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<SawmillRecipe> {
        @Override
        protected MapCodec<SawmillRecipe> getCodec() {
            return eitherSerializerMapCodec(SawmillRecipe::new);
        }

        @Override
        protected PortStreamCodec<PortRegistryFriendlyByteBuf, SawmillRecipe> getStreamCodec() {
            return eitherSerializerStreamCodec(SawmillRecipe::new);
        }
    }
}
