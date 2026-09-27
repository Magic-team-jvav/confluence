package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.effect.harmful.ElectrifiedEffect;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record ElectrifiedInputPacketC2S(boolean moving) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("electrified_input");
    public static final PortStreamCodec<ByteBuf, ElectrifiedInputPacketC2S> STREAM_CODEC =
            PortByteBufCodecs.BOOL.map(ElectrifiedInputPacketC2S::new, ElectrifiedInputPacketC2S::moving);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        if (player != null && player.hasEffect(ModEffects.ELECTRIFIED.get())) {
            ElectrifiedEffect.recordHorizontalInput(player, moving);
        }
    }

    public static void send(boolean moving) {
        PortPacketDistributor.sendToServer(new ElectrifiedInputPacketC2S(moving));
    }
}
