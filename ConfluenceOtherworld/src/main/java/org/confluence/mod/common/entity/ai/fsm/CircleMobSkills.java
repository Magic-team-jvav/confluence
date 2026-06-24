package org.confluence.mod.common.entity.ai.fsm;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CircleMobSkills {
    public Entity owner;
    protected final List<AbstractMobSkill> mobSkills = new ArrayList<>();
    AbstractMobSkill cur;
    public int tick = 0;
    public int index = 0;
    public EntityDataAccessor<Integer> skillIndexData;
    public CircleMobSkills(Entity owner, @Nullable EntityDataAccessor<Integer> skillIndexData){
        this.owner = owner;
        this.skillIndexData = skillIndexData;
    }

    public CircleMobSkills(Entity owner){
        this.owner = owner;
    }

    public int count(){return mobSkills.size();};

    public boolean pushSkill(AbstractMobSkill skill){
        mobSkills.add(skill);
        if(mobSkills.size()==1) tick = 0;
        return true;
    }

    public void tick(){
        if(owner.level().isClientSide()) {
            cur = mobSkills.get(index);
            this.tick++;
            return;
        }
        if(mobSkills.isEmpty()) {
            return ;
        }
        this.tick++;

        cur = mobSkills.get(index);
        cur.tick(owner, tick);

        if(mobSkills.isEmpty()){
            return;
        }
        if(mobSkills.get(index).timeContinue < tick) {
            forceEnd();
            forceStartIndex(index);
        }
    }

    /** 强制结束当前状态 **/
    public void forceEnd(){
        tick = 0;
        int lastIndex = index;
        index = (index +1) % mobSkills.size();

        //状态结束
        mobSkills.get(lastIndex).stop(owner);
        if(skillIndexData!= null) {
            owner.getEntityData().set(skillIndexData, index);
        }
    }
    /** 强制跳转状态 **/
    public void forceStartIndex(int index){
        tick = 0;
        this.index = index;

        //初次进入状态
        mobSkills.get(index).start(owner);
        if(skillIndexData!= null) {
            owner.getEntityData().set(skillIndexData, index);
        }
    }

    public void syncForce(int index){
        owner.getEntityData().set(skillIndexData, index);
    }


    /** tick == triggerTime **/
    public boolean canTrigger(){
        if(mobSkills.isEmpty()) return false;
        return mobSkills.get(index).timeTrigger == this.tick;
    }
    /** tick > triggerTime **/
    public boolean canContinue(){
        if(mobSkills.isEmpty()) return false;
        return mobSkills.get(index).timeTrigger < this.tick;
    }
    public RawAnimation getCurAnim(){
        if(!mobSkills.isEmpty())
            return mobSkills.get(index).anim;
        return null;
    }
    public int getCurAnimFullTick(){
        if(!mobSkills.isEmpty())
            return mobSkills.get(index).timeContinue;
        return -1;
    }

    public Optional<Integer> getCurContinue(){
        return this.cur==null? Optional.empty() : Optional.of(cur.timeContinue);
    }

}
