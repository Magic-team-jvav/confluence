package org.confluence.mod.mixin.integration.terrafurniture;

import net.minecraft.util.Mth;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.terra_furniture.common.block.misc.PinWheel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PinWheel.BEntity.class)
public abstract class TFPinWheelMixin {
    @Shadow
    private float rotate;

    /**
     * @author MakerTechno
     * @reason Add wind effect
     */
    @Overwrite
    public float getStepNext() {
        if (rotate <= -Mth.PI * 24) rotate = 0;
        else rotate -= WeatherHandler.WIND_SPEED.length() * 0.09f;
        return rotate;
    }
}
