package org.confluence.mod.mixed;

import net.minecraft.network.chat.MutableComponent;

public interface IDeathScreen {
    void confluence$setDelayTicker(int ticker);

    void confluence$setRespawnWaitTime(int time);

    void confluence$setDropsMoney(MutableComponent prefix);
}
