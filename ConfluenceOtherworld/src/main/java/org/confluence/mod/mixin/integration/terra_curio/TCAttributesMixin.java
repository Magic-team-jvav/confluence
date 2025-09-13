package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.effect.beneficial.HeartReachEffect;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TCAttributes.class, remap = false)
public abstract class TCAttributesMixin {
    @ModifyVariable(method = "applyPickupRange", at = @At("STORE"))
    private static float[] inflate(float[] rangesSqr, @Local(argsOnly = true) Player player) {
        rangesSqr[0] = Mth.square(TCUtils.getAccessoriesValue(player, AccessoryItems.MANA$PICKUP$RANGE).getA());
        rangesSqr[1] = Mth.square(TCUtils.getAccessoriesValue(player, AccessoryItems.COIN$PICKUP$RANGE).getA());
        rangesSqr[2] = Mth.square(HeartReachEffect.getRange(player));
        return rangesSqr;
    }

    @Inject(method = "forMixin$skip", at = @At("HEAD"), cancellable = true)
    private static void skip(Player player, ItemEntity itemEntity, float[] rangesSqr, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = itemEntity.getItem();
        double sqr = itemEntity.position().distanceToSqr(player.position());
        if ((sqr > rangesSqr[0] && itemStack.is(ModTags.Items.PROVIDE_MANA)) ||
                (sqr > rangesSqr[1] && itemStack.is(ModTags.Items.COINS)) ||
                (sqr > rangesSqr[2] && itemStack.is(ModTags.Items.PROVIDE_LIFE))
        ) cir.setReturnValue(true);
    }
}
