package org.confluence.mod.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementToast;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AchievementOffsetLoader extends SingleJsonFileReloadListener {
    public static volatile CompletableFuture<Void> WAITING_FOR = CompletableFuture.completedFuture(null);
    private static AchievementOffsetLoader INSTANCE;
    private Map<ResourceLocation, AchievementOffset> registeredAchievements = ImmutableMap.of();

    @Override
    public final CompletableFuture<Void> reload(
            PreparableReloadListener.PreparationBarrier stage,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return WAITING_FOR = super.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }

    protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
        ImmutableMap.Builder<ResourceLocation, AchievementOffset> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement json = entry.getValue();
            AchievementOffset.CODEC.parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(errorMsg -> Confluence.LOGGER.warn("Could not decode achievement offset with json id {} - error: {}", location, errorMsg))
                    .ifPresent(offset -> builder.put(location, offset));
        }
        this.registeredAchievements = builder.build();
    }

    @Override
    protected ResourceLocation resourcePath() {
        return Confluence.asResource("achievement_offset.json");
    }

    @Override
    protected String identifier() {
        return "Achievement Offset";
    }

    public Map<ResourceLocation, AchievementOffset> getRegisteredAchievements() {
        return registeredAchievements;
    }

    public static AchievementOffsetLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AchievementOffsetLoader();
        }
        return INSTANCE;
    }

    public static void handle(Object2BooleanMap<ResourceLocation> value) {
        AchievementToast.clearToast();
        for (Object2BooleanMap.Entry<ResourceLocation> entry : value.object2BooleanEntrySet()) {
            AchievementToast.registerToast(entry.getKey(), entry.getBooleanValue());
        }
    }

    public static Map<ResourceLocation, AchievementOffset> getDisplayOffset() {
        return getInstance().getRegisteredAchievements();
    }
}
