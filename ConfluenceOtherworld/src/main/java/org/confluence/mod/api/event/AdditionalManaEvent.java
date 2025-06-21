package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.attachment.ManaStorage;

public class AdditionalManaEvent extends PlayerEvent implements ICancellableEvent {
    private final ManaStorage manaStorage;
    private int neoValue;
    private final int original;

    public AdditionalManaEvent(Player player, ManaStorage manaStorage, int neoValue, int original) {
        super(player);
        this.manaStorage = manaStorage;
        this.neoValue = neoValue;
        this.original = original;
    }

    public ManaStorage getManaStorage() {
        return manaStorage;
    }

    public void setNeoValue(int neoValue) {
        this.neoValue = neoValue;
    }

    public int getNeoValue() {
        return neoValue;
    }

    public int getOriginal() {
        return original;
    }
}
