package org.confluence.mod.mixed;

import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;

public interface IMinecraftServer {
    void confluence$updateSecretFlag(long flag);

    boolean confluence$matchesSecretFlag(long flag);

    default boolean confluence$matchesSecretFlag(SecretSeed secretSeed) {
        return confluence$matchesSecretFlag(secretSeed.getFlag());
    }
}
