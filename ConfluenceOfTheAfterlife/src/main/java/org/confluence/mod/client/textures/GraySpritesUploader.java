package org.confluence.mod.client.textures;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GraySpritesUploader implements PreparableReloadListener, AutoCloseable {
    public static final ResourceLocation LOCATION = Confluence.asResource("textures/atlas/gray.png");
    protected final GrayTextureAtlas textureAtlas;
    private final ResourceLocation atlasInfoLocation;
    private final Set<MetadataSectionSerializer<?>> metadataSections;

    public GraySpritesUploader(TextureManager textureManager) {
        this.atlasInfoLocation = ResourceLocation.withDefaultNamespace("blocks");
        this.textureAtlas = new GrayTextureAtlas(LOCATION);
        textureManager.register(textureAtlas.location(), textureAtlas);
        this.metadataSections = SpriteLoader.DEFAULT_METADATA_SECTIONS;
    }

    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return textureAtlas.getSprite(location);
    }

    @Override
    public final @NotNull CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        return new GraySpriteLoader(textureAtlas.location(), textureAtlas.maxSupportedTextureSize(), textureAtlas.getWidth(), textureAtlas.getHeight())
                .loadAndStitch(resourceManager, atlasInfoLocation, 0, backgroundExecutor, metadataSections)
                .thenCompose(SpriteLoader.Preparations::waitForUpload)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(p_249246_ -> apply(p_249246_, reloadProfiler), gameExecutor);
    }

    private void apply(SpriteLoader.Preparations preparations, ProfilerFiller profiler) {
        profiler.startTick();
        profiler.push("upload");
        textureAtlas.upload(preparations);
        profiler.pop();
        profiler.endTick();
    }

    @Override
    public void close() {
        textureAtlas.clearTextureData();
    }
}
