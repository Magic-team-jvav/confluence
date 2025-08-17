package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.AltarRecipe;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class AltarCategory implements IRecipeCategory<RecipeHolder<AltarRecipe>> {
    public static final RecipeType<RecipeHolder<AltarRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("altar"));
    private static final Component TITLE = Component.translatable("title.confluence.altar");
    private final IDrawable icon;

    public AltarCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.DEMON_ALTAR.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<AltarRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AltarRecipe> recipe, IFocusGroup focusGroup) {
        // input
        NonNullList<Ingredient> ingredients = recipe.value().getIngredients();
        int size = ingredients.size();
        if (size == 1) {
            addInput(builder, 24, 8, ingredients.getFirst(), true);
        } else if (size == 2) {
            addInput(builder, 15, 8, ingredients.get(0), true);
            addInput(builder, 33, 8, ingredients.get(1), true);
        } else if (size == 3) {
            addInput(builder, 15, 15, ingredients.get(0), true);
            addInput(builder, 33, 15, ingredients.get(1), true);
            addInput(builder, 24, 1, ingredients.get(2), true);
        } else {
            addInput(builder, 15, -1, ingredients.get(0), true);
            addInput(builder, 15, 17, ingredients.get(1), true);
            addInput(builder, 33, -1, ingredients.get(2), true);
            addInput(builder, 33, 17, ingredients.get(3), true);
        }
        // output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 8).addItemStack(recipe.value().getResultItem(null)).setOutputSlotBackground();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<AltarRecipe> recipe, IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        builder.addRecipeArrow().setPosition(50 + 5, 6 + 2);
    }
}
