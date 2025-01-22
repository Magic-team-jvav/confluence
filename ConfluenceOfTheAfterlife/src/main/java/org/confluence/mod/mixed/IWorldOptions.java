package org.confluence.mod.mixed;

import java.util.Optional;

public interface IWorldOptions {
    void confluence$withSecretFlag(long flag);

    long confluence$getSecretFlag();

    void confluence$setLegacyCustomOptions(Optional<String> legacyCustomOptions);

    long THE_CORRUPTION = 0b01;
    long TR_CRIMSON = 0b10;
}
