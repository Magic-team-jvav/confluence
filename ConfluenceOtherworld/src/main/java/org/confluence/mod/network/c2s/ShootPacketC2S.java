package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.ShootingService;
import org.confluence.mod.network.s2c.ShotFeedbackPacketS2C;

///
public enum ShootPacketC2S implements IPacketC2S {
    INSTANCE;

    public static final Type<ShootPacketC2S> TYPE = Confluence.createType("shoot");
    public static final StreamCodec<RegistryFriendlyByteBuf, ShootPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<ShootPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (ShootingService.tryShoot(player)) {
            ShotFeedbackPacketS2C.sendTo(player);
        }
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
