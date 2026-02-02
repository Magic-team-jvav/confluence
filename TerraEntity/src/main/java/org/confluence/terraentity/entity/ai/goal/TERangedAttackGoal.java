
package org.confluence.terraentity.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.utils.TEUtils;

import java.util.EnumSet;

/**
 * 不同于原版的远程攻击ai，怪物可以根据情况选择前进和后退
 * @param <T>
 */
public class TERangedAttackGoal<T extends PathfinderMob & RangedAttackMob> extends Goal {
    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime;
    private int seeTime;

    /**
     * @param attackIntervalMin 攻击冷却时间
     * @param attackRadius 攻击半径
     */
    public TERangedAttackGoal(T mob, double speedModifier, int attackIntervalMin, float attackRadius) {
        this.attackTime = -1;
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void setMinAttackInterval(int attackCooldown) {
        this.attackIntervalMin = attackCooldown;
    }

    public boolean canUse() {
        return this.mob.getTarget() != null && this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.mob.isHolding((is) -> {
            return is.getItem() instanceof BowItem;
        });
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingBow();
    }

    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.stopUsingItem();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            double d0 = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(target);
            if (flag) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }

            if (d0 <= (double)this.attackRadiusSqr && this.seeTime >= 5) {
//                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
            }
            double angle = TEUtils.angleBetween(this.mob.getLookAngle(), target.position().subtract(this.mob.position()));
            if(this.mob.getNavigation().isDone() || angle < 0.85){
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }


            if (this.mob.isUsingItem()) {
                // 正在使用武器
                this.mob.lookAt(target, 30.0F, 30.0F);
                this.mob.getLookControl().setLookAt(target);
                if (!flag && this.seeTime < -60) {
                    this.mob.stopUsingItem();
                } else if (flag) {
                    int i = this.mob.getTicksUsingItem();
                    if (i >= 20) {

                        if(angle < 0.1) {
                            this.mob.stopUsingItem();
                            this.mob.performRangedAttack(target, BowItem.getPowerForTime(i));
                            this.attackTime = this.attackIntervalMin;
                            this.retrateOrFollow(5F, 7F, (float) d0, target);
                        }
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                // 触发攻击
                this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, (item) -> {
                    return item instanceof BowItem;
                }));
            }else{
                // 逃跑或者跟随
//                this.retrateOrFollow(5F, 7F, (float) d0, target);

            }
        }

    }

    protected void retrateOrFollow(float minRange, float maxRange, float distance, LivingEntity target){
//        if(!this.mob.getNavigation().isDone()){
//            return;
//        }
        if(distance < minRange * minRange){
            Vec3 targetPos = LandRandomPos.getPosAway(this.mob,  (int)minRange ,5 ,  target.position());
            if(targetPos != null){
                this.mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, this.speedModifier * 1.2f);
            }
        } else if (distance > maxRange * maxRange) {
            Vec3 targetPos = LandRandomPos.getPosTowards(this.mob,(int)maxRange, 5 ,  target.position());
            if(targetPos != null){
                this.mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, this.speedModifier);
            }
        }else{
            Vec3 targetPos = LandRandomPos.getPos(this.mob, 3,  2);
            if(targetPos != null){
                this.mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, this.speedModifier * 0.9F);
            }
        }
    }
}
