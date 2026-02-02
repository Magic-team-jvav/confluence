package org.confluence.terra_furniture.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.recipe.LivingLoomRecipe;
import org.jetbrains.annotations.Nullable;

public class LivingLoomCategory implements IRecipeCategory<RecipeHolder<LivingLoomRecipe>> {
    public static final RecipeType<RecipeHolder<LivingLoomRecipe>> TYPE = RecipeType.createRecipeHolderType(TerraFurniture.asResource("living_loom"));
    private static final ResourceLocation BACKGROUND = TerraFurniture.asResource("textures/gui/living_loom.png");
    private final IDrawable icon;

    public LivingLoomCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(TFBlocks.LIVING_LOOM.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<LivingLoomRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.terra_furniture.living_loom");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<LivingLoomRecipe> recipe, IFocusGroup focuses) {
        TFJeiPlugin.setEitherRecipe4x(builder, recipe);
    }

    @Override
    public void draw(RecipeHolder<LivingLoomRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(RecipeHolder<LivingLoomRecipe> recipe) {
        return TerraFurniture.asResource(recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
