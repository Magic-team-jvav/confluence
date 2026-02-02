package org.confluence.terraentity.entity.ai.goal.skill;

import org.confluence.terraentity.api.entity.ai.ISkill;
import org.confluence.terraentity.api.entity.ai.ISkillManager;

import java.util.*;

/**
 * 并行冷却时间管理器
 */
public class SkillCooldownManager implements ISkillManager {

    private final HashSet<ISkill> cooldownMap = new HashSet<>(); // 技能名 -> 剩余冷却时间

    protected final PriorityQueue<ISkill> cooldownQueue = new PriorityQueue<>(Comparator.comparing(ISkill::getCooldown));
    protected final Queue<ISkill> availableSkills = new LinkedList<>(); // 可用的技能列表


    /**
     * 获取剩余冷却时间最短的技能
     */
    public ISkill getNextAvailableSkill() {
        return cooldownQueue.peek();
    }

    @Override
    public void addSkill(ISkill skill) {
        cooldownMap.add(skill);
        cooldownQueue.add(skill);
    }

    @Override
    public boolean triggerSkill(ISkill skill) {
        if(canTriggerSkill(skill)){
            this.availableSkills.poll();
            skill.reset();
            addSkill(skill);
            return true;
        }
        return false;
    }

    @Override
    public void update(int deltaTime) {
        cooldownMap.forEach(v->v.update(deltaTime));
        cooldownMap.removeIf(v->v.getCooldown()<=0);

        while (!cooldownQueue.isEmpty() &&
               (!cooldownMap.contains(cooldownQueue.peek()) || cooldownQueue.peek()!= null &&
                cooldownQueue.peek().getCooldown() <= 0)) {
            this.availableSkills.add(cooldownQueue.poll());
        }

    }

    @Override
    public String toString(){
        StringBuilder log = new StringBuilder();
        for (ISkill skill : cooldownQueue) {
            log.append(String.format(skill.getIndex() + ":" + skill.getCooldown() + " ; "));
        }
        return log.toString();
    }


    @Override
    public boolean canTriggerSkill(ISkill skill) {
        return !this.availableSkills.isEmpty() && this.availableSkills.peek() == skill;
    }

    protected void exchangeQueue(){
        if(!this.availableSkills.isEmpty()) {
            this.availableSkills.add(this.availableSkills.poll());
        }
    }
}