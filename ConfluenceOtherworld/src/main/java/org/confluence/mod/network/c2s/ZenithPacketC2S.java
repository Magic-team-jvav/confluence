package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachment.ZenithData;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * 天顶剑：客户端按住使用键时每 tick 上报一次。
 * <p>
 * 服务端据此重算挥砍与蓄力（{@link ZenithData#swing()} / {@link ZenithData#addPower()}），
 * 蓄力超过阈值后由 {@link ZenithData#tick()} 发射飞剑。
 * </p>
 */
public final class ZenithPacketC2S implements IPortPacket.C2S {
    private static final ZenithPacketC2S INSTANCE = new ZenithPacketC2S();
    public static final ResourceLocation ID = Confluence.asResource("zenith");
    public static final PortStreamCodec<ByteBuf, ZenithPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    private ZenithPacketC2S() {}

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

    public static void sendToServer() {
        PortPacketDistributor.sendToServer(INSTANCE);
    }
}
