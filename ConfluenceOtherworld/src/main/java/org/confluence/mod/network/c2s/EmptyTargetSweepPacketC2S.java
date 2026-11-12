package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum EmptyTargetSweepPacketC2S implements IPortPacket.C2S {
    INSTANCE;
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, EmptyTargetSweepPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);
    public static final ResourceLocation ID = Confluence.asResource("empty_target_sweep");

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        if (PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            /// 空挥：原版横扫伤害公式、要求满蓄力、挥完重置蓄力。
            PlayerUtils.performSweep(player, 0.0F, 1.0F, true);
        }
    }

    public static void send2Server() {
        PortPacketDistributor.sendToServer(INSTANCE);
    }
}
