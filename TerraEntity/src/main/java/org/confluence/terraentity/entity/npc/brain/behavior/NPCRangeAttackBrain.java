package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.server.level.ServerLevel;
import org.confluence.terraentity.entity.ai.brain.behavior.range.RangeAttackBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.TEAi;

/**
 * NPC的远程攻击，默认发射箭
 */
public class NPCRangeAttackBrain<T extends AbstractTerraNPC> extends RangeAttackBrain<T> {
    public NPCRangeAttackBrain(int prepareTime, float attackRange) {
        super(prepareTime, attackRange);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T owner) {
        // 添加了npc自身能力的检测条件
        return super.checkExtraStartConditions(level, owner) && owner.canPerformerAttack();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, T entity, long gameTimeIn) {
        return super.canStillUse(level, entity, gameTimeIn) && entity.getBrain().isActive(TEAi.Activities.RANGE_ATTACK);
    }

}
