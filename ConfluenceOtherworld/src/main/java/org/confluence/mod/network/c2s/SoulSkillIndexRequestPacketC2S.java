package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.SoulSkillIndexSyncPacketS2C;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillIndexRequestPacketC2S(int index) implements IPacketC2S {
    public static final Type<SoulSkillIndexRequestPacketC2S> TYPE = Confluence.createType("soulcerer_skill_index_request");
    public static final StreamCodec<ByteBuf, SoulSkillIndexRequestPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SoulSkillIndexRequestPacketC2S::index,
            SoulSkillIndexRequestPacketC2S::new
    );

    @Override
    public Type<SoulSkillIndexRequestPacketC2S> type() {
        return TYPE;
    }

    /// 在服务端设置索引，并回传实际选中值。
    ///
    /// 若切到的槽位是未解锁技能，服务端会把它回退到手持武器提供的同标签技能（或同标签已装备槽位），
    /// 因此回传的是修正后的索引。
    @Override
    public void work(ServerPlayer player) {
        if (Confluence.SOUL_SKILLS) {
            PlayerUtils.setCurrentSoulSkillIndex(player, index);
            PlayerUtils.refreshSoulSkills(player);
            PlayerUtils.ensureHeldWeaponSkill(player);
        }
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }
}
