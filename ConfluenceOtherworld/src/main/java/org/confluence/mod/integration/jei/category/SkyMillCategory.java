package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SkyMillRecipe;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class SkyMillCategory implements IRecipeCategory<SkyMillRecipe> {
    public static final RecipeType<SkyMillRecipe> TYPE = new RecipeType<>(Confluence.asResource("sky_mill"), SkyMillRecipe.class);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/jei/sky_mill.png");
    private final IDrawable icon;

    public SkyMillCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.SKY_MILL.toStack());
    }

    @Override
    public RecipeType<SkyMillRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.sky_mill");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SkyMillRecipe recipe, IFocusGroup focusGroup) {
        // input
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int size = ingredients.size();
        if (size == 1) {
            addInput(builder, 28, 51, ingredients.get(0));
        } else if (size == 2) {
            addInput(builder, 9, 32, ingredients.get(0));
            addInput(builder, 47, 32, ingredients.get(1));
        } else {
            addInput(builder, 28, 51, ingredients.get(0));
            addInput(builder, 9, 32, ingredients.get(1));
            addInput(builder, 47, 32, ingredients.get(2));
        }
        // output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 28, 8).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(SkyMillRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 72, 72, 72, 72);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, SkyMillRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        tooltip.addAll(recipe.getEnvironment().toDescriptions());
    }

    @Override
    public ResourceLocation getRegistryName(SkyMillRecipe recipe) {
        return Confluence.asResource(recipe.getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.getResult().getItem()).getPath());
    }
}
