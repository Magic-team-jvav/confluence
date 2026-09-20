package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.vampire_frog.VampireFrogAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.vampire_frog.VampireFrogIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/** 吸血鬼青蛙：贴近后按冷却出手，攻击留下 3 tick 静态无敌帧。 */
public class VampireFrogMinion extends GroundMinion {

    public int attackTime = -1;

    public VampireFrogMinion() {
        super(SummonerAttachmentEntityTypes.VAMPIRE_FROG);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> attackTime, value -> attackTime = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new VampireFrogAttackGoal(this));
        goalSelector.addGoal(1, new VampireFrogIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "vampire_frog", 0, state -> {
            if (attackTime != -1 && getTickCount() - attackTime < 20) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack.strike"));
            }
            if (isWalking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move.walk"));
            }
            if (isFlying()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move.fly"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
    }

    @Override
    public Immunity.Type confluence$getImmunityType() {
        return Immunity.Type.STATIC;
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.45, 0, -0.45, 0.45, 0.7, 0.45);
    }
}
