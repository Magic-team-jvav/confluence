package org.confluence.mod.util.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.api.entity.IHeightControlMob;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.EnumSet;

public class ComeAndBackDashAttackGoal<T extends Mob & IHeightControlMob> extends Goal {
    protected boolean randomDirection;
    protected final T worm;
    private final float _distanceToTurn;
    int soundTick = 0;
    int soundInternal = 50;
    protected boolean turning = false;
    float speedModifier;

    public ComeAndBackDashAttackGoal(T worm, float distanceToTurn, float speedModifier) {
        this.worm = worm;
        this._distanceToTurn = distanceToTurn * distanceToTurn;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.speedModifier = speedModifier;
    }

    public ComeAndBackDashAttackGoal(T worm, float distanceToTurn) {
        this(worm, distanceToTurn, 1.0f);
    }

    @Override
    public boolean canUse() {
        return worm.getTarget() != null && worm.isAttackableHeight((float) worm.getY());
    }

    public boolean canContinueToUse() {
        return this.canUse() && worm.getTarget() != null && worm.getTarget().isAlive();
    }

    public void tick() {
        LivingEntity target = worm.getTarget();
        if (target == null) return;
        double distance = worm.distanceToSqr(target);
        double angle = LibMathUtils.angleBetween(worm.getLookAngle(), target.position().subtract(worm.position()));

        if (distance > _distanceToTurn) {
            // 转向
            worm.getLookControl().setLookAt(target);
            worm.lookAt(target, 5, 30);
            this.turning = true;
            if (angle > Math.PI / 6.0D) {
                worm.setDeltaMovement(this.getBackTurnMovement(distance));
                return;
            }
        }
        // 攻击
        if (angle < Math.PI / 2) {
            if (distance < 400) {
                soundInternal = (int) (Math.pow(distance, 0.3f) * 0.5f);
                if (++soundTick >= soundInternal) {
                    if (worm.isInWall())
                        worm.playSound(ModSoundEvents.DIG_SOUND.get(), 2f, 1f);
                    soundTick = 0;
                }
            }
            this.turning = false;
            this.attackLookAt(target);
        }

        worm.setDeltaMovement(this.getAttackMovement(target, distance));

        randomDirection = !randomDirection;

    }

    protected void attackLookAt(LivingEntity target) {
        worm.getLookControl().setLookAt(target);
        worm.lookAt(target, 2f, 30f);
    }

    protected Vec3 getBackTurnMovement(double distance) {
        return worm.getLookAngle().normalize().scale(0.4f).add(0, 0.1f * (randomDirection ? 1 : -1), 0);
    }

    protected Vec3 getAttackMovement(LivingEntity target, double distance) {
        return worm.getLookAngle().normalize().scale(0.4f * this.speedModifier);
    }
}
