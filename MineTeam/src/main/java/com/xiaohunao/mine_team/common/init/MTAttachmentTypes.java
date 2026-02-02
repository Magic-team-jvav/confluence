package com.xiaohunao.mine_team.common.init;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class MTAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MineTeam.MODID);

    public static final Supplier<AttachmentType<TeamAttachment>> TEAM = ATTACHMENT_TYPES.register(
            "team", () -> AttachmentType.builder(() -> new TeamAttachment(TeamAttachment.EMPTY_UUID, false)).serialize(TeamAttachment.CODEC).copyOnDeath().build()
    );
}
