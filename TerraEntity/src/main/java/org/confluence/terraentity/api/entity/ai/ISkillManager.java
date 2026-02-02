package org.confluence.terraentity.api.entity.ai;

/**
 * 技能冷却时间管理类
 */
public interface ISkillManager {

    /**
     * 判断是否可以触发技能
     */
    boolean canTriggerSkill(ISkill skill);

    /**
     * 触发技能
     */
    boolean triggerSkill(ISkill skill);

    /**
     * 每帧更新，减少所有技能的剩余冷却时间
     * @param deltaTime 默认为1
     */
    void update(int deltaTime);

    /**
     * 添加或更新技能冷却时间
     */
    void addSkill(ISkill skill);



}
