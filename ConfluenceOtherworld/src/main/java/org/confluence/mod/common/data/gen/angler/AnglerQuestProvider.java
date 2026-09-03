package org.confluence.mod.common.data.gen.angler;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AnglerQuestLoader;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.QuestedFishes;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/// 生成内置渔夫任务池。运行时从同一 Codec 的数据包资源加载，附属模组可追加自己的任务文件。
public class AnglerQuestProvider implements DataProvider {
    protected static final com.mojang.serialization.Codec<List<AnglerQuestLoader.Entry>> CODEC = AnglerQuestLoader.Entry.CODEC.listOf().fieldOf("quests").codec();
    protected static final List<TagKey<Biome>> EVIL_BIOMES = List.of(ModTags.Biomes.THE_CORRUPTION, ModTags.Biomes.THE_CRIMSON, ModTags.Biomes.THE_HALLOW);

    protected final PackOutput.PathProvider pathProvider;

    public AnglerQuestProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "angler_quests");
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        ArrayList<AnglerQuestLoader.Entry> entries = new ArrayList<>();
        buildEntries(entries);
        DataResult<JsonElement> encoded = CODEC.encodeStart(JsonOps.INSTANCE, entries);
        JsonElement json = encoded.result().orElseThrow(() -> new IllegalStateException("Unable to encode angler quests: " + encoded.error().map(DataResult.PartialResult::message).orElse("unknown error")));
        Path path = pathProvider.json(Confluence.asResource("default"));
        return DataProvider.saveStable(output, json, path);
    }

    public void buildEntries(List<AnglerQuestLoader.Entry> entries) {
        List<TagKey<Biome>> forest = List.of(PortTags.Biomes.IS_FOREST);
        List<TagKey<Biome>> jungle = List.of(PortTags.Biomes.IS_JUNGLE, PortTags.Biomes.IS_LUSH);
        List<TagKey<Biome>> desert = List.of(PortTags.Biomes.IS_DESERT);
        List<TagKey<Biome>> ocean = List.of(PortTags.Biomes.IS_OCEAN, PortTags.Biomes.IS_BEACH);
        List<TagKey<Biome>> snowy = List.of(PortTags.Biomes.IS_SNOWY, PortTags.Biomes.IS_ICY);
        List<TagKey<Biome>> corruption = List.of(ModTags.Biomes.THE_CORRUPTION);
        List<TagKey<Biome>> crimson = List.of(ModTags.Biomes.THE_CRIMSON);
        List<TagKey<Biome>> hallow = List.of(ModTags.Biomes.THE_HALLOW);
        List<TagKey<Biome>> mushroom = List.of(PortTags.Biomes.IS_MUSHROOM);
        int caveY = OverworldUtils.getCaveY();
        int undergroundY = OverworldUtils.getUndergroundY();
        int surfaceY = OverworldUtils.getSurfaceY();
        int spaceY = OverworldUtils.getSpaceY();
        int ultraY = OverworldUtils.getUltraY();

        add(entries, QuestedFishes.SLIMEFISH, water(forest, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.ZOMBIE_FISH, water(forest, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.BUNNYFISH, water(forest, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.DYNAMITE_FISH, water(List.of(), EVIL_BIOMES, surfaceY, spaceY));
        add(entries, QuestedFishes.ANGELFISH, water(List.of(), EVIL_BIOMES, spaceY, ultraY));
        add(entries, QuestedFishes.CLOUDFISH, water(List.of(), EVIL_BIOMES, spaceY, ultraY));
        add(entries, QuestedFishes.WYVERNTAIL, water(List.of(), EVIL_BIOMES, spaceY, ultraY));
        add(entries, QuestedFishes.FALLEN_STARFISH, water(List.of(), List.of(), spaceY, ultraY));
        add(entries, QuestedFishes.THE_FISH_OF_CTHULHU, water(List.of(), EVIL_BIOMES, surfaceY, ultraY));
        add(entries, QuestedFishes.HARPYFISH, water(List.of(), EVIL_BIOMES, surfaceY, ultraY));
        add(entries, QuestedFishes.BATFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        add(entries, QuestedFishes.BONEFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        add(entries, QuestedFishes.JEWELFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        add(entries, QuestedFishes.SPIDERFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        add(entries, QuestedFishes.DIRTFISH, water(List.of(), EVIL_BIOMES, undergroundY, spaceY));
        add(entries, QuestedFishes.DEMONIC_HELLFISH, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        add(entries, QuestedFishes.FISHOTRON, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        add(entries, QuestedFishes.GUIDE_VOODOO_FISH, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        add(entries, QuestedFishes.HUNGERFISH, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        add(entries, QuestedFishes.CATFISH, water(jungle, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.DERPFISH, water(jungle, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.TROPICAL_BARRACUDA, water(jungle, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.MUDFISH, water(jungle, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.SCARAB_FISH, water(desert, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.SCORPIO_FISH, water(desert, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.CAPN_TUNABEARD, water(ocean, List.of(), surfaceY, ultraY));
        add(entries, QuestedFishes.CLOWNFISH, water(ocean, List.of(), surfaceY, ultraY));
        add(entries, QuestedFishes.PENGFISH, water(snowy, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.TUNDRA_TROUT, water(snowy, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.FISHRON, water(snowy, List.of(), caveY, surfaceY));
        add(entries, QuestedFishes.MUTANT_FLINXFIN, water(snowy, List.of(), caveY, surfaceY));
        add(entries, QuestedFishes.EATER_OF_PLANKTON, water(corruption, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.INFECTED_SCABBARDFISH, water(corruption, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.CURSEDFISH, water(corruption, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.BLOODY_MANOWAR, water(crimson, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.ICHORFISH, water(crimson, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.PIXIEFISH, water(hallow, List.of(), surfaceY, ultraY));
        add(entries, QuestedFishes.UNICORN_FISH, water(hallow, List.of(), surfaceY, spaceY));
        add(entries, QuestedFishes.MIRAGE_FISH, water(hallow, List.of(), caveY, surfaceY));
        add(entries, QuestedFishes.AMANITA_FUNGIFIN, water(mushroom, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(entries, QuestedFishes.BUMBLEBEE_TUNA, fluid(PortTags.Fluids.HONEY));
    }

    protected AnglerQuestLoader.CatchCondition water(List<TagKey<Biome>> biomes, List<TagKey<Biome>> excluded, int minY, int maxY) {
        return new AnglerQuestLoader.CatchCondition(biomes, excluded, minY, maxY, Optional.of(PortTags.Fluids.WATER));
    }

    protected AnglerQuestLoader.CatchCondition fluid(TagKey<Fluid> fluid) {
        return new AnglerQuestLoader.CatchCondition(List.of(), List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE, Optional.of(fluid));
    }

    protected void add(List<AnglerQuestLoader.Entry> entries, ItemLike fish, AnglerQuestLoader.CatchCondition condition) {
        entries.add(new AnglerQuestLoader.Entry(fish.asItem(), condition));
    }

    @Override
    public String getName() {
        return "Confluence Angler Quests";
    }
}
