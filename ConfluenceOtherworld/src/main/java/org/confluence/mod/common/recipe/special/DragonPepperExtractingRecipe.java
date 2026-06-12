package org.confluence.mod.common.recipe.special;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PortRegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.natural.DragonsBreathPepperBlock;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.FoodItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DragonPepperExtractingRecipe extends ShapelessRecipe {
    private static DragonPepperExtractingRecipe INSTANCE;

    private DragonPepperExtractingRecipe() {
        super("", CraftingBookCategory.MISC, FoodItems.END_DRAGON_PEPPER_SEED.toStack(), NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(FoodItems.END_DRAGON_PEPPER)
        ));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DRAGON_PEPPER_EXTRACTING_SERIALIZER.get();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (super.matches(input, level)) {
            for (int i = 0; i < input.ingredientCount(); i++) {
                ItemStack itemStack = input.getItem(i);
                if (itemStack.isEmpty() || !itemStack.is(FoodItems.END_DRAGON_PEPPER.get()))
                    continue;
                return LibUtils.getItemStackNbtIfPresent(itemStack) != null;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.ingredientCount(); i++) {
            ItemStack itemStack = input.getItem(i);
            if (itemStack.isEmpty() || !itemStack.is(FoodItems.END_DRAGON_PEPPER.get())) continue;
            int maturity = DragonsBreathPepperBlock.getMaturity(itemStack);
            int count = maturity >= 2 ? 2 : 1;
            return FoodItems.END_DRAGON_PEPPER_SEED.toStack(count);
        }
        return FoodItems.END_DRAGON_PEPPER_SEED.toStack();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    public static DragonPepperExtractingRecipe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DragonPepperExtractingRecipe();
        }
        return INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<DragonPepperExtractingRecipe> {
        public static final MapCodec<DragonPepperExtractingRecipe> CODEC = MapCodec.unit(DragonPepperExtractingRecipe::getInstance);
        public static final StreamCodec<PortRegistryFriendlyByteBuf, DragonPepperExtractingRecipe> STREAM_CODEC = LibStreamCodecUtils.unit(DragonPepperExtractingRecipe::getInstance);

        @Override
        public MapCodec<DragonPepperExtractingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<PortRegistryFriendlyByteBuf, DragonPepperExtractingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
