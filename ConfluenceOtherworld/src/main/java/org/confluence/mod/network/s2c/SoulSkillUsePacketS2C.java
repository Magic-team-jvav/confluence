package org.confluence.mod.network.s2c;

import org.mesdag.portlib.wrapper.common.extensions.IPortResourceLocationExtension;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.IPortPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;

public record SoulSkillUsePacketS2C(int entityId, ResourceLocation skillId, int cd, int maxCd) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("soulcerer_skill_use");
    public static final PortStreamCodec<ByteBuf, SoulSkillUsePacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, SoulSkillUsePacketS2C::entityId,
            IPortResourceLocationExtension.STREAM_CODEC, SoulSkillUsePacketS2C::skillId,
            PortByteBufCodecs.VAR_INT, SoulSkillUsePacketS2C::cd,
            PortByteBufCodecs.VAR_INT, SoulSkillUsePacketS2C::maxCd,
            SoulSkillUsePacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 在客户端处理服务端确认的技能使用结果。
    @Override
    public void work(Player player) {
        ClientSoulHandler.onSoulSkillUse(player, entityId, skillId, cd, maxCd);
    }

    /// 向施法玩家及追踪玩家发送一次技能使用结果。
    public static void sendToClient(ServerPlayer player, SoulSkillStack castStack) {
        PortPacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SoulSkillUsePacketS2C(player.getId(), castStack.getSoulSkill().getId(), castStack.getCd(), castStack.getMaxCd()));
    }
}

