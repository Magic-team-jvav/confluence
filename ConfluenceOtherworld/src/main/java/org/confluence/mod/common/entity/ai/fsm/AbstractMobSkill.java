package org.confluence.mod.common.entity.ai.fsm;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.RawAnimation;

public abstract class AbstractMobSkill<T extends Entity> {
    public int timeContinue;
    public int timeTrigger;
    public RawAnimation anim;

    /**
     * @param anim 动画名称
     * @param timeContinue 状态持续时间
     * @param timeTrigger 逻辑触发时间
     */
    public AbstractMobSkill(RawAnimation anim, int timeContinue, int timeTrigger){
        this.anim = anim;
        this.timeContinue = timeContinue;
        this.timeTrigger = timeTrigger;
    }

    public abstract void start(T mob);
    public abstract void tick(T mob, int time);
    public abstract void stop(T mob);


}
