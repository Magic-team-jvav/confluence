package org.confluence.mod.common.worldgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.confluence.mod.mixed.IMinecraftServer;

import java.util.function.Function;
import java.util.stream.Stream;

public class SecretFlagPlacementModifier extends PlacementModifier {
    public static final MapCodec<SecretFlagPlacementModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codec.LONG, Codec.STRING).fieldOf("flag").forGetter(config -> Either.left(config.flag)),
            Codec.BOOL.lenientOptionalFieldOf("flip", false).forGetter(modifier -> modifier.flip)
    ).apply(instance, (n, b) -> new SecretFlagPlacementModifier(n.map(Function.identity(), str -> {
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

    private final long flag;
    private final boolean flip;

    public SecretFlagPlacementModifier(long flag, boolean flip) {
        this.flag = flag;
        this.flip = flip;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        if (IMinecraftServer.matchesSecretFlag(context.getLevel().getLevel().getServer(), flag) != flip) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.SECRET_FLAG_PLACEMENT_MODIFIER.get();
    }
}
