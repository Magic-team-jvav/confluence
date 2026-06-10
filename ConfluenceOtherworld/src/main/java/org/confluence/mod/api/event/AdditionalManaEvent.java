package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.confluence.mod.common.attachment.ManaStorage;

@Cancelable
public class AdditionalManaEvent extends PlayerEvent {
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
