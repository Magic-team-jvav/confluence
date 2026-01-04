package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.CrystalBallRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.confluence.terra_furniture.TerraFurniture;
import org.jetbrains.annotations.Nullable;

public class CrystalBallCategory implements IRecipeCategory<RecipeHolder<CrystalBallRecipe>> {
    public static final RecipeType<RecipeHolder<CrystalBallRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("crystal_ball"));
    private final IDrawable icon;

    public CrystalBallCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.CRYSTAL_BALL.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<CrystalBallRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.crystal_ball");
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CrystalBallRecipe> recipe, IFocusGroup focusGroup) {
        ModJeiPlugin.set4IngredientsRecipe(builder, recipe);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<CrystalBallRecipe> recipe, IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        builder.addRecipeArrow().setPosition(50 + 5, 6 + 2);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<CrystalBallRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        tooltip.addAll(recipe.value().getEnvironment().toDescriptions());
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(RecipeHolder<CrystalBallRecipe> recipe) {
        return ResourceLocation.fromNamespaceAndPath(TerraFurniture.MODID, recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
