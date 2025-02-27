package org.confluence.mod.mixed;

import net.minecraft.server.MinecraftServer;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;

public interface IMinecraftServer {
    void confluence$updateSecretFlag(long flag);

    boolean confluence$matchesSecretFlag(long flag);

    default boolean confluence$matchesSecretFlag(SecretSeed secretSeed) {
        return confluence$matchesSecretFlag(secretSeed.getFlag());
    }

    static boolean matchesSecretFlag(MinecraftServer server, long flag) {
        return ((IMinecraftServer) server).confluence$matchesSecretFlag(flag);
    }
}
