package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.sculk_wisp.SculkWispAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.sculk_wisp.SculkWispIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * 幽匿游灵：飞行仆从，靠近目标时保持距离，蓄力结束后放出音爆。
 */
public class SculkWispMinion extends MomentumMinion implements IBlockCollision<SculkWispMinion> {

    public int castTime = -1;

    public SculkWispMinion() {
        super(SummonerAttachmentEntityTypes.SCULK_WISP);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> castTime, value -> castTime = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new SculkWispAttackGoal(this));
        goalSelector.addGoal(1, new SculkWispIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "sculk_wisp", 2, state -> {
            if (isCasting()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    public boolean isCasting() {
        return castTime != -1 && getTickCount() - castTime < 20;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.4, -0.4, -0.4, 0.4, 0.4, 0.4);
    }
}
