package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.recipe.FletchingTableRecipe;
import org.confluence.terra_curio.integration.jei.JeiBackGround;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class FletchingTableCategory implements IRecipeCategory<FletchingTableRecipe> {
    public static final RecipeType<FletchingTableRecipe> TYPE = RecipeType.create(Confluence.MODID, "fletching_table", FletchingTableRecipe.class);
    private static final Component TITLE = Component.translatable("title.confluence.fletching_table");
    private static final IDrawable BACKGROUND = new JeiBackGround(128, 64, Confluence.asResource("textures/gui/fletching_table.png"));
    private final IDrawable icon;

    public FletchingTableCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(Blocks.FLETCHING_TABLE.asItem().getDefaultInstance());
    }

    @Override
    public RecipeType<FletchingTableRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FletchingTableRecipe recipe, IFocusGroup focuses) {
        addInput(builder, 7, 42, recipe.getTail());
        addInput(builder, 25, 24, recipe.getBody());
        addInput(builder, 43, 6, recipe.getHead());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 24).addItemStack(recipe.getResultItem(null));
    }
}
