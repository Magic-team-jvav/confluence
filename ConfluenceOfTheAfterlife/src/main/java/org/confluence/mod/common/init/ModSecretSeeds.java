package org.confluence.mod.common.init;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.common.worldgen.secret_seed.*;
import org.confluence.mod.mixed.IWorldOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;
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

    // 新增的
    public static final SecretSeed BOULDER_WORLD = register(BoulderWorld::new);

    /**
     * 1: 腐化<br>
     * 2: 猩红<br>
     * 3: 肉后<br>
     * 4: 毕业<br>
     * 5 ~ 8: 暂无，也许附属模组可以利用这个空缺
     *
     * @see org.confluence.mod.mixed.IWorldOptions
     */
    private static SecretSeed register(Function<Long, SecretSeed> function) {
        SecretSeed secretSeed = function.apply(1L << (VALUES.size() + 8));
        VALUES.add(secretSeed);
        return secretSeed;
    }

    public static Pair<SecretSeed, WorldOptions> matchSeed(String seed, WorldOptions worldOptions) {
        String s = seed.trim().toLowerCase(Locale.ROOT);
        for (SecretSeed secretSeed : VALUES) {
            if (secretSeed.match(s)) {
                ((IWorldOptions) worldOptions).confluence$withSecretFlag(secretSeed.getFlag());
                return new Pair<>(secretSeed, worldOptions.withSeed(OptionalLong.of(WorldOptions.randomSeed())));
            }
        }
        return new Pair<>(null, worldOptions);
    }
}
