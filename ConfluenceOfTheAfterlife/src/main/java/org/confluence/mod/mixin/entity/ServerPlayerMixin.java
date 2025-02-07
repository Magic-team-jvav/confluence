package org.confluence.mod.mixin.entity;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements IServerPlayer, SelfGetter<ServerPlayer> {
    @Shadow
    @Final
    private ServerStatsCounter stats;

    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract PlayerAdvancements getAdvancements();

    @Unique
    private boolean confluence$couldPickupItem = true;
    @Unique
    private boolean confluence$marathon = false;

    @Override
    public void confluence$setCouldPickupItem(boolean enable) {
        this.confluence$couldPickupItem = enable;
    }

    @Override
    public boolean confluence$isCouldPickupItem() {
        return confluence$couldPickupItem;
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSprinting()Z"))
    private void checkMarathon(double dx, double dy, double dz, CallbackInfo ci) {
        if (confluence$marathon) return;
        this.confluence$marathon = ModAchievements.marathonMedalist(self(), stats, confluence$marathon);
    }
}
