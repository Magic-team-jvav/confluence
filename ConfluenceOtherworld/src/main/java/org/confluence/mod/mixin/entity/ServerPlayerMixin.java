package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerStatsCounter;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.network.s2c.PlayerDeathInfoPacketS2C;
import org.confluence.mod.util.AchievementUtils;
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
    private boolean confluence$marathon_medalist = false;
    @Unique
    private short confluence$bulldozer = 0;

    @Override
    public void confluence$setCouldPickupItem(boolean enable) {
        this.confluence$couldPickupItem = enable;
    }

    @Override
    public boolean confluence$isCouldPickupItem() {
        return confluence$couldPickupItem;
    }

    @Override
    public void confluence$bulldozer() {
        if (confluence$bulldozer < 0) return;
        if (this.confluence$bulldozer++ >= 9999) {
            AdvancementHolder advancement = server.getAdvancements().get(AchievementUtils.asAchievement("bulldozer"));
            if (advancement != null) {
                getAdvancements().award(advancement, "never");
            }
            this.confluence$bulldozer = -1;
        }
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSprinting()Z"))
    private void checkMarathon(double dx, double dy, double dz, CallbackInfo ci) {
        if (confluence$marathon_medalist) return;
        this.confluence$marathon_medalist = AchievementUtils.marathonMedalist(confluence$self(), stats, confluence$marathon_medalist);
    }

    @WrapWithCondition(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"))
    private boolean replacePacket(ServerGamePacketListenerImpl instance, Packet<?> packet, PacketSendListener packetSendListener) {
        if (packet instanceof ClientboundPlayerCombatKillPacket combatKillPacket) {
            return PlayerDeathInfoPacketS2C.replaceCombatKillPacket(instance.player, combatKillPacket.message());
        }
        return true;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        this.confluence$bulldozer = compound.getShort("confluence:bulldozer");
        this.confluence$marathon_medalist = compound.getBoolean("confluence:marathon_medalist");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putShort("confluence:bulldozer", confluence$bulldozer);
        compound.putBoolean("confluence:marathon_medalist", confluence$marathon_medalist);
    }
}
