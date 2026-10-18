package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
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

public record SoulSkillSyncPacketS2C(CompoundTag data) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("soulcerer_skill_sync");
    public static final PortStreamCodec<ByteBuf, SoulSkillSyncPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.COMPOUND_TAG, SoulSkillSyncPacketS2C::data,
            SoulSkillSyncPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
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
        PortPacketDistributor.sendToPlayer(player, new SoulSkillSyncPacketS2C(data));
    }
}
