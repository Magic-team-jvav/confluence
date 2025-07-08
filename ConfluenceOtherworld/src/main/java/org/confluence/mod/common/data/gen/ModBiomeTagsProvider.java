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
        tag(Tags.Biomes.IS_ICY).add(
                ModBiomes.THE_CORRUPTION_TUNDRA,
                ModBiomes.THE_CRIMSON_TUNDRA,
                ModBiomes.THE_HALLOW_TUNDRA
        );
        tag(Tags.Biomes.IS_DESERT).add(
                ModBiomes.THE_CORRUPTION_DESERT,
                ModBiomes.THE_CRIMSON_DESERT,
                ModBiomes.THE_HALLOW_DESERT
        );
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
        tag(ModTags.Biomes.IS_FOREST).add(  // 出现较大群系内容扩展时更改此标签(当对应群系同时具有专属群系地下宝箱，渔获，敌怪时）
                Biomes.BIRCH_FOREST,
                Biomes.CHERRY_GROVE,
                Biomes.DARK_FOREST,
                Biomes.DEEP_DARK,
                Biomes.DRIPSTONE_CAVES,
                Biomes.FLOWER_FOREST,
                Biomes.FOREST,
                Biomes.MEADOW,
                Biomes.MUSHROOM_FIELDS,
                Biomes.OLD_GROWTH_BIRCH_FOREST,
                Biomes.OLD_GROWTH_PINE_TAIGA,
                Biomes.OLD_GROWTH_SPRUCE_TAIGA,
                Biomes.PLAINS,
                Biomes.RIVER,
                Biomes.SAVANNA,
                Biomes.SAVANNA_PLATEAU,
                Biomes.STONY_PEAKS,
                Biomes.STONY_SHORE,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.SWAMP,
                Biomes.MANGROVE_SWAMP,
                Biomes.TAIGA,
                Biomes.WINDSWEPT_FOREST,
                Biomes.WINDSWEPT_GRAVELLY_HILLS,
                Biomes.WINDSWEPT_HILLS,
                Biomes.WINDSWEPT_SAVANNA
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
