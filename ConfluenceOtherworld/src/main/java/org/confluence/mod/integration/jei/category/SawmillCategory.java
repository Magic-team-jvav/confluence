package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SawmillRecipe;
import org.confluence.mod.integration.jei.EitherRecipe4xHelper;
import org.jetbrains.annotations.Nullable;

public class SawmillCategory implements IRecipeCategory<RecipeHolder<SawmillRecipe>> {
    public static final RecipeType<RecipeHolder<SawmillRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("sawmill"));
    private static final Component TITLE = Component.translatable("title.confluence.sawmill");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/sawmill.png");
    private final IDrawable icon;
    private final EitherRecipe4xHelper helper;

    public SawmillCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.SAWMILL.toStack());
        this.helper = new EitherRecipe4xHelper(jeiHelpers.getIngredientManager());
    }

    @Override
    public RecipeType<RecipeHolder<SawmillRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 144;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SawmillRecipe> recipe, IFocusGroup focuses) {
        EitherRecipe4xHelper.setEitherRecipe4x(builder, recipe);
    }

    @Override
    public void draw(RecipeHolder<SawmillRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
        if (mouseX >= 80 && mouseX <= 80 + 28 && mouseY >= 29 && mouseY <= 29 + 23) {
            helper.drawSummary(recipeSlotsView, guiGraphics);
        }
    }
}
