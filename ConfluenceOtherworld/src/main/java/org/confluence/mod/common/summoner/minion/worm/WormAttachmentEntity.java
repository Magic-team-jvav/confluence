package org.confluence.mod.common.summoner.minion.worm;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.minion.MomentumMinion;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class WormAttachmentEntity extends MomentumMinion implements IEntityCollision<WormAttachmentEntity> {
    private float spiralPhase;

    protected WormAttachmentEntity(Holder<AttachmentEntityType<? extends AttachmentEntity>> type) {
        super(type);
        setGravity(0);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(1, new WormIdleGoal(this));
        goalSelector.addGoal(2, new WormFollowGoal(this));
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public LivingEntity searchTarget() {
        if (isHead()) {
            TargetCache cache = getTargetCache();
            List<LivingEntity> targets = cache.getEntitiesInRadius(owner.getBoundingBox().getCenter(), getSearchDistance(), living -> cache.isVisibility(owner, living) && cache.canAcquire(living));
            if (!targets.isEmpty()) {
                return cache.getNewTarget(this, targets, 0, true);
            }
        }
        return null;
    }

    @Override
    public void onRemove() {
        if (isHead()) {
            owner.getData(SummonerAttachmentTypes.ENTITY_DATA).remove(getType());
        }
    }

    public boolean isHead() {
        return getOrder() == 0;
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && (isHead() || getPrecedingSegment() != null);
    }

    @Nullable
    public WormAttachmentEntity getHead() {
        return getSegment(0);
    }

    @Nullable
    public WormAttachmentEntity getPrecedingSegment() {
        return isHead() ? null : getSegment(getOrder() - 1);
    }

    @Nullable
    public WormAttachmentEntity getNextSegment() {
        return getSegment(getOrder() + 1);
    }

    @Nullable
    private WormAttachmentEntity getSegment(int index) {
        List<AttachmentEntity> segments = owner.getData(SummonerAttachmentTypes.ENTITY_DATA).getGroups().get(getType());
        if (segments != null && index < segments.size() && segments.get(index) instanceof WormAttachmentEntity segment && !segment.isRemove()) {
            return segment;
        }
        return null;
    }

    @Override
    public @NotNull AABB getHitbox() {
        int segment = getOrder();
        int total = Math.max(1, getSameSize());
        double minZ = segment == 0 ? -0.3 : segment == total - 1 ? -1.45 : -0.35;
        double maxZ = segment == 0 ? 0.65 : segment == total - 1 ? 0.3 : 0.35;
        return new AABB(-0.25, -0.25, minZ, 0.25, 0.25, maxZ);
    }

    @Override
    public boolean canCollideAttack() {
        WormAttachmentEntity head = getHead();
        return !isRemove() && head != null && head.getTarget() != null;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        WormAttachmentEntity head = getHead();
        if (head != null) {
            for (HitContext hit : hitContexts) {
                head.attack(hit.entity(), getDamage(), 2);
            }
        }
    }

    @Override
    public float getDamage() {
        return super.getDamage() * (1 + 0.23f * (getSameSize() - 1));
    }

    public double getSegmentDistance() {
        return 0.65;
    }

    public void orbitToward(Vec3 targetPos, float maxTurnDegrees, double acceleration, boolean spiral) {
        Vec3 velocity = getVelocity();
        Vec3 toTarget = targetPos.subtract(getPos());
        double distance = toTarget.length();
        spiralPhase += 0.2f * (float) velocity.length();
        Vec3 forward = getCurrentVelocity();
        WormAttachmentEntity next = getNextSegment();
        double maxTurn = next == null ? Math.toRadians(12) : Math.max(Math.toRadians(2),
                Math.toRadians(maxTurnDegrees) - Math.acos(Mth.clamp(forward.dot(next.getCurrentVelocity()), -1, 1)));
        if (distance > 0.01) {
            Vec3 desired = toTarget.scale(1 / distance);
            double cosTurn = Math.cos(maxTurn);
            if (forward.dot(desired) < cosTurn) {
                Vec3 axis = forward.cross(desired);
                if (axis.lengthSqr() < 0.0001) {
                    axis = forward.cross(new Vec3(0, 1, 0));
                }
                if (axis.lengthSqr() < 0.0001) {
                    axis = forward.cross(new Vec3(1, 0, 0));
                }
                forward = forward.scale(cosTurn).add(axis.normalize().cross(forward).scale(Math.sin(maxTurn)));
            } else {
                forward = desired;
            }
        }
        if (spiral) {
            double amplitude = Math.clamp(distance * 0.08, 0.15, 0.4);
            Vec3 right = forward.cross(new Vec3(0, 1, 0));
            if (right.lengthSqr() < 0.001) {
                right = new Vec3(1, 0, 0);
            } else {
                right = right.normalize();
            }
            Vec3 up = right.cross(forward).normalize();
            forward = forward.add(right.scale(Math.cos(spiralPhase) * amplitude))
                    .add(up.scale(Math.sin(spiralPhase) * amplitude)).normalize();
        }
        addVelocity(forward.scale(acceleration));
        lookAtDirection(getVelocity());
    }

    public void orbitToward(Vec3 targetPos, float maxTurnDegrees, double acceleration) {
        orbitToward(targetPos, maxTurnDegrees, acceleration, true);
    }
}
