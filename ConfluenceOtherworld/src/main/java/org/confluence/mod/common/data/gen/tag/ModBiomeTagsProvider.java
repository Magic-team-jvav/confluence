package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {
    /// 常规地表与浅层地下结构允许出现的温带群系。
    private static final List<ResourceKey<Biome>> TEMPERATE_STRUCTURE_BIOMES = List.of(
            Biomes.BIRCH_FOREST,
            Biomes.CHERRY_GROVE,
            Biomes.DARK_FOREST,
            Biomes.FLOWER_FOREST,
            Biomes.FOREST,
            Biomes.MANGROVE_SWAMP,
            Biomes.OLD_GROWTH_BIRCH_FOREST,
            Biomes.OLD_GROWTH_SPRUCE_TAIGA,
            Biomes.RIVER,
            Biomes.PLAINS,
            Biomes.SAVANNA,
            Biomes.SAVANNA_PLATEAU,
            Biomes.STONY_PEAKS,
            Biomes.STONY_SHORE,
            Biomes.SUNFLOWER_PLAINS,
            Biomes.SWAMP,
            Biomes.TAIGA,
            Biomes.WINDSWEPT_FOREST,
            Biomes.WINDSWEPT_GRAVELLY_HILLS,
            Biomes.WINDSWEPT_HILLS,
            Biomes.WINDSWEPT_SAVANNA,
            Biomes.OLD_GROWTH_PINE_TAIGA
    );

    /// 冰雪地下小屋及宽泛地下结构允许出现的寒冷群系。
    private static final List<ResourceKey<Biome>> ICY_STRUCTURE_BIOMES = List.of(
            Biomes.FROZEN_PEAKS,
            Biomes.FROZEN_RIVER,
            Biomes.GROVE,
            Biomes.ICE_SPIKES,
            Biomes.JAGGED_PEAKS,
            Biomes.SNOWY_BEACH,
            Biomes.SNOWY_PLAINS,
            Biomes.SNOWY_SLOPES,
            Biomes.SNOWY_TAIGA
    );

    /// 丛林地下小屋及宽泛地下结构允许出现的湿润群系。
    private static final List<ResourceKey<Biome>> JUNGLE_STRUCTURE_BIOMES = List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.LUSH_CAVES);

    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Confluence.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PortTags.Biomes.IS_ICY).add(
                ModBiomes.THE_CORRUPTION_TUNDRA,
                ModBiomes.THE_CRIMSON_TUNDRA,
                ModBiomes.THE_HALLOW_TUNDRA
        );
        tag(PortTags.Biomes.IS_DESERT).add(
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
                PortTags.Biomes.IS_PLAINS,
                PortTags.Biomes.IS_TAIGA,
                PortTags.Biomes.IS_SAVANNA,
                PortTags.Biomes.IS_WINDSWEPT,
                PortTags.Biomes.IS_OLD_GROWTH,
                PortTags.Biomes.IS_SWAMP,
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
        // 同时写入原版标签，保证原版结构、生成检查与第三方数据包能识别全部本体主世界群系。
        tag(BiomeTags.IS_OVERWORLD).add(
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

        addStructureBiomeTags();
    }

    /// 生成结构白名单标签。群系集合集中维护，避免多个手写 JSON 在加入或删除群系时产生偏差。
    private void addStructureBiomeTags() {
        TagsProvider.TagAppender<Biome> enchantedSwordShrine = tag(ModTags.Biomes.HAS_STRUCTURE_ENCHANTED_SWORD_SHRINE);
        addBiomeGroups(enchantedSwordShrine, TEMPERATE_STRUCTURE_BIOMES, ICY_STRUCTURE_BIOMES, JUNGLE_STRUCTURE_BIOMES);
        enchantedSwordShrine.add(Biomes.DRIPSTONE_CAVES);

        TagsProvider.TagAppender<Biome> mineTunnels = tag(ModTags.Biomes.HAS_STRUCTURE_MINE_TUNNELS);
        addBiomeGroups(mineTunnels, TEMPERATE_STRUCTURE_BIOMES, ICY_STRUCTURE_BIOMES, JUNGLE_STRUCTURE_BIOMES);
        mineTunnels.add(Biomes.DRIPSTONE_CAVES);

        TagsProvider.TagAppender<Biome> skyVillage = tag(ModTags.Biomes.HAS_STRUCTURE_SKY_VILLAGE);
        addBiomeGroups(skyVillage, TEMPERATE_STRUCTURE_BIOMES, ICY_STRUCTURE_BIOMES, JUNGLE_STRUCTURE_BIOMES);
        skyVillage.add(Biomes.DRIPSTONE_CAVES);

        TagsProvider.TagAppender<Biome> undergroundCabins = tag(ModTags.Biomes.HAS_STRUCTURE_UNDERGROUND_CABINS);
        addBiomeGroups(undergroundCabins, TEMPERATE_STRUCTURE_BIOMES, ICY_STRUCTURE_BIOMES);
        undergroundCabins.add(Biomes.DRIPSTONE_CAVES);

        addBiomeGroups(tag(ModTags.Biomes.HAS_STRUCTURE_ICE_UNDERGROUND_CABINS), ICY_STRUCTURE_BIOMES);
        addBiomeGroups(tag(ModTags.Biomes.HAS_STRUCTURE_JUNGLE_UNDERGROUND_CABINS), JUNGLE_STRUCTURE_BIOMES);
        tag(ModTags.Biomes.HAS_STRUCTURE_NETHER_TOWER).add(Biomes.NETHER_WASTES, ModBiomes.ASH_WASTELAND);
    }

    /// 将若干语义群系集合按声明顺序写入同一个标签。
    @SafeVarargs
    private static void addBiomeGroups(TagsProvider.TagAppender<Biome> appender, List<ResourceKey<Biome>>... groups) {
        for (List<ResourceKey<Biome>> group : groups) {
            group.forEach(appender::add);
        }
    }
}
