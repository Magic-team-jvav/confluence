package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import org.mesdag.portlib.network.IPortPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.SoulSkillIndexSyncPacketS2C;
import org.confluence.mod.network.s2c.SoulSkillSyncPacketS2C;
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
    ///
    /// 若切到的槽位是未解锁技能，服务端会把它回退到手持武器提供的同标签技能（或同标签已装备槽位），
    /// 因此回传的是修正后的索引。刷新时「武器专属技能槽跟随手持武器」也可能改动槽内容，
    /// 这时要一并补发技能数据同步，避免客户端还显示旧槽位。
    @Override
    public void work(ServerPlayer player) {
        if (Confluence.SOUL_SKILLS) {
            PlayerUtils.setCurrentSoulSkillIndex(player, index);
            boolean changed = PlayerUtils.refreshSoulSkills(player);
            changed |= PlayerUtils.ensureHeldWeaponSkill(player);
            if (changed) {
                SoulSkillSyncPacketS2C.sendToClient(player);
            }
        }
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }
}
