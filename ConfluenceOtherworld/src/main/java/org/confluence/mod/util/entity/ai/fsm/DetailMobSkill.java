package org.confluence.mod.util.entity.ai.fsm;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.RawAnimation;

public abstract class DetailMobSkill<T extends Entity> extends AbstractMobSkill<T> {

    /**
     * @param anim         动画名称
     * @param timeContinue 状态持续时间
     * @param timeTrigger  逻辑触发时间
     */
    public DetailMobSkill(RawAnimation anim, int timeContinue, int timeTrigger) {
        super(anim, timeContinue, timeTrigger);
    }

    @Override
    public void tick(T mob, int time){
        if(timeTrigger > time){
            this.beforeTrigger(mob);
        }else if(timeTrigger < time ){
            this.afterTrigger(mob);
        }else{
            this.onTrigger(mob);
        }
    }

    public abstract void beforeTrigger(T mob);
    public abstract void onTrigger(T mob);
    public abstract void afterTrigger(T mob);

}
