package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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
import org.confluence.mod.common.recipe.AltarRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.confluence.terra_furniture.TerraFurniture;
import org.jetbrains.annotations.Nullable;

public class AltarCategory implements IRecipeCategory<RecipeHolder<AltarRecipe>> {
    public static final RecipeType<RecipeHolder<AltarRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("altar"));
    private final IDrawable icon;

    public AltarCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.DEMON_ALTAR.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<AltarRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.altar");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AltarRecipe> recipe, IFocusGroup focusGroup) {
        ModJeiPlugin.set4IngredientsRecipe(builder, recipe);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<AltarRecipe> recipe, IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        builder.addRecipeArrow().setPosition(50 + 5, 6 + 2);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(RecipeHolder<AltarRecipe> recipe) {
        return ResourceLocation.fromNamespaceAndPath(TerraFurniture.MODID, recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
