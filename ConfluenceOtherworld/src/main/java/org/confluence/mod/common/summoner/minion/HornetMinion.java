package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.hornet.HornetAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.hornet.HornetIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;

public class HornetMinion extends MomentumMinion {

    public int attackTime = -1;

    public HornetMinion() {
        super(SummonerAttachmentEntityTypes.HORNET);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> attackTime, value -> attackTime = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new HornetAttackGoal(this));
        goalSelector.addGoal(1, new HornetIdleGoal(this));
    }

    public float attackAnimation(float partialTick) {
        float value = tickCount - attackTime + partialTick;
        if (attackTime != -1 && value < 0.375 * 20) {
            return value;
        }
        return -1;
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public void lookAtDirection(Vec3 direction) {
        float targetYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        setDesiredRotation(targetYaw, 0, getRoll());
    }
}
