package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {
    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Confluence.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Biomes.THE_CORRUPTION).add(
                ModBiomes.THE_CORRUPTION,
                ModBiomes.THE_CORRUPTION_DESERT,
                ModBiomes.THE_CORRUPTION_TUNDRA
        );
        tag(ModTags.Biomes.THE_CRIMSON).add(
                ModBiomes.THE_CRIMSON,
                ModBiomes.THE_CRIMSON_DESERT,
                ModBiomes.THE_CRIMSON_TUNDRA
        );
        tag(ModTags.Biomes.THE_HALLOW).add(
                ModBiomes.THE_HALLOW,
                ModBiomes.THE_HALLOW_DESERT,
                ModBiomes.THE_HALLOW_TUNDRA
        );
        tag(ModTags.Biomes.SPREADABLE).addTags(
                ModTags.Biomes.THE_CORRUPTION,
                ModTags.Biomes.THE_CRIMSON,
                ModTags.Biomes.THE_HALLOW
        );
        tag(ModTags.Biomes.IS_CONFLUENCE).add(
                ModBiomes.THE_CORRUPTION,
                ModBiomes.THE_CORRUPTION_DESERT,
                ModBiomes.THE_CORRUPTION_TUNDRA,

                ModBiomes.THE_CRIMSON,
                ModBiomes.THE_CRIMSON_DESERT,
                ModBiomes.THE_CRIMSON_TUNDRA,

                ModBiomes.THE_HALLOW,
                ModBiomes.THE_HALLOW_DESERT,
                ModBiomes.THE_HALLOW_TUNDRA,

                ModBiomes.ASH_FOREST,
                ModBiomes.ASH_WASTELAND,

                ModBiomes.GLOWING_MUSHROOM
        );
        tag(Tags.Biomes.IS_OVERWORLD).add(
                ModBiomes.THE_CORRUPTION,
                ModBiomes.THE_CORRUPTION_DESERT,
                ModBiomes.THE_CORRUPTION_TUNDRA,

                ModBiomes.THE_CRIMSON,
                ModBiomes.THE_CRIMSON_DESERT,
                ModBiomes.THE_CRIMSON_TUNDRA,

                ModBiomes.THE_HALLOW,
                ModBiomes.THE_HALLOW_DESERT,
                ModBiomes.THE_HALLOW_TUNDRA,

                ModBiomes.GLOWING_MUSHROOM
        );
        tag(Tags.Biomes.IS_NETHER).add(
                ModBiomes.ASH_FOREST,
                ModBiomes.ASH_WASTELAND
        );
        tag(Tags.Biomes.IS_NETHER_FOREST).add(ModBiomes.ASH_FOREST);
        TagAppender<Biome> yellowWillowReplaceable = tag(ModTags.Biomes.VANITY_TREES_REPLACEABLE);
        yellowWillowReplaceable.add(Biomes.PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST);
        yellowWillowReplaceable.addTag(Tags.Biomes.IS_BIRCH_FOREST);
    }
}
