package org.confluence.mod.api.event;

import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.network.s2c.RedGloveArcPacketS2C;

/** Keeps common packet decoding free of references to client rendering classes. */
public final class RedGloveVisualEvent extends Event {
    public final RedGloveArcPacketS2C packet;
    public RedGloveVisualEvent(RedGloveArcPacketS2C packet) { this.packet = packet; }
}
