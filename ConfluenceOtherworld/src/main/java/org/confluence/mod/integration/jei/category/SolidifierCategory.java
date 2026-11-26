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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SolidifierRecipe;
import org.confluence.mod.integration.jei.EitherRecipe4xHelper;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class SolidifierCategory implements IRecipeCategory<SolidifierRecipe> {
    public static final RecipeType<SolidifierRecipe> TYPE = new RecipeType<>(Confluence.asResource("solidifier"), SolidifierRecipe.class);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/jei/solidifier.png");
    private final IDrawable icon;
    private final EitherRecipe4xHelper helper;

    public SolidifierCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.SOLIDIFIER.toStack());
        this.helper = new EitherRecipe4xHelper(jeiHelpers.getIngredientManager());
    }

    @Override
    public RecipeType<SolidifierRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.solidifier");
    }

    @Override
    public IDrawable getIcon() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, SolidifierRecipe recipe, IFocusGroup focuses) {
        PortShapedRecipePattern pattern = recipe.either.orThrow();
        for (int i = 0; i < pattern.height(); i++) {
            for (int j = 0; j < pattern.width(); j++) {
                if (pattern.symmetrical()) {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.getIngredients().get(pattern.width() - j - 1 + i * pattern.width()));
                } else {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.getIngredients().get(j + i * pattern.width()));
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(SolidifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
        if (mouseX >= 80 && mouseX <= 80 + 28 && mouseY >= 29 && mouseY <= 29 + 23) {
            helper.drawSummary(recipeSlotsView, guiGraphics);
        }
    }

    @Override
    public ResourceLocation getRegistryName(SolidifierRecipe recipe) {
        return Confluence.asResource(recipe.getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.getResult().getItem()).getPath());
    }
}
