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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.recipe.FletchingTableRecipe;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class FletchingTableCategory implements IRecipeCategory<RecipeHolder<FletchingTableRecipe>> {
    public static final RecipeType<RecipeHolder<FletchingTableRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("fletching_table"));
    private static final Component TITLE = Component.translatable("title.confluence.fletching_table");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/fletching_table.png");
    private final IDrawable icon;

    public FletchingTableCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(Blocks.FLETCHING_TABLE.asItem().getDefaultInstance());
    }

    @Override
    public RecipeType<RecipeHolder<FletchingTableRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return 128;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FletchingTableRecipe> recipe, IFocusGroup focuses) {
        addInput(builder, 7, 42, recipe.value().getTail());
        addInput(builder, 25, 24, recipe.value().getBody());
        addInput(builder, 43, 6, recipe.value().getHead());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 24).addItemStack(recipe.value().getResultItem(null));
    }

    @Override
    public void draw(RecipeHolder<FletchingTableRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 128, 64, 128, 64);
    }
}
