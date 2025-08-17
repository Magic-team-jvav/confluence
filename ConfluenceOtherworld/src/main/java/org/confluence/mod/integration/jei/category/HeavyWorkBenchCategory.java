package org.confluence.mod.integration.jei.category;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.confluence.mod.integration.jei.RecipeTransferPacketC2S;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        ModJeiPlugin.setEitherRecipe4x(builder, recipe);
    }

    @Override
    public void draw(RecipeHolder<HeavyWorkBenchRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 144, 80, 144, 80);
        if (mouseX >= 80 && mouseX <= 80 + 28 && mouseY >= 29 && mouseY <= 29 + 23) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 80, 0);
            for (ImmutableTriple<ITypedIngredient<Object>, Object, Integer> entry : summary(recipeSlotsView)) {
                ingredientManager.getIngredientRenderer(entry.getLeft().getType()).render(guiGraphics, entry.getMiddle());
                guiGraphics.pose().translate(16, 0, 0);
            }
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<HeavyWorkBenchRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        tooltip.addAll(recipe.value().getEnvironment().toDescriptions());
    }

    @SuppressWarnings("unchecked")
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

    public static class HeavyRecipeTransferHandler implements IRecipeTransferHandler<HeavyWorkBenchMenu, RecipeHolder<HeavyWorkBenchRecipe>> {
        private final IJeiHelpers jeiHelpers;
        private final IRecipeTransferHandlerHelper transferHelper;

        public HeavyRecipeTransferHandler(IJeiHelpers jeiHelpers, IRecipeTransferHandlerHelper transferHelper) {
            this.jeiHelpers = jeiHelpers;
            this.transferHelper = transferHelper;
        }

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


        /**
         * @see mezz.jei.common.transfer.RecipeTransferUtil#getRecipeTransferOperations(IStackHelper, Map, List, List)
         */
        @Override
        public @Nullable IRecipeTransferError transferRecipe(HeavyWorkBenchMenu container, RecipeHolder<HeavyWorkBenchRecipe> recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            IStackHelper stackHelper = jeiHelpers.getStackHelper();
            List<IRecipeSlotView> slotViews = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT).stream().filter(view -> !view.isEmpty()).toList();
            Map<IRecipeSlotView, Set<Object>> slotUidCache = new IdentityHashMap<>();

            Object2IntOpenHashMap<Object> total = new Object2IntOpenHashMap<>();
            int[] requires = new int[slotViews.size()];
            Arrays.fill(requires, -1);
            for (Slot slot : container.slots) {
                if (slot.isFake() || slot.getItem().isEmpty()) continue;
                ItemStack slotItemStack = slot.getItem();
                for (int i = 0; i < slotViews.size(); i++) {
                    IRecipeSlotView slotView = slotViews.get(i);
                    Object slotItemStackUid = stackHelper.getUidForStack(slotItemStack, UidContext.Ingredient);
                    if (slotUidCache.computeIfAbsent(slotView, s -> calculateUids(s, stackHelper)).contains(slotItemStackUid)) {
                        total.put(slotItemStackUid, slot.getItem().getCount());
                        requires[i] = slotView.getItemStacks().findAny().map(ItemStack::getCount).orElse(1);
                    }
                }
            }
            if (total.isEmpty()) {
                return transferHelper.createUserErrorForMissingSlots(Component.translatable("jei.tooltip.error.recipe.transfer.missing"), slotViews);
            }

            List<IRecipeSlotView> missing = new ArrayList<>();
            for (int i = 0; i < slotViews.size(); i++) {
                IRecipeSlotView slotView = slotViews.get(i);
                int require = requires[i];
                if (require < 0) {
                    missing.add(slotView);
                    continue;
                }
                Set<Object> objects = slotUidCache.computeIfAbsent(slotView, s -> calculateUids(s, stackHelper));
                for (Object object : objects) {
                    if (total.containsKey(object)) {
                        if (total.getInt(object) < require) {
                            missing.add(slotView);
                        }
                        total.addTo(object, -require);
                        break;
                    }
                }
            }
            if (!missing.isEmpty()) {
                return transferHelper.createUserErrorForMissingSlots(Component.translatable("jei.tooltip.error.recipe.transfer.missing"), missing);
            }

            if (!recipe.value().getEnvironment().matches(container.getAccess())) {
                return transferHelper.createUserErrorWithTooltip(Component.translatable("jei.tooltip.error.recipe.transfer.environment"));
            }

            if (doTransfer) {
                PacketDistributor.sendToServer(new RecipeTransferPacketC2S(recipe.id(), maxTransfer, false));
            }
            return null;
        }

        private static Set<Object> calculateUids(IRecipeSlotView recipeSlotView, IStackHelper stackhelper) {
            List<@Nullable ITypedIngredient<?>> allIngredientsList = recipeSlotView.getAllIngredientsList();
            Set<Object> uids = new HashSet<>(allIngredientsList.size());
            for (ITypedIngredient<?> typedIngredient : allIngredientsList) {
                if (typedIngredient == null) continue;
                ITypedIngredient<ItemStack> typedItemStack = typedIngredient.cast(VanillaTypes.ITEM_STACK);
                if (typedItemStack != null) {
                    uids.add(stackhelper.getUidForStack(typedItemStack, UidContext.Ingredient));
                }
            }
            return uids;
        }
    }

    public static class CraftingRecipeTransferHandler implements IUniversalRecipeTransferHandler<HeavyWorkBenchMenu> {
        private final HeavyRecipeTransferHandler heavyRecipeTransferHandler;

        public CraftingRecipeTransferHandler(HeavyWorkBenchCategory.HeavyRecipeTransferHandler heavyRecipeTransferHandler) {
            this.heavyRecipeTransferHandler = heavyRecipeTransferHandler;
        }

        @Override
        public Class<? extends HeavyWorkBenchMenu> getContainerClass() {
            return HeavyWorkBenchMenu.class;
        }

        @Override
        public Optional<MenuType<HeavyWorkBenchMenu>> getMenuType() {
            return Optional.of(ModMenuTypes.HEAVY_WORK_BENCH.get());
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(HeavyWorkBenchMenu container, Object o, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            if (o instanceof RecipeHolder(ResourceLocation id, Recipe<?> value)) {
                HeavyWorkBenchRecipe recipe4x = RecipeTransferPacketC2S.getRecipe4x(HeavyWorkBenchRecipe.class, value, either -> new HeavyWorkBenchRecipe(value.getResultItem(player.registryAccess()), either, EnvironmentLevelAccess.Matcher.EMPTY));
                if (recipe4x != null) {
                    return heavyRecipeTransferHandler.transferRecipe(container, new RecipeHolder<>(id, recipe4x), recipeSlots, player, maxTransfer, doTransfer);
                }
            }
            return heavyRecipeTransferHandler.transferHelper.createInternalError();
        }
    }
}
