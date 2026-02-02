package org.confluence.terra_curio.mixin.integration.curios;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.network.client.CuriosClientPackets;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncCurios;

@Mixin(value = CuriosClientPackets.class, remap = false)
public abstract class CuriosClientPacketsMixin {
    @Inject(method = "lambda$handle$6", at = @At("TAIL"))
    private static void flush(SPacketSyncCurios data, Entity entity, ICuriosItemHandler handler, CallbackInfo ci) {
        if (entity instanceof LocalPlayer localPlayer) {
            TCUtils.updateWalkableFluidStates(localPlayer);
        }
    }
}
