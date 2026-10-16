package org.confluence.mod.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.network.s2c.SoulSkillSyncPacketS2C;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillRequestPacketC2S(Action action, int index, ResourceLocation skillId, int cd, int maxCd) implements IPacketC2S {
    public static final Type<SoulSkillRequestPacketC2S> TYPE = Confluence.createType("soulcerer_skill_request");
    public static final StreamCodec<FriendlyByteBuf, SoulSkillRequestPacketC2S> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Action.class), SoulSkillRequestPacketC2S::action,
            ByteBufCodecs.VAR_INT, SoulSkillRequestPacketC2S::index,
            ResourceLocation.STREAM_CODEC, SoulSkillRequestPacketC2S::skillId,
            ByteBufCodecs.VAR_INT, SoulSkillRequestPacketC2S::cd,
            ByteBufCodecs.VAR_INT, SoulSkillRequestPacketC2S::maxCd,
            SoulSkillRequestPacketC2S::new
    );

    @Override
    public Type<SoulSkillRequestPacketC2S> type() {
        return TYPE;
    }

    /// 处理技能数据请求；槽数请求仅返回服务端认可的容量。
    @Override
    public void work(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS) {
            SoulSkillSyncPacketS2C.sendToClient(player);
            return;
        }

        PlayerUtils.refreshSoulSkills(player);
        switch (action) {
            case SET_SLOT_COUNT -> {
            }
            case EQUIP, SET_STACK -> {
                SoulSkill skill = SoulSkillStorage.findSoulSkill(skillId);
                if (skill != null) {
                    SoulSkillStack stack = skill.getStack();
                    if (!stack.isEmpty()) {
                        stack.setCd(cd);
                        stack.setMaxCd(maxCd);
                    }

                    if (action == Action.EQUIP) {
                        PlayerUtils.equipSoulSkill(player, index, stack);
                    } else {
                        PlayerUtils.setSoulSkillStack(player, index, stack);
                    }
                }
            }
            case UNEQUIP -> PlayerUtils.unequipSoulSkill(player, index);
        }

        PlayerUtils.updateSoulSkillSlots(player);
        PlayerUtils.refreshSoulSkills(player);
        SoulSkillSyncPacketS2C.sendToClient(player);
    }

    public enum Action {
        SET_SLOT_COUNT,
        EQUIP,
        UNEQUIP,
        SET_STACK
    }
}
