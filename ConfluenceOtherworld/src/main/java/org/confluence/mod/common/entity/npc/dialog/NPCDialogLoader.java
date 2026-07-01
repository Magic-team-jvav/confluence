package org.confluence.mod.common.entity.npc.dialog;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
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
            Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), Codec.STRING.listOf().xmap(NPCDialog::new, NPCDialog::keys));
    private static final ResourceLocation PATH = Confluence.asResource("npc/dialogs.json");
    private static NPCDialogLoader INSTANCE;
    private Map<EntityType<?>, NPCDialog> dialogs = ImmutableMap.of();

    public NPCDialogLoader() {
        super(new GsonBuilder().create(), "npc");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        JsonElement json = map.get(PATH);
        if (json == null) return;
        this.dialogs = CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(msg -> Confluence.LOGGER.warn("Failed to parse npc/dialogs.json: {}", msg))
                .orElse(ImmutableMap.of());
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
