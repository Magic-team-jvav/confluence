package org.confluence.mod.mixed;

import net.minecraft.server.MinecraftServer;
import org.confluence.mod.api.SecretFlagMatcher;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;

public interface IMinecraftServer {
    void confluence$updateSecretFlag(long flag);

    long confluence$getSecretFlag();

    default boolean matchesSecretFlag(long flag) {
        return SecretFlagMatcher.matchesSecretFlag(confluence$getSecretFlag(), flag);
    }

    default boolean matchesSecretFlag(SecretSeed secretSeed) {
        return matchesSecretFlag(secretSeed.getFlag());
    }

    default boolean equalsSecretFlag(long flag) {
        return SecretFlagMatcher.equalsSecretFlag(confluence$getSecretFlag(), flag);
    }

    static boolean isGraduated(MinecraftServer server) {
        return IMinecraftServer.of(server).matchesSecretFlag(IWorldOptions.GRADUATED);
    }

    static IMinecraftServer of(MinecraftServer server) {
        return (IMinecraftServer) server;
    }
}
