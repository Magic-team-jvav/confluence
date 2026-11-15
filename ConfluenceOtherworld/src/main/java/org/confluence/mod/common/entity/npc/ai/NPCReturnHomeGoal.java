package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumSet;

public final class NPCReturnHomeGoal extends Goal {
    private final BaseNPC npc;

    public NPCReturnHomeGoal(BaseNPC npc) {
        this.npc = npc;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!npc.getHouse().isValid() || npc.getTarget() != null || npc.getInteractingPlayer() != null)
            return false;
        double distance = npc.blockPosition().distSqr(npc.getHouse().center());
        return distance >= 4 && (LibDateUtils.isNight(npc.level()) || distance > 400);
    }

    @Override
    public void start() {
        moveHome();
    }

    @Override
    public void tick() {
        if (npc.tickCount % 20 == 0) moveHome();
    }

    @Override
    public void stop() {
        npc.getNavigation().stop();
    }

    private void moveHome() {
        var pos = npc.getHouse().center();
        npc.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
    }
}
