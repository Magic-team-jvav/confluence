package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SkyMillRecipe;
import org.confluence.terra_curio.integration.jei.JeiBackGround;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class SkyMillCategory implements IRecipeCategory<SkyMillRecipe> {
    public static final RecipeType<SkyMillRecipe> TYPE = RecipeType.create(Confluence.MODID, "sky_mill", SkyMillRecipe.class);
    private static final Component TITLE = Component.translatable("title.confluence.sky_mill");
    private static final JeiBackGround BACK_GROUND = new JeiBackGround(72, 72, Confluence.asResource("textures/gui/sky_mill.png"));
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
        return TITLE;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return BACK_GROUND;
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
        builder.addSlot(RecipeIngredientRole.OUTPUT, 28, 8).addItemStack(recipe.getResultItem(null));
    }
}
