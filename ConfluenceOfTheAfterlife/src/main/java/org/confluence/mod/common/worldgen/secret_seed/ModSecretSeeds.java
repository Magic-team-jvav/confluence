package org.confluence.mod.common.worldgen.secret_seed;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ModSecretSeeds {
    public static final List<SecretSeed> VALUES = new ArrayList<>();

    public static final SecretSeed DRUNK_WORLD = register(DrunkWorld::new);
    public static final SecretSeed NOT_THE_BEES = register(NotTheBees::new);
    public static final SecretSeed FOR_THE_WORTHY = register(ForTheWorthy::new);
    public static final SecretSeed CELEBRATIONMK10 = register(Celebrationmk10::new);
    public static final SecretSeed THE_CONSTANT = register(TheConstant::new);
    public static final SecretSeed NO_TRAPS = register(NoTraps::new);
    public static final SecretSeed DONT_DIG_UP = register(DontDigUp::new);
    public static final SecretSeed GET_FIXED_BOI = register(GetFixedBoi::new);

    private static SecretSeed register(Function<Long, SecretSeed> function) {
        SecretSeed secretSeed = function.apply(1L << (VALUES.size() + 2)); // 猩红腐化
        VALUES.add(secretSeed);
        return secretSeed;
    }
}
