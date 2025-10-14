package org.confluence.mod.integration.jei.category;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HardmodeForgeRecipe;

public class HardmodeForgeCategory extends EnhancedForgeCategory<HardmodeForgeRecipe> {
    public static final RecipeType<RecipeHolder<HardmodeForgeRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("hardmode_forge"));

    public HardmodeForgeCategory(IJeiHelpers jeiHelpers) {
        super(jeiHelpers, FunctionalBlocks.TITANIUM_FORGE.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<HardmodeForgeRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.hardmode_forge");
    }
}
