package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
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
        this.goalSelector.addGoal(1, new NPCHealGoal(this, 16));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
}
