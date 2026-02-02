package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.entity.ai.brain.behavior.panic.PanicCalmDownBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

/**
 * npc的恐慌行为清除器，当npc不能攻击敌怪的时候会始终逃跑
 */
public class NPCPanicCalmDownBrain<T extends AbstractTerraNPC> extends PanicCalmDownBrain<T> {

    public NPCPanicCalmDownBrain() {
        super();
    }

    public NPCPanicCalmDownBrain(float chance) {
        super(chance);
    }

    public float getCalmDownChance(T living){
        return living.canPerformerAttack()? super.getCalmDownChance(living) : 0f;
    }

    protected boolean hurtByTargetPredicate(LivingEntity target) {
        return super.hurtByTargetPredicate(target) && !(target instanceof Player) && !(target instanceof AbstractTerraNPC);
    }

    protected void beforeCalmDown(ServerLevel level, T living, Brain<?> brain, LivingEntity target) {
        if(living.getRandom().nextFloat() < 0.6f) {
            // 概率看向敌人
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.getEyePosition()));
        }
    }

}
