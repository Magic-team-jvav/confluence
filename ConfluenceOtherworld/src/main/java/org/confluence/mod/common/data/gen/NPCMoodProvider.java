package org.confluence.mod.common.data.gen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.mood.Mood;
import org.confluence.mod.common.entity.npc.mood.MoodData;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// 生成 NPC 邻居与群系偏好；两者写入同一份 NPC 配置。
///
/// 运行时按 NPC 实体 ID 读取独立文件，附属模组可在自己的命名空间提供同格式文件
public final class NPCMoodProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public NPCMoodProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/moods");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, List<MoodData.Entry>> moods = new LinkedHashMap<>();
        Map<ResourceLocation, List<MoodData.BiomeEntry>> biomeMoods = new LinkedHashMap<>();
        put(moods, NpcEntities.GOBLIN_TINKERER, entry(Mood.LIKE, NpcEntities.DYE_TRADER), entry(Mood.LOVER, NpcEntities.MECHANIC), entry(Mood.DISLIKE, NpcEntities.CLOTHIER));
        put(moods, NpcEntities.GUIDE, entry(Mood.HATE, NpcEntities.PAINTER), entry(Mood.LIKE, NpcEntities.CLOTHIER), entry(Mood.LIKE, NpcEntities.ZOOLOGIST));
        put(moods, NpcEntities.ARMS_DEALER, entry(Mood.HATE, NpcEntities.DEMOLITIONIST), entry(Mood.LOVER, NpcEntities.NURSE));
        put(moods, NpcEntities.ANGLER, entry(Mood.LIKE, NpcEntities.DEMOLITIONIST), entry(Mood.LIKE, NpcEntities.PARTY_GIRL));
        put(moods, NpcEntities.FEMALE_ANGLER, entry(Mood.DISLIKE, NpcEntities.DEMOLITIONIST), entry(Mood.DISLIKE, NpcEntities.PARTY_GIRL));
        put(moods, NpcEntities.DYE_TRADER, entry(Mood.LIKE, NpcEntities.ARMS_DEALER), entry(Mood.LIKE, NpcEntities.PAINTER));
        put(moods, NpcEntities.DEMOLITIONIST, entry(Mood.DISLIKE, NpcEntities.ARMS_DEALER), entry(Mood.DISLIKE, NpcEntities.GOBLIN_TINKERER), entry(Mood.LIKE, NpcEntities.MECHANIC));
        put(moods, NpcEntities.PAINTER, entry(Mood.LOVER, NpcEntities.DRYAD), entry(Mood.LIKE, NpcEntities.PARTY_GIRL), entry(Mood.DISLIKE, NpcEntities.TRUFFLE));
        put(moods, NpcEntities.DRYAD, entry(Mood.DISLIKE, NpcEntities.ANGLER), entry(Mood.LIKE, NpcEntities.FEMALE_ANGLER), entry(Mood.LIKE, NpcEntities.WITCH_DOCTOR), entry(Mood.LIKE, NpcEntities.TRUFFLE));
        put(moods, NpcEntities.MERCHANT, entry(Mood.LIKE, NpcEntities.NURSE), entry(Mood.HATE, NpcEntities.ANGLER), entry(Mood.LIKE, NpcEntities.FEMALE_ANGLER));
        put(moods, NpcEntities.NURSE,
                entry(Mood.LOVER, NpcEntities.ARMS_DEALER),
                entry(Mood.DISLIKE, NpcEntities.DRYAD),
                entry(Mood.DISLIKE, NpcEntities.PARTY_GIRL),
                entry(Mood.HATE, NpcEntities.ZOOLOGIST),
                entry(Mood.LIKE, NpcEntities.WIZARD));
        put(moods, NpcEntities.MECHANIC, entry(Mood.LOVER, NpcEntities.GOBLIN_TINKERER), entry(Mood.DISLIKE, NpcEntities.ARMS_DEALER), entry(Mood.HATE, NpcEntities.CLOTHIER));
        put(moods, NpcEntities.WITCH_DOCTOR, entry(Mood.LIKE, NpcEntities.DRYAD), entry(Mood.LIKE, NpcEntities.GUIDE), entry(Mood.DISLIKE, NpcEntities.NURSE), entry(Mood.HATE, NpcEntities.TRUFFLE));
        put(moods, NpcEntities.PARTY_GIRL, entry(Mood.DISLIKE, NpcEntities.MERCHANT), entry(Mood.LOVER, NpcEntities.ZOOLOGIST), entry(Mood.LOVER, NpcEntities.WIZARD));
        put(moods, NpcEntities.CLOTHIER, entry(Mood.LOVER, NpcEntities.TRUFFLE), entry(Mood.DISLIKE, NpcEntities.NURSE), entry(Mood.HATE, NpcEntities.MECHANIC));
        put(moods, NpcEntities.ZOOLOGIST, entry(Mood.LOVER, NpcEntities.WITCH_DOCTOR), entry(Mood.DISLIKE, NpcEntities.ANGLER), entry(Mood.LIKE, NpcEntities.FEMALE_ANGLER), entry(Mood.HATE, NpcEntities.ARMS_DEALER));
        put(moods, NpcEntities.TRUFFLE, entry(Mood.LOVER, NpcEntities.GUIDE), entry(Mood.LIKE, NpcEntities.DYE_TRADER), entry(Mood.DISLIKE, NpcEntities.CLOTHIER), entry(Mood.HATE, NpcEntities.WITCH_DOCTOR));
        put(moods, NpcEntities.WIZARD, entry(Mood.LIKE, NpcEntities.MERCHANT), entry(Mood.DISLIKE, NpcEntities.DYE_TRADER));
        put(moods, NpcEntities.STEAMPUNKER, entry(Mood.LOVER, NpcEntities.CYBORG), entry(Mood.LIKE, NpcEntities.PAINTER), entry(Mood.DISLIKE, NpcEntities.PARTY_GIRL), entry(Mood.DISLIKE, NpcEntities.WIZARD), entry(Mood.DISLIKE, NpcEntities.DRYAD));

        putBiome(biomeMoods, NpcEntities.GUIDE, biome(Mood.LIKE, PortTags.Biomes.IS_FOREST), biome(Mood.DISLIKE, PortTags.Biomes.IS_OCEAN));
        putBiome(biomeMoods, NpcEntities.MERCHANT, biome(Mood.LIKE, PortTags.Biomes.IS_FOREST), biome(Mood.DISLIKE, PortTags.Biomes.IS_DESERT));
        putBiome(biomeMoods, NpcEntities.NURSE, biome(Mood.LIKE, ModTags.Biomes.THE_HALLOW), biome(Mood.DISLIKE, PortTags.Biomes.IS_SNOWY));
        putBiome(biomeMoods, NpcEntities.DRYAD, biome(Mood.LIKE, PortTags.Biomes.IS_JUNGLE), biome(Mood.DISLIKE, PortTags.Biomes.IS_DESERT));
        putBiome(biomeMoods, NpcEntities.ZOOLOGIST, biome(Mood.LIKE, PortTags.Biomes.IS_FOREST), biome(Mood.DISLIKE, PortTags.Biomes.IS_DESERT));
        putBiome(biomeMoods, NpcEntities.PAINTER, biome(Mood.LIKE, PortTags.Biomes.IS_JUNGLE), biome(Mood.DISLIKE, PortTags.Biomes.IS_FOREST));
        putBiome(biomeMoods, NpcEntities.WITCH_DOCTOR, biome(Mood.LIKE, PortTags.Biomes.IS_JUNGLE), biome(Mood.DISLIKE, ModTags.Biomes.THE_HALLOW));
        putBiome(biomeMoods, NpcEntities.TRUFFLE, biome(Mood.LOVER, PortTags.Biomes.IS_MUSHROOM));

        return CompletableFuture.allOf(moods.entrySet().stream()
                .map(entry -> save(output, entry.getKey(), new MoodData.Profile(
                        entry.getValue(), biomeMoods.getOrDefault(entry.getKey(), List.of()))))
                .toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> save(CachedOutput output, ResourceLocation npcId, MoodData.Profile profile) {
        DataResult<JsonElement> encoded = MoodData.Profile.CODEC.encodeStart(JsonOps.INSTANCE, profile);
        JsonElement json = encoded.result().orElseThrow(() ->
                new IllegalStateException("Unable to encode NPC mood " + npcId + ": "
                        + encoded.error().map(DataResult.PartialResult::message)
                        .orElse("unknown error")));
        Path path = pathProvider.json(npcId);
        return DataProvider.saveStable(output, json, path);
    }

    private static void put(Map<ResourceLocation, List<MoodData.Entry>> moods, RegistryObject<? extends EntityType<?>> owner, MoodData.Entry... entries) {
        if (owner.getId() != null) {
            moods.put(Confluence.asResource(owner.getId().getPath()), List.of(entries));
        }
    }

    private static MoodData.Entry entry(Mood mood, RegistryObject<? extends EntityType<?>> target) {
        return new MoodData.Entry(mood, target.get());
    }

    private static void putBiome(Map<ResourceLocation, List<MoodData.BiomeEntry>> moods, RegistryObject<? extends EntityType<?>> owner, MoodData.BiomeEntry... entries) {
        if (owner.getId() != null) {
            moods.put(Confluence.asResource(owner.getId().getPath()), List.of(entries));
        }
    }

    private static MoodData.BiomeEntry biome(Mood mood, TagKey<Biome> tag) {
        return new MoodData.BiomeEntry(mood, tag);
    }

    @Override
    public String getName() {
        return "Confluence NPC Moods";
    }
}
