package org.confluence.mod.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class HardmodeAnvilRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public HardmodeAnvilRecipe(ItemStack result, Either<ShapedRecipePattern, NonNullList<Ingredient>> either) {
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

    public static class Serializer implements RecipeSerializer<HardmodeAnvilRecipe> {
        public static final MapCodec<HardmodeAnvilRecipe> CODEC = eitherSerializerMapCodec(HardmodeAnvilRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, HardmodeAnvilRecipe> STREAM_CODEC = eitherSerializerStreamCodec(HardmodeAnvilRecipe::new);

        @Override
        public MapCodec<HardmodeAnvilRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HardmodeAnvilRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
