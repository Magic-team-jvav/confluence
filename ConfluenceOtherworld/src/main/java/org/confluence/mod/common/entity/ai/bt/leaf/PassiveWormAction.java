package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

// 控制蠕虫交替爬行和停顿，遇到障碍时转向。
public final class PassiveWormAction extends BTNode {
    private final PathfinderMob mob;
    private Vec3 direction = Vec3.ZERO;
    private boolean crawling;
    private int phaseTicks;
    private int turnCooldown;

    public PassiveWormAction(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0);
        mob.setSpeed(0.0F);
        mob.setXxa(0.0F);
        mob.setYya(0.0F);
        mob.setZza(0.0F);
        double angle = mob.getRandom().nextDouble() * Math.PI * 2.0;
        direction = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
        crawling = false;
        phaseTicks = 100 + mob.getRandom().nextInt(200);
        turnCooldown = 0;
    }

    @Override
    public BTStatus execute() {
        if (!mob.onGround() || mob.isInWaterOrBubble()) return BTStatus.RUNNING;
        if (--phaseTicks <= 0) {
            crawling = !crawling;
            phaseTicks = crawling ? 200 + mob.getRandom().nextInt(400)
                    : 100 + mob.getRandom().nextInt(200);
        }
        if (turnCooldown > 0) turnCooldown--;
        if (crawling && mob.horizontalCollision && turnCooldown == 0) {
            direction = direction.scale(-1.0);
            turnCooldown = 10;
        }
        double speed = crawling ? mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.18 : 0.0;
        mob.setDeltaMovement(direction.x * speed, mob.getDeltaMovement().y, direction.z * speed);
        float yaw = (float) (Mth.atan2(-direction.x, direction.z) * Mth.RAD_TO_DEG);
        LibEntityUtils.setEntityYaw(mob, yaw);
        mob.setXRot(0.0F);
        mob.getLookControl().setLookAt(mob.getX() + direction.x, mob.getEyeY(), mob.getZ() + direction.z);
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        Vec3 movement = mob.getDeltaMovement();
        mob.setDeltaMovement(0.0, movement.y, 0.0);
        mob.getNavigation().stop();
        phaseTicks = 0;
    }
}
