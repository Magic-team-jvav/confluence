package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.server.level.ServerLevel;
import org.confluence.terraentity.entity.ai.brain.behavior.range.AttackCalmDownBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class NPCAttackCalmDownBrain<T extends AbstractTerraNPC> extends AttackCalmDownBrain<T> {

    public NPCAttackCalmDownBrain(float distanceToRemove) {
        super(distanceToRemove);
    }

    protected void start(ServerLevel level, T living, long gameTimeIn) {
        super.start(level, living, gameTimeIn);
        if(!living.canPerformerAttack()){
            calmDown(level, living, gameTimeIn);
        }
    }
}
