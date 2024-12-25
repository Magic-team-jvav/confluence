package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.AltarRecipe;
import org.jetbrains.annotations.NotNull;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class AltarCategory implements IRecipeCategory<AltarRecipe> {
    public static final RecipeType<AltarRecipe> TYPE = RecipeType.create(Confluence.MODID, "altar", AltarRecipe.class);
    private static final Component TITLE = Component.translatable("title.confluence.altar");
    private final IDrawable icon;

    public AltarCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.DEMON_ALTAR.toStack());
    }

    @Override
    public @NotNull RecipeType<AltarRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TITLE;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return ModJeiPlugin.QUARTER_BACKGROUND;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull AltarRecipe recipe, @NotNull IFocusGroup focusGroup) {
        // input
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int size = ingredients.size();
        if (size == 1) {
            addInput(builder, 24, 8, ingredients.getFirst());
        } else if (size == 2) {
            addInput(builder, 16, 8, ingredients.getFirst());
            addInput(builder, 32, 8, ingredients.get(1));
        } else if (size == 3) {
            addInput(builder, 16, 16, ingredients.getFirst());
            addInput(builder, 32, 16, ingredients.get(1));
            addInput(builder, 24, 0, ingredients.get(2));
        } else {
            addInput(builder, 16, 0, ingredients.getFirst());
            addInput(builder, 16, 16, ingredients.get(1));
            addInput(builder, 32, 0, ingredients.get(2));
            addInput(builder, 32, 16, ingredients.get(3));
        }
        // output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 8).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(@NotNull AltarRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ModJeiPlugin.drawArrowRight(guiGraphics, 50, 6, true);
    }
}
