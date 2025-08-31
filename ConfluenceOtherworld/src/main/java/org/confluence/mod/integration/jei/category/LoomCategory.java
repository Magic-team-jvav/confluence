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
import org.confluence.mod.common.recipe.LoomRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.jetbrains.annotations.Nullable;

public class LoomCategory implements IRecipeCategory<RecipeHolder<LoomRecipe>> {
    public static final RecipeType<RecipeHolder<LoomRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("loom"));
    private static final Component TITLE = Component.translatable("title.confluence.loom");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/loom.png");
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
        ModJeiPlugin.setEitherRecipe4x(builder, recipe);
    }

    @Override
    public void draw(RecipeHolder<LoomRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
    }
}
