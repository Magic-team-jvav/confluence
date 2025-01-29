package org.confluence.mod.common.worldgen.feature;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.IMinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SecretFlagFeature extends Feature<SecretFlagFeature.Config> {
    public SecretFlagFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<Config> context) {
        if (((IMinecraftServer) context.level().getLevel().getServer()).confluence$matchesSecretFlag(context.config().flag)) {
            Either<ResourceLocation, ConfiguredFeature<?, ?>> subFeature = context.config().subFeature;
            return subFeature.map(
                    id -> context.level().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(id).orElseThrow().value()
                            .place(context.level(), context.chunkGenerator(), context.random(), context.origin()),
                    cf -> cf.place(context.level(), context.chunkGenerator(), context.random(), context.origin())
            );
        }
        return false;
    }

    public record Config(Either<ResourceLocation, ConfiguredFeature<?, ?>> subFeature, long flag) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.either(ResourceLocation.CODEC, ConfiguredFeature.DIRECT_CODEC).fieldOf("sub_feature").forGetter(Config::subFeature),
                Codec.either(Codec.LONG, Codec.STRING).fieldOf("flag").forGetter(config -> Either.left(config.flag))
        ).apply(instance, (sf, num) -> new Config(sf, num.map(Function.identity(), str -> {
            long ret = 0;
            if (str.startsWith("&")) {
                try {
                    ret = Long.parseLong(str.substring(1), 2);
                } catch (NumberFormatException ignored) {}
            } else {
                try {
                    ret = Long.parseLong(str);
                } catch (NumberFormatException ignored) {}
            }
            if (ret == 0) {
                Confluence.LOGGER.error("Can not decode '{}'!", str);
            }
            return ret;
        }))));
    }
}
