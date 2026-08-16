package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCHealGoal;

/// 护士 —— 向五格内需要治疗的城镇 NPC 投掷治疗药水。
public class NurseNPC extends BaseNPC {

    public NurseNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new NPCHealGoal(this, 5.0));
    }
}
