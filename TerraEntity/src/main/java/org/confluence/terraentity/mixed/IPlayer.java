package org.confluence.terraentity.mixed;

import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;

import javax.annotation.Nullable;

public interface IPlayer {
    @Nullable
    ITradeHolder terra_entity$getTradeHolder(); //

    void terra_entity$setTradeHolder(ITradeHolder  entity); //

    boolean terra_entity$isInfiniteInteractBlock();

    void terra_entity$setInfiniteInteractBlock(boolean flag);

    static IPlayer of(Player player) {
        return (IPlayer) player;
    }
}
