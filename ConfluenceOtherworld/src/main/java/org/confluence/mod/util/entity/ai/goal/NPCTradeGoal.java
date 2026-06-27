package org.confluence.mod.util.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.confluence.mod.common.entity.npc.AbstractTerraNPC;

import java.util.EnumSet;

public class NPCTradeGoal extends Goal {
    private final AbstractTerraNPC mob;

    public NPCTradeGoal(AbstractTerraNPC mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        } else if (this.mob.isInWater()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else if (this.mob.hurtMarked) {
            return false;
        } else {
            Player player = this.mob.tradingPlayer;
            if (player == null) {
                return false;
            } else {
                return !(this.mob.distanceToSqr(player) > 16.0);
            }
        }
    }
    public void tick() {
        this.mob.getNavigation().stop();
    }

    public void start() {
        this.mob.getNavigation().stop();
    }

    public void stop() {
        this.mob.tradingPlayer.closeContainer();
        this.mob.tradingPlayer = null;

    }

    public boolean canContinueToUse() {
        return this.canUse() && !(this.mob.tradingPlayer.containerMenu instanceof InventoryMenu);
    }
}
