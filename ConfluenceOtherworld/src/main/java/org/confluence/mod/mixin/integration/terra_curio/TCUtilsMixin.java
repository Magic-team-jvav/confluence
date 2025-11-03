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

    @WrapMethod(method = "getAccessoriesValue")
    private static <T, V extends PrimitiveValue<T>> T wrap1(LivingEntity living, ValueType<T, V> type, Operation<T> original) {
        T called = original.call(living, type);
        if (living instanceof Player player) {
            return type.combineRule().combine(called, ModArmorBonus.getValue(player, type));
        }
        return called;
    }

    @WrapMethod(method = "hasAccessoriesType")
    private static boolean wrap2(LivingEntity living, ValueType<Unit, UnitValue> type, Operation<Boolean> original) {
        boolean called = original.call(living, type);
        if (living instanceof Player player) {
            return called || ModArmorBonus.hasType(player, type);
        }
        return called;
    }
}
