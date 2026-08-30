package org.confluence.mod.mixin.client.model;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.renderer.item.ShortSwordInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> implements SelfGetter<HumanoidModel<?>> {
    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true)
    private void shortSword(CallbackInfo ci, @Local(argsOnly = true) T livingEntity) {
        if (ShortSwordInHandRenderer.setupAttackAnimation(livingEntity, confluence$self())) {
            ci.cancel();
        }
    }
}
