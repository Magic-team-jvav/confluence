package org.confluence.terraentity.integration.touhou_little_maid.task_boomerang;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;

public class WrapTriggerBrain extends Behavior<EntityMaid> {

    boolean isDelegate = false;

    public WrapTriggerBrain(Map<MemoryModuleType<?>, MemoryStatus> entryCondition) {
        super(entryCondition);
    }

    public WrapTriggerBrain(Map<MemoryModuleType<?>, MemoryStatus> entryCondition, boolean isDelegate) {
        super(entryCondition);
        this.isDelegate = isDelegate;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid owner) {
        if(owner.getTask() instanceof TaskDistributor distributor){
            if(distributor.currentTask==null){
                for( var task : distributor.tasks){
                    if(task.getConditionDescription(owner).stream()
                            .allMatch(pair -> pair.getSecond().test(owner))){
                        distributor.currentTask = task;
                        return !isDelegate;
                    }
                }
            }else{
                for (var pair : distributor.currentTask.getConditionDescription(owner)) {
                    if (!pair.getSecond().test(owner)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid entity, long gameTime) {
        if(entity.getTask() instanceof TaskDistributor distributor){
            entity.refreshBrain(level);
            if(distributor.currentTask!=null){
                distributor.currentTask = null;
            }
            distributor.isDelegate = false;
        }
    }


}
