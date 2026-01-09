package org.confluence.mod.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public class ArrowMixin implements SelfGetter<Arrow> {
    @Inject(method = "doPostHurtEffects", at = @At("HEAD"))
    public void doPostHurtEffects(LivingEntity living, CallbackInfo ci) {
        @Nullable ItemStack weapon = confluence$self().getWeaponItem();
        if (weapon != null && weapon.getItem() instanceof BaseTerraBowItem bow) {
            try {
                if (bow.arrowModifier.onHitEffects != null) {
                    bow.arrowModifier.onHitEffects.forEach(effect -> effect.applyAll((LivingEntity) confluence$self().getOwner(), living));
                }
            } catch (Exception ignored) {}
        }
    }
}
