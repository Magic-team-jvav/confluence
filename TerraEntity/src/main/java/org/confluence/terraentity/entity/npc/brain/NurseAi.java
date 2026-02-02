package org.confluence.terraentity.entity.npc.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.confluence.terraentity.entity.ai.brain.behavior.panic.PanicCalmDownBrain;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCPanicTriggerBrain;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCRangeAttackBrain;
import org.confluence.terraentity.entity.npc.brain.behavior.NurseAttackTriggerBrain;
import org.confluence.terraentity.entity.npc.brain.behavior.NurseRangeAttackBrain;
import org.confluence.terraentity.init.TEAi;

import java.util.List;

/**
 * 护士AI，向残血玩家或npc丢治疗药水
 */
public class NurseAi extends NPCAi {

    /**
     * 在生成npc时的构造函数调用
     * @param npc npc实例
     */
    public NurseAi(AbstractTerraNPC npc) {
        super(npc);

    }

    @Override
    protected void init(){
        npc.setAttackRange(5);
        npc.setCooldownTicks(30);
        npc.setCanPerformerAttackTest(npc1->{
            var targetOpt = npc1.getBrain().getMemory(TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET.get());
            return targetOpt.isPresent();
        });
    }

    protected List<MemoryModuleType<?>> getMemoryAddition(){
        return ImmutableList.of(TEAi.MemoryModules.NEAREST_VISIBLE_ALLIANCE_NURSE_TARGET.get());
    }

    protected List<SensorType<? extends Sensor<? super AbstractTerraNPC>>> getSensorAddition(){
        return ImmutableList.of(TEAi.Sensors.NEAREST_NURSE_TARGET_SENSOR.get());
    }

    /**
     * 替换触发攻击行为
     */
    protected Behavior<? super AbstractTerraNPC> getAttackTriggerBrain(){
        return new NurseAttackTriggerBrain<>();
    }

    protected Behavior<? super AbstractTerraNPC> getPanicTriggerBrain(){
        return new NPCPanicTriggerBrain<>();
    }

    public ImmutableList<Pair<Integer, ? extends BehaviorControl<? super AbstractTerraNPC>>> getPanicPackage(float speedModifier) {
        float f = speedModifier * 1.3F;
        return ImmutableList.of(
                Pair.of(0, new PanicCalmDownBrain<>(){
                    protected void onCalmDown(ServerLevel level, Brain<?> brain, LivingEntity target) {
//                        // 护士不会给攻击自己的target治疗
                        brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
//                        super.onCalmDown(level, brain, target);
                    }
                }),
                Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)),
                Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false))
        );
    }

    /**
     * 替换不同的远程攻击行为
     */
    protected NPCRangeAttackBrain<? super AbstractTerraNPC> getRangeAttackBrain() {
        return new NurseRangeAttackBrain<>(10, npc.getAttackRange());
    }
}
