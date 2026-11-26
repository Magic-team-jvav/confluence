package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.LoomRecipe;
import org.confluence.mod.integration.jei.EitherRecipe4xHelper;

public class LoomCategory implements IRecipeCategory<LoomRecipe> {
    public static final RecipeType<LoomRecipe> TYPE = new RecipeType<>(Confluence.asResource("loom"), LoomRecipe.class);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/jei/loom.png");
    private final IDrawable icon;
    private final EitherRecipe4xHelper helper;

    public LoomCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.LOOM.toStack());
        this.helper = new EitherRecipe4xHelper(jeiHelpers.getIngredientManager());
    }

    @Override
    public RecipeType<LoomRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.loom");
    }

    @Override
    public IDrawable getIcon() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, LoomRecipe recipe, IFocusGroup focuses) {
        EitherRecipe4xHelper.setEitherRecipe4x(builder, recipe);
    }

    @Override
    public void draw(LoomRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
        if (mouseX >= 80 && mouseX <= 80 + 28 && mouseY >= 29 && mouseY <= 29 + 23) {
            helper.drawSummary(recipeSlotsView, guiGraphics);
        }
    }

    @Override
    public ResourceLocation getRegistryName(LoomRecipe recipe) {
        return Confluence.asResource(recipe.getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.getResult().getItem()).getPath());
    }
}
