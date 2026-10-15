package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soul.skill.SoulSkill;
import org.confluence.mod.common.soul.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

public record SoulSkillRequestPacketC2S(byte action, int index, ResourceLocation skillId) implements IPacketC2S {
    public static final byte ACTION_EQUIP = 0;
    public static final byte ACTION_UNEQUIP = 1;
    public static final byte ACTION_SELECT = 2;
    public static final Type<SoulSkillRequestPacketC2S> TYPE = Confluence.createType("soul_skill_request");
    public static final StreamCodec<RegistryFriendlyByteBuf, SoulSkillRequestPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, SoulSkillRequestPacketC2S::action,
            ByteBufCodecs.VAR_INT, SoulSkillRequestPacketC2S::index,
            ResourceLocation.STREAM_CODEC, SoulSkillRequestPacketC2S::skillId,
            SoulSkillRequestPacketC2S::new
    );

    @Override
    public Type<SoulSkillRequestPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        PlayerUtils.refreshSoulSkillAvailability(player);
        SoulSkillStorage storage = player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE);
        if (index < 0 || index >= storage.getEquippedSkillMaxNumber()) {
            storage.sync(player);
            return;
        }

        switch (action) {
            case ACTION_SELECT -> storage.setCurrentIndex(index);
            case ACTION_UNEQUIP -> storage.unequipSkill(index);
            case ACTION_EQUIP -> {
                SoulSkill skill = ModSoulSkills.getById(skillId);
                SoulSkillStack equipped = storage.getCurrentSkillStack(index);
                if (skill != null
                        && skill != ModSoulSkills.EMPTY.get()
                        && storage.isSkillAvailable(skillId)
                        && (SoulSkillStack.isEmpty(equipped) || !equipped.getSoulSkill().getId().equals(skillId))) {
                    storage.equipSkill(index, skill.getStack());
                }
            }
        }

        storage.sync(player);
    }
}
