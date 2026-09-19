package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.Ellipse;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.goal.blood_bat.BloodBatAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.blood_bat.BloodBatIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 血蝙蝠：环绕主人待机，准备完成后沿椭圆轨迹冲向目标再折返。
 */
public class BloodBatMinion extends Minion implements IEntityCollision<BloodBatMinion> {

    public boolean prep = false;
    public float progress = 0;
    public Vec3 normal = new Vec3(0, 1, 0);
    public boolean hasDamaged = false;
    public final Set<LivingEntity> hitTargets = new HashSet<>();

    public BloodBatMinion() {
        super(SummonerAttachmentEntityTypes.VAMPIRE_BAT);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new BloodBatAttackGoal(this));
        goalSelector.addGoal(1, new BloodBatIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "blood_bat", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"))));
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public LivingEntity searchTarget() {
        if (!prep) {
            return null;
        }
        return getTarget() != null ? getTarget() : super.searchTarget();
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15);
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            if (hitTargets.add(hit.entity())) {
                attack(hit.entity(), getDamage(), 4);
                if (hit.entity() == getTarget()) {
                    hasDamaged = true;
                }
            }
        }
    }

    /**
     * 待机位置：绕主人一圈（比其它仆从更低），朝向与主人视线一致。
     */
    public PathNode getInterpolatedIdleState(float partialTick) {
        int total = Math.max(1, getSameSize());
        float angle = (getOwner().tickCount + partialTick) * 0.05F + getOrder() * Mth.TWO_PI / total;
        Vec3 ownerPos = getOwner().getPosition(partialTick);
        Vec3 targetPos = ownerPos.add(Math.cos(angle) * 1.2, getOwner().getBbHeight() + 0.4, Math.sin(angle) * 1.2);
        return new PathNode(targetPos, Mth.rotLerp(partialTick, getOwner().yHeadRotO, getOwner().yHeadRot), 0, 0);
    }

    /**
     * 以待机位置与目标位置为支点计算椭圆轨迹节点（切线为视角方向，无翻滚角）。
     */
    public PathNode getEllipseNode(float progress, Vec3 idlePos, Vec3 targetPos) {
        Ellipse ellipse = new Ellipse(targetPos, idlePos, normal, 0.5F);
        Vec3 point = ellipse.getPoint(progress);
        Vec3 tangent = ellipse.getTangent(progress);
        return new PathNode(point, (float) Math.toDegrees(Math.atan2(-tangent.x, tangent.z)), (float) Math.toDegrees(Math.asin(-tangent.y)), 0);
    }
}
