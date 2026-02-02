package org.confluence.terra_curio.api.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.ApiStatus;

public class PlayerAboutToAutoAttackEvent extends PlayerInteractEvent {
    private final boolean couldPerform;
    private TriState action = TriState.DEFAULT;

    public PlayerAboutToAutoAttackEvent(Player player, boolean couldPerform) {
        super(player, InteractionHand.MAIN_HAND, player.blockPosition(), null);
        this.couldPerform = couldPerform;
    }

    public void setAction(TriState action) {
        this.action = action;
    }

    public TriState getAction() {
        return action;
    }

    public boolean isCouldPerform() {
        return couldPerform;
    }

    @ApiStatus.Internal
    public boolean couldPerform() {
        return action.isTrue() || (couldPerform && action.isDefault());
    }
}
