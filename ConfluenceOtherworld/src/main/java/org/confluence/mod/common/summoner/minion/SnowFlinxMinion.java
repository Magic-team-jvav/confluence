package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.minion.goal.snow_flinx.SnowFlinxAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.snow_flinx.SnowFlinxIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

/** 小雪怪：靠身体碰撞伤害所有接触到的敌人。 */
public class SnowFlinxMinion extends GroundMinion implements IEntityCollision<SnowFlinxMinion> {

    public SnowFlinxMinion() {
        super(SummonerAttachmentEntityTypes.SNOW_FLINX);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new SnowFlinxAttackGoal(this));
        goalSelector.addGoal(1, new SnowFlinxIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "snow_flinx", 0, state -> {
            if (isWalking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move.walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public @NotNull AABB getHitbox() {
        return getBlockCollisionBox();
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 4);
        }
    }
}
