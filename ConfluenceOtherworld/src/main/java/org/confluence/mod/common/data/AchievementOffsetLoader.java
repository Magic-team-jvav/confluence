package org.confluence.mod.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementToast;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AchievementOffsetLoader extends ContextAwareReloadListener {
    public static volatile CompletableFuture<Void> WAITING_FOR = CompletableFuture.completedFuture(null);
    private static AchievementOffsetLoader INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private Map<ResourceLocation, Vec2> registeredAchievements = ImmutableMap.of();

    @Override
    public final CompletableFuture<Void> reload(
            PreparableReloadListener.PreparationBarrier stage,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return WAITING_FOR = CompletableFuture.supplyAsync(
                () -> prepare(resourceManager), backgroundExecutor
        ).thenCompose(stage::wait).thenAcceptAsync(this::apply, gameExecutor);
    }

    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        ResourceLocation resourceLocation = Confluence.asResource("achievement_offset.json");
        for (Resource resource : resourceManager.getResourceStack(resourceLocation)) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    ResourceLocation loc = ResourceLocation.parse(entry.getKey());
                    map.put(loc, entry.getValue());
                }
            } catch (RuntimeException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't read achievement offset {} in data pack {}", resourceLocation, resource.sourcePackId(), ioexception);
            }
        }
        return map;
    }

    protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
        ImmutableMap.Builder<ResourceLocation, Vec2> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement json = entry.getValue();
            LibUtils.VEC_2_CODEC.parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(errorMsg -> Confluence.LOGGER.warn("Could not decode achievement offset with json id {} - error: {}", location, errorMsg))
                    .ifPresent(vec2 -> builder.put(location, vec2));
        }
        this.registeredAchievements = builder.build();
    }

    public Map<ResourceLocation, Vec2> getRegisteredAchievements() {
        return registeredAchievements;
    }

    public static AchievementOffsetLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AchievementOffsetLoader();
        }
        return INSTANCE;
    }

    public static void handle(Map<ResourceLocation, Vec2> value) {
        getInstance().registeredAchievements = ImmutableMap.copyOf(value);
        AchievementToast.clearToast();
        for (ResourceLocation id : value.keySet()) {
            AchievementToast.registerToast(id);
        }
    }

    public static Map<ResourceLocation, Vec2> getDisplayOffset() {
        return getInstance().getRegisteredAchievements();
    }
}
