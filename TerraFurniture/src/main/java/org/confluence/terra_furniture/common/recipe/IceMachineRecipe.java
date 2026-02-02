package org.confluence.terra_furniture.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFRegistries;

public class IceMachineRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public IceMachineRecipe(ItemStack result, ShapedRecipePattern pattern) {
        super(result, pattern);
    }

    @Override
    public String getGroup() {
        return "ice_machine";
    }

    @Override
    public ItemStack getToastSymbol() {
        return TFBlocks.ICE_MACHINE.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TFRegistries.ICE_MACHINE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TFRegistries.ICE_MACHINE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<IceMachineRecipe> {
        public static final MapCodec<IceMachineRecipe> CODEC = EitherAmountRecipe4x.shapedSerializerMapCodec(IceMachineRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, IceMachineRecipe> STREAM_CODEC = EitherAmountRecipe4x.shapedSerializerSteamCodec(IceMachineRecipe::new);

        @Override
        public MapCodec<IceMachineRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IceMachineRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
