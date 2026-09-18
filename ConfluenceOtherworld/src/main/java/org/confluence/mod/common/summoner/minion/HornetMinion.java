package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.hornet.HornetAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.hornet.HornetIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;

public class HornetMinion extends MomentumMinion {

    public int maxCooldown = 0;
    public int cooldown = 0;
    public int lastCooldown = 0;

    public HornetMinion() {
        super(SummonerAttachmentEntityTypes.HORNET);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> maxCooldown, value -> maxCooldown = value);
        fields.field(LyraStreamCodecs.INT, () -> cooldown, value -> cooldown = value);
        fields.field(LyraStreamCodecs.INT, () -> lastCooldown, value -> lastCooldown = value);
    }

    @Override
    public void tick() {
        super.tick();
        lastCooldown = cooldown;
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new HornetAttackGoal(this));
        goalSelector.addGoal(1, new HornetIdleGoal(this));
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
