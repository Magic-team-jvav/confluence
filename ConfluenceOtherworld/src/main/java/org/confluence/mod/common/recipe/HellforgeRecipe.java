package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class HellforgeRecipe extends EnhancedForgeRecipe {
    public HellforgeRecipe(ItemStack result, NonNullList<Ingredient> ingredients, float experience, int cookingTime, boolean requiresFuel) {
        super(result, ingredients, experience, cookingTime, requiresFuel);
    }

    @Override
    public String getGroup() {
        return "hellforge";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.HELLFORGE.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HELLFORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HELLFORGE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HellforgeRecipe> {
        public static final MapCodec<HellforgeRecipe> CODEC = EnhancedForgeRecipe.codec(HellforgeRecipe::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, HellforgeRecipe> STREAM_CODEC = EnhancedForgeRecipe.streamCodec(HellforgeRecipe::new);

        @Override
        public MapCodec<HellforgeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HellforgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
