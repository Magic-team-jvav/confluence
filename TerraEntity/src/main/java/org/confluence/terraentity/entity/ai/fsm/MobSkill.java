package org.confluence.terraentity.entity.ai.fsm;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Consumer;

public class MobSkill<T extends Entity> extends AbstractMobSkill<T> {

    private Consumer<T> stateInit;
    private Consumer<T> stateTick;
    private Consumer<T> stateOver;

    /**
     * @param anim 动画名称
     * @param timeContinue 状态持续时间
     * @param timeTrigger 逻辑触发时间
     */
    public MobSkill(RawAnimation anim, int timeContinue, int timeTrigger){
        super(anim, timeContinue, timeTrigger);
    }

    public MobSkill(RawAnimation anim, int timeContinue, int timeTrigger,
                    Consumer<T> stateInit,
                    Consumer<T> stateTick,
                    Consumer<T> stateOver
    ){
        this(anim, timeContinue, timeTrigger);
        this.stateInit = stateInit;
        this.stateTick = stateTick;
        this.stateOver = stateOver;
    }

    public void addStateReset(Consumer<T> stateTick){
        this.stateTick = stateTick;
    };
    public void addStateInit(Consumer<T> stateInit){
        this.stateInit = stateInit;
    };
    public void addStateOver(Consumer<T> stateOver){
        this.stateOver = stateOver;
    };

    public void start(T mob){
        if(stateInit!= null) stateInit.accept(mob);
    }
    public void tick(T mob, int time){
        if(stateTick!= null) stateTick.accept(mob);
    }
    public void stop(T mob){
        if(stateOver!= null) stateOver.accept(mob);
    }


    public MobSkill<T> onTick (Consumer<T> stateTick){
        this.stateTick = stateTick;
        return this;
    }
    public MobSkill<T> onInit (Consumer<T> stateInit){
        this.stateInit = stateInit;
        return this;
    }
    public MobSkill<T> onOver (Consumer<T> stateOver){
        this.stateOver = stateOver;
        return this;
    }
}
