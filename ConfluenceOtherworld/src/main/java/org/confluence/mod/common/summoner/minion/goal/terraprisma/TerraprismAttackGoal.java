package org.confluence.mod.common.summoner.minion.goal.terraprisma;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.Ellipse;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;

import java.util.function.Function;

public class TerraprismAttackGoal extends AttachmentEntityGoal<TerraprismaMinion> {

    private boolean dash;
    private boolean resetHits;
    private int frame;
    private Vec3 start;
    private Vec3 planeNormal;
    private Vec3 lastTargetPos;
    private Vec3 lastOwnerPos;
    private Vec3 velocity = Vec3.ZERO;
    private float curvature;
    private float direction;

    public TerraprismAttackGoal(TerraprismaMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.attacking && minion.getTarget() != null;
    }

    @Override
    public void start() {
        velocity = Vec3.ZERO;
        beginAttack(true);
    }

    @Override
    public void stop() {
        minion.attacking = false;
    }

    @Override
    public void tick() {
        if (frame >= (dash ? 39 : 40) || !minion.getTargetCache().canKeep(minion.getTarget())) {
            minion.setTarget(minion.findAttackTarget(true));
            if (minion.getTarget() != null) {
                beginAttack(minion.getRandom().nextBoolean());
            } else {
                minion.attacking = false;
                minion.attackTimer = -1;
            }
        }
        if (minion.attacking) {
            planTick();
        }
    }

    private void beginAttack(boolean dash) {
        this.dash = dash;
        frame = 0;
        resetHits = true;
        start = minion.getPos();
        lastTargetPos = LibEntityUtils.aimPoint(minion.getTarget(), minion.getPos());
        lastOwnerPos = minion.getOwner().getBoundingBox().getCenter();
        Vec3 axis = lastTargetPos.subtract(start);
        if (axis.lengthSqr() < 1.0E-6) {
            axis = minion.getLookAngle();
        }
        planeNormal = Ellipse.randomPlaneNormal(minion.getRandom(), Vec3.ZERO, axis);
        curvature = (float) (0.4 + 0.1 * Math.sin(minion.getUuid().hashCode() * 2.3));
        Vec3 forward = minion.getOwner().getLookAngle();
        Vec3 right = new Vec3(forward.z, 0, -forward.x);
        direction = start.subtract(lastTargetPos).dot(right) < 0 ? -1f : 1f;
    }

    private void planTick() {
        int duration = dash ? 39 : 40;
        int startFrame = frame;
        Vec3 previousTarget = lastTargetPos;
        Vec3 previousOwner = lastOwnerPos;
        Vec3 target = LibEntityUtils.aimPoint(minion.getTarget(), minion.getPos());
        Vec3 owner = minion.getOwner().getBoundingBox().getCenter();
        PathNode from = minion.getCurrentPathNode();
        Function<Float, PathNode> curve = partialTick -> {
            float elapsed = Math.min(startFrame + partialTick * 3f, duration);
            float progress = Mth.clamp((elapsed - 1f) / (duration - 1f), 0f, 1f);
            Vec3 targetPos = previousTarget.lerp(target, partialTick);
            Vec3 ownerPos = previousOwner.lerp(owner, partialTick);
            return dash ? dashNode(progress, targetPos, from, (elapsed - startFrame) * 36f)
                    : sweepNode(progress, targetPos, ownerPos);
        };
        Vec3 correction = from.pos().subtract(curve.apply(0f).pos());
        minion.move(dash ? "terraprism_dash" : "terraprism_sweep", partialTick -> {
            PathNode node = curve.apply(partialTick);
            return node.modifyPos(node.pos().add(correction.scale(1f - partialTick)));
        }, true, resetHits,
                (1 + (duration - 1) * (dash ? 0.4f : 0.5f) - startFrame) / 3f,
                (1 + (duration - 1) * (dash ? 0.6f : 1f) - startFrame) / 3f);
        frame = Math.min(frame + 3, duration);
        minion.attackTimer = (dash ? 80 : 40) - frame;
        resetHits = false;
        lastTargetPos = target;
        lastOwnerPos = owner;
        if (!dash) {
            PathNode end = curve.apply(1f);
            velocity = Vec3.directionFromRotation(end.pitch(), end.yaw()).scale(10.0 / 16);
        }
    }

    private PathNode dashNode(float progress, Vec3 target, PathNode from, float maxRotation) {
        Vec3 lift = start.add(0, Mth.clamp(progress / 0.4f, 0f, 1f) * 100.0 / 16, 0);
        Vec3 aim = target.subtract(lift);
        double distance = aim.length();
        Vec3 tip = distance > 1.0E-6 ? aim.scale(1 / distance) : minion.getLookAngle();
        Vec3 beyond = target.add(tip.scale(Mth.clamp(distance, 60.0 / 16, 150.0 / 16)));
        Vec3 pos = progress < 0.6f
                ? lift.lerp(target, Mth.clamp((progress - 0.4f) / 0.2f, 0f, 1f))
                : target.lerp(beyond, (progress - 0.6f) / 0.4f);
        PathNode node = minion.getEulerNode(pos.add(velocity), tip, planeNormal);
        float dot = Math.abs(from.toQuaternion().dot(node.toQuaternion()));
        float angle = (float) Math.toDegrees(2 * Math.acos(Mth.clamp(dot, 0f, 1f)));
        float amount = angle > 0f ? Math.min(1f, maxRotation / angle) : 1f;
        return from.lerp(node, amount).modifyPos(node.pos());
    }

    private PathNode sweepNode(float progress, Vec3 target, Vec3 owner) {
        Vec3 pivot = progress < 0.5f ? start : target.lerp(owner, 0.5);
        Vec3 axis = target.subtract(pivot);
        double radius = axis.length() * 0.5;
        Vec3 major = radius > 1.0E-6 ? axis.scale(0.5 / radius) : minion.getLookAngle();
        Vec3 minor = planeNormal.cross(major);
        if (minor.lengthSqr() < 1.0E-6) {
            minor = new Vec3(0, 1, 0).cross(major);
            if (minor.lengthSqr() < 1.0E-6) {
                minor = new Vec3(1, 0, 0).cross(major);
            }
        }
        minor = minor.normalize();
        double angle = direction * Math.PI * (1 - 2 * progress);
        double cosine = Math.cos(angle);
        double sine = Math.sin(angle);
        Vec3 pos = pivot.lerp(target, 0.5).add(major.scale(cosine * radius))
                .add(minor.scale(sine * radius * curvature));
        Vec3 tip = major.scale(cosine).add(minor.scale(sine));
        return minion.getEulerNode(pos, tip, major.cross(minor).normalize());
    }
}
