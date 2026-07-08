package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCHealGoal;

/**
 * 护士 —— 治疗 16 格范围内受伤 NPC。
 */
public class NurseNPC extends BaseNPC {

    public NurseNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new NPCHealGoal(this, 16));
    }
}
