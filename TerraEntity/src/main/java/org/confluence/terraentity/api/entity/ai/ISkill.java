package org.confluence.terraentity.api.entity.ai;

/**
 * 技能类
 */
public interface ISkill {

    /**
     * 当前冷却时间
     */
    int getCooldown();

    /**
     * 更新冷却时间
     * @param delta 时间间隔
     */
    void update(int delta);


    /**
     * 技能名(索引)(optional, for toString)
     */
    default int getIndex(){
        return -1;
    }

    /**
     * 重置冷却
     */
    void reset();

}
