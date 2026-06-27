package org.confluence.mod.util.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.common.api.entity.ai.ISkill;

public abstract class CdGoal<T extends Mob> extends Goal implements ISkill {
    protected T mob;
    protected int interval;
    protected final int _interval;
    protected int time;
    protected int maxCount;
    protected int count;

    public CdGoal(T mob, int interval, int maxCount){
        this.mob = mob;
        this.interval = interval / 2;
        this._interval = this.interval;
        this.time = this.interval;
        this.maxCount = maxCount;
        this.count = 0;
    }


    @Override
    public void tick(){
        if(this.count < maxCount && --time <= 0){
            this.doAction(this.mob.getTarget());
            this.reset();
        }
    }

    public void setCdReduce(float percent){
        this.interval = (int) (this._interval * (1 - percent));
    }

    protected abstract void doAction(LivingEntity target);

    @Override
    public int getCooldown() {
        return this.time;
    }

    @Override
    public void update(int delta) {
        this.time--;
    }

    @Override
    public void reset() {
        this.time = this.interval;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
