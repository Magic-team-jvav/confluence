package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DemonEye.class, remap = false)
public abstract class DemonEyeMixin implements SelfGetter<DemonEye> {
    @Inject(method = "push", at = @At("HEAD"))
    private void collidingCheck(Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            TCUtils.applyCthulhuTouch(player, confluence$self());
        }
    }
}
