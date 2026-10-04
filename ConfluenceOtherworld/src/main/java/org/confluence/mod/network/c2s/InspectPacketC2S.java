package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.BaseGun;

///
public enum InspectPacketC2S implements IPacketC2S {
    INSTANCE;

    public static final Type<InspectPacketC2S> TYPE = Confluence.createType("inspect");
    public static final StreamCodec<RegistryFriendlyByteBuf, InspectPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<InspectPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (!player.isSpectator() && player.getMainHandItem().getItem() instanceof BaseGun gun) {
            gun.inspectAnimator(player.getMainHandItem(), player);
        }
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
