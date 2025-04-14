package org.confluence.mod.integration.jei;

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
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.AlchemyTableRecipe;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class AlchemyTableCategory implements IRecipeCategory<AlchemyTableRecipe> {
    public static final RecipeType<AlchemyTableRecipe> TYPE = RecipeType.create(Confluence.MODID, "alchemy_table", AlchemyTableRecipe.class);
    private static final Component TITLE = Component.translatable("title.confluence.alchemy_table");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/alchemy_table.png");
    private final IDrawable icon;

    public AlchemyTableCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.ALCHEMY_TABLE.toStack());
    }

    @Override
    public RecipeType<AlchemyTableRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return 112;
    }

    @Override
    public int getHeight() {
        return 64;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyTableRecipe recipe, IFocusGroup focuses) {
        int y = 1;
        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (i % 2 == 0) {
                addInput(builder, 7, y, ingredient);
            } else {
                addInput(builder, 89, y, ingredient);
                y += 20;
            }
            i++;
        }
        addInput(builder, 48, 1, recipe.getBase());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 46).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(AlchemyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 112, 64, 112, 64);
    }
}
