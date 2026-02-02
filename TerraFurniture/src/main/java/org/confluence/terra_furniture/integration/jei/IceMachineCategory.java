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
import org.confluence.terra_furniture.common.recipe.IceMachineRecipe;
import org.jetbrains.annotations.Nullable;

public class IceMachineCategory implements IRecipeCategory<RecipeHolder<IceMachineRecipe>> {
    public static final RecipeType<RecipeHolder<IceMachineRecipe>> TYPE = RecipeType.createRecipeHolderType(TerraFurniture.asResource("ice_machine"));
    private static final ResourceLocation BACKGROUND = TerraFurniture.asResource("textures/gui/ice_machine.png");
    private final IDrawable icon;

    public IceMachineCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(TFBlocks.ICE_MACHINE.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<IceMachineRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.terra_furniture.ice_machine");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IceMachineRecipe> recipe, IFocusGroup focuses) {
        TFJeiPlugin.setEitherRecipe4x(builder, recipe);
    }

    @Override
    public void draw(RecipeHolder<IceMachineRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(RecipeHolder<IceMachineRecipe> recipe) {
        return TerraFurniture.asResource(recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
