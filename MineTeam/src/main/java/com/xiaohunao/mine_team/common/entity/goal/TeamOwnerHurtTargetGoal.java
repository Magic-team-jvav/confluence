package com.xiaohunao.mine_team.common.entity.goal;


import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import com.xiaohunao.mine_team.common.team.Team;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class TeamOwnerHurtTargetGoal extends TargetGoal {
    private LivingEntity teamLastHurt;
    private long timestamp;
    public TeamOwnerHurtTargetGoal(Mob tameLivingEntity) {
        super(tameLivingEntity, false);
           this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (mob.hasData(MTAttachmentTypes.TEAM)) {
            TeamAttachment data = TeamAttachment.of(mob);
            TeamManager teamManager = TeamManager.of(mob.level());
            Team team = teamManager.getTeam(data.getTeamUid());
            if (team != null){
                this.teamLastHurt = team.getLastHurtByMob();
                int lastAttackedTime = team.getLastHurtByMobTimestamp();
                return lastAttackedTime != this.timestamp && this.canAttack(this.teamLastHurt, TargetingConditions.DEFAULT) && attackTeam(this.mob, this.teamLastHurt);

            }
        }
        return false;
    }

    private boolean attackTeam(LivingEntity attack,LivingEntity target) {
        TeamAttachment targetAttachment = TeamAttachment.of(target);
        TeamManager teamManager = TeamManager.of(target.level());
        Team targetTeam = teamManager.getTeam(targetAttachment.getTeamUid());
        Team attackTeam = teamManager.getTeam(TeamAttachment.of(attack).getTeamUid());
        return attackTeam != targetTeam;
    }

    public void start() {
        this.mob.setTarget(this.teamLastHurt);

        TeamManager teamManager = TeamManager.of(this.mob.level());
        Team team = teamManager.getTeam(TeamAttachment.of(mob).getTeamUid());
        if (team != null){
            this.timestamp = team.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}