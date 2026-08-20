package org.confluence.mod.common.summon.terraprisma;

import org.confluence.mod.common.summon.SummonPose;

/// 泰拉棱镜的旋转技能。
final class TerraprismaRotateGoal extends TerraprismaSkillGoal {
    static final int DURATION = 10;
    static final int BASE_COOLDOWN = 80;

    TerraprismaRotateGoal(TerraprismaSummon summon) {
        super(summon, DURATION, BASE_COOLDOWN);
    }

    @Override
    public void start() {
        super.start();
        summon.beginRotateAnimation();
    }

    @Override
    public void tick() {
        summon.moveTo(new SummonPose(summon.position().add(summon.velocity().scale(0.91)), summon.currentPose().yaw(), summon.currentPose().pitch(), summon.currentPose().roll()));
        elapsedTicks++;
    }

    @Override
    public void stop() {
        super.stop();
        summon.finishRotateAnimation();
    }
}
