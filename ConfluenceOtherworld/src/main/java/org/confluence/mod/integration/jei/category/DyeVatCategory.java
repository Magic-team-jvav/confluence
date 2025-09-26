package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.DyeVatRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.jetbrains.annotations.Nullable;

public class DyeVatCategory implements IRecipeCategory<RecipeHolder<DyeVatRecipe>> {
    public static final RecipeType<RecipeHolder<DyeVatRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("dye_vat"));
    private final IDrawable icon;

    public DyeVatCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.DYE_VAT.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<DyeVatRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.dye_vat");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DyeVatRecipe> recipe, IFocusGroup focusGroup) {
        ModJeiPlugin.set4IngredientsRecipe(builder, recipe);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<DyeVatRecipe> recipe, IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        builder.addRecipeArrow().setPosition(50 + 5, 6 + 2);
    }
}
