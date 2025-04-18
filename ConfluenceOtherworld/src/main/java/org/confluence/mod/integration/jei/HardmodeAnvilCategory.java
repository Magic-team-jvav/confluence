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
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HardmodeAnvilRecipe;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class HardmodeAnvilCategory implements IRecipeCategory<HardmodeAnvilRecipe> {
    public static final RecipeType<HardmodeAnvilRecipe> TYPE = RecipeType.create(Confluence.MODID, "hardmode_anvil", HardmodeAnvilRecipe.class);
    private static final Component TITLE = Component.translatable("title.confluence.hardmode_anvil");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/hardmode_anvil.png");
    private final IDrawable icon;

    public HardmodeAnvilCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.ORICHALCUM_ANVIL.toStack());
    }

    @Override
    public RecipeType<HardmodeAnvilRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
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
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HardmodeAnvilRecipe recipe, IFocusGroup focuses) {
        recipe.either.ifLeft(shaped -> {
            int width = shaped.width();
            int height = shaped.height();
            boolean symmetrical = shaped.symmetrical;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (symmetrical) {
                        addInput(builder, j * 18 + 6, i * 18 + 5, shaped.ingredients().get(width - j - 1 + i * width));
                    } else {
                        addInput(builder, j * 18 + 6, i * 18 + 5, shaped.ingredients().get(j + i * width));
                    }
                }
            }
        });
        recipe.either.ifRight(shapeless -> {
            builder.setShapeless();
            int i = 0, j = 0;
            for (Ingredient ingredient : shapeless) {
                addInput(builder, j * 18 + 6, i * 18 + 5, ingredient);
                if (++j >= 4) {
                    j = 0;
                    i++;
                }
            }
        });
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(HardmodeAnvilRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
    }
}
