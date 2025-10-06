package org.confluence.mod.mixed;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;

public interface IClientboundUpdateMobEffectPacket {
    boolean confluence$isEnabled();

    static IClientboundUpdateMobEffectPacket of(ClientboundUpdateMobEffectPacket packet) {
        return (IClientboundUpdateMobEffectPacket) packet;
    }
}
