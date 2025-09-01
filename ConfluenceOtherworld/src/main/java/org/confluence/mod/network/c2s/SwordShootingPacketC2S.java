package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.network.IPacket;

public final class SwordShootingPacketC2S implements IPacketC2S {
    public static final SwordShootingPacketC2S INSTANCE = new SwordShootingPacketC2S();
    public static final Type<SwordShootingPacketC2S> TYPE = IPacket.createType("sword_shooting");
    public static final StreamCodec<ByteBuf, SwordShootingPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private SwordShootingPacketC2S() {}

    @Override
    public Type<SwordShootingPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof BaseSwordItem sword) {
            sword.genProjectile(player, mainHandItem);
        }
    }
}
