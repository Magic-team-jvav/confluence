package org.confluence.mod.common.data;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.ProfilerFiller;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementToast;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record AchievementOffset(float x, float y, boolean hideLink, Category category, int order) {
    public static final Codec<AchievementOffset> SERVER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(AchievementOffset::x),
            Codec.FLOAT.fieldOf("y").forGetter(AchievementOffset::y),
            PortCodecExtension.lenientOptionalFieldOf(Codec.BOOL, "hide_link", true).forGetter(AchievementOffset::hideLink)
    ).apply(instance, (x, y, hideLink) -> new AchievementOffset(x, y, hideLink, Category.COLLECTOR, 0)));
    public static final Codec<AchievementOffset> CLIENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Category.CODEC.fieldOf("category").forGetter(AchievementOffset::category),
            Codec.INT.fieldOf("order").forGetter(AchievementOffset::order)
    ).apply(instance, (category, order) -> new AchievementOffset(0, 0, false, category, order)));

    public AchievementOffset(float x, float y, Category category, int order) {
        this(x, y, true, category, order);
    }

    public enum Category implements StringRepresentable, PortTranslatableEnum {
        COLLECTOR,
        EXPLORER,
        SLAYER,
        CHALLENGER;

        public static final Category[] CATEGORIES = values();
        public static final Codec<Category> CODEC = StringRepresentable.fromEnum(() -> CATEGORIES);
        public static final PortStreamCodec<FriendlyByteBuf, Category> STREAM_CODEC = LibStreamCodecUtils.fromEnum(CATEGORIES);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("achievements.confluence.category." + getSerializedName());
        }
    }

    public static class Loader extends SingleJsonFileReloadListener {
        public static volatile CompletableFuture<Void> WAITING_FOR = CompletableFuture.completedFuture(null);
        private static Loader INSTANCE;
        private final Codec<AchievementOffset> codec;
        private Map<ResourceLocation, AchievementOffset> registeredAchievements = ImmutableMap.of();

        private Loader(Codec<AchievementOffset> codec) {
            this.codec = codec;
        }

        @Override
        public final CompletableFuture<Void> reload(
                PreparationBarrier stage,
                ResourceManager resourceManager,
                ProfilerFiller preparationsProfiler,
                ProfilerFiller reloadProfiler,
                Executor backgroundExecutor,
                Executor gameExecutor
        ) {
            return WAITING_FOR = super.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
            ImmutableMap.Builder<ResourceLocation, AchievementOffset> builder = ImmutableMap.builder();
            for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
                ResourceLocation location = entry.getKey();
                JsonElement json = entry.getValue();
                codec.parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(errorMsg -> Confluence.LOGGER.warn("Could not decode achievement offset with json id {} - error: {}", location, errorMsg))
                        .ifPresent(offset -> builder.put(location, offset));
            }
            this.registeredAchievements = builder.build();
        }

        public static Map<ResourceLocation, AchievementOffset> load(ResourceManager manager) {
            Loader loader = new Loader(CLIENT_CODEC);
            loader.apply(loader.prepare(manager));
            return loader.registeredAchievements;
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

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader(SERVER_CODEC);
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
}
