package org.confluence.mod.integration.jei.category;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HellforgeRecipe;

public class HellforgeCategory extends EnhancedForgeCategory<HellforgeRecipe> {
    public static final RecipeType<RecipeHolder<HellforgeRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("hellforge"));
    private static final Component TITLE = Component.translatable("title.confluence.hellforge");

    public HellforgeCategory(IJeiHelpers jeiHelpers) {
        super(jeiHelpers, FunctionalBlocks.HELLFORGE.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<HellforgeRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}
