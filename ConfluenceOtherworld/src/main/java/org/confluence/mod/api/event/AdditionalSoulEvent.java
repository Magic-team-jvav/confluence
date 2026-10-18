package org.confluence.mod.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.attachment.SoulStorage;

@Cancelable
public class AdditionalSoulEvent extends PlayerEvent {
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

