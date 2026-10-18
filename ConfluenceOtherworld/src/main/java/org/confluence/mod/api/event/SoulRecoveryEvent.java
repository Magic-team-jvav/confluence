package org.confluence.mod.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class SoulRecoveryEvent extends PlayerEvent {
    private final float originalRatio;
    private float recoveryRatio;

    public SoulRecoveryEvent(ServerPlayer player, float recoveryRatio) {
        super(player);
        this.originalRatio = recoveryRatio;
        this.recoveryRatio = recoveryRatio;
    }

    public float getOriginalRatio() {
        return originalRatio;
    }

    public float getRecoveryRatio() {
        return recoveryRatio;
    }

    public void setRecoveryRatio(float recoveryRatio) {
        this.recoveryRatio = recoveryRatio;
    }
}

