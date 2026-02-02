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

public class LivingLoomRecipe extends EitherAmountRecipe4x<MenuRecipeInput> {
    public LivingLoomRecipe(ItemStack result, ShapedRecipePattern pattern) {
        super(result, pattern);
    }

    @Override
    public String getGroup() {
        return "living_loom";
    }

    @Override
    public ItemStack getToastSymbol() {
        return TFBlocks.LIVING_LOOM.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TFRegistries.LIVING_LOOM_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TFRegistries.LIVING_LOOM_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<LivingLoomRecipe> {
        public static final MapCodec<LivingLoomRecipe> CODEC = EitherAmountRecipe4x.shapedSerializerMapCodec(LivingLoomRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, LivingLoomRecipe> STREAM_CODEC = EitherAmountRecipe4x.shapedSerializerSteamCodec(LivingLoomRecipe::new);

        @Override
        public MapCodec<LivingLoomRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LivingLoomRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
