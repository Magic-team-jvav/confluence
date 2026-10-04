package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachment.ZenithData;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public final class ZenithPacketC2S implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("zenith");
    public static final PortStreamCodec<ByteBuf, ZenithPacketC2S> STREAM_CODEC = PortStreamCodec.unit(new ZenithPacketC2S());

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ZenithData data = player.getData(SummonerAttachmentTypes.ZENITH_DATA);
        if (data.swing()) {
            data.addPower();
        }
    }
}
