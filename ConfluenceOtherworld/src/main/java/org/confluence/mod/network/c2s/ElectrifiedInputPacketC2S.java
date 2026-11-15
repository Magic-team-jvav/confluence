package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.effect.harmful.ElectrifiedEffect;
import org.confluence.mod.common.init.ModEffects;

public record ElectrifiedInputPacketC2S(boolean moving) implements IPacketC2S {
    public static final Type<ElectrifiedInputPacketC2S> TYPE = Confluence.createType("electrified_input");
    public static final StreamCodec<ByteBuf, ElectrifiedInputPacketC2S> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(ElectrifiedInputPacketC2S::new, ElectrifiedInputPacketC2S::moving);

    @Override
    public Type<ElectrifiedInputPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (player != null && player.hasEffect(ModEffects.ELECTRIFIED)) {
            ElectrifiedEffect.recordHorizontalInput(player, moving);
        }
    }

    public static void send(boolean moving) {
        PacketDistributor.sendToServer(new ElectrifiedInputPacketC2S(moving));
    }
}
