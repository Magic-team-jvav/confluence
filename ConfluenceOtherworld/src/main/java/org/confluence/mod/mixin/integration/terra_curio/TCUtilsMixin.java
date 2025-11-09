package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TCUtils.class, remap = false)
public abstract class TCUtilsMixin {
    @Inject(method = "isFluidWalkable", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private static void waterWalkingEffect(LivingEntity living, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && living.hasEffect(ModEffects.WATER_WALKING)) {
            cir.setReturnValue(true);
        }
    }

    @WrapMethod(method = "getValue")
    private static <T, V extends PrimitiveValue<T>> T wrap1(LivingEntity living, ValueType<T, V> type, Operation<T> original) {
        T called = original.call(living, type);
        if (living instanceof Player player) {
            T t = ModArmorBonus.getValue(player, type);
            if (called == null) return t;
            return type.combineRule().combine(called, t);
        }
        return called;
    }

    @WrapMethod(method = "hasType")
    private static boolean wrap2(LivingEntity living, ValueType<Unit, UnitValue> type, Operation<Boolean> original) {
        boolean called = original.call(living, type);
        if (living instanceof Player player) {
            return called || ModArmorBonus.hasType(player, type);
        }
        return called;
    }

    @WrapMethod(method = "getPrimitiveValue")
    private static <T, V extends PrimitiveValue<T>> @Nullable V wrap3(LivingEntity living, ValueType<T, V> type, Operation<V> original) {
        V called = original.call(living, type);
        if (living instanceof Player player) {
            V v = ModArmorBonus.getPrimitiveValue(player, type);
            if (called == null) return v;
            if (v == null) return null;
            return type.newInstance(type.combineRule().combineValue(called, v));
        }
        return called;
    }
}
