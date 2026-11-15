package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.attachment.SoulStorage;

public class AdditionalSoulEvent extends PlayerEvent implements ICancellableEvent {
    private SoulStorage soulStorage;
    private int neoValue;
    private int original;

    public AdditionalSoulEvent(Player player, SoulStorage soulStorage, int neoValue, int original) {
        super(player);
        this.soulStorage = soulStorage;
        this.neoValue = neoValue;
        this.original = original;
    }

    public SoulStorage getSoulStorage() {
        return soulStorage;
    }

    public void setNeoValue(int neoValue) {
        this.neoValue = neoValue;
    }

    public int getNeoValue() {
        return neoValue;
    }

    /// 读取刷新前保存的额外灵魂上限。
    public int getOriginal() {
        return original;
    }
}
