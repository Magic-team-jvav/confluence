package org.confluence.mod.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementToast;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AchievementOffsetLoader extends SingleJsonFileReloadListener {
    public static volatile CompletableFuture<Void> WAITING_FOR = CompletableFuture.completedFuture(null);
    private static AchievementOffsetLoader INSTANCE;
    private Map<ResourceLocation, Vec2> registeredAchievements = ImmutableMap.of();

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

    @Override
    protected ResourceLocation resourcePath() {
        return Confluence.asResource("achievement_offset.json");
    }

    @Override
    protected String identifier() {
        return "Achievement Offset";
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
