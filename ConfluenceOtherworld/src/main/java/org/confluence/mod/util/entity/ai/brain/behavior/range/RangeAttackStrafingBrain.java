package org.confluence.mod.util.entity.ai.brain.behavior.range;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

/**
 * from {@link com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidAttackStrafingTask}
 */
public class RangeAttackStrafingBrain extends Behavior<PathfinderMob> {
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    float attackRange;


    public RangeAttackStrafingBrain(float attackRange) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                        MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT),
                1200);
        this.attackRange = attackRange;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob owner) {
        return
//                owner.getMainHandItem().getItem() instanceof ProjectileWeaponItem &&
               owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                       .filter(e->e.isAlive() && owner.distanceTo(e) < 20)
                       .isPresent();
    }

    @Override
    protected void tick(ServerLevel level, PathfinderMob owner, long gameTime) {
//        ItemStack stack = owner.getMainHandItem();
//        if (!(stack.getItem() instanceof ProjectileWeaponItem)) {
//            return;
//        }
        owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> {
//            int maxAttackDistance = ((ProjectileWeaponItem) stack.getItem()).getDefaultProjectileRange();
            int maxAttackDistance = 32;
            double distance = owner.distanceTo(target);

            // 如果在最大攻击距离之内，而且看见的时长足够长
            if (distance < attackRange) {
                ++this.strafingTime;
            } else {
                this.strafingTime = -1;
            }

            // 如果攻击时间也足够长，随机对走位方向和前后走位进行反转
            if (this.strafingTime >= 20) {
                if (owner.getRandom().nextFloat() < 0.3) {
                    this.strafingClockwise = !this.strafingClockwise;
                }
                if (owner.getRandom().nextFloat() < 0.3) {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }

            // 如果攻击时间大于 -1
            if (this.strafingTime > -1) {
                // 依据距离远近决定是否前后走位
                if (distance > maxAttackDistance * 0.5) {
                    this.strafingBackwards = false;
                } else if (distance < maxAttackDistance * 0.2) {
                    this.strafingBackwards = true;
                }

                // 应用走位
                owner.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                owner.setYRot(Mth.rotateIfNecessary(owner.getYRot(), owner.yHeadRot, 0.0F));
                BehaviorUtils.lookAtEntity(owner, target);
            } else {
                // 否则只朝向攻击目标
                BehaviorUtils.lookAtEntity(owner, target);
            }
        });
    }

    @Override
    protected void start(ServerLevel level, PathfinderMob entity, long gameTimeIn) {

    }

    @Override
    protected void stop(ServerLevel level, PathfinderMob entity, long gameTimeIn) {

        entity.getMoveControl().strafe(0, 0);
        entity.getBrain().updateActivityFromSchedule(level.getDayTime(), gameTimeIn);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, PathfinderMob entity, long gameTimeIn) {
        return this.checkExtraStartConditions(level, entity);
    }
}
