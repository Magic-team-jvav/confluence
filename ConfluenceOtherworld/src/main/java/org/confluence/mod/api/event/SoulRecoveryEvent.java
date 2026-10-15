package org.confluence.mod.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/// 服务端每秒灵魂恢复比例修正，0.01 表示每秒恢复上限的 1%；所有来源修正后统一向上取整。
public class SoulRecoveryEvent extends PlayerEvent implements ICancellableEvent {
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
