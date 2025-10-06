package org.confluence.mod.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.confluence.mod.mixed.IMinecraftServer;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface SecretFlagMatcher {
    long secretFlag();

    /**
     * @return 如果为true，则反向匹配
     */
    boolean flipMatch();

    default boolean matchesSecretFlag() {
        return IMinecraftServer.matchesSecretFlag(secretFlag()) != flipMatch();
    }

    static <M extends SecretFlagMatcher> MapCodec<M> createMapCodec(BiFunction<Long, Boolean, M> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.either(Codec.LONG, Codec.STRING).fieldOf("flag").forGetter(matcher -> Either.left(matcher.secretFlag())),
                Codec.BOOL.lenientOptionalFieldOf("flip", false).forGetter(SecretFlagMatcher::flipMatch)
        ).apply(instance, (n, b) -> factory.apply(n.map(Function.identity(), str -> {
            long ret = 0;
            if (str.startsWith("&")) {
                try {
                    ret = Long.parseLong(str.substring(1), 2);
                } catch (NumberFormatException ignored) {}
            } else {
                ret = ModSecretSeeds.CODEC.parse(JavaOps.INSTANCE, str).mapOrElse(SecretSeed::getFlag, err -> {
                    try {
                        return Long.parseLong(str);
                    } catch (NumberFormatException ignored) {
                        return 0;
                    }
                }).longValue();
            }
            if (ret == 0) {
                Confluence.LOGGER.error("Can not decode '{}'!", str);
            }
            return ret;
        }), b)));
    }
}
