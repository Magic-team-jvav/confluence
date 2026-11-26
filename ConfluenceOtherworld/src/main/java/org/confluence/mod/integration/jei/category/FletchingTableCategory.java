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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.recipe.FletchingTableRecipe;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class FletchingTableCategory implements IRecipeCategory<FletchingTableRecipe> {
    public static final RecipeType<FletchingTableRecipe> TYPE = new RecipeType<>(Confluence.asResource("fletching_table"), FletchingTableRecipe.class);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/jei/fletching_table.png");
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
        return Component.translatable("title.confluence.fletching_table");
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
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FletchingTableRecipe recipe, IFocusGroup focuses) {
        addInput(builder, 7, 42, recipe.getTail());
        addInput(builder, 25, 24, recipe.getBody());
        addInput(builder, 43, 6, recipe.getHead());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 24).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(FletchingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 128, 64, 128, 64);
    }

    @Override
    public ResourceLocation getRegistryName(FletchingTableRecipe recipe) {
        return Confluence.asResource(recipe.getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.getResult().getItem()).getPath());
    }
}
