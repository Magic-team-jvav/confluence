package org.confluence.mod.api.event;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.Event;

public class EnterHardmodeEvent extends Event {
    private final MinecraftServer server;

    public EnterHardmodeEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
