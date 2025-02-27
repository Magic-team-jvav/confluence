package org.confluence.mod.mixin.client;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Unique private static final Map<Object, Long> confluence$counter = new ConcurrentHashMap<>();
    @Inject(method = "genericsFtw",at = @At("HEAD"))
    private static <T extends PacketListener> void packet(Packet<T> pPacket, PacketListener pListener, CallbackInfo ci){
        confluence$counter.putIfAbsent(pPacket.getClass(), 1L);
        confluence$counter.computeIfPresent(pPacket.getClass(), (k, v) -> v + 1);
    }
}
