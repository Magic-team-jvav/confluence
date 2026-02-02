package org.confluence.terra_curio.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class CuriosUtils {
    public static boolean noSameCurio(LivingEntity living, Class<?> clazz) {
        return noSameCurio(living, (Predicate<ItemStack>) itemStack -> clazz.isInstance(itemStack.getItem()));
    }

    public static boolean noSameCurio(LivingEntity living, ValueType<?, ? extends PrimitiveValue<?>> type) {
        return noSameCurio(living, (Predicate<ItemStack>) itemStack -> {
            PrimitiveValueComponent component = itemStack.get(TCDataComponentTypes.ACCESSORIES);
            return component == null || !component.types().containsKey(type);
        });
    }

    public static boolean noSameCurio(LivingEntity living, Predicate<ItemStack> predicate) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return true;
        for (ICurioStacksHandler curioStacksHandler : curiosInventory.get().getCurios().values()) {
            IDynamicStackHandler stackHandler = curioStacksHandler.getStacks();
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <C extends Item & ICurioItem> boolean noSameCurio(LivingEntity living, C curio) {
        return noSameCurio(living, (Predicate<ItemStack>) itemStack -> itemStack.getItem() == curio);
    }

    public static boolean hasCurio(LivingEntity living, Class<?> clazz) {
        return !noSameCurio(living, clazz);
    }

    public static boolean hasCurio(LivingEntity living, ValueType<?, ? extends PrimitiveValue<?>> type) {
        return !noSameCurio(living, type);
    }

    public static boolean hasCurio(LivingEntity living, Predicate<ItemStack> predicate) {
        return !noSameCurio(living, predicate);
    }

    public static <C extends Item & ICurioItem> boolean hasCurio(LivingEntity living, C curio) {
        return !noSameCurio(living, curio);
    }

    public static <T, V extends PrimitiveValue<T>> T calculateValue(LivingEntity living, ValueType<T, V> type) {
        V value = type.newInstance(type.defaultValue());
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return value.get();
        for (ICurioStacksHandler curioStacksHandler : curiosInventory.get().getCurios().values()) {
            IDynamicStackHandler stackHandler = curioStacksHandler.getStacks();
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(stack);
                if (component == null) continue;
                V other = component.get(type);
                if (other == null) continue;
                T t = value.combine(other, type.combineRule());
                value = type.newInstance(t);
            }
        }
        return value.get();
    }

    public static <C> @Nullable C findCurio(LivingEntity living, Class<C> clazz) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return null;
        Optional<SlotResult> results = curiosInventory.get().findFirstCurio(itemStack -> clazz.isInstance(itemStack.getItem()));
        return results.map(result -> clazz.cast(result.stack().getItem())).orElse(null);
    }

    public static <C extends Item & ICurioItem> @Nullable ItemStack findCurio(LivingEntity living, C curio) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return null;
        Optional<SlotResult> results = curiosInventory.get().findFirstCurio(itemStack -> itemStack.getItem() == curio);
        return results.map(SlotResult::stack).orElse(null);
    }

    public static @Nullable ItemStack findCurio(LivingEntity living, Predicate<ItemStack> predicate) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return null;
        Optional<SlotResult> results = curiosInventory.get().findFirstCurio(predicate);
        return results.map(SlotResult::stack).orElse(null);
    }

    public static <C extends Item & ICurioItem> @Nullable ItemStack findCurioAt(LivingEntity living, C curio, String id) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return null;
        Map<String, ICurioStacksHandler> curios = curiosInventory.get().getCurios();
        ICurioStacksHandler stacksHandler = curios.get(id);
        if (stacksHandler != null) {
            IDynamicStackHandler stackHandler = stacksHandler.getStacks();
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == curio) {
                    return stack;
                }
            }
        }
        return null;
    }

    public static ArrayList<ItemStack> getCurios(LivingEntity living) {
        ArrayList<ItemStack> items = new ArrayList<>();
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return items;
        IItemHandlerModifiable itemHandlerModifiable = curiosInventory.get().getEquippedCurios();
        for (int i = 0; i < itemHandlerModifiable.getSlots(); i++) {
            ItemStack itemStack = itemHandlerModifiable.getStackInSlot(i);
            if (!itemStack.isEmpty()) items.add(itemStack);
        }
        return items;
    }

    public static <C> ArrayList<C> getCurios(LivingEntity living, Class<C> clazz) {
        ArrayList<C> items = new ArrayList<>();
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return items;
        IItemHandlerModifiable itemHandlerModifiable = curiosInventory.get().getEquippedCurios();
        for (int i = 0; i < itemHandlerModifiable.getSlots(); i++) {
            ItemStack itemStack = itemHandlerModifiable.getStackInSlot(i);
            Item item = itemStack.getItem();
            if (clazz.isInstance(item)) items.add(clazz.cast(item));
        }
        return items;
    }

    public static @Nullable ItemStack getSlot(LivingEntity living, String id, int index) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return null;
        Map<String, ICurioStacksHandler> curios = curiosInventory.get().getCurios();
        ICurioStacksHandler stacksHandler = curios.get(id);
        if (stacksHandler != null) {
            IDynamicStackHandler stackHandler = stacksHandler.getStacks();
            if (index < stackHandler.getSlots()) {
                ItemStack stack = stackHandler.getStackInSlot(index);
                if (!stack.isEmpty()) return stack;
            }
        }
        return null;
    }

    public static @Nullable ItemStack getSlot(LivingEntity living, Predicate<ItemStack> predicate, int index) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(living);
        if (curiosInventory.isEmpty()) return null;
        for (ICurioStacksHandler curioStacksHandler : curiosInventory.get().getCurios().values()) {
            IDynamicStackHandler stackHandler = curioStacksHandler.getStacks();
            if (stackHandler.getSlots() <= index) continue;
            ItemStack stack = stackHandler.getStackInSlot(index);
            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack;
            }
        }
        return null;
    }
}
