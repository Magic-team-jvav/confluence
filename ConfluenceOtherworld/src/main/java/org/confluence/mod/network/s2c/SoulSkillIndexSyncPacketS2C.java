package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillIndexSyncPacketS2C(int index) implements IPacketS2C {
    public static final Type<SoulSkillIndexSyncPacketS2C> TYPE = Confluence.createType("soulcerer_skill_index_sync");
    public static final StreamCodec<ByteBuf, SoulSkillIndexSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SoulSkillIndexSyncPacketS2C::index,
            SoulSkillIndexSyncPacketS2C::new
    );

    @Override
    public Type<SoulSkillIndexSyncPacketS2C> type() {
        return TYPE;
    }

    /// 还原服务端选中索引，并刷新技能 HUD。
    @Override
    public void work(Player player) {
        SoulSkillStorage.of(player).setCurrentSoulSkillIndex(index);
        ClientSoulHandler.updateSoulSkillHuds();
    }

    /// 仅向指定玩家发送当前选中索引。
    public static void sendToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SoulSkillIndexSyncPacketS2C(PlayerUtils.getCurrentSoulSkillIndex(player)));
    }
}
