package org.confluence.mod.mixin.integration.terra_furniture;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.terra_furniture.common.block.func.be.BaseSwayingBE;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSwayingBE.class)
public abstract class TFSwayingBEMixin {
    @Shadow
    public abstract void applyDelta(Vec3 input);

    @Inject(method = "tickAtClient", at = @At(value = "RETURN"))
    public void tickAtClient(CallbackInfo ci) {
        applyDelta(new Vec3(WeatherHandler.getWindSpeedX(), 0, WeatherHandler.getWindSpeedZ()).scale(0.017));
    }
}
