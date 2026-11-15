package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;

public record SoulSkillUsePacketS2C(int entityId, ResourceLocation skillId, int cd, int maxCd) implements IPacketS2C {
    public static final Type<SoulSkillUsePacketS2C> TYPE = Confluence.createType("soulcerer_skill_use");
    public static final StreamCodec<ByteBuf, SoulSkillUsePacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SoulSkillUsePacketS2C::entityId,
            ResourceLocation.STREAM_CODEC, SoulSkillUsePacketS2C::skillId,
            ByteBufCodecs.VAR_INT, SoulSkillUsePacketS2C::cd,
            ByteBufCodecs.VAR_INT, SoulSkillUsePacketS2C::maxCd,
            SoulSkillUsePacketS2C::new
    );

    @Override
    public Type<SoulSkillUsePacketS2C> type() {
        return TYPE;
    }

    /// 在客户端处理服务端确认的技能使用结果。
    @Override
    public void work(Player player) {
        ClientSoulHandler.onSoulSkillUse(player, entityId, skillId, cd, maxCd);
    }

    /// 向施法玩家及追踪玩家发送一次技能使用结果。
    public static void sendToClient(ServerPlayer player, SoulSkillStack castStack) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SoulSkillUsePacketS2C(player.getId(), castStack.getSoulSkill().getId(), castStack.getCd(), castStack.getMaxCd()));
    }
}
