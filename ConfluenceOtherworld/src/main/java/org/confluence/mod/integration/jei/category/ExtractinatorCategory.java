package org.confluence.mod.integration.jei.category;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_furniture.TerraFurniture;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class ExtractinatorCategory implements IRecipeCategory<ExtractinatorCategory.IngredientPair> {
    public static final RecipeType<IngredientPair> EXTRACTINATOR = new RecipeType<>(Confluence.asResource("extractinator"), IngredientPair.class);
    public static final RecipeType<IngredientPair> CHLOROPHYTE_EXTRACTINATOR = new RecipeType<>(Confluence.asResource("chlorophyte_extractinator"), IngredientPair.class);
    private static final IIngredientRenderer<ItemStack> INGREDIENT_RENDERER = new IIngredientRenderer<>() {
        @Override
        public void render(GuiGraphics guiGraphics, ItemStack ingredient) {
            RenderSystem.enableDepthTest();
            guiGraphics.renderFakeItem(ingredient, 0, 0);
            RenderSystem.disableBlend();
        }

        @SuppressWarnings("removal")
        @Override
        public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
            if (ingredient instanceof DataItemStack data) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                Item.TooltipContext tooltipContext = Item.TooltipContext.of(minecraft.level);
                List<Component> lines = Lists.newArrayList(ingredient.getTooltipLines(tooltipContext, player, tooltipFlag));
                lines.add(1, data.min == data.max
                        ? Component.translatable("tooltip.jei.count_exact", data.min)
                        : Component.translatable("tooltip.jei.count_range", data.min, data.max));
                lines.add(2, Component.translatable("tooltip.jei.drop_chance", ATTRIBUTE_MODIFIER_FORMAT.format(data.chance * 100)));
                return lines;

            }
            return List.of();
        }
    };
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
    public @Nullable IDrawable getIcon() {
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
                        .addIngredientsUnsafe(List.of(new DataItemStack(
                                entry.item,
                                entry.minCount * pool.minRoll,
                                entry.maxCount * pool.maxRoll,
                                (float) entry.getWeight().asInt() / pool.totalWeight
                        )))
                        .setCustomRenderer(VanillaTypes.ITEM_STACK, INGREDIENT_RENDERER)
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

        IRecipeSlotDrawable inputSlot = recipeSlots.getSlots(RecipeIngredientRole.INPUT).getFirst();
        inputSlot.setPosition(scrollGridWidget.getScreenRectangle().position().x() + 1, 1);
    }

    public static List<IngredientPair> collectAll(DataMapType<Item, ExtractinatorData> type, RegistryAccess registryAccess) {
        HolderLookup.RegistryLookup<Item> lookup = registryAccess.lookupOrThrow(Registries.ITEM);
        Map<Item, ExtractinatorData> map = new IdentityHashMap<>();
        collectAny(map, type, lookup, Tags.Items.GRAVELS);
        collectAny(map, type, lookup, ModTags.Items.DESERT_FOSSIL);
        collectAny(map, type, lookup, ModTags.Items.JUNK);
        collectAny(map, type, lookup, ModTags.Items.MARINE_GRAVEL);
        collectAny(map, type, lookup, ModTags.Items.SILT_BLOCK);
        collectAny(map, type, lookup, ModTags.Items.SLUSH);
        List<IngredientPair> list = Lists.newArrayListWithExpectedSize(map.size());
        map.forEach((item, data) -> list.add(new IngredientPair(item.getDefaultInstance(), data)));
        return list;
    }

    // 没有合并多个Data
    private static void collectAny(Map<Item, ExtractinatorData> map, DataMapType<Item, ExtractinatorData> type, HolderLookup.RegistryLookup<Item> lookup, TagKey<Item> tagKey) {
        lookup.get(tagKey).ifPresent(holders -> holders.stream().forEach(itemHolder -> {
            ExtractinatorData data = itemHolder.getData(type);
            if (data != null) map.put(itemHolder.value(), data);
        }));
    }

    public record IngredientPair(ItemStack ingredient, ExtractinatorData data) {}

    public static class DataItemStack extends ItemStack {
        public final int min;
        public final int max;
        public final float chance;

        public DataItemStack(ItemStack original, int min, int max, float chance) {
            super(original.getItemHolder(), original.getCount(), original.getComponentsPatch());
            this.min = min;
            this.max = max;
            this.chance = chance;
        }

        public DataItemStack(Item item, int min, int max, float chance) {
            super(item);
            this.min = min;
            this.max = max;
            this.chance = chance;
        }

        @Override
        public ItemStack copy() {
            return new DataItemStack(getItem(), min, max, chance);
        }
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(IngredientPair recipe) {
        return ResourceLocation.fromNamespaceAndPath(TerraFurniture.MODID, "extractinator/" + BuiltInRegistries.ITEM.getKey(recipe.ingredient.getItem()).getPath());
    }
}
