package org.confluence.terraentity.mixin;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.confluence.terraentity.mixed.IBossEvent;
import org.confluence.terraentity.network.s2c.SyncBossEventHealthPacket;
import org.confluence.terraentity.utils.AdapterUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerBossEvent.class)
public class ServerBossEventMixin {
    @Final
    @Shadow
    private Set<ServerPlayer> players;

    @Inject(method = "setProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;broadcast(Ljava/util/function/Function;)V"))
    public void setProgressMixin(float progress, CallbackInfo ci) {
        for (ServerPlayer player : players) {
            AdapterUtils.sendToPlayer(player, new SyncBossEventHealthPacket(
                    ((BossEvent) (Object) this).getId(),
                    ((IBossEvent) this).terra_enity$getBossHealth(),
                    ((IBossEvent) this).terra_enity$getBossMaxHealth()
            ));
        }
    }
}
