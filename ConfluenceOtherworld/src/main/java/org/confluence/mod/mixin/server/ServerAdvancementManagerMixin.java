package org.confluence.mod.mixin.server;

import net.minecraft.Util;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin {
    @Shadow
    private Map<ResourceLocation, AdvancementHolder> advancements;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void confluence$setLocation(CallbackInfo ci) {
        AchievementOffsetLoader.WAITING_FOR.thenAcceptAsync(v -> {
            for (Map.Entry<ResourceLocation, AchievementOffset> entry : AchievementOffsetLoader.getDisplayOffset().entrySet()) {
                AdvancementHolder advancement = advancements.get(entry.getKey());
                if (advancement == null) {
                    Confluence.LOGGER.warn("Unknown advancement {}", entry.getKey());
                } else {
                    advancement.value().display().ifPresent(displayInfo -> {
                        AchievementOffset offset = entry.getValue();
                        displayInfo.setLocation(offset.x(), offset.y());
                    });
                }
            }
        }, Util.backgroundExecutor());
    }
}
