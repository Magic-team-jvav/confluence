package org.confluence.mod.integration.jei.category;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.mesdag.portlib.datamap.PortDataMapType;

import java.util.List;
import java.util.Map;

public class ExtractinatorCategory implements IRecipeCategory<ExtractinatorCategory.IngredientPair> {
    public static final RecipeType<IngredientPair> EXTRACTINATOR = new RecipeType<>(Confluence.asResource("extractinator"), IngredientPair.class);
    public static final RecipeType<IngredientPair> CHLOROPHYTE_EXTRACTINATOR = new RecipeType<>(Confluence.asResource("chlorophyte_extractinator"), IngredientPair.class);
    private final RecipeType<IngredientPair> recipeType;
    private final Component title;
    private final IDrawable icon;

    public ExtractinatorCategory(IJeiHelpers jeiHelpers, RecipeType<IngredientPair> recipeType, Block block) {
        this.recipeType = recipeType;
        this.title = block.getName();
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemLike(block);
    }

    @Override
    public RecipeType<IngredientPair> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getHeight() {
        return 110;
    }

    @Override
    public int getWidth() {
        return 142;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IngredientPair recipe, IFocusGroup focuses) {
        builder.addInputSlot()
                .addItemStack(recipe.ingredient)
                .setStandardSlotBackground();
        for (ExtractinatorData.Pool pool : recipe.data.pools()) {
            for (ExtractinatorData.Entry entry : pool.entries) {
                if (entry.isEmpty()) continue;
                builder.addOutputSlot()
                        .addItemStack(entry.item().getDefaultInstance())
                        .setStandardSlotBackground();
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IngredientPair recipe, IFocusGroup focuses) {
        IRecipeSlotDrawablesView recipeSlots = builder.getRecipeSlots();
        List<IRecipeSlotDrawable> outputSlots = recipeSlots.getSlots(RecipeIngredientRole.OUTPUT);

        IScrollGridWidget scrollGridWidget = builder.addScrollGridWidget(outputSlots, 7, 5);
        scrollGridWidget.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);

        IRecipeSlotDrawable inputSlot = recipeSlots.getSlots(RecipeIngredientRole.INPUT).get(0);
        inputSlot.setPosition(scrollGridWidget.getScreenRectangle().position().x() + 1, 1);
    }

    public static List<IngredientPair> collectAll(PortDataMapType<Item, ExtractinatorData> type) {
        Map<ResourceKey<Item>, ExtractinatorData> map = BuiltInRegistries.ITEM.getDataMap(type);
        List<IngredientPair> list = Lists.newArrayListWithExpectedSize(map.size());
        map.forEach((key, data) -> {
            Item item = BuiltInRegistries.ITEM.get(key);
            if (item != null) {
                list.add(new IngredientPair(item.getDefaultInstance(), data));
            }
        });
        return list;
    }

    public record IngredientPair(ItemStack ingredient, ExtractinatorData data) {}

    @Override
    public ResourceLocation getRegistryName(IngredientPair recipe) {
        return Confluence.asResource("extractinator/" + BuiltInRegistries.ITEM.getKey(recipe.ingredient.getItem()).getPath());
    }
}
