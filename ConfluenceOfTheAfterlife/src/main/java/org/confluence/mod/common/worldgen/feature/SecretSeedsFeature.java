package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.mod.mixed.IMinecraftServer;
import org.jetbrains.annotations.NotNull;

public class SecretSeedsFeature extends Feature<SecretSeedsFeature.Config> {
    public SecretSeedsFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<Config> context) {
        MinecraftServer server = context.level().getServer();
        if (server != null && ((IMinecraftServer) server).confluence$matchesSecretFlag(context.config().flag)) {
            return context.level().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                    .getHolder(context.config().subFeature).orElseThrow().value()
                    .place(context.level(), context.chunkGenerator(), context.random(), context.origin());
        }
        return false;
    }

    public record Config(ResourceLocation subFeature, long flag) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("sub_feature").forGetter(Config::subFeature),
                Codec.LONG.fieldOf("flag").forGetter(Config::flag)
        ).apply(instance, Config::new));
    }
}
