package org.confluence.mod.common.recipe.special;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.DragonsBreathPepperBlock;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.Nullable;

public class DragonPepperExtractingRecipe extends ShapelessRecipe {
    public static final ResourceLocation ID = Confluence.asResource("dragon_pepper_extracting");
    private static DragonPepperExtractingRecipe INSTANCE;

    private DragonPepperExtractingRecipe() {
        super(ID, "", CraftingBookCategory.MISC, FoodItems.END_DRAGON_PEPPER_SEED.toStack(), NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(FoodItems.END_DRAGON_PEPPER)
        ));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DRAGON_PEPPER_EXTRACTING_SERIALIZER.get();
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        if (super.matches(input, level)) {
            for (ItemStack stack : input.getItems()) {
                if (stack.isEmpty() || !stack.is(FoodItems.END_DRAGON_PEPPER.get())) {
                    continue;
                }
                return LibUtils.getItemStackNbtIfPresent(stack) != null;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registryAccess) {
        for (ItemStack stack : input.getItems()) {
            if (stack.isEmpty() || !stack.is(FoodItems.END_DRAGON_PEPPER.get())) continue;
            int maturity = DragonsBreathPepperBlock.getMaturity(stack);
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
        @Override
        public DragonPepperExtractingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            return getInstance();
        }

        @Override
        public @Nullable DragonPepperExtractingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return getInstance();
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DragonPepperExtractingRecipe recipe) {}
    }
}
