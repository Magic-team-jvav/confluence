package org.confluence.mod.integration.jei;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EitherRecipe4xHelper {
    private final IIngredientManager ingredientManager;

    public EitherRecipe4xHelper(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
    }

    public static <
            I extends MenuRecipeInput,
            R extends EitherAmountRecipe4x<I>,
            S extends ToggleAmountResultSlot<R>,
            A extends ContainerLevelAccess,
            C extends EitherAmountContainerMenu4x<I, R, S, A>
    > void register(
            IRecipeTransferRegistration registration,
            Class<R> recipeClazz,
            Class<C> containerClazz,
            @Nullable MenuType<C> menuType,
            FakeRecipeFactory<I, R> factory,
            RecipeType<RecipeHolder<R>> recipeType
    ) {
        TransferHandler<I, R, S, A, C> transferHandler = new TransferHandler<>(
                registration.getJeiHelpers(),
                registration.getTransferHelper(),
                containerClazz,
                menuType,
                recipeType
        );
        registration.addRecipeTransferHandler(transferHandler, recipeType);
        registration.addUniversalRecipeTransferHandler(new CraftingRecipeTransferHandler<>(
                transferHandler,
                recipeClazz,
                factory,
                containerClazz,
                menuType
        ));
    }

    public static void setEitherRecipe4x(IRecipeLayoutBuilder builder, RecipeHolder<? extends EitherAmountRecipe4x<?>> recipe) {
        recipe.value().either.ifLeft(shaped -> {
            int width = shaped.width();
            int height = shaped.height();
            boolean symmetrical = shaped.symmetrical;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (symmetrical) {
                        ModJeiPlugin.addInput(builder, j * 18 + 6, i * 18 + 5, shaped.ingredients().get(width - j - 1 + i * width));
                    } else {
                        ModJeiPlugin.addInput(builder, j * 18 + 6, i * 18 + 5, shaped.ingredients().get(j + i * width));
                    }
                }
            }
        }).ifRight(shapeless -> {
            builder.setShapeless();
            int i = 0, j = 0;
            for (Ingredient ingredient : shapeless) {
                ModJeiPlugin.addInput(builder, j * 18 + 6, i * 18 + 5, ingredient);
                if (++j >= 4) {
                    j = 0;
                    i++;
                }
            }
        });
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33).addItemStack(recipe.value().getResultItem(null));
    }

    public void drawSummary(IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 80, 0);
        for (ImmutableTriple<ITypedIngredient<Object>, Object, Integer> entry : summary(recipeSlotsView)) {
            ingredientManager.getIngredientRenderer(entry.getLeft().getType()).render(guiGraphics, entry.getMiddle());
            guiGraphics.pose().translate(16, 0, 0);
        }
        guiGraphics.pose().popPose();
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

    public static class TransferHandler<I extends MenuRecipeInput, R extends EitherAmountRecipe4x<I>, S extends ToggleAmountResultSlot<R>, A extends ContainerLevelAccess, C extends EitherAmountContainerMenu4x<I, R, S, A>> implements IRecipeTransferHandler<C, RecipeHolder<R>> {
        private final IJeiHelpers jeiHelpers;
        private final IRecipeTransferHandlerHelper transferHelper;
        private final Class<C> containerClazz;
        private final @Nullable MenuType<C> menuType;
        private final RecipeType<RecipeHolder<R>> recipeType;

        public TransferHandler(IJeiHelpers jeiHelpers, IRecipeTransferHandlerHelper transferHelper, Class<C> containerClazz, @Nullable MenuType<C> menuType, RecipeType<RecipeHolder<R>> recipeType) {
            this.jeiHelpers = jeiHelpers;
            this.transferHelper = transferHelper;
            this.containerClazz = containerClazz;
            this.menuType = menuType;
            this.recipeType = recipeType;
        }

        @Override
        public Class<C> getContainerClass() {
            return containerClazz;
        }

        @Override
        public Optional<MenuType<C>> getMenuType() {
            return Optional.ofNullable(menuType);
        }

        @Override
        public RecipeType<RecipeHolder<R>> getRecipeType() {
            return recipeType;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(C container, RecipeHolder<R> recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
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

    public static class CraftingRecipeTransferHandler<I extends MenuRecipeInput, R extends EitherAmountRecipe4x<I>, S extends ToggleAmountResultSlot<R>, A extends ContainerLevelAccess, C extends EitherAmountContainerMenu4x<I, R, S, A>> implements IUniversalRecipeTransferHandler<C> {
        private final TransferHandler<I, R, S, A, C> transferHandler;
        private final Class<R> recipeClazz;
        private final FakeRecipeFactory<I, R> factory;
        private final Class<C> containerClazz;
        private final @Nullable MenuType<C> menuType;

        public CraftingRecipeTransferHandler(TransferHandler<I, R, S, A, C> heavyRecipeTransferHandler, Class<R> recipeClazz, FakeRecipeFactory<I, R> factory, Class<C> containerClazz, @Nullable MenuType<C> menuType) {
            this.transferHandler = heavyRecipeTransferHandler;
            this.recipeClazz = recipeClazz;
            this.factory = factory;
            this.containerClazz = containerClazz;
            this.menuType = menuType;
        }

        @Override
        public Class<C> getContainerClass() {
            return containerClazz;
        }

        @Override
        public Optional<MenuType<C>> getMenuType() {
            return Optional.ofNullable(menuType);
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(C container, Object o, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            if (o instanceof RecipeHolder(ResourceLocation id, Recipe<?> value)) {
                @Nullable R recipe4x = RecipeTransferPacketC2S.getRecipe4x(recipeClazz, value, either -> factory.create(value.getResultItem(player.registryAccess()), either));
                if (recipe4x != null) {
                    return transferHandler.transferRecipe(container, new RecipeHolder<>(id, recipe4x), recipeSlots, player, maxTransfer, doTransfer);
                }
            }
            return transferHandler.transferHelper.createInternalError();
        }
    }

    @FunctionalInterface
    public interface FakeRecipeFactory<I extends MenuRecipeInput, R extends EitherAmountRecipe4x<I>> {
        R create(ItemStack result, Either<ShapedRecipePattern, NonNullList<Ingredient>> either);
    }
}
