package org.confluence.mod.mixin.integration.terracurio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InformationHandler.class, remap = false)
public abstract class InformationHandlerMixin {
    @ModifyExpressionValue(method = "getFishingPowerInfo", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object modifyFishingPower(Object original) {
        return ClientPacketHandler.getFishingPower();
    }

    @Inject(method = "getWeatherInfo", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$Inject()V"), cancellable = true)
    private static void modifyWeather(CallbackInfoReturnable<Component> cir, @Local(name = "weather") String weather) {
        cir.setReturnValue(Component.translatable("info.confluence.weather_radio." + weather, WeatherHandler.windSpeedInfo));
    }

    @Inject(method = "hasMechanicalView", at = @At("RETURN"), cancellable = true)
    private static void modifyView(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) cir.setReturnValue(ClientPacketHandler.isShowSignal());
    }
}
