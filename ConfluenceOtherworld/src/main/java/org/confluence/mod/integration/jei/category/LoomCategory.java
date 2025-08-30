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
import org.confluence.mod.common.recipe.LoomRecipe;
import org.confluence.terra_furniture.TerraFurniture;
import org.jetbrains.annotations.Nullable;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class LoomCategory implements IRecipeCategory<RecipeHolder<LoomRecipe>> {
    public static final RecipeType<RecipeHolder<LoomRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("loom"));
    private static final Component TITLE = Component.translatable("title.confluence.loom");
    private static final ResourceLocation BACKGROUND = TerraFurniture.asResource("textures/gui/loom.png");
    private final IDrawable icon;

    public LoomCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.LOOM.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<LoomRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<LoomRecipe> recipe, IFocusGroup focuses) {
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
    public void draw(RecipeHolder<LoomRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
    }
}
