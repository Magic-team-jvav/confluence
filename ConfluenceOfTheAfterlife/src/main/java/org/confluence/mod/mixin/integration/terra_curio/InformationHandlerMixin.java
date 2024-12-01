package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.confluence.terra_curio.network.s2c.WindSpeedPacketS2C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InformationHandler.class, remap = false)
public abstract class InformationHandlerMixin {
    @ModifyExpressionValue(method = "getFishingPowerInfo", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object modifyFishingPower(Object original, @Local(argsOnly = true) Player player) {
        return ClientPacketHandler.getFishingPower();
    }

    @Inject(method = "handleWindSpeed", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$Inject()V"))
    private static void getNearestDirection(WindSpeedPacketS2C packet, CallbackInfo ci) {
        WeatherHandler.windDirection = Direction.getNearest(packet.x(), 0.0F, packet.z());
    }
}
