package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class HardmodeForgeRecipe extends EnhancedForgeRecipe {
    public HardmodeForgeRecipe(ItemStack result, NonNullList<Ingredient> ingredients, float experience, int cookingTime, boolean requiresFuel) {
        super(result, ingredients, experience, cookingTime, requiresFuel);
    }

    @Override
    public String getGroup() {
        return "hardmode_forge";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.TITANIUM_FORGE.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HARDMODE_FORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HARDMODE_FORGE_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<HardmodeForgeRecipe> {
        @Override
        protected MapCodec<HardmodeForgeRecipe> getCodec() {
            return EnhancedForgeRecipe.codec(HardmodeForgeRecipe::new);
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, HardmodeForgeRecipe> getStreamCodec() {
            return EnhancedForgeRecipe.streamCodec(HardmodeForgeRecipe::new);
        }
    }
}
