package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillUsePacketC2S() implements IPacketC2S {
    public static final Type<SoulSkillUsePacketC2S> TYPE = Confluence.createType("soulcerer_skill_use_request");
    public static final StreamCodec<ByteBuf, SoulSkillUsePacketC2S> STREAM_CODEC = StreamCodec.unit(new SoulSkillUsePacketC2S());

    @Override
    public Type<SoulSkillUsePacketC2S> type() {
        return TYPE;
    }

    /// 在服务端尝试使用当前灵魂技能。
    @Override
    public void work(ServerPlayer player) {
        PlayerUtils.tryUseSoulSkill(player);
    }
}
