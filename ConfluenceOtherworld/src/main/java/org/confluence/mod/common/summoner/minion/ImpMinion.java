package org.confluence.mod.common.summoner.minion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.imp.ImpAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.imp.ImpIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * 小鬼：在目标周围盘旋，周期性发射穿透火球。
 */
public class ImpMinion extends MomentumMinion {

    public int attackTime = -1;

    public ImpMinion() {
        super(SummonerAttachmentEntityTypes.IMP);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> attackTime, value -> attackTime = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new ImpAttackGoal(this));
        goalSelector.addGoal(1, new ImpIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "imp", 2, state -> {
            if (attackTime != -1 && getTickCount() - attackTime < 18) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack.cast"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
    }

    @Override
    public void tick() {
        super.tick();
        if (getLevel() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.FLAME, getPos().x, getPos().y, getPos().z, 1, 0.15, 0.15, 0.15, 0.01);
        }
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public void lookAtDirection(Vec3 direction) {
        setDesiredRotation((float) Math.toDegrees(Math.atan2(-direction.x, direction.z)), 0, getRoll());
    }
}
