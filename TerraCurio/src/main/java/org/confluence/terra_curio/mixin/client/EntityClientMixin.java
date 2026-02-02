package org.confluence.terra_curio.mixin.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.client.handler.GravitationHandler;
import org.confluence.terra_curio.mixed.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityClientMixin implements SelfGetter<Entity> {
    @Shadow
    protected abstract BlockPos getOnPos(float pYOffset);

    @Inject(method = "getEyeHeight()F", at = @At("RETURN"), cancellable = true)
    private void eyeHeight(CallbackInfoReturnable<Float> cir) {
        if (GravitationHandler.isShouldRot(confluence$self())) {
            cir.setReturnValue(IEntity.of(confluence$self()).terra_curio$getDimensionHeight() * 0.15F);
        }
    }

    @Inject(method = "getOnPosLegacy", at = @At("RETURN"), cancellable = true)
    private void getOnPosAbove(CallbackInfoReturnable<BlockPos> cir) {
        if (GravitationHandler.isShouldRot(confluence$self())) {
            cir.setReturnValue(getOnPos(-2.2F));
        }
    }
}
