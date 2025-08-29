package org.confluence.mod.mixed;

import net.minecraft.server.dedicated.DedicatedServer;

public interface IDedicatedServer {
    void confluence$setOnHardmodeConversation(boolean on);

    boolean confluence$isOnHardmodeConversation();

    static IDedicatedServer of(DedicatedServer server) {
        return (IDedicatedServer) server;
    }
}
