package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.jetbrains.annotations.Nullable;

// todo 解析战利品表
public class ExtractinatorCategory implements IRecipeCategory<ExtractinatorData> {
    public static final RecipeType<ExtractinatorData> EXTRACTINATOR = new RecipeType<>(Confluence.asResource("extractinator"), ExtractinatorData.class);
    public static final RecipeType<ExtractinatorData> CHLOROPHYTE_EXTRACTINATOR = new RecipeType<>(Confluence.asResource("chlorophyte_extractinator"), ExtractinatorData.class);
    private final RecipeType<ExtractinatorData> recipeType;
    private final Component title;
    private final IDrawable icon;

    public ExtractinatorCategory(IJeiHelpers jeiHelpers, RecipeType<ExtractinatorData> recipeType, Block block) {
        this.recipeType = recipeType;
        this.title = block.getName();
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemLike(block);
    }

    @Override
    public RecipeType<ExtractinatorData> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getHeight() {
        return 128;
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ExtractinatorData recipe, IFocusGroup focuses) {

    }
}
