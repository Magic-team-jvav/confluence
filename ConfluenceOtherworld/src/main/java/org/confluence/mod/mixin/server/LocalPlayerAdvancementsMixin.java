package org.confluence.mod.mixin.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.util.AchievementUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/// 仅在local环境才直接读本地的成就
@Mixin(PlayerAdvancements.class)
public abstract class LocalPlayerAdvancementsMixin {
    @Shadow
    @Final
    public Map<AdvancementHolder, AdvancementProgress> progress;

    @Shadow
    @Final
    private Codec<PlayerAdvancements.Data> codec;

    @Shadow
    @Final
    private static Gson GSON;

    @Shadow
    protected abstract void applyFrom(ServerAdvancementManager advancementManager, PlayerAdvancements.Data data);

    @Shadow
    private ServerPlayer player;
    @Unique
    private Path confluence$savePath;

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    private void cacheConfluence(CallbackInfo ci, @Local(argsOnly = true) ServerPlayer player) {
        this.confluence$savePath = AchievementUtils.CONFLUENCE_ACHIEVEMENTS_DIR.resolve(player.getUUID() + ".json");
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void saveConfluence(CallbackInfo ci) { // 单独保存玩家的成就
        if (LibUtils.isSingleplayerOwner(player)) {
            Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>();
            progress.forEach((holder, progress) -> {
                if (progress.hasProgress() && AchievementOffsetLoader.getDisplayOffset().containsKey(holder.id())) {
                    map.put(holder.id(), progress);
                }
            });
            AchievementUtils.saveData(new PlayerAdvancements.Data(map), confluence$savePath, GSON, codec);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void loadConfluence(ServerAdvancementManager manager, CallbackInfo ci) {
        if (LibUtils.isSingleplayerOwner(player) && Files.isRegularFile(confluence$savePath)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(confluence$savePath, StandardCharsets.UTF_8))) {
                reader.setLenient(false);
                JsonElement element = Streams.parse(reader);
                PlayerAdvancements.Data data = codec.parse(JsonOps.INSTANCE, element).getOrThrow(JsonParseException::new);
                applyFrom(manager, data);
            } catch (JsonIOException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't access confluence achievements in {}", confluence$savePath, ioexception);
            } catch (JsonParseException jsonParseException) {
                Confluence.LOGGER.error("Couldn't parse confluence achievements in {}", confluence$savePath, jsonParseException);
            }
        }
    }
}
