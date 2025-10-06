package org.confluence.mod.integration.terra_entity;

import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public interface IAbstractTerraNPC {
    void confluence$setRegion(NPCSpawner.Region region);

    NPCSpawner.Region confluence$getRegion();

    void confluence$setShouldInteract(boolean should);

    boolean confluence$shouldInteract();

    static IAbstractTerraNPC of(AbstractTerraNPC npc) {
        return (IAbstractTerraNPC) npc;
    }
}
