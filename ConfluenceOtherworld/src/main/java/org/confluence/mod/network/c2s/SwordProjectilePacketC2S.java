package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.sword.BaseSwordItem;

public final class SwordProjectilePacketC2S implements IPacketC2S {
    private static final SwordProjectilePacketC2S INSTANCE = new SwordProjectilePacketC2S();
    public static final Type<SwordProjectilePacketC2S> TYPE = Confluence.createType("sword_projectile");
    public static final StreamCodec<ByteBuf, SwordProjectilePacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private SwordProjectilePacketC2S() {}

    @Override
    public Type<SwordProjectilePacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof BaseSwordItem sword) {
            sword.tryFireProjectile(player, InteractionHand.MAIN_HAND);
        }
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
