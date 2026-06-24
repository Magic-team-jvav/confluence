package org.confluence.mod.common.entity.ai.brain.sensor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.npc.AbstractTerraNPC;
import org.confluence.mod.common.init.TEAi;

import java.util.Optional;

public class NPCNurseTargetSensor<T extends AbstractTerraNPC> extends NPCNearestVisibleAllianceSensor<T>{
    public NPCNurseTargetSensor(float range) {
        super(range);
    }

    @Override
    protected boolean isMatchingEntity(T owner, LivingEntity target) {
        double percent = target.getHealth() / target.getMaxHealth();
        if(target instanceof Player) {
            if (ConfluenceMagicLib.IS_CONFLUENCE_LOAD) return false;
            // 玩家生命小于0.33才治疗
            return percent <= 0.33f;
        }
        // 其他玩家生命值小于0.5才治疗
        return super.isMatchingEntity(owner, target) && percent < 0.5f;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET.get();
    }

    @Override
    protected Optional<LivingEntity> getNearestEntity(T entity) {
        // 生命值过低优先治疗自己
        double percent = entity.getHealth() / entity.getMaxHealth();
        if(percent <= 0.5f){
            return Optional.of(entity);
        }
        // 否则治疗队友
        Optional<LivingEntity> target = super.getNearestEntity(entity);
        if(target.isPresent()){
            return target;
        }
        // 队友也没有生命值过低的，则治疗自己
        if(percent <= 0.9f){
            return Optional.of(entity);
        }
        return Optional.empty();
    }
}
