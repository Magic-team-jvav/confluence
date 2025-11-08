package org.confluence.mod.mixin.integration.curios;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.mixin.CuriosUtilMixinHooks;

@Mixin(value = CuriosUtilMixinHooks.class, remap = false)
public abstract class CuriosUtilMixinHooksMixin {
    @ModifyReturnValue(method = "getFortuneLevel", at = @At(value = "RETURN", ordinal = 0))
    private static int modify(int original, @Local LivingEntity living) {
        if (living instanceof Player player) {
            return original + ModArmorBonus.getValue(player, ModArmorBonus.FORTUNE);
        }
        return original;
    }
}
