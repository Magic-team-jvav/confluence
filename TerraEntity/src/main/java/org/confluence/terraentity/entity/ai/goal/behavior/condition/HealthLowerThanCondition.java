package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import net.minecraft.world.entity.Mob;

/**
 * 生命值小于阈值
 */
public class HealthLowerThanCondition extends AbstractConditionLeaf {

    final Mob mob;
    final float percentage;

    public HealthLowerThanCondition(Mob mob, float percentage) {
        this.mob = mob;
        this.percentage = percentage;
    }

    @Override
    public boolean check() {
        return mob.getHealth() / mob.getMaxHealth() < percentage;
    }

    @Override
    public String getDesc() {
        return "Health lower than " + percentage * 100 + "%";
    }
}
