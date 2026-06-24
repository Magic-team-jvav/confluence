package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public abstract class SummonGoal<T extends Mob, M extends Entity> extends CdGoal<T> {

    public SummonGoal(T mob, int interval, int maxCount){
        super(mob, interval, maxCount);
    }

    protected final void doAction(LivingEntity target){
        this.spawnEntity(target);
    }

    protected void spawnEntity(LivingEntity target) {
        if (this.mob.level() instanceof ServerLevel serverLevel) {
            M minion = this.createMinion(target, serverLevel);
            if(minion != null) {
                this.onMinionSpawned(minion, target );
                this.count++;
            }
        }
    }

    protected abstract M createMinion(LivingEntity target, ServerLevel serverLevel);

    protected abstract void onMinionSpawned(M minion, LivingEntity target);

}
