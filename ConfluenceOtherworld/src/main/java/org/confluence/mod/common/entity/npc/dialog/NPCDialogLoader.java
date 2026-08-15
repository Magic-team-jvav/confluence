package org.confluence.mod.common.entity.npc.dialog;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class NPCDialogLoader extends SimpleJsonResourceReloadListener {
    private static final Codec<Map<EntityType<?>, NPCDialog>> CODEC =
            Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), NPCDialog.CODEC);
    /**
     * SimpleJsonResourceReloadListener 会移除监听目录和 .json 后缀。
     */
    private static final ResourceLocation PATH = Confluence.asResource("dialogs");
    private static NPCDialogLoader INSTANCE;
    private Map<EntityType<?>, NPCDialog> dialogs = ImmutableMap.of();

    public NPCDialogLoader() {
        super(new GsonBuilder().create(), "npc");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        JsonElement json = map.get(PATH);
        if (json == null) return;
        DataResult<Map<EntityType<?>, NPCDialog>> decoded = CODEC.parse(JsonOps.INSTANCE, json);
        var parsed = decoded.result();
        if (parsed.isEmpty()) {
            String reason = decoded.error()
                    .map(DataResult.PartialResult::message)
                    .orElse("unknown decode error");
            Confluence.LOGGER.error("Cannot load npc/dialogs.json: {}", reason);
            Confluence.LOGGER.error(
                    "NPC dialog reload was rejected; the previous valid table remains active");
            return;
        }
        this.dialogs = ImmutableMap.copyOf(parsed.get());
        Confluence.LOGGER.info("Loaded {} NPC dialog tables", dialogs.size());
    }

    @Nullable
    public NPCDialog getDialog(EntityType<?> type) {
        return dialogs.get(type);
    }

    @Nullable
    public String getRandomDialogKey(RandomSource random, EntityType<?> type) {
        NPCDialog dialog = getDialog(type);
        return dialog == null || dialog.isEmpty() ? null : dialog.randomKey(random);
    }

    public static NPCDialogLoader getInstance() {
        if (INSTANCE == null) INSTANCE = new NPCDialogLoader();
        return INSTANCE;
    }
}
