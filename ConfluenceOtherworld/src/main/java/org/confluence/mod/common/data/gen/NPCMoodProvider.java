package org.confluence.mod.common.data.gen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.mood.Mood;
import org.confluence.mod.common.entity.npc.mood.MoodData;
import org.confluence.mod.common.init.entity.NpcEntities;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// 生成当前 1.21 内容对应的 NPC 邻居偏好。
///
/// 运行时按 NPC 实体 ID 读取独立文件，附属模组可在自己的命名空间提供同格式文件，
/// 不需要修改本体代码。这里仅迁移 1.21 当前已经声明的关系，尚未存在的 NPC 关系不提前补写。
public final class NPCMoodProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public NPCMoodProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/moods");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, List<MoodData.Entry>> moods = new LinkedHashMap<>();
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

        return CompletableFuture.allOf(moods.entrySet().stream()
                .map(entry -> save(output, entry.getKey(), entry.getValue()))
                .toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> save(CachedOutput output, ResourceLocation npcId, List<MoodData.Entry> entries) {
        DataResult<JsonElement> encoded = MoodData.Entry.CODEC.listOf()
                .encodeStart(JsonOps.INSTANCE, entries);
        JsonElement json = encoded.result().orElseThrow(() ->
                new IllegalStateException("Unable to encode NPC mood " + npcId + ": "
                        + encoded.error().map(DataResult.PartialResult::message)
                        .orElse("unknown error")));
        Path path = pathProvider.json(npcId);
        return DataProvider.saveStable(output, json, path);
    }

    @SafeVarargs
    private static void put(Map<ResourceLocation, List<MoodData.Entry>> moods, RegistryObject<? extends EntityType<?>> owner, MoodData.Entry... entries) {
        moods.put(Confluence.asResource(owner.getId().getPath()), List.of(entries));
    }

    private static MoodData.Entry entry(Mood mood, RegistryObject<? extends EntityType<?>> target) {
        return new MoodData.Entry(mood, target.get());
    }

    @Override
    public String getName() {
        return "Confluence NPC Moods";
    }
}
