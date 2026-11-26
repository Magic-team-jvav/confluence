package org.confluence.mod.common.summoner.minion;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.attachment.TargetCache;
import org.confluence.mod.common.init.ModAttachmentEntityTypes;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.worm.WormAttachmentEntity;
import org.confluence.mod.common.summoner.projectile.ProbeLaser;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MiniProbeMinion extends MomentumMinion {
    private Optional<UUID> bodyId = Optional.empty();
    private boolean docked = true;
    private long nextShotTick;

    public MiniProbeMinion() {
        super(ModAttachmentEntityTypes.MINI_PROBE);
        setGravity(0);
        setSlotCost(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.OPTIONAL_UUID, () -> bodyId, value -> bodyId = value);
        fields.field(ByteBufCodecs.BOOL, () -> docked, value -> docked = value);
    }

    public void attachTo(MiniDestroyerMinion body) {
        bodyId = Optional.of(body.getUuid());
        setOwner(body.getOwner());
        init(dockNode(body.getCurrentPathNode()));
    }

    @Nullable
    private MiniDestroyerMinion getBody() {
        if (bodyId.isPresent()) {
            List<AttachmentEntity> segments = owner.getData(ModAttachmentTypes.SUMMONER_ATTACHMENT_ENTITY_DATA).getGroups()
                    .getOrDefault(ModAttachmentEntityTypes.MINI_DESTROYER.get(), List.of());
            for (AttachmentEntity segment : segments) {
                if (segment instanceof MiniDestroyerMinion body && body.getUuid().equals(bodyId.get()) && !body.isRemove() && body.getNextSegment() != null) {
                    return body;
                }
            }
        }
        return null;
    }

    private PathNode dockNode(PathNode bodyNode) {
        Vector3f offset = bodyNode.toQuaternion().transform(new Vector3f(0, 3f / 16, -0.5f / 16));
        return bodyNode.modifyPos(bodyNode.pos().add(offset.x, offset.y, offset.z));
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && getBody() != null;
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public LivingEntity searchTarget() {
        return docked ? null : searchTargetAt(getPos());
    }

    private void shoot(Vec3 origin, LivingEntity target, long tick) {
        nextShotTick = tick + getRandom().nextIntBetweenInclusive(20, 30);
        Vec3 direction = target.getBoundingBox().getCenter().subtract(origin).normalize();
        ProbeLaser laser = new ProbeLaser();
        laser.setOwner(getOwner());
        laser.init(new PathNode(origin.add(direction), direction));
        laser.copyAttributes(this);
        laser.setVelocity(direction);
        laser.setDamage(getDamage() * 1.1f);
        SummonerHelper.get(getOwner()).add(laser);
        getLevel().playSound(null, origin.x, origin.y, origin.z, ModSoundEvents.ITEM_LASER_SHOOT.get(), getSoundSource(), 1, 1);
    }

    private void dockAt(PathNode anchor) {
        docked = true;
        setPlannedPath(null);
        setCurrentPathNode(anchor);
        setDesiredRotation(anchor.yaw(), anchor.pitch(), anchor.roll());
        setVelocity(Vec3.ZERO);
    }

    @Override
    public void tickPhysics() {
        MiniDestroyerMinion body = getBody();
        if (body != null) {
            PathNode anchor = dockNode(body.getCurrentPathNode());
            LivingEntity target = getTarget();
            long tick = getLevel().getGameTime();
            if (docked) {
                setCurrentPathNode(anchor);
                if (tick >= nextShotTick) {
                    WormAttachmentEntity head = body.getHead();
                    LivingEntity headTarget = head != null ? head.getTarget() : null;
                    if (headTarget != null && headTarget.isAlive()) {
                        shoot(anchor.pos(), headTarget, tick);
                    }
                }
                if (getRandom().nextFloat() < 0.005f) {
                    LivingEntity detachTarget = searchTargetAt(anchor.pos());
                    if (detachTarget != null) {
                        docked = false;
                        setTarget(detachTarget);
                        setDesiredRotation(anchor.yaw(), anchor.pitch(), anchor.roll());
                        setVelocity(body.getCurrentNormal().scale(0.4));
                        super.tickPhysics();
                    }
                }
            } else if (target != null) {
                Vec3 targetPos = target.getBoundingBox().getCenter();
                Vec3 toTarget = targetPos.subtract(getPos());
                double acceleration = Math.clamp((toTarget.length() - 5) * 0.02, -0.05, 0.05);
                addVelocity(toTarget.normalize().scale(acceleration));
                lookAtPos(targetPos);
                if (tick >= nextShotTick) {
                    shoot(getPos(), target, tick);
                }
                super.tickPhysics();
            } else {
                Vec3 toAnchor = anchor.pos().subtract(getPos());
                double distance = toAnchor.length();
                if (distance > 0.35) {
                    setVelocity(toAnchor.normalize().scale(Math.min(distance, 2.5) / getDrag()));
                    lookAtPos(anchor.pos());
                    super.tickPhysics();
                }
                if (getPos().distanceToSqr(anchor.pos()) <= 0.35 * 0.35) {
                    dockAt(anchor);
                }
            }
        }
    }

    private LivingEntity searchTargetAt(Vec3 center) {
        TargetCache cache = getTargetCache();
        List<LivingEntity> targets = cache.getEntitiesInRadius(center, getSearchDistance(), cache::canAcquire);
        return targets.isEmpty() ? null : cache.getNewTarget(this, targets, 0, true);
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        MiniDestroyerMinion body = docked ? getBody() : null;
        return body != null ? dockNode(body.getRenderNode(partialTick)) : super.getRenderNode(partialTick);
    }
}
