package org.confluence.mod.common.summoner.minion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

/**
 * 铁傀儡召唤物：沿地面寻路节点移动，靠近目标后进行近战攻击。
 */
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
            if (isAttackAnimationActive()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attacking"));
            }
            if (isWalking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walking"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("standing"));
        }));
    }

    @Override
    public float getEyeHeight() {
        return 2;
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    public boolean isAttackAnimationActive() {
        return attackTime != -1 && getTickCount() - attackTime < 20;
    }

    /**
     * 动画控制器只在客户端求值，用插值节点与当前位置的差判断是否在行走。
     */
    public boolean isWalking() {
        return getPos().distanceToSqr(getRenderNode(0.0F).pos()) > 1.0E-4;
    }

    /**
     * 待机位置：在主人身后横向排成一列，位置不可站立时退到最近的落脚点。
     */
    public Vec3 getIdlePosition() {
        LivingEntity owner = getOwner();
        Vec3 forward = Vec3.directionFromRotation(0.0F, owner.yBodyRot);
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() < 1.0E-6) {
            right = new Vec3(1.0, 0.0, 0.0);
        } else {
            right = right.normalize();
        }
        double spacing = getBlockCollisionBox().getXsize() + 0.4;
        double lateral = (getOrder() - (getSameSize() - 1) * 0.5) * spacing;
        Vec3 pos = owner.position().subtract(forward.scale(2.5)).add(right.scale(lateral));
        return Vec3.atBottomCenterOf(getNavigation().nearestStableDestination(BlockPos.containing(pos), 4));
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.5, 0, -0.5, 0.5, 2, 0.5);
    }
}
