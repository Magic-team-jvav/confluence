package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.attachment.SoulSkillStorage;

public record SoulSkillSyncPacketS2C(CompoundTag data) implements IPacketS2C {
    public static final Type<SoulSkillSyncPacketS2C> TYPE = Confluence.createType("soulcerer_skill_sync");
    public static final StreamCodec<ByteBuf, SoulSkillSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SoulSkillSyncPacketS2C::data,
            SoulSkillSyncPacketS2C::new
    );

    @Override
    public Type<SoulSkillSyncPacketS2C> type() {
        return TYPE;
    }

    /// 在客户端还原槽数、装备冷却和解锁技能，保留当前索引。
    @Override
    public void work(Player player) {
        SoulSkillStorage.of(player).deserializeSkillDataNBT(player.registryAccess(), data);
        ClientSoulHandler.onSoulSkillSync();
    }

    /// 向指定玩家发送技能数据快照，不包含当前索引。
    public static void sendToClient(ServerPlayer player) {
        CompoundTag data = SoulSkillStorage.of(player).serializeSkillDataNBT(player.registryAccess());
        PacketDistributor.sendToPlayer(player, new SoulSkillSyncPacketS2C(data));
    }
}
