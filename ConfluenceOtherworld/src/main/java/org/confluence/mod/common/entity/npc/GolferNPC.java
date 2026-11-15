package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;

import java.util.EnumSet;

public class GolferNPC extends BaseNPC {
    public GolferNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile profile) {
        super(type, level, profile);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(-1, new Goal() {
            {setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));}

            @Override
            public boolean canUse() {return shouldInteract();}

            @Override
            public void start() {
                getNavigation().stop();
                setTarget(null);
                setSpeed(0);
                setXxa(0);
                setZza(0);
            }
        });
    }

    @Override
    public boolean canDefendSelf() {
        return !shouldInteract();
    }

    @Override
    protected void customServerAiStep() {
        if (!shouldInteract()) super.customServerAiStep();
    }
}
