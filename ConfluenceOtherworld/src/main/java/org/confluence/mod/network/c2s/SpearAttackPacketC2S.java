package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.network.IPacket;

public final class SpearAttackPacketC2S implements IPacketC2S {
    public static final SpearAttackPacketC2S INSTANCE = new SpearAttackPacketC2S();
    public static final Type<SpearAttackPacketC2S> TYPE = IPacket.createType("spear_attack");
    public static final StreamCodec<ByteBuf, SpearAttackPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private SpearAttackPacketC2S() {}

    @Override
    public Type<SpearAttackPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.is(ModTags.Items.SPEAR)) {
            itemStack.onEntitySwing(player, InteractionHand.MAIN_HAND);
        }
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
