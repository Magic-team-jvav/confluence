package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.xiaohunao.phase_journey.common.phase.block.BlockPhaseManager;
import com.xiaohunao.phase_journey.common.phase.block.BlockReplacementPhaseContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
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
    private static Object modifyFishingPower(Object original, @Local(argsOnly = true) Player player) {
        return ClientPacketHandler.getFishingPower();
    }

    @Inject(method = "getWeatherInfo", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$Inject()V"), cancellable = true)
    private static void modifyWeather(Player player, CallbackInfoReturnable<Component> cir, @Local String weather) {
        cir.setReturnValue(Component.translatable("info.confluence.weather_radio." + weather, WeatherHandler.windSpeedInfo));
    }

    @Inject(method = "hasMechanicalView", at = @At("RETURN"), cancellable = true)
    private static void modifyView(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) cir.setReturnValue(ClientPacketHandler.isShowSignal());
    }

    @ModifyReturnValue(method = "mapCloakedBlock", at = @At("RETURN"))
    private static BlockState checkRevealed(BlockState original, @Local(argsOnly = true) Player player) {
        return BlockPhaseManager.MANAGER.isRestricteds(player.level(),null,player, ctx -> {
            BlockReplacementPhaseContext blockReplacementPhaseContext = BlockPhaseManager.MANAGER.getBlockReplacementPhaseContext(original);
            if (ctx.equals(blockReplacementPhaseContext)){
                return blockReplacementPhaseContext.getTarget();
            }
            return original;
        },original);
    }
}
