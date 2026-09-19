package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.hornet.HornetAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.hornet.HornetIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "hornet", 2, state -> {
            if (isAttackAnimationActive()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack.cast"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
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

    public boolean isAttackAnimationActive() {
        return attackTime != -1 && tickCount - attackTime < 8;
    }
}
