package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.PlayerUtils;

public enum EmptyTargetSweepPacketC2S implements IPacketC2S {
    INSTANCE;
    public static final Type<EmptyTargetSweepPacketC2S> TYPE = Confluence.createType("empty_target_sweep");
    public static final StreamCodec<ByteBuf, EmptyTargetSweepPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<EmptyTargetSweepPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            /// 空挥：原版横扫伤害公式、要求满蓄力、挥完重置蓄力、照常叠附魔加伤。
            PlayerUtils.performSweep(player, 0.0F, 1.0F, true, true);
        }
    }

    public static void send2Server() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
