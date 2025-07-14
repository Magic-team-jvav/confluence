package org.confluence.mod.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.confluence.lib.common.data.IdFixer;

public class BannedBiomeNoiseBasedChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<BannedBiomeNoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MultiNoiseBiomeSource.CODEC.fieldOf("biome_source").forGetter(BannedBiomeNoiseBasedChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(BannedBiomeNoiseBasedChunkGenerator::generatorSettings),
            ResourceKey.codec(Registries.BIOME).mapResult(IdFixer.FIX_BIOME_KEY_FUNC).fieldOf("banned_biome").forGetter(generator -> generator.bannedBiome),
            ResourceKey.codec(Registries.BIOME).mapResult(IdFixer.FIX_BIOME_KEY_FUNC).fieldOf("target_biome").forGetter(generator -> generator.targetBiome)
    ).apply(instance, BannedBiomeNoiseBasedChunkGenerator::new));
    public final ResourceKey<Biome> bannedBiome;
    public final ResourceKey<Biome> targetBiome;

    public BannedBiomeNoiseBasedChunkGenerator(MultiNoiseBiomeSource biomeSource, Holder<NoiseGeneratorSettings> settingsHolder, ResourceKey<Biome> bannedBiome, ResourceKey<Biome> targetBiome) {
        super(new BannedBiomeMultiNoiseBiomeSource(biomeSource, bannedBiome, targetBiome), settingsHolder);
        this.bannedBiome = bannedBiome;
        this.targetBiome = targetBiome;
    }

    @Override
    public MultiNoiseBiomeSource getBiomeSource() {
        return (MultiNoiseBiomeSource) super.getBiomeSource();
    }

    @Override
    protected MapCodec<BannedBiomeNoiseBasedChunkGenerator> codec() {
        return CODEC;
    }
}
