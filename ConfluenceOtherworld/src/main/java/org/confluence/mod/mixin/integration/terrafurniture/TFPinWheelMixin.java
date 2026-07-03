package org.confluence.mod.mixin.integration.terrafurniture;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.Mth;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.terra_furniture.common.block.misc.PinWheel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PinWheel.BEntity.class, remap = false)
public abstract class TFPinWheelMixin {
    @Shadow
    private float rotate;

    /// @author MakerTechno
    /// @reason Add wind effect
    @WrapMethod(method = "getStepNext")
    public float replace(Operation<Float> original) {
        if (rotate <= -Mth.PI * 24) rotate = 0;
        else rotate -= WeatherHandler.WIND_SPEED.length() * 0.09f;
        return rotate;
    }
}
