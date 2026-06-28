package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {
    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Confluence.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
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
        tag(ModTags.Biomes.THE_CHORUS).add(
                ModBiomes.CHORUS_FOREST,
                ModBiomes.CHORUS_PLAINS
        );
        tag(ModTags.Biomes.THE_INVERSE).add(
                ModBiomes.INVERSE_FOREST,
                ModBiomes.INVERSE_PLAINS
        );
        tag(ModTags.Biomes.THE_MOONBLIGHT).add(
                ModBiomes.MOONBLIGHT_FOREST,
                ModBiomes.MOONBLIGHT_PLAINS,
                ModBiomes.MOONLIT_DRY_SEA,
                ModBiomes.DARK_MOON_FLATS
        );
        tag(ModTags.Biomes.THE_END_SEA).add(
                ModBiomes.MOONLIT_DRY_SEA,
                ModBiomes.DARK_MOON_FLATS
        );
        tag(ModTags.Biomes.SPREADABLE).addTags(
                ModTags.Biomes.THE_CORRUPTION,
                ModTags.Biomes.THE_CRIMSON,
                ModTags.Biomes.THE_HALLOW
        ).add(ModBiomes.GLOWING_MUSHROOM);
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

                ModBiomes.GLOWING_MUSHROOM,

                ModBiomes.CHORUS_FOREST,
                ModBiomes.CHORUS_PLAINS,

                ModBiomes.INVERSE_FOREST,
                ModBiomes.INVERSE_PLAINS,

                ModBiomes.MOONBLIGHT_FOREST,
                ModBiomes.MOONBLIGHT_PLAINS,
                ModBiomes.MOONLIT_DRY_SEA,
                ModBiomes.DARK_MOON_FLATS
        );
        tag(ModTags.Biomes.IS_FOREST).addTags(
                // 出现较大群系内容扩展时更改此标签(当对应群系同时具有专属群系地下宝箱，渔获，敌怪时）
                PortTags.Biomes.IS_FOREST,
                Tags.Biomes.IS_PLAINS,
                PortTags.Biomes.IS_TAIGA,
                PortTags.Biomes.IS_SAVANNA,
                PortTags.Biomes.IS_WINDSWEPT,
                PortTags.Biomes.IS_OLD_GROWTH,
                Tags.Biomes.IS_SWAMP,
                PortTags.Biomes.IS_STONY_SHORES
        );
        tag(ModTags.Biomes.IS_FOREST)
                // 出现较大群系内容扩展时更改此标签(当对应群系同时具有专属群系地下宝箱，渔获，敌怪时）
                .add(Biomes.DRIPSTONE_CAVES,
                        Biomes.DEEP_DARK
                );
        tag(PortTags.Biomes.IS_OVERWORLD).add(
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
        tag(PortTags.Biomes.IS_NETHER).add(
                ModBiomes.ASH_FOREST,
                ModBiomes.ASH_WASTELAND
        );
        tag(PortTags.Biomes.IS_NETHER_FOREST).add(ModBiomes.ASH_FOREST);
        tag(ModTags.Biomes.VANITY_TREES_REPLACEABLE)
                .add(Biomes.PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST)
                .addTag(PortTags.Biomes.IS_BIRCH_FOREST);
        tag(BiomeTags.IS_END).add(
                ModBiomes.CHORUS_FOREST,
                ModBiomes.CHORUS_PLAINS,
                ModBiomes.INVERSE_FOREST,
                ModBiomes.INVERSE_PLAINS,
                ModBiomes.MOONBLIGHT_FOREST,
                ModBiomes.MOONBLIGHT_PLAINS,
                ModBiomes.MOONLIT_DRY_SEA,
                ModBiomes.DARK_MOON_FLATS
        );
    }
}
