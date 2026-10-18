package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.IPortPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillIndexSyncPacketS2C(int index) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("soulcerer_skill_index_sync");
    public static final PortStreamCodec<ByteBuf, SoulSkillIndexSyncPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, SoulSkillIndexSyncPacketS2C::index,
            SoulSkillIndexSyncPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 还原服务端选中索引，并刷新技能 HUD。
    @Override
    public void work(Player player) {
        SoulSkillStorage.of(player).setCurrentSoulSkillIndex(index);
        ClientSoulHandler.updateSoulSkillHuds();
    }

    /// 仅向指定玩家发送当前选中索引。
    public static void sendToClient(ServerPlayer player) {
        PortPacketDistributor.sendToPlayer(player, new SoulSkillIndexSyncPacketS2C(PlayerUtils.getCurrentSoulSkillIndex(player)));
    }
}
