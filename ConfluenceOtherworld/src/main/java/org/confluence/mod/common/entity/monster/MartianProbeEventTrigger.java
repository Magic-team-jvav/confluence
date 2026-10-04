package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerPlayer;

/** Integration point for the Martian invasion; the probe itself does not start an event. */
@FunctionalInterface
public interface MartianProbeEventTrigger {
    boolean requestStart(ServerPlayer player);
}
