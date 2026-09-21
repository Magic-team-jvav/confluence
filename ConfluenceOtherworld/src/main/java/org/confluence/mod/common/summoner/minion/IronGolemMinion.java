package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.iron_golem.IronGolemAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.iron_golem.IronGolemIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/** 铁傀儡：贴身近战，命中播放动画、音效并上挑击退。 */
public class IronGolemMinion extends GroundMinion {

    public int attackTime = -1;

    public IronGolemMinion() {
        super(SummonerAttachmentEntityTypes.IRON_GOLEM);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> attackTime, value -> attackTime = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new IronGolemAttackGoal(this));
        goalSelector.addGoal(1, new IronGolemIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "iron_golem", 2, state -> {
            if (attackTime != -1 && getTickCount() - attackTime < 20) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attacking"));
            }
            if (isWalking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walking"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("standing"));
        }));
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.7, 0, -0.7, 0.7, 2.6, 0.7);
    }
}
