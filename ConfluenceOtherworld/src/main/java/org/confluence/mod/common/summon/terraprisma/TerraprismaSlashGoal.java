package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonPose;

/// 控制泰拉棱镜移动到目标处并向下斜劈。
final class TerraprismaSlashGoal extends TerraprismaSkillGoal {
    static final int BASE_COOLDOWN = 120;
    private boolean triggered;

    TerraprismaSlashGoal(TerraprismaSummon summon) {
        super(summon, 10, BASE_COOLDOWN);
    }

    @Override
    public void start() {
        super.start();
        triggered = false;
        summon.beginSlashAnimation();
    }

    @Override
    public void tick() {
        Vec3 baseTargetPosition = summon.targetPosition();
        Vec3 distance = baseTargetPosition.subtract(summon.position());
        if (distance.length() > 3.0 && !triggered) {
            summon.moveTo(new SummonPose(summon.position().add(distance.normalize().scale(0.5)), summon.currentPose().yaw(), summon.currentPose().pitch(), summon.currentPose().roll()));
            return;
        }
        Vec3 targetPosition = baseTargetPosition.add(0.0, 10.0 - elapsedTicks, 0.0);
        triggered = true;
        elapsedTicks++;
        summon.moveAndLook(summon.velocity().scale(0.637), targetPosition);
    }

    @Override
    public void stop() {
        super.stop();
        triggered = false;
        summon.finishSlashAnimation();
    }
}
