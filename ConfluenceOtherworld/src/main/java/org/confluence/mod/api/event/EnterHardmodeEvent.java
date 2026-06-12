package org.confluence.mod.api.event;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;

public record EnterHardmodeEvent(MinecraftServer server) extends Event {
}
