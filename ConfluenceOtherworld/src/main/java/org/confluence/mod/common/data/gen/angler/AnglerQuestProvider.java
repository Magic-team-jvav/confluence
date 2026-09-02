package org.confluence.mod.common.data.gen.angler;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.AnglerCatchCondition;
import org.confluence.mod.common.data.saved.AnglerQuestEntry;
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
public final class AnglerQuestProvider implements DataProvider {
    private static final com.mojang.serialization.Codec<List<AnglerQuestEntry>> CODEC = AnglerQuestEntry.CODEC.listOf().fieldOf("quests").codec();
    private static final List<TagKey<Biome>> EVIL_BIOMES = List.of(ModTags.Biomes.THE_CORRUPTION, ModTags.Biomes.THE_CRIMSON, ModTags.Biomes.THE_HALLOW);
    private final PackOutput.PathProvider pathProvider;

    public AnglerQuestProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "angler_quests");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        DataResult<JsonElement> encoded = CODEC.encodeStart(JsonOps.INSTANCE, buildEntries());
        JsonElement json = encoded.result().orElseThrow(() -> new IllegalStateException("Unable to encode angler quests: " + encoded.error().map(DataResult.PartialResult::message).orElse("unknown error")));
        Path path = pathProvider.json(Confluence.asResource("default"));
        return DataProvider.saveStable(output, json, path);
    }

    public static List<AnglerQuestEntry> buildEntries() {
        List<AnglerQuestEntry> entries = new ArrayList<>();
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

        e(entries, QuestedFishes.SLIMEFISH, water(forest, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.ZOMBIE_FISH, water(forest, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.BUNNYFISH, water(forest, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.DYNAMITE_FISH, water(List.of(), EVIL_BIOMES, surfaceY, spaceY));
        e(entries, QuestedFishes.ANGELFISH, water(List.of(), EVIL_BIOMES, spaceY, ultraY));
        e(entries, QuestedFishes.CLOUDFISH, water(List.of(), EVIL_BIOMES, spaceY, ultraY));
        e(entries, QuestedFishes.WYVERNTAIL, water(List.of(), EVIL_BIOMES, spaceY, ultraY));
        e(entries, QuestedFishes.FALLEN_STARFISH, water(List.of(), List.of(), spaceY, ultraY));
        e(entries, QuestedFishes.THE_FISH_OF_CTHULHU, water(List.of(), EVIL_BIOMES, surfaceY, ultraY));
        e(entries, QuestedFishes.HARPYFISH, water(List.of(), EVIL_BIOMES, surfaceY, ultraY));
        e(entries, QuestedFishes.BATFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        e(entries, QuestedFishes.BONEFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        e(entries, QuestedFishes.JEWELFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        e(entries, QuestedFishes.SPIDERFISH, water(List.of(), EVIL_BIOMES, caveY, surfaceY));
        e(entries, QuestedFishes.DIRTFISH, water(List.of(), EVIL_BIOMES, undergroundY, spaceY));
        e(entries, QuestedFishes.DEMONIC_HELLFISH, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        e(entries, QuestedFishes.FISHOTRON, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        e(entries, QuestedFishes.GUIDE_VOODOO_FISH, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        e(entries, QuestedFishes.HUNGERFISH, water(List.of(), EVIL_BIOMES, caveY, undergroundY));
        e(entries, QuestedFishes.CATFISH, water(jungle, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.DERPFISH, water(jungle, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.TROPICAL_BARRACUDA, water(jungle, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.MUDFISH, water(jungle, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.SCARAB_FISH, water(desert, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.SCORPIO_FISH, water(desert, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.CAPN_TUNABEARD, water(ocean, List.of(), surfaceY, ultraY));
        e(entries, QuestedFishes.CLOWNFISH, water(ocean, List.of(), surfaceY, ultraY));
        e(entries, QuestedFishes.PENGFISH, water(snowy, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.TUNDRA_TROUT, water(snowy, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.FISHRON, water(snowy, List.of(), caveY, surfaceY));
        e(entries, QuestedFishes.MUTANT_FLINXFIN, water(snowy, List.of(), caveY, surfaceY));
        e(entries, QuestedFishes.EATER_OF_PLANKTON, water(corruption, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.INFECTED_SCABBARDFISH, water(corruption, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.CURSEDFISH, water(corruption, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.BLOODY_MANOWAR, water(crimson, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.ICHORFISH, water(crimson, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.PIXIEFISH, water(hallow, List.of(), surfaceY, ultraY));
        e(entries, QuestedFishes.UNICORN_FISH, water(hallow, List.of(), surfaceY, spaceY));
        e(entries, QuestedFishes.MIRAGE_FISH, water(hallow, List.of(), caveY, surfaceY));
        e(entries, QuestedFishes.AMANITA_FUNGIFIN, water(mushroom, List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        e(entries, QuestedFishes.BUMBLEBEE_TUNA, fluid(PortTags.Fluids.HONEY));
        return List.copyOf(entries);
    }

    private static AnglerCatchCondition water(List<TagKey<Biome>> biomes, List<TagKey<Biome>> excluded, int minY, int maxY) {
        return new AnglerCatchCondition(biomes, excluded, minY, maxY, Optional.of(PortTags.Fluids.WATER));
    }

    private static AnglerCatchCondition fluid(TagKey<Fluid> fluid) {
        return new AnglerCatchCondition(List.of(), List.of(), Integer.MIN_VALUE, Integer.MAX_VALUE, Optional.of(fluid));
    }

    private static void e(List<AnglerQuestEntry> entries, ItemLike fish, AnglerCatchCondition condition) {
        entries.add(new AnglerQuestEntry(new ItemStack(fish), condition));
    }

    @Override
    public String getName() {
        return "Confluence Angler Quests";
    }
}
