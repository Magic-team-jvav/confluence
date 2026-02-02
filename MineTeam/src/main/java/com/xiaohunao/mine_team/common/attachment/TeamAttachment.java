package com.xiaohunao.mine_team.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;

import java.util.UUID;

public class TeamAttachment {
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final Codec<TeamAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("team_uid").orElseGet(UUID::randomUUID).forGetter(TeamAttachment::getTeamUid),
            Codec.BOOL.optionalFieldOf("can_pvp", true).forGetter(TeamAttachment::isCanPvP)
    ).apply(instance, TeamAttachment::new));
    public static final StreamCodec<FriendlyByteBuf, TeamAttachment> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TeamAttachment::getTeamUid,
            ByteBufCodecs.BOOL, TeamAttachment::isCanPvP,
            TeamAttachment::new
    );

    private UUID teamUid;
    private boolean canPvP;

    public TeamAttachment(UUID teamUid, boolean canPvP) {
        this.teamUid = teamUid;
        this.canPvP = canPvP;
    }

    public UUID getTeamUid() {
        return teamUid;
    }

    public TeamAttachment setTeamUid(UUID teamUid) {
        this.teamUid = teamUid;
        return this;
    }

    public boolean isCanPvP() {
        return canPvP;
    }

    public TeamAttachment setCanPvP(boolean canPvP) {
        this.canPvP = canPvP;
        return this;
    }

    public TeamAttachment sync(TeamAttachment attachment) {
        this.teamUid = attachment.teamUid;
        this.canPvP = attachment.canPvP;
        return this;
    }

    public static TeamAttachment of(Entity entity) {
        TeamAttachment data = entity.getData(MTAttachmentTypes.TEAM);
        if (!entity.level().isClientSide && data.teamUid != null) {
            TeamManager teamManager = TeamManager.of(entity.level());
            data.setTeamUid(teamManager.getTeam(DyeColor.WHITE).getUid());
        }
        return data;
    }
}
