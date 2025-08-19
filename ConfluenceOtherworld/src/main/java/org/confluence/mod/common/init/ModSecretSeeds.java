package org.confluence.mod.common.init;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.secret_seed.*;
import org.confluence.mod.mixed.IWorldOptions;

import java.util.*;
import java.util.function.BiFunction;

public final class ModSecretSeeds {
    public static final List<SecretSeed> VALUES = new ArrayList<>();
    public static final Map<ResourceLocation, SecretSeed> BY_ID = new Hashtable<>();
    public static final Codec<SecretSeed> CODEC = ResourceLocation.CODEC.xmap(BY_ID::get, SecretSeed::getId);
    public static final int RESERVE = 8;

    public static final SecretSeed DRUNK_WORLD = register(Confluence.asResource("drunk_world"), DrunkWorld::new); //                 1_00000000
    public static final SecretSeed NOT_THE_BEES = register(Confluence.asResource("not_the_bees"), NotTheBees::new); //              10_00000000
    public static final SecretSeed FOR_THE_WORTHY = register(Confluence.asResource("for_the_worthy"), ForTheWorthy::new); //       100_00000000
    public static final SecretSeed CELEBRATIONMK10 = register(Confluence.asResource("celebrationmk10"), Celebrationmk10::new); // 1000_00000000
    public static final SecretSeed THE_CONSTANT = register(Confluence.asResource("the_constant"), TheConstant::new); //          10000_00000000
    public static final SecretSeed NO_TRAPS = register(Confluence.asResource("no_traps"), NoTraps::new); //                     100000_00000000
    public static final SecretSeed DONT_DIG_UP = register(Confluence.asResource("dont_dig_up"), DontDigUp::new); //            1000000_00000000
    public static final SecretSeed GET_FIXED_BOI = register(Confluence.asResource("get_fixed_boi"), GetFixedBoi::new); //     10000000_00000000

    // 新增的
    public static final SecretSeed BOULDER_WORLD = register(Confluence.asResource("boulder_world"), BoulderWorld::new); //   100000000_00000000

    /**
     * 0b00000001: 1.腐化<br>
     * 0b00000010: 2.猩红<br>
     * 0b00000100: 3.肉后<br>
     * 0b00001000: 4.毕业<br>
     * 5 ~ 8: 暂无，也许附属模组可以利用这个空缺
     *
     * @see org.confluence.mod.mixed.IWorldOptions
     */
    private static SecretSeed register(ResourceLocation id, BiFunction<Long, ResourceLocation, SecretSeed> function) {
        SecretSeed secretSeed = function.apply(1L << (VALUES.size() + RESERVE), id);
        VALUES.add(secretSeed);
        BY_ID.put(id, secretSeed);
        return secretSeed;
    }

    public static Pair<SecretSeed, WorldOptions> matchSeed(String seed, WorldOptions worldOptions) {
        String s = seed;
        String[] split = seed.split("\\|");
        OptionalLong l = OptionalLong.empty();
        if (split.length > 1) {
            s = split[0];
            try {
                l = OptionalLong.of(Long.parseLong(split[1].trim()));
            } catch (Exception ignored) {}
        }
        s = s.trim().toLowerCase(Locale.ROOT);
        for (SecretSeed secretSeed : VALUES) {
            if (secretSeed.match(s)) {
                ((IWorldOptions) worldOptions).confluence$withSecretFlag(secretSeed.getFlag());
                return new Pair<>(secretSeed, worldOptions.withSeed(l.isPresent() ? l : OptionalLong.of(WorldOptions.randomSeed())));
            }
        }
        return new Pair<>(null, worldOptions);
    }
}
