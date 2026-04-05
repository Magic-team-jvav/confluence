package org.confluence.mod.common.init;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import it.unimi.dsi.fastutil.objects.ObjectLongImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
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
    public static final SecretSeed DONT_DIG_UP = register(Confluence.asResource("dont_dig_up"), DontDigUp::new); //             100000_00000000
    public static final SecretSeed NO_TRAPS = register(Confluence.asResource("no_traps"), NoTraps::new); //                    1000000_00000000
    public static final SecretSeed GET_FIXED_BOI = register(Confluence.asResource("get_fixed_boi"), GetFixedBoi::new); //     10000000_00000000
    public static final SecretSeed SKYBLOCK = register(Confluence.asResource("skyblock"), Skyblock::new); //                 100000000_00000000

    // 新增的
    public static final SecretSeed BOULDER_WORLD = register(Confluence.asResource("boulder_world"), BoulderWorld::new); //  1000000000_00000000
    public static final SecretSeed REALLY_SMALL = register(ReallySmall.ID, ReallySmall::new); //                                10000000000_00000000
    public static final SecretSeed TOO_EASY = register(Confluence.asResource("too_easy"), TooEasy::new); //              100000000000_00000000
    public static final SecretSeed NEVER_SLEEP = register(Confluence.asResource("never_sleep"), NeverSleep::new); //    1000000000000_00000000

    /// 0b00000001: 1.腐化
    /// 0b00000010: 2.猩红
    /// 0b00000100: 3.肉后
    /// 0b00001000: 4.毕业
    /// 5 ~ 8: 暂无，也许附属模组可以利用这个空缺
    ///
    /// @see org.confluence.mod.mixed.IWorldOptions
    private static SecretSeed register(ResourceLocation id, BiFunction<Long, ResourceLocation, SecretSeed> function) {
        SecretSeed secretSeed = function.apply(1L << (VALUES.size() + RESERVE), id);
        VALUES.add(secretSeed);
        BY_ID.put(id, secretSeed);
        return secretSeed;
    }

    public static ObjectBooleanPair<WorldOptions> matchSeed(String seed, WorldOptions worldOptions) {
        ObjectLongPair<OptionalLong> pair = tryMatch(seed, VALUES);
        IWorldOptions.of(worldOptions).confluence$withSecretFlag(pair.rightLong());
        return new ObjectBooleanImmutablePair<>(worldOptions.withSeed(OptionalLong.of(pair.key().orElseGet(WorldOptions::randomSeed))), pair.rightLong() != 0);
    }

    /// 可以匹配
    ///
    /// secret seed
    ///
    /// secret seed 1 | secret seed 2 | (...)
    ///
    /// secret seed 1 | secret seed 2 | (...) | identifier
    public static ObjectLongPair<OptionalLong> tryMatch(String seed, List<SecretSeed> seeds) {
        String[] split = seed.split("\\|");
        OptionalLong identifier = OptionalLong.empty();
        long flag = 0;
        if (split.length > 1) {
            int endIndex = split.length - 1;
            boolean missmatchLast = true;
            for (int i = 0; i < split.length; i++) {
                String s = split[i].trim().toLowerCase(Locale.ROOT);
                for (SecretSeed secretSeed : seeds) {
                    if (secretSeed.match(s)) {
                        flag |= secretSeed.getFlag();
                        if (i == endIndex) { // 如果是最后一个，先尝试进行匹配，匹配失败则作为标识符
                            missmatchLast = false;
                            break;
                        }
                    }
                }
            }
            if (missmatchLast) {
                try {
                    identifier = OptionalLong.of(Long.parseLong(split[endIndex].trim()));
                } catch (Exception ignored) {}
            }
        } else {
            String s = seed.trim().toLowerCase(Locale.ROOT);
            for (SecretSeed secretSeed : seeds) {
                if (secretSeed.match(s)) {
                    flag = secretSeed.getFlag();
                    break;
                }
            }
        }
        return new ObjectLongImmutablePair<>(identifier, flag);
    }

    public static long fixWorldOptions(long secretFlag, int lastVersion) {
        if (lastVersion == 0) {
            lastVersion = 1;
            long lastNoTraps = 0b100000_00000000;
            long lastDontDigUp = 0b1000000_00000000;
            long lastBoulderWorld = 0b100000000_00000000;
            long currentNoTraps = 0b1000000_00000000;
            long currentDontDigUp = 0b100000_00000000;
            long currentBoulderWorld = 0b1000000000_00000000;
            boolean hasNoTraps = (secretFlag & lastNoTraps) == lastNoTraps;
            boolean hasDontDigUp = (secretFlag & lastDontDigUp) == lastDontDigUp;
            boolean hasBoulderWorld = (secretFlag & lastBoulderWorld) == lastBoulderWorld;
            if (hasNoTraps && !hasDontDigUp) {
                secretFlag = secretFlag - lastNoTraps + currentNoTraps;
            } else if (!hasNoTraps && hasDontDigUp) {
                secretFlag = secretFlag - lastDontDigUp + currentDontDigUp;
            }
            if (hasBoulderWorld) {
                secretFlag = secretFlag - lastBoulderWorld + currentBoulderWorld;
            }
        }
        // if (lastVersion == 1) lastVersion = 2;
        return secretFlag;
    }
}
