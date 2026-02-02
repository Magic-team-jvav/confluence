package org.confluence.terraentity.entity.npc.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.schedule.Activity;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.TEAi;

public class OldManAi extends NPCAi {
    /**
     * 在生成npc时的构造函数调用
     *
     * @param npc
     */
    public OldManAi(AbstractTerraNPC npc) {
        super(npc);
    }

    @Override
    public Brain<AbstractTerraNPC> makeBrain(Brain<AbstractTerraNPC> brain) {
        brain.setSchedule(TEAi.NPC_SCHEDULE.get());
        initCoreActivity(brain);

        brain.addActivity(Activity.IDLE, getIdlePackage(1.0F));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    @Override
    public ImmutableList<Pair<Integer, ? extends BehaviorControl<? super AbstractTerraNPC>>> getIdlePackage(float speedModifier) {
        return ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60)))
        );
    }
}
