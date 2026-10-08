package org.confluence.mod.integration.jei.category;

import com.google.common.collect.Streams;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.CookingPotRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.confluence.mod.integration.jei.ModJeiPlugin.addInput;

public class CookingPotCategory implements IRecipeCategory<CookingPotRecipe> {
    public static final RecipeType<CookingPotRecipe> TYPE = new RecipeType<>(Confluence.asResource("cooking_pot"), CookingPotRecipe.class);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/cooking_pot.png");
    private final IDrawable icon;

    public CookingPotCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.COOKING_POT.toStack());
    }

    @Override
    public RecipeType<CookingPotRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.cooking_pot");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 142;
    }

    @Override
    public int getHeight() {
        return 49;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookingPotRecipe recipe, IFocusGroup focuses) {
        int i = 0;
        int j = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            addInput(builder, 13 + i * 18, 7 + j * 18, ingredient);
            if (i == 1) {
                j++;
                i = 0;
            } else {
                i++;
            }
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 1).addIngredients(recipe.getContainer());
        if (recipe.getHeatSource().blocks().isPresent()) {
            Ingredient heatSource = Ingredient.of(recipe.getHeatSource().blocks().get().map(
                    tag -> Streams.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(tag)), HolderSet::stream
            ).map(holder -> holder.value().asItem().getDefaultInstance()));
            builder.addSlot(RecipeIngredientRole.CATALYST, 79, 32).addIngredients(heatSource)
                    .addRichTooltipCallback((slotView, tooltip) -> recipe.getHeatSource().properties().ifPresent(predicate -> {
                        tooltip.add(Component.translatable("tooltip.jei.state_properties"));
                        for (StatePropertiesPredicate.PropertyMatcher property : predicate.properties) {
                            String prefix = "  " + property.name + '=';
                            if (property instanceof StatePropertiesPredicate.ExactPropertyMatcher exact) {
                                tooltip.add(Component.literal(prefix + exact.value));
                            } else if (property instanceof StatePropertiesPredicate.RangedPropertyMatcher ranged) {
                                tooltip.add(Component.literal(prefix + '[' + Objects.requireNonNullElse(ranged.minValue, "") + ',' + Objects.requireNonNullElse(ranged.maxValue, "") + ']'));
                            } else {
                                LibUtils.forMixin$Inject();
                            }
                        }
                    }));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 121, 16).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(CookingPotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 142, 49, 159, 49);
        if (recipe.getContainer().isEmpty()) {
            guiGraphics.blit(BACKGROUND, 79, 1, 143, 0, 16, 16, 159, 49);
        }
        if (recipe.getHeatSource().blocks().isEmpty()) {
            guiGraphics.blit(BACKGROUND, 79, 32, 143, 33, 16, 16, 159, 49);
        }
    }

    @Override
    public ResourceLocation getRegistryName(CookingPotRecipe recipe) {
        return Confluence.asResource(recipe.getGroup() + "/" + BuiltInRegistries.ITEM.getKey(recipe.getResult().getItem()).getPath());
    }
}
