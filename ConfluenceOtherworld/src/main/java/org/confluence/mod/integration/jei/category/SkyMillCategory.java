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
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SkyMillRecipe;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class SkyMillCategory implements IRecipeCategory<RecipeHolder<SkyMillRecipe>> {
    public static final RecipeType<RecipeHolder<SkyMillRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("sky_mill"));
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/sky_mill.png");
    private final IDrawable icon;

    public SkyMillCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.SKY_MILL.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<SkyMillRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SkyMillRecipe> recipe, IFocusGroup focusGroup) {
        // input
        NonNullList<Ingredient> ingredients = recipe.value().getIngredients();
        int size = ingredients.size();
        if (size == 1) {
            addInput(builder, 28, 51, ingredients.getFirst());
        } else if (size == 2) {
            addInput(builder, 9, 32, ingredients.getFirst());
            addInput(builder, 47, 32, ingredients.get(1));
        } else {
            addInput(builder, 28, 51, ingredients.getFirst());
            addInput(builder, 9, 32, ingredients.get(1));
            addInput(builder, 47, 32, ingredients.get(2));
        }
        // output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 28, 8).addItemStack(recipe.value().getResultItem(null));
    }

    @Override
    public void draw(RecipeHolder<SkyMillRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 72, 72, 72, 72);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<SkyMillRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        tooltip.addAll(recipe.value().getEnvironment().toDescriptions());
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<SkyMillRecipe> recipe) {
        return Confluence.asResource(recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
