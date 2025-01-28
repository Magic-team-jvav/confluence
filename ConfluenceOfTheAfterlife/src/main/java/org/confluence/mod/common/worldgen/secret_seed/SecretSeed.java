package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.mixed.IMinecraftServer;

public abstract class SecretSeed {
    private final long flag;

    SecretSeed(long flag) {
        this.flag = flag;
    }

    public abstract boolean match(String seed);

    public long getFlag() {
        return flag;
    }

    public boolean match(MinecraftServer server) {
        return ((IMinecraftServer) server).confluence$matchesSecretFlag(this);
    }

    public boolean match(ServerLevel serverLevel) {
        return match(serverLevel.getServer());
    }

    public boolean match() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && match(server);
    }

    public boolean match(long secretFlag) {
        return (secretFlag & flag) != 0;
    }
}
