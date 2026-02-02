package org.confluence.terraentity.entity.npc.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class NonAttackableNPCAi extends NPCAi {

    public NonAttackableNPCAi(AbstractTerraNPC npc) {
        super(npc);
    }

    public ImmutableList<Pair<Integer, ? extends BehaviorControl<? super AbstractTerraNPC>>> getRangeAttackPackage(float speedModifier) {
        return ImmutableList.of();
    }

}
