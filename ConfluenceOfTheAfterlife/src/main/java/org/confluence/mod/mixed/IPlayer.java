package org.confluence.mod.mixed;

import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.npc.NPCTrades;

public interface IPlayer {

    NPCTrades rhyme$getDaveTrades(); // getRhyme$daveTrades
    void rhyme$setDaveTrades(NPCTrades NPCTrades); // setRhyme$daveTrades

    Entity rhyme$getInteractingEntity(); // getRhyme$dave
    void rhyme$setInteractingEntity(Entity entity); // setRhyme$dave
}
