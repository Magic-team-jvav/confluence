package org.confluence.mod.integration.jei.category;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BrewingStandTerraPotionCategory implements IRecipeCategory<BrewingStandTerraPotionCategory.Recipe> {
    public static final RecipeType<Recipe> TYPE = RecipeType.create(ConfluenceMagicLib.CONFLUENCE_ID, "brewing_stand_terra_potion", Recipe.class);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/brewing_stand_terra_potion.png");
    private final IDrawable icon;

    public BrewingStandTerraPotionCategory() {
        this.icon = new IDrawable() {
            private final ItemStack brewingStand = Items.BREWING_STAND.getDefaultInstance();
            private final ItemStack chaosPotion = PotionItems.CHAOS_POTION.toStack();

            @Override
            public int getWidth() {
                return 16;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
                guiGraphics.renderFakeItem(brewingStand, xOffset, yOffset);
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(xOffset + 8, yOffset + 8, 100);
                pose.scale(0.5F, 0.5F, 0.5F);
                guiGraphics.renderFakeItem(chaosPotion, 0, 0);
                pose.popPose();
            }
        };
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.brewing_stand_terra_potion");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 64;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.setShapeless();
        builder.addInputSlot(7, 24).addItemStacks(recipe.input);
        builder.addInputSlot(37, 2).addItemStacks(recipe.ingredient);
        builder.addOutputSlot(101, 24).addItemStack(recipe.output);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, Recipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseY > 45) {
            if (mouseX < 64) {
                tooltip.add(Component.translatable("tooltip.jei.intermediate_product"));
            }
        } else if (mouseX < 96) {
            tooltip.add(Component.translatable("tooltip.jei.brewing_stand_terra_potion"));
        }
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 128, 64, 128, 64);
    }

    @Override
    public ResourceLocation getRegistryName(Recipe recipe) {
        return recipe.id;
    }

    public static final class Recipe {
        public final List<ItemStack> input;
        public final List<ItemStack> ingredient;
        public final ItemStack output;
        public final ResourceLocation id;

        private Recipe(List<ItemStack> input, List<ItemStack> ingredient, ItemStack output, ResourceLocation id) {
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
            this.id = id;
        }

        public static List<Recipe> getAllRecipes() {
            Map<ItemStack, List<Recipe>> recipes = new Object2ObjectLinkedOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);

            add(recipes, PotionItems.BOTTLE.toStack(), List.of(MaterialItems.GEL.toStack(), MaterialItems.LIFE_MUSHROOM.toStack()), PotionItems.LESSER_HEALING_POTION.toStack());
            add(recipes, PotionItems.LESSER_HEALING_POTION.toStack(), List.of(PotionItems.LESSER_HEALING_POTION.toStack(), MaterialItems.GLOWING_MUSHROOM.toStack()), PotionItems.HEALING_POTION.toStack());
            add(recipes, PotionItems.LESSER_MANA_POTION.toStack(), List.of(PotionItems.LESSER_MANA_POTION.toStack(), MaterialItems.GLOWING_MUSHROOM.toStack()), PotionItems.MANA_POTION.toStack());

            Object2ObjectMap<Integer, Item> id2Item = new Object2ObjectOpenHashMap<>(ModRecipes.Brewing.MATERIAL_ID_MAP.size());
            for (Object2IntMap.Entry<Item> entry : ModRecipes.Brewing.MATERIAL_ID_MAP.object2IntEntrySet()) {
                id2Item.put(entry.getIntValue(), entry.getKey());
            }
            for (Map.Entry<int[], ItemStack> entry : ModRecipes.Brewing.MATERIAL_TO_RESULT.entrySet()) {
                int[] ids = entry.getKey();
                Object2IntLinkedOpenHashMap<Item> items = new Object2IntLinkedOpenHashMap<>();
                for (int id : ids) {
                    items.addTo(id2Item.get(id), 1);
                }
                ItemStack input = PotionItems.BOTTLED_WATER.toStack();
                List<ItemStack> ingredient = items.object2IntEntrySet().stream().map(e -> new ItemStack(e.getKey(), e.getIntValue())).toList();
                ItemStack output = entry.getValue();
                add(recipes, input, ingredient, output);
            }

            return recipes.values().stream().flatMap(Collection::stream).toList();
        }

        private static void add(Map<ItemStack, List<Recipe>> map, ItemStack input, List<ItemStack> ingredient, ItemStack output) {
            List<Recipe> list = map.computeIfAbsent(output, s -> Lists.newArrayList());
            ResourceLocation id = Confluence.asResource("brewing_stand_terra_potion/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath());
            List<ItemStack> stack = List.of(PotionItems.CHAOS_POTION.toStack(), input);
            if (list.isEmpty()) {
                list.add(new Recipe(stack, ingredient, output, id));
            } else {
                list.add(new Recipe(stack, ingredient, output, id.withSuffix("_" + list.size())));
            }
        }
    }
}
