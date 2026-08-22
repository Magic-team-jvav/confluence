package org.confluence.mod.common.summon.sword;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;

/// 召唤剑的斜劈技能。
final class SwordSlashGoal extends SummonGoal<SummonSword> {
    private static final int DURATION = 10;
    private static final int BASE_COOLDOWN = 150;
    private int cooldown;
    private int slashTicks;
    private boolean triggered;

    SwordSlashGoal(SummonSword summon) {
        super(summon);
    }

    @Override
    public boolean canUse() {
        return summon.tickCount() > 0 && cooldown == 0 && summon.hasValidTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return summon.hasValidTarget() && slashTicks < DURATION;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void start() {
        slashTicks = 0;
        triggered = false;
        summon.setDamageMultiplier(1.3F);
    }

    @Override
    public void tick() {
        Vec3 targetPosition = summon.targetPosition();
        Vec3 distance = targetPosition.subtract(summon.position());
        if (distance.length() > 3.0 && !triggered) {
            Vec3 movement = distance.normalize().scale(0.5);
            summon.moveTo(new SummonPose(summon.position().add(movement), summon.currentPose().yaw(), summon.currentPose().pitch(), summon.currentPose().roll()));
            return;
        }
        Vec3 lookPosition = targetPosition.add(0.0, 10.0 - slashTicks, 0.0);
        Vec3 direction = lookPosition.subtract(summon.eyePosition()).normalize();
        SummonPose aimed = summon.aimAt(summon.position(), direction);
        triggered = true;
        slashTicks++;
        summon.moveTo(new SummonPose(summon.position().add(summon.velocity().scale(0.7)), aimed.yaw(), aimed.pitch(), aimed.roll()));
    }

    @Override
    public void stop() {
        slashTicks = 0;
        triggered = false;
        summon.setDamageMultiplier(1.0F);
        if (summon.owner().getRandom().nextFloat() < 0.5F) {
            summon.beginPostSlashSpin();
        }
        cooldown = BASE_COOLDOWN + summon.owner().getRandom().nextInt((int) (BASE_COOLDOWN * 0.3F));
    }

    void updateCooldown() {
        cooldown = Math.max(0, cooldown - 1);
    }

    boolean isSlashing() {
        return triggered;
    }

    int slashTicks() {
        return slashTicks;
    }
}
