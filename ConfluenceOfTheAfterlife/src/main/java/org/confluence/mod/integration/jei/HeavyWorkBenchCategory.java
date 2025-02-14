package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_curio.integration.jei.JeiBackGround;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class HeavyWorkBenchCategory implements IRecipeCategory<HeavyWorkBenchRecipe> {
    public static final RecipeType<HeavyWorkBenchRecipe> TYPE = RecipeType.create(Confluence.MODID, "heavy_work_bench", HeavyWorkBenchRecipe.class);
    private static final Component TITLE = Component.translatable("title.confluence.heavy_work_bench");
    private static final IDrawable BACKGROUND = new JeiBackGround(144, 80, Confluence.asResource("textures/gui/heavy_work_bench.png"));
    private final IDrawable icon;

    public HeavyWorkBenchCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.HEAVY_WORK_BENCH.toStack());
    }

    @Override
    public @NotNull RecipeType<HeavyWorkBenchRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TITLE;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, HeavyWorkBenchRecipe recipe, @NotNull IFocusGroup focuses) {
        ShapedRecipePattern pattern = recipe.pattern;
        int width = pattern.width();
        int height = pattern.height();
        boolean symmetrical = pattern.symmetrical;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (symmetrical) {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.ingredients.get(width - j - 1 + i * width));
                } else {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.ingredients.get(j + i * width));
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33).addItemStack(recipe.getResultItem(null));
    }
}
