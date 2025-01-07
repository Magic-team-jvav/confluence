package org.confluence.mod.mixed;

import java.util.Optional;

public interface IWorldOptions {
    void confluence$withSecretFlag(long flag);

    long confluence$getSecretFlag();

    void confluence$setLegacyCustomOptions(Optional<String> legacyCustomOptions);
}
