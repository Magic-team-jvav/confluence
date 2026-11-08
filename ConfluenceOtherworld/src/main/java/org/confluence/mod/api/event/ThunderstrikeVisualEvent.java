package org.confluence.mod.api.event;

import net.neoforged.bus.api.Event;
import org.confluence.mod.network.s2c.ThunderstrikePacketS2C;

public final class ThunderstrikeVisualEvent extends Event {
    public final ThunderstrikePacketS2C packet;
    public ThunderstrikeVisualEvent(ThunderstrikePacketS2C packet) { this.packet = packet; }
}
