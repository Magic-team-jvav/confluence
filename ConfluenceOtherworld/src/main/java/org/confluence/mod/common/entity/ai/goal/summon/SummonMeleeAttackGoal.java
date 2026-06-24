//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.confluence.mod.common.entity.ai.goal.summon;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.mod.common.api.entity.IMeleeAttackPartGoal;
import org.confluence.mod.common.api.entity.IMovablePartEntity;
import org.confluence.mod.common.api.entity.IPartEntityTargetable;
import org.confluence.mod.common.api.entity.ISummonMob;

import java.util.EnumSet;

public class SummonMeleeAttackGoal<T extends Mob & ISummonMob> extends Goal implements IMeleeAttackPartGoal {
    protected final T mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval = 20;
    private long lastCanUseCheck;
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public SummonMeleeAttackGoal(T mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;

            // 检查是否有 PartEntity 实际目标
            Entity actualTarget = null;
            if (this.mob instanceof IPartEntityTargetable targetable) {
                actualTarget = targetable.getActualTargetEntity();
            }

            // 如果有 PartEntity 目标，使用它；否则使用 getTarget()
            Entity targetEntity = actualTarget != null ? actualTarget : this.mob.getTarget();

            if (targetEntity == null) {
                return false;
            } else if (!targetEntity.isAlive()) {
                return false;
            } else if (this.canPenalize) {
                if (--this.ticksUntilNextPathRecalculation <= 0) {
                    // 对于 PartEntity，使用其位置创建路径
                    if (targetEntity instanceof PartEntity<?> partEntity && partEntity instanceof IMovablePartEntity movablePart) {
                        Vec3 pos = movablePart.getMoveTargetPosition();
                        this.path = this.mob.getNavigation().createPath(pos.x, pos.y, pos.z, 0);
                    } else if (targetEntity instanceof LivingEntity living) {
                        this.path = this.mob.getNavigation().createPath(living, 0);
                    } else {
                        return false;
                    }
                    this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                    return this.path != null;
                } else {
                    return true;
                }
            } else {
                // 对于 PartEntity，使用其位置创建路径
                if (targetEntity instanceof PartEntity<?> partEntity && partEntity instanceof IMovablePartEntity movablePart) {
                    Vec3 pos = movablePart.getMoveTargetPosition();
                    this.path = this.mob.getNavigation().createPath(pos.x, pos.y, pos.z, 0);
                    return this.path != null;
                } else if (targetEntity instanceof LivingEntity living) {
                    this.path = this.mob.getNavigation().createPath(living, 0);
                    return this.path != null || this.mob.isWithinMeleeAttackRange(living);
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        // 检查是否有 PartEntity 实际目标
        Entity actualTarget = null;
        if (this.mob instanceof IPartEntityTargetable targetable) {
            actualTarget = targetable.getActualTargetEntity();
        }

        // 如果有 PartEntity 目标，使用它；否则使用 getTarget()
        Entity targetEntity = actualTarget != null ? actualTarget : this.mob.getTarget();

        if (targetEntity == null) {
            return false;
        } else if(mob.summon_shouldTryTeleportToOwner()){
            return false;
        } else if (!targetEntity.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else {
            // 对于 PartEntity，使用其位置检查限制
            if (targetEntity instanceof PartEntity<?> partEntity && partEntity instanceof IMovablePartEntity movablePart) {
                Vec3 pos = movablePart.getMoveTargetPosition();
                return this.mob.isWithinRestriction(net.minecraft.core.BlockPos.containing(pos));
            } else if (targetEntity instanceof LivingEntity living) {
                return this.mob.isWithinRestriction(living.blockPosition()) && (!(living instanceof Player) || !living.isSpectator() && !((Player) living).isCreative());
            } else {
                return false;
            }
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingentity = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            this.mob.setTarget(null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // 检查是否有 PartEntity 实际目标
        Entity actualTargetEntity = null;
        if (this.mob instanceof IPartEntityTargetable targetable) {
            var actualTarget = targetable.getActualTargetEntity();
            if (actualTarget != null) {
                actualTargetEntity = actualTarget;
            }
        }

        // 如果有 PartEntity 目标，使用它；否则使用 getTarget()
        Entity targetEntity = actualTargetEntity != null ? actualTargetEntity : this.mob.getTarget();

        if (targetEntity != null) {
            // 使用接口方法获取的位置进行路径计算
            Vec3 targetPos = this.getTargetPosition(this.mob, targetEntity);

            this.mob.getLookControl().setLookAt(targetEntity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);

            double targetX = targetPos != null ? targetPos.x : targetEntity.getX();
            double targetY = targetPos != null ? targetPos.y : targetEntity.getY();
            double targetZ = targetPos != null ? targetPos.z : targetEntity.getZ();

            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(targetEntity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || targetEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = targetX;
                this.pathedTargetY = targetY;
                this.pathedTargetZ = targetZ;
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                double d0 = this.mob.distanceToSqr(targetEntity);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += this.failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && targetEntity.distanceToSqr((double)finalPathPoint.x, (double)finalPathPoint.y, (double)finalPathPoint.z) < 1.0) {
                            this.failedPathFindingPenalty = 0;
                        } else {
                            this.failedPathFindingPenalty += 10;
                        }
                    } else {
                        this.failedPathFindingPenalty += 10;
                    }
                }

                if (d0 > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                // 使用接口方法获取的位置进行移动
                if (targetPos != null) {
                    if (!this.mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, this.speedModifier)) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }
                } else if (targetEntity instanceof LivingEntity living) {
                    if (!this.mob.getNavigation().moveTo(living, this.speedModifier)) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }
                } else {
                    if (!this.mob.getNavigation().moveTo(targetX, targetY, targetZ, this.speedModifier)) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(targetEntity);
        }

    }

    protected void checkAndPerformAttack(Entity target) {
        if (this.canMeleeAttackTarget(target)) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(target);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity entity) {
        return this.isTimeToAttack() && this.mob.isWithinMeleeAttackRange(entity) && this.mob.getSensing().hasLineOfSight(entity);
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(20);
    }

    @Override
    public boolean canMeleeAttackTarget(Entity target) {
        if(target instanceof PartEntity<?> partEntity){
            return this.isTimeToAttack() && this.mob.getAttackBoundingBox().intersects(partEntity.getBoundingBox()) && this.mob.getSensing().hasLineOfSight(partEntity);
        }else if(target instanceof LivingEntity living) {
            return this.canPerformAttack(living);
        }
        return false;
    }
}
