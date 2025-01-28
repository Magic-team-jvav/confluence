package org.confluence.mod.mixed;

import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;

public interface IMinecraftServer {
    void confluence$updateSecretFlag(long flag);

    boolean confluence$matchesSecretFlag(SecretSeed secretSeed);
}
