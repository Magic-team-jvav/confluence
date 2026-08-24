package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;

/// 未获救时留在地牢中的机械师。
public class MechanicNPC extends BaseNPC {
    public MechanicNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !getRegion().isOnRegion(chunkPosition());
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if (isRemoved()) {
            NPCSpawner.INSTANCE.setNPCAlive(getRegion(), getType(), false);
        }
    }
}
