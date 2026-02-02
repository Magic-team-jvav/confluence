package org.confluence.terra_furniture.integration.jei;

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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.recipe.GlassKilnRecipe;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_furniture.integration.jei.TFJeiPlugin.addInput;

public class GlassKilnCategory implements IRecipeCategory<RecipeHolder<GlassKilnRecipe>> {
    public static final RecipeType<RecipeHolder<GlassKilnRecipe>> TYPE = RecipeType.createRecipeHolderType(TerraFurniture.asResource("glass_kiln"));
    private static final ResourceLocation BACKGROUND = TerraFurniture.asResource("textures/gui/glass_kiln.png");
    private final IDrawable icon;

    public GlassKilnCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(TFBlocks.GLASS_KILN.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<GlassKilnRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.terra_furniture.glass_kiln");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 142;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<GlassKilnRecipe> recipe, IFocusGroup focuses) {
        ShapedRecipePattern pattern = recipe.value().pattern;
        int width = pattern.width();
        int height = pattern.height();
        boolean symmetrical = pattern.symmetrical;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (symmetrical) {
                    addInput(builder, j * 18 + 5, i * 18 + 5, recipe.value().ingredients.get(width - j - 1 + i * width));
                } else {
                    addInput(builder, j * 18 + 5, i * 18 + 5, recipe.value().ingredients.get(j + i * width));
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 9).addItemStack(recipe.value().getResultItem(null));
    }

    @Override
    public void draw(RecipeHolder<GlassKilnRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 142, 80, 142, 80);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(RecipeHolder<GlassKilnRecipe> recipe) {
        return TerraFurniture.asResource(recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
