package org.confluence.mod.mixin.server;

import net.minecraft.server.dedicated.DedicatedServer;
import org.confluence.mod.mixed.IDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin implements IDedicatedServer {
    @Unique
    private volatile boolean confluence$onHardmodeConversation = false;

    @Override
    public void confluence$setOnHardmodeConversation(boolean on) {
        this.confluence$onHardmodeConversation = on;
    }

    @Override
    public boolean confluence$isOnHardmodeConversation() {
        return confluence$onHardmodeConversation;
    }
}
