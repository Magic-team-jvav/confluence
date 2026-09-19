package org.confluence.mod.common.summoner.minion.goal.terraprisma;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.Ellipse;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.PlannedPath;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;

import java.util.ArrayList;
import java.util.List;

/**
 * 泰拉棱镜的攻击状态机：直线突刺 → 椭圆斩击／沙漏刺击循环，并实时修正轨迹跟随目标。
 */
public class TerraprismAttackGoal extends AttachmentEntityGoal<TerraprismaMinion> {

    private boolean firstStrike = true;
    private boolean lastEllipse = false;
    private boolean flipRoll = false;
    private Vec3 lastTargetPos = Vec3.ZERO;

    public TerraprismAttackGoal(TerraprismaMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null && minion.attacking;
    }

    @Override
    public void start() {
        firstStrike = true;
        lastTargetPos = minion.getTarget().getBoundingBox().getCenter();
    }

    @Override
    public void tick() {
        PlannedPath currentPath = minion.getCurrentPath();
        if (currentPath != null && currentPath.getCurrentIndex() == currentPath.getNodes().size() / 3) {
            minion.hitTargets.clear();
        }
        applyPositionCorrection();
        if (minion.isTargetChange()) {
            planChainStrike();
            lastEllipse = false;
        }
        if (!minion.isExecutingPath()) {
            if (firstStrike) {
                planFirstStrike();
                firstStrike = false;
                lastEllipse = false;
            } else if (minion.getRandom().nextDouble() < 0.75 && !flipRoll) {
                planEllipseSlash();
                lastEllipse = true;
            } else {
                planHourglassSlash();
                lastEllipse = false;
                flipRoll = false;
            }
        }
    }

    /**
     * 直线突刺：从当前位置缓入缓出地穿过目标，停在目标后方 2 格。
     */
    private void planFirstStrike() {
        LivingEntity target = minion.getTarget();
        Vec3 start = minion.getPos();
        Vec3 end = target.getBoundingBox().getCenter();
        Vec3 direction = end.subtract(start);
        if (direction.lengthSqr() < 1.0E-4) {
            direction = new Vec3(0, 0, 1);
        }
        end = end.add(direction.normalize().scale(2));
        Vec3 planeNormal = direction.cross(new Vec3(0, 1, 0)).normalize();
        if (planeNormal.lengthSqr() < 1.0E-4) {
            planeNormal = new Vec3(1, 0, 0);
        }
        List<PathNode> nodes = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            float progress = (float) i / 7;
            float eased = progress < 0.5F ? 2 * progress * progress : -1 + (4 - 2 * progress) * progress;
            nodes.add(minion.getEulerNode(start.lerp(end, eased), direction, planeNormal));
        }
        minion.setPath(nodes);
    }

    /**
     * 椭圆斩击：绕目标头顶的椭圆飞行半周，25% 概率后半段翻转到镜像椭圆继续斩击。
     */
    private void planEllipseSlash() {
        Vec3 center = lastTargetPos.add(0, 3, 0);
        Vec3 startPos = minion.getPos();
        Vec3 toStart = startPos.subtract(center);
        double angle = Math.atan2(toStart.z, toStart.x);
        Vec3 attackPrepPos = center.add(Math.cos(angle) * 5, 0, Math.sin(angle) * 5);
        Vec3 currentVel = minion.getCurrentVelocity();
        Vec3 planeNormal = lastEllipse ? minion.getCurrentNormal() : Ellipse.randomPlaneNormal(minion.getRandom(), lastTargetPos, attackPrepPos);
        Ellipse ellipse = new Ellipse(lastTargetPos, attackPrepPos, planeNormal, 0.45F);
        PathNode prepNode = minion.getEulerNode(ellipse.getPoint(0), ellipse.getPoint(0).subtract(ellipse.getCenter()).normalize(), planeNormal);
        List<PathNode> nodes = new ArrayList<>();
        int prepTicks = Math.min(4, (int) startPos.distanceTo(attackPrepPos));
        for (int i = 0; i <= prepTicks; i++) {
            float progress = prepTicks == 0 ? 1 : (float) i / prepTicks;
            Vec3 point = minion.calculateBezierPoint(progress, startPos, startPos.add(currentVel), attackPrepPos);
            PathNode lerp = minion.getCurrentPathNode().lerp(prepNode, progress);
            nodes.add(new PathNode(point, lerp.yaw(), lerp.pitch(), lerp.roll()));
        }
        int attackTicks = 14 - prepTicks;
        int halfTicks = attackTicks / 2;
        for (int i = 0; i < halfTicks; i++) {
            Vec3 point = ellipse.getPoint((float) i / attackTicks);
            nodes.add(minion.getEulerNode(point, point.subtract(ellipse.getCenter()).normalize(), planeNormal));
        }
        flipRoll = minion.getRandom().nextDouble() < 0.25;
        Ellipse flipEllipse = flipRoll
                ? new Ellipse(lastTargetPos, lastTargetPos.add(lastTargetPos.x - attackPrepPos.x, attackPrepPos.y - lastTargetPos.y, lastTargetPos.z - attackPrepPos.z), planeNormal.scale(-1), 0.6F)
                : null;
        for (int i = halfTicks; i < attackTicks; i++) {
            if (flipEllipse != null) {
                Vec3 point = flipEllipse.getPoint((float) i / attackTicks);
                nodes.add(minion.getEulerNode(point, flipEllipse.getCenter().subtract(point).normalize(), planeNormal));
            } else {
                Vec3 point = ellipse.getPoint((float) i / attackTicks);
                nodes.add(minion.getEulerNode(point, point.subtract(ellipse.getCenter()).normalize(), planeNormal));
            }
        }
        minion.setPath(nodes);
    }

    /**
     * 沙漏刺击：先抬到目标头顶侧方，再快速刺穿目标。
     */
    private void planHourglassSlash() {
        LivingEntity target = minion.getTarget();
        Vec3 startPos = minion.getPos();
        Vec3 center = new Vec3(target.getX(), target.getY() + 3, target.getZ());
        Vec3 toStart = startPos.subtract(center);
        double angle = Math.atan2(toStart.z, toStart.x);
        Vec3 attackPrepPos = center.add(Math.cos(angle) * 5, 0, Math.sin(angle) * 5);
        Vec3 endPos = target.getBoundingBox().getCenter();
        Vec3 attackDir = endPos.subtract(attackPrepPos);
        if (attackDir.lengthSqr() < 1.0E-5) {
            attackDir = new Vec3(0, -1, 0);
        }
        endPos = endPos.add(attackDir.normalize().scale(3));
        Vec3 currentVel = minion.getCurrentVelocity();
        Vec3 currentTip = Vec3.directionFromRotation(minion.getPitch(), minion.getYaw()).normalize();
        Vec3 currentNormal = minion.getCurrentNormal();
        List<PathNode> nodes = new ArrayList<>();
        PathNode attackStartNode = null;
        for (int i = 0; i <= 7; i++) {
            float progress = (float) i / 7;
            float eased = progress * (2 - progress);
            Vec3 point = minion.calculateBezierPoint(eased, startPos, startPos.add(currentVel), attackPrepPos);
            PathNode pathNode = minion.getEulerNode(point, currentTip.lerp(attackDir, eased), currentNormal);
            nodes.add(pathNode);
            attackStartNode = pathNode;
        }
        PathNode attackEndNode = new PathNode(endPos, attackStartNode.yaw(), attackStartNode.pitch(), attackStartNode.roll());
        for (int i = 0; i <= 5; i++) {
            float progress = (float) i / 5;
            nodes.add(attackStartNode.lerp(attackEndNode, progress * progress));
        }
        minion.setPath(nodes);
    }

    /**
     * 连锁突刺：目标切换时从当前位置沿当前平面椭圆直接切向新目标。
     */
    private void planChainStrike() {
        Vec3 endPos = minion.getTarget().getBoundingBox().getCenter();
        Vec3 currentNormal = minion.getCurrentNormal();
        Ellipse ellipse = new Ellipse(endPos, minion.getPos(), currentNormal, 0.25F);
        List<PathNode> nodes = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Vec3 point = ellipse.getPoint((float) i / 7 * 0.5F);
            nodes.add(minion.getEulerNode(point, point.subtract(ellipse.getCenter()).normalize(), currentNormal));
        }
        minion.setPath(nodes);
    }

    /**
     * 把目标在本次规划后产生的位移按剩余进度分摊到未消费的节点上。
     */
    private void applyPositionCorrection() {
        Vec3 currentTargetCenter = minion.getTarget().getBoundingBox().getCenter();
        Vec3 correction = currentTargetCenter.subtract(lastTargetPos);
        PlannedPath path = minion.getCurrentPath();
        if (path != null && correction.lengthSqr() > 1.0E-5) {
            List<PathNode> nodes = path.getNodes();
            int startIndex = path.getCurrentIndex();
            int remaining = nodes.size() - startIndex;
            for (int i = 0; i < remaining; i++) {
                PathNode node = nodes.get(startIndex + i);
                Vec3 blended = correction.scale((float) (i + 1) / remaining);
                nodes.set(startIndex + i, new PathNode(node.pos().add(blended), node.yaw(), node.pitch(), node.roll()));
            }
        }
        lastTargetPos = currentTargetCenter;
    }
}
