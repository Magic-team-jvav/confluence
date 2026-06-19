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

public class HardmodeAnvilRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public HardmodeAnvilRecipe(ItemStack result, Either<PortShapedRecipePattern, NonNullList<Ingredient>> either) {
        super(result, either);
    }

    @Override
    public String getGroup() {
        return "hardmode_anvil";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.ORICHALCUM_ANVIL.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HARDMODE_ANVIL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HARDMODE_ANVIL_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<HardmodeAnvilRecipe> {
        @Override
        protected MapCodec<HardmodeAnvilRecipe> getCodec() {
            return eitherSerializerMapCodec(HardmodeAnvilRecipe::new);
        }

        @Override
        protected PortStreamCodec<PortRegistryFriendlyByteBuf, HardmodeAnvilRecipe> getStreamCodec() {
            return eitherSerializerStreamCodec(HardmodeAnvilRecipe::new);
        }
    }
}
