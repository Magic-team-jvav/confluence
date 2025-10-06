package org.confluence.mod.common.attachment;

import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.mod.common.block.functional.SafeBlock;

public class PlayerSafeContainer extends PlayerContainer<SafeBlock.BEntity> {
    public PlayerSafeContainer() {
        super(6);
    }

    @Override
    public void startOpen(Player player) {
        if (activeContainer != null && !activeContainer.isRemoved() && !player.isSpectator()) {
            activeContainer.openersCounter.incrementOpeners(player, player.level(), activeContainer.getBlockPos(), activeContainer.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (activeContainer != null && !activeContainer.isRemoved() && !player.isSpectator()) {
            activeContainer.openersCounter.decrementOpeners(player, player.level(), activeContainer.getBlockPos(), activeContainer.getBlockState());
        }
    }
}
