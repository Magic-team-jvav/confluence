package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCGrenadeGoal;

/**
 * 爆破专家 —— 投掷手榴弹攻击 5 格内敌人。
 */
public class DemolitionistNPC extends BaseNPC {

    public DemolitionistNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new NPCGrenadeGoal(this, 5));
    }
}
