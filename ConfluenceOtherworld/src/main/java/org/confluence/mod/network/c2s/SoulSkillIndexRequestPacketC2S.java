package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import org.mesdag.portlib.network.IPortPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.SoulSkillIndexSyncPacketS2C;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillIndexRequestPacketC2S(int index) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("soulcerer_skill_index_request");
    public static final PortStreamCodec<ByteBuf, SoulSkillIndexRequestPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, SoulSkillIndexRequestPacketC2S::index,
            SoulSkillIndexRequestPacketC2S::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 在服务端设置索引，并回传实际选中值。
    @Override
    public void work(ServerPlayer player) {
        if (Confluence.SOUL_SKILLS) {
            PlayerUtils.setCurrentSoulSkillIndex(player, index);
        }
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }
}
