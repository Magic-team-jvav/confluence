package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.finch.FinchAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.finch.FinchIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FinchMinion extends MomentumMinion implements IEntityCollision<FinchMinion> {

    public float idleBlend;
    public float idleBlendO;

    public FinchMinion() {
        super(SummonerAttachmentEntityTypes.FINCH);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.FLOAT, () -> idleBlendO, value -> idleBlendO = value);
        fields.field(LyraStreamCodecs.FLOAT, () -> idleBlend, value -> idleBlend = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new FinchAttackGoal(this));
        goalSelector.addGoal(1, new FinchIdleGoal(this));
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.2);
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 2);
        }
    }

    @Override
    public void tick() {
        super.tick();
        idleBlendO = idleBlend;
        if (getTarget() != null) {
            idleBlend = Math.max(0, idleBlend - 0.1F);
        } else {
            double distance = getPos().distanceTo(getInterpolatedIdleState(1).pos());
            if (distance < 0.1) {
                idleBlend = Math.min(1, idleBlend + 0.05F);
            } else {
                if (idleBlend != 1) {
                    idleBlend = Math.max(0, idleBlend - 0.1F);
                }
            }
        }
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        PathNode base = super.getRenderNode(partialTick);
        PathNode idle = getInterpolatedIdleState(partialTick);
        return base.lerp(idle, Mth.lerp(partialTick, idleBlendO, idleBlend));
    }

    /**
     * 计算当前次序对应肩头的渲染目标。
     */
    public PathNode getInterpolatedIdleState(float partialTick) {
        float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        Vec3 forward = Vec3.directionFromRotation(0, bodyYaw);
        Vec3 right = forward.cross(new Vec3(0, 1, 0));
        right = right.lengthSqr() < 1.0E-8 ? new Vec3(1, 0, 0) : right.normalize();
        Vec3 shoulder = (getOrder() & 1) == 0 ? right : right.scale(-1);
        double sideOffset = owner.getBbWidth() * 0.55;
        double backOffset = owner.getBbWidth() * 0.13;
        Vec3 position = owner.getPosition(partialTick)
                .add(0, owner.getBbHeight() * 0.83 + ((getOrder() - (getOrder() % 2 == 0 ? 0 : 1)) * 0.08), 0)
                .add(shoulder.scale(sideOffset))
                .subtract(forward.scale(backOffset));
        return new PathNode(position, bodyYaw, 0, 0);
    }
}
