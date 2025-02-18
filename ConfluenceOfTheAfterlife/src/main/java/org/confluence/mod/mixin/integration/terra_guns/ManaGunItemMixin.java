package org.confluence.mod.mixin.integration.terra_guns;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_guns.common.item.gun.ManaGunItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ManaGunItem.class, remap = false)
public abstract class ManaGunItemMixin {
    @Shadow
    @Final
    private int manaCost;

    @Inject(method = "consumeMana", at = @At("HEAD"), cancellable = true)
    private void inject(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(PlayerUtils.extractMana(player, () -> manaCost));
    }
}
