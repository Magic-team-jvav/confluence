package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.minion.goal.slime.SlimeAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.slime.SlimeIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

/** 史莱姆：跳跃移动，靠身体碰撞伤害所有接触到的敌人。 */
public class SlimeMinion extends GroundMinion implements IEntityCollision<SlimeMinion> {

    public SlimeMinion() {
        super(SummonerAttachmentEntityTypes.SLIME);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new SlimeAttackGoal(this));
        goalSelector.addGoal(1, new SlimeIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "slime", 0, state -> {
            if (isWalking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move.walk"));
            }
            if (isFlying()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move.fly"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
    }

    /** 移动方式改成跳跃：每次靠近都朝目标位置起跳一次。 */
    @Override
    public void moveTo(Vec3 pos, float speed) {
        if (isOnGround()) {
            setWalking(true);
            Vec3 subtract = pos.subtract(getPos()).normalize().scale(speed);
            addVelocity(new Vec3(subtract.x(), 0.6f, subtract.z()));
        }
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.25, 0, -0.25, 0.25, 0.5, 0.25);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.25, 0, -0.25, 0.25, 0.5, 0.25);
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

    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }
}
