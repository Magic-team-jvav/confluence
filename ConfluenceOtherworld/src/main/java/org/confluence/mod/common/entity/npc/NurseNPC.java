package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCHealGoal;

/// 护士 —— 向 5 格范围内的低生命 NPC 投掷治疗药水。
public class NurseNPC extends BaseNPC {

    public NurseNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new NPCHealGoal(this, 5));
    }
}
