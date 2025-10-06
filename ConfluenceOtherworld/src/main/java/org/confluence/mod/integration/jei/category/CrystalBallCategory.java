package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.CrystalBallRecipe;

public class CrystalBallCategory implements IRecipeCategory<RecipeHolder<CrystalBallRecipe>> {
    public static final RecipeType<RecipeHolder<CrystalBallRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("crystal_ball"));
    private static final Component TITLE = Component.translatable("title.confluence.crystal_ball");
    private final IDrawable icon;

    public CrystalBallCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.SKY_MILL.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<CrystalBallRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return 72;
    }

    @Override
    public int getHeight() {
        return 72;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CrystalBallRecipe> recipe, IFocusGroup focuses) {

    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<CrystalBallRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        tooltip.addAll(recipe.value().getEnvironment().toDescriptions());
    }
}
