package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.confluence.terra_curio.integration.jei.ModJeiPlugin.addInput;

public class HeavyWorkBenchCategory implements IRecipeCategory<RecipeHolder<HeavyWorkBenchRecipe>> {
    public static final RecipeType<RecipeHolder<HeavyWorkBenchRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("heavy_work_bench"));
    private static final Component TITLE = Component.translatable("title.confluence.heavy_work_bench");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/heavy_work_bench.png");
    private final IDrawable icon;
    private final IIngredientManager ingredientManager;

    public HeavyWorkBenchCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(FunctionalBlocks.HEAVY_WORK_BENCH.toStack());
        this.ingredientManager = jeiHelpers.getIngredientManager();
    }

    @Override
    public RecipeType<RecipeHolder<HeavyWorkBenchRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HeavyWorkBenchRecipe> recipe, IFocusGroup focuses) {
        ShapedRecipePattern pattern = recipe.value().either.orThrow();
        int width = pattern.width();
        int height = pattern.height();
        boolean symmetrical = pattern.symmetrical;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (symmetrical) {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.value().ingredients.get(width - j - 1 + i * width));
                } else {
                    addInput(builder, j * 18 + 6, i * 18 + 5, recipe.value().ingredients.get(j + i * width));
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33).addItemStack(recipe.value().getResultItem(null));
    }

    @Override
    public void draw(RecipeHolder<HeavyWorkBenchRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
        if (mouseX >= 80 && mouseX <= 80 + 28 && mouseY >= 29 && mouseY <= 29 + 23) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 80, 0);
            for (ImmutableTriple<ITypedIngredient<Object>, Object, Integer> entry : summary(recipeSlotsView)) {
                render(entry.getLeft(), entry.getMiddle(), guiGraphics);
                guiGraphics.pose().translate(16, 0, 0);
            }
            guiGraphics.pose().popPose();
        }
    }

    private <T> void render(ITypedIngredient<T> typedIngredient, T ingredient, GuiGraphics guiGraphics) {
        IIngredientRenderer<T> renderer = ingredientManager.getIngredientRenderer(typedIngredient.getType());
        renderer.render(guiGraphics, ingredient);
    }

    private <T> List<ImmutableTriple<ITypedIngredient<T>, T, Integer>> summary(IRecipeSlotsView recipeSlotsView) {
        Map<Item, ImmutableTriple<ITypedIngredient<T>, T, Integer>> pairMap = new HashMap<>();
        recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).stream()
                .map(IRecipeSlotView::getDisplayedIngredient)
                .filter(Optional::isPresent)
                .<ITypedIngredient<?>>map(Optional::get)
                .forEach(typedIngredient -> {
                    ITypedIngredient<T> iTypedIngredient = (ITypedIngredient<T>) typedIngredient;
                    IIngredientHelper<T> helper = ingredientManager.getIngredientHelper(iTypedIngredient.getType());
                    Optional<ItemStack> optional = iTypedIngredient.getItemStack();
                    T ingredient = iTypedIngredient.getIngredient();
                    int amount = (int) helper.getAmount(ingredient);
                    optional.ifPresent(itemStack ->
                            pairMap.compute(itemStack.getItem(), (k, t) -> {
                                int amount1 = t == null ? amount : (int) helper.getAmount(t.getMiddle()) + amount;
                                return new ImmutableTriple<>(iTypedIngredient, helper.copyWithAmount(ingredient, amount1), amount1);
                            })
                    );
                });
        Comparator<ImmutableTriple<ITypedIngredient<T>, T, Integer>> comparator = Comparator.comparingInt(ImmutableTriple::getRight);
        return pairMap.values().stream().sorted(comparator.reversed()).toList();
    }

    public static class RecipeTransfer implements IRecipeTransferHandler<HeavyWorkBenchMenu, RecipeHolder<HeavyWorkBenchRecipe>> {
        @Override
        public Class<? extends HeavyWorkBenchMenu> getContainerClass() {
            return HeavyWorkBenchMenu.class;
        }

        @Override
        public Optional<MenuType<HeavyWorkBenchMenu>> getMenuType() {
            return Optional.of(ModMenuTypes.HEAVY_WORK_BENCH.get());
        }

        @Override
        public RecipeType<RecipeHolder<HeavyWorkBenchRecipe>> getRecipeType() {
            return TYPE;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(HeavyWorkBenchMenu container, RecipeHolder<HeavyWorkBenchRecipe> recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            return null;
        }
    }
}
