package org.confluence.mod.mixin.world.entity.npc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.ModVillagers;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @ModifyExpressionValue(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z"))
    private boolean hasSpecialOffer(boolean original, @Local(argsOnly = true) Player player, @Share("specialPrice") LocalIntRef specialPrice) {
        int value = TCUtils.getValue(player, AccessoryItems.SPECIAL$PRICE);
        specialPrice.set(value);
        return original || value > 0;
    }

    @ModifyExpressionValue(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I"))
    private int modifyAmplifier(int original, @Local MobEffectInstance effectInstance, @Share("specialPrice") LocalIntRef specialPrice) {
        int value = specialPrice.get();
        if (value > 0) {
            if (effectInstance == null) {
                return value - 1;
            }
            return value;
        }
        return original;
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void setVillagerType(CallbackInfoReturnable<SpawnGroupData> cir) {
        ModVillagers.setVillagerType((Villager) (Object) this);
    }
}
