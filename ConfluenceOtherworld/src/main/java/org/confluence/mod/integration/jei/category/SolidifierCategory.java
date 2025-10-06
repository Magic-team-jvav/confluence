package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SolidifierRecipe;
import org.confluence.mod.integration.jei.EitherRecipe4xHelper;
import org.jetbrains.annotations.Nullable;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class SolidifierCategory implements IRecipeCategory<RecipeHolder<SolidifierRecipe>> {
    public static final RecipeType<RecipeHolder<SolidifierRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("solidifier"));
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/solidifier.png");
    private final IDrawable icon;
    private final EitherRecipe4xHelper helper;

    public SolidifierCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.SOLIDIFIER.toStack());
        this.helper = new EitherRecipe4xHelper(jeiHelpers.getIngredientManager());
    }

    @Override
    public RecipeType<RecipeHolder<SolidifierRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.solidifier");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SolidifierRecipe> recipe, IFocusGroup focuses) {
        ShapedRecipePattern pattern = recipe.value().either.orThrow();
        int width = pattern.width();
        int height = pattern.height();
        boolean symmetrical = pattern.symmetrical;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (symmetrical) {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.value().ingredients.get(width - j - 1 + i * width));
                } else {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.value().ingredients.get(j + i * width));
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33).addItemStack(recipe.value().getResultItem(null));
    }

    @Override
    public void draw(RecipeHolder<SolidifierRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
        if (mouseX >= 80 && mouseX <= 80 + 28 && mouseY >= 29 && mouseY <= 29 + 23) {
            helper.drawSummary(recipeSlotsView, guiGraphics);
        }
    }
}
