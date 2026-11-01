package org.confluence.mod.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.confluence.mod.mixed.IMinecraftServer;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface SecretFlagMatcher {
    long secretFlag();

    /// @return 如果为true，则反向匹配
    boolean flipMatch();

    default boolean matchesSecretFlag() {
        return matchesSecretFlag(secretFlag()) != flipMatch();
    }

    static boolean matchesSecretFlag(long secretFlag, long flag) {
        return (secretFlag & flag) != 0;
    }

    /// 用于复数位的flag
    static boolean equalsSecretFlag(long secretFlag, long flag) {
        return (secretFlag & flag) == flag;
    }

    static boolean matchesSecretFlag(long flag) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            if (LibUtils.isLogicalClient()) {
                return matchesSecretFlag(ClientPacketHandler.getSecretFlag(), flag);
            }
            return false;
        }
        return IMinecraftServer.of(server).matchesSecretFlag(flag);
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
