package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.container.EnhanceForgeScreen;
import org.confluence.mod.common.recipe.EnhancedForgeRecipe;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public abstract class EnhancedForgeCategory<R extends EnhancedForgeRecipe> implements IRecipeCategory<RecipeHolder<R>> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/hellforge.png");
    private final IDrawable icon;

    public EnhancedForgeCategory(IJeiHelpers jeiHelpers, ItemStack iconStack) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(iconStack);
    }

    @Override
    public int getWidth() {
        return 112;
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> recipe, IFocusGroup focuses) {
        int i = 0;
        int j = 0;
        for (Ingredient ingredient : recipe.value().ingredients) {
            addInput(builder, 4 + i * 18, 7 + j * 18, ingredient);
            if (i == 1) {
                j++;
                i = 0;
            } else {
                i++;
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 16).addItemStack(recipe.value().getResultItem(null));
    }

    @Override
    public void draw(RecipeHolder<R> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 112, 48, 112, 48);
        if (recipe.value().isRequiresFuel()) {
            guiGraphics.blit(EnhanceForgeScreen.SUPER_LIT_PROGRESS, 54, 25, 0, 0, 14, 14, 14, 14);
            if (mouseX >= 54 && mouseX <= 68 && mouseY >= 25 && mouseY <= 39) {
                Component text = Component.translatable("condition.confluence.requires_fuel").withColor(ModRarity.CYAN.color());
                guiGraphics.renderTooltip(Minecraft.getInstance().font, text, (int) mouseX, (int) mouseY);
            }
        }
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<R> recipe) {
        return Confluence.asResource(recipe.value().getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(null).getItem()).getPath());
    }
}
