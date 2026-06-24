package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.mod.common.api.entity.IMovablePartEntity;
import org.confluence.mod.common.api.entity.IPartEntityTargetable;
import org.confluence.mod.common.entity.ai.motion.DashComponent;

import java.util.EnumSet;

public class FlyRangeAttackGoal<T extends Mob & RangedAttackMob> extends Goal {

    T mob;
    DashComponent component;

    int cooldown;
    int _cooldown;

    int replaceCooldown;
    int _replaceCooldown;

    float hangOnDistance;
    float hangOnHeight;
    float replaceDistance;

    public FlyRangeAttackGoal(T mob, int cooldown, int replaceCooldown) {
        this(mob, cooldown, replaceCooldown, 5, 3, 5);
    }

    /**
     *
     * @param cooldown 攻击间隔
     * @param replaceCooldown 寻路间隔
     * @param hangOnDistance 悬挂目标的水平距离
     * @param hangOnHeight 悬挂目标的高度
     * @param replaceDistance 寻路阈值，超过这个距离就靠近目标
     */
    public FlyRangeAttackGoal(T mob, int cooldown, int replaceCooldown, float hangOnDistance, float hangOnHeight, float replaceDistance) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.component = new DashComponent(mob);
        this._cooldown = cooldown;
        this._replaceCooldown = replaceCooldown;

        this.hangOnDistance = hangOnDistance;
        this.hangOnHeight = hangOnHeight;
        this.replaceDistance = replaceDistance;
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null;
    }

    @Override
    public void start() {
        mob.setAggressive(true);
    }

    @Override
    public void stop() {
        mob.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return;
        }
        shootTick(target);
        moveTick(target);
    }


    protected void shootTick(LivingEntity target) {
        // 检查是否有 PartEntity 实际目标且实现了 IMovablePartEntity
        Entity shootTargetEntity = target;
        if (mob instanceof IPartEntityTargetable targetable) {
            var actualTarget = targetable.getActualTargetEntity();
            if (actualTarget instanceof PartEntity<?> partEntity &&
                partEntity instanceof IMovablePartEntity movablePart &&
                movablePart.shouldMoveToPart()) {
                // 使用 PartEntity 作为瞄准目标
                shootTargetEntity = partEntity;
            }
        }

        mob.lookAt(shootTargetEntity, 15, 85);
        mob.getLookControl().setLookAt(shootTargetEntity);

        if (--cooldown <= 0) {
            this.cooldown = _cooldown;
        } else {
            return;
        }

        mob.performRangedAttack(target, 1.0F);

    }

    protected void moveTick(LivingEntity target) {
        // 检查是否有 PartEntity 实际目标且实现了 IMovablePartEntity
        Entity moveTargetEntity = target;
        if (mob instanceof IPartEntityTargetable targetable) {
            var actualTarget = targetable.getActualTargetEntity();
            if (actualTarget instanceof PartEntity<?> partEntity &&
                partEntity instanceof IMovablePartEntity movablePart &&
                movablePart.shouldMoveToPart()) {
                // 使用 PartEntity 作为移动目标
                moveTargetEntity = partEntity;
            }
        }

        // 使用 PartEntity 或父实体的位置
        Vec3 targetPos = component.setNearestTargetPos(moveTargetEntity, hangOnDistance, hangOnHeight);
        float distance = (float) targetPos.distanceTo(mob.position());

        if (distance > replaceDistance) {
            if (--replaceCooldown <= 0) {
                mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.5F);
                replaceCooldown = 20;
            }
        } else {
            if (mob.getRandom().nextFloat() < 0.5f) {
                component.setDirection(targetPos.subtract(mob.position()));
                component.accelerate(0.03f);
            }
        }
    }
}
