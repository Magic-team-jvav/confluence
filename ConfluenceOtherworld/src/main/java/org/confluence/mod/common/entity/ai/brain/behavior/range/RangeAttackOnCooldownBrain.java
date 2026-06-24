package org.confluence.mod.common.entity.ai.brain.behavior.range;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

/**
 * 当远程攻击冷却时, 或者距离过远试图接近敌人的走a行为
 */
public class RangeAttackOnCooldownBrain<T extends PathfinderMob> extends Behavior<T> {

    float attackRange;
    float speedModifier;

    public RangeAttackOnCooldownBrain(int cooldownTime, float attackRange, float speedModifier) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_PRESENT
                ),cooldownTime);
        this.attackRange = attackRange;
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T owner) {

        var cooldownMemory = owner.getBrain().getMemory(MemoryModuleType.ATTACK_COOLING_DOWN);
        if(cooldownMemory.isPresent()){
            // 在冷却时执行一定执行
            if(cooldownMemory.get()){
                return true;
            }
        }

        var memory = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if(memory.isPresent()){
            // 否则距离过远时执行
            LivingEntity target = memory.get();
            return target.distanceTo(owner) > attackRange;
        }

        return false;
    }

    @Override
    protected void tick(ServerLevel level, T owner, long gameTime) {

        if(!owner.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) { // 防止一直寻路导致鬼畜
            owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> {

                float safeDistance = attackRange;
                Vec3 targetPos = target.position();
                Vec3 ownerPos = owner.position();
                Vec3 toPos;
                if (ownerPos.distanceTo(targetPos) > safeDistance) {
                    toPos = LandRandomPos.getPosTowards(owner, (int) safeDistance, 5, targetPos);

                } else {
                    toPos = LandRandomPos.getPosAway(owner, (int) safeDistance, 5, targetPos);
                }
                if (toPos != null) {
                    // 这里注释掉就不会动了，方便观察动作
                    owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(toPos, speedModifier, (int) 1f));
                } else {
                    // debug
//                    owner.setDeltaMovement(new Vec3(0, 0.02f, 0));
                }

            });
        }
    }

    @Override
    protected void start(ServerLevel level, T entity, long gameTimeIn) {

        var memory = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if(memory.isPresent()){
            LivingEntity target = memory.get();
            if(target.distanceTo(entity) < attackRange * 0.6f){
                // 快速撤离
                entity.setSprinting(true);
            }
        }
    }

    @Override
    protected void stop(ServerLevel level,T entity, long gameTimeIn) {
        entity.getBrain().setMemory(MemoryModuleType.ATTACK_COOLING_DOWN, false);
        entity.setSprinting(false);

    }

    @Override
    protected boolean canStillUse(ServerLevel level, T entity, long gameTimeIn) {
        return this.checkExtraStartConditions(level, entity);
    }
}
