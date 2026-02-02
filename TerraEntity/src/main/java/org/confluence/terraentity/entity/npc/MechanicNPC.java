package org.confluence.terraentity.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MechanicNPC extends AbstractTerraNPC {
    public MechanicNPC(EntityType<? extends AbstractTerraNPC> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true; // confluence mixin here
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        // confluence mixin here
    }
}
