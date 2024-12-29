package org.confluence.terraentity.entity.ai;

import net.minecraft.world.entity.Mob;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleBossSkills<T extends Mob> {
    public T owner;
    protected final List<BossSkill> bossSkills = new ArrayList<>();

    public int tick = 0;
    public int index = 0;
    public boolean ifStateInit = false;

    public CircleBossSkills(T owner){ this.owner = owner;}
    public int count(){return bossSkills.size();};

    public boolean pushSkill(BossSkill skill){
        bossSkills.add(skill);
        if(bossSkills.size()==1) tick = 0;
        return true;
    }


    public void tick(){
        if(bossSkills.isEmpty()) return ;
        this.tick++;

        if(bossSkills.get(index).stateTick !=null) {
            bossSkills.get(index).stateTick.accept(owner);
        }
        if(bossSkills.isEmpty())return;
        if(bossSkills.get(index).timeContinue < tick) forceEnd();
    }


    /** 强制结束当前状态 **/
    public void forceEnd(){
        tick = 0;
        int lastIndex = index;
        index = (index +1) % bossSkills.size();

        //状态结束
        if(bossSkills.get(lastIndex).stateOver!=null) bossSkills.get(lastIndex).stateOver.accept(owner);

        //初次进入状态
        if(bossSkills.get(index).stateInit!=null) bossSkills.get(index).stateInit.accept(owner);
        owner.getEntityData().set(AbstractTerraBossBase.DATA_SKILL_INDEX, index);
    }
    /** 强制跳转状态 **/
    public void forceStartIndex(int index){
        tick = 0;
        this.index = index;
    }


    /** tick == triggerTime **/
    public boolean canTrigger(){
        if(bossSkills.isEmpty()) return false;
        return bossSkills.get(index).timeTrigger == this.tick;
    }
    /** tick > triggerTime **/
    public boolean canContinue(){
        if(bossSkills.isEmpty()) return false;
        return bossSkills.get(index).timeTrigger < this.tick;
    }
    public RawAnimation getCurAnim(){
        if(!bossSkills.isEmpty())
            return bossSkills.get(index).anim;
        return null;
    }
    public int getCurAnimFullTick(){
        if(!bossSkills.isEmpty())
            return bossSkills.get(index).timeContinue;
        return -1;
    }



}
