package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.advancement.AchievementAwardService;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * 请求授予“致敬经典”成就。
 *
 * <p>消息不携带可由客户端伪造的成就编号，服务端只处理这一项固定行为。
 */
public enum GoingOldschoolPacketC2S implements IPortPacket.C2S {
    INSTANCE;

    public static final ResourceLocation ID = Confluence.asResource("going_oldschool");
    public static final PortStreamCodec<ByteBuf, GoingOldschoolPacketC2S> STREAM_CODEC =
            PortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /**
     * 成就进度属于服务端玩家状态，必须回到服务端主线程修改。
     */
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        AchievementAwardService.award(player, "going_oldschool");
    }
}
