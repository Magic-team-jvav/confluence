package org.confluence.terraentity.entity.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;

public class DifficultSelector {

    private static boolean expert;
    private static boolean master;
    private boolean ftw;

    public DifficultSelector(Level level){
        this.changeDifficulty(level);
    }

    public void changeDifficulty(Level level){
        Difficulty difficulty = level.getDifficulty();
        if(difficulty.equals(Difficulty.PEACEFUL)){
            expert = false;
            master = false;
            ftw = false;
            return;
        }

        expert = true;
        master = true;

        if(difficulty.equals(Difficulty.EASY)) {
            master = false;
            expert = false;
        }else if(difficulty.equals(Difficulty.NORMAL)){
            master = false;
        }else{
            if(level instanceof ServerLevel serverLevel){
                this.setFtb(serverLevel);
            }
        }
    }

    public void setFtb(ServerLevel level){
        ftw = TEUtils.isFTWWorld(level);
    }

    public boolean isMaster() {
        return master;
    }

    public boolean isFtw() {
        return ftw;
    }

    public boolean isExpert() {
        return expert;
    }

    public <T> T switchBy(T classic, T expert, T master, T ftw){
        if(!DifficultSelector.expert){
            return classic;
        }else if(!DifficultSelector.master){
            return expert;
        }else if(!this.ftw){
            return master;
        }
        return ftw;
    }

    public <T> T switchBy(List<T> list){
        return switchBy(list, 0);
    }

    public <T> T switchBy(List<T> list, int offset){
        int idx = 0;
        if (!DifficultSelector.expert) {
            idx = 1;
        } else if(!DifficultSelector.master) {
            idx = 2;
        } else if (!this.ftw) {
            idx = 3;
        }
        idx += offset;
        if (idx < 0) idx = 0;
        else if (idx >= list.size()) idx = list.size() - 1;
        return list.get(idx);
    }
}
