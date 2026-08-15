package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 恐慌逃跑：向远离玩家的方向随机跑。
 * 持续 RUNNING 直到到达目标或超时。
 */
public class PanicFleeAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected Vec3 fleeTarget;
    protected int tick;
    protected boolean pathStarted;
    protected static final int TIMEOUT = 60;

    public PanicFleeAction(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public void start() {
        tick = 0;
        LivingEntity attacker = mob.getLastHurtByMob();
        Player nearbyPlayer = mob.level().getNearestPlayer(
                mob.getX(), mob.getY(), mob.getZ(), 16.0,
                entity -> entity instanceof Player player && !player.isSpectator() && !player.isCreative());
        /*
         * 受伤后的攻击者比附近旁观玩家更可信。只有“玩家靠近”分支主动使用本节点时，
         * 才回退到最近玩家；着火但没有攻击者时仍应寻找普通安全点，不能原地烧死。
         */
        Vec3 threatPosition = attacker != null
                ? attacker.position()
                : nearbyPlayer != null ? nearbyPlayer.position() : null;
        fleeTarget = threatPosition != null
                ? DefaultRandomPos.getPosAway(mob, 10, 7, threatPosition)
                : mob.isOnFire()
                ? DefaultRandomPos.getPos(mob, 10, 7)
                : null;
        pathStarted = fleeTarget != null
                && mob.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, speed);
    }

    @Override
    public BTStatus execute() {
        if (!pathStarted) {
            return BTStatus.FAILURE;
        }
        tick++;
        if (tick > TIMEOUT || mob.getNavigation().isDone()) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }
}
