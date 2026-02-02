package org.confluence.terraentity.data.gen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TETags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TEBiomeTagsProvider extends TagsProvider<Biome> {

    public TEBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, lookupProvider, TerraEntity.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(TETags.Biomes.IS_EVER_WHERE).addTags(
                // 出现较大群系内容扩展时更改此标签(当对应群系同时具有专属群系地下宝箱，渔获，敌怪时）
                Tags.Biomes.IS_FOREST,
                Tags.Biomes.IS_PLAINS,
                Tags.Biomes.IS_MUSHROOM,
                Tags.Biomes.IS_TAIGA,
                Tags.Biomes.IS_SAVANNA,
                Tags.Biomes.IS_WINDSWEPT,
                Tags.Biomes.IS_OLD_GROWTH,
                Tags.Biomes.IS_SWAMP,
                Tags.Biomes.IS_STONY_SHORES
        ).add(
                Biomes.DRIPSTONE_CAVES,
                Biomes.DEEP_DARK
        );
    }
}
