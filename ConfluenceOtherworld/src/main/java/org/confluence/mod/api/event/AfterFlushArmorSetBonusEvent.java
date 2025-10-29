package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.attachment.PlayerSpecialData;

public class AfterFlushArmorSetBonusEvent extends PlayerEvent {
    private final PlayerSpecialData data;

    public AfterFlushArmorSetBonusEvent(Player player, PlayerSpecialData data) {
        super(player);
        this.data = data;
    }

    public PlayerSpecialData getData() {
        return data;
    }
}
