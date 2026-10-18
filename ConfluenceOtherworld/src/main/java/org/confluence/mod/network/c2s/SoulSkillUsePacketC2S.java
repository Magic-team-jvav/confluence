package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import org.mesdag.portlib.network.IPortPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillUsePacketC2S() implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("soulcerer_skill_use_request");
    public static final PortStreamCodec<ByteBuf, SoulSkillUsePacketC2S> STREAM_CODEC = PortStreamCodec.unit(new SoulSkillUsePacketC2S());

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 在服务端尝试使用当前灵魂技能。
    @Override
    public void work(ServerPlayer player) {
        PlayerUtils.tryUseSoulSkill(player);
    }
}
