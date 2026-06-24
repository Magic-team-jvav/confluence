package org.confluence.mod.common.entity.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HillOfFleshModelAnimationTable extends SimplePreparableReloadListener<ModelPositionTable> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    static ModelPositionTable table;

    private static volatile HillOfFleshModelAnimationTable INSTANCE;
    public static HillOfFleshModelAnimationTable getInstance() {
        if (INSTANCE == null) {
            synchronized (HillOfFleshModelAnimationTable.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HillOfFleshModelAnimationTable();
                }
            }
        }
        return INSTANCE;
    }

    public static ModelPositionTable getTable() {
        return table;
    }

    public static void handle(ModelPositionTable table) {
        HillOfFleshModelAnimationTable.table = table;
    }

    @Override
    protected @NotNull ModelPositionTable prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ResourceLocation location = Confluence.asResource("animationtable/hill_of_flesh_parts.json");
        Optional<Resource> file = resourceManager.getResource(location);
        if(file.isPresent()){
            try (Reader reader = file.get().openAsReader()) {
                JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                ModelPositionTable animTable = ModelPositionTable.CODEC.decode(JsonOps.INSTANCE, jsonobject).getOrThrow().getFirst();
                Map<String, Vec3KeyframeAnimation> positions = new HashMap<>();
                // 填充首尾关键帧
                for (Map.Entry<String, Vec3KeyframeAnimation> entry : animTable) {
                    var anim = entry.getValue().copyFrame(0, 40);
                    if (anim != null) {
                        positions.put(entry.getKey(), anim);
                    } else {
                        positions.put(entry.getKey(), entry.getValue());
                    }
                }
                animTable = new ModelPositionTable(positions);
                return animTable;
            } catch (RuntimeException | IOException ioexception) {
                TerraEntity.LOGGER.error("Failed to load Hill Of Flesh ModelAnimation Table {}", location, ioexception);
            }
        }
        return new ModelPositionTable(Map.of());
    }

    @Override
    protected void apply(ModelPositionTable table, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        HillOfFleshModelAnimationTable.handle(table);
    }

}
