package org.confluence.mod.mixin.server.level;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;
import org.confluence.mod.common.advancement.AchievementAwardService;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.network.s2c.PlayerDeathInfoPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements IServerPlayer {
    @Unique
    private boolean confluence$couldPickupItem = true;
    @Unique
    private short confluence$bulldozer = 0;
    @Unique
    private ChunkPos confluence$lastChunkPosition;
    @Unique
    private final Vector3f confluence$movementSpeed = new Vector3f();
    @Unique
    private @Nullable TitaniumShardsProjectile confluence$titaniumShards;

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
            if (AchievementAwardService.award(confluence$self(), "bulldozer").completed()) {
                this.confluence$bulldozer = -1;
            }
        }
    }

    @Override
    public boolean confluence$chunkPosChanged() {
        ChunkPos pos = confluence$self().chunkPosition();
        if (confluence$lastChunkPosition != pos) {
            this.confluence$lastChunkPosition = pos;
            return true;
        }
        return false;
    }

    @Override
    public Vector3f confluence$getMovementSpeed() {
        return confluence$movementSpeed;
    }

    @Override
    public void confluence$setTitaniumShards(@Nullable TitaniumShardsProjectile projectile) {
        this.confluence$titaniumShards = projectile;
    }

    @Override
    public boolean confluence$hasTitaniumShards() {
        return confluence$titaniumShards != null && !confluence$titaniumShards.isRemoved();
    }

    @WrapWithCondition(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"))
    private boolean replacePacket(ServerGamePacketListenerImpl instance, Packet<?> packet, PacketSendListener packetSendListener) {
        if (packet instanceof ClientboundPlayerCombatKillPacket combatKillPacket) {
            return PlayerDeathInfoPacketS2C.replaceCombatKillPacket(instance.player, combatKillPacket.getMessage());
        }
        return true;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        this.confluence$bulldozer = compound.getShort("confluence:bulldozer");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putShort("confluence:bulldozer", confluence$bulldozer);
    }
}
