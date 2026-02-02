package nowebsite.makertechno.the_trackers.core.tool;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Function4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import nowebsite.makertechno.the_trackers.TheTrackers;
import nowebsite.makertechno.the_trackers.client.gui.components.Icon;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class TextureBuildTool {
    private static final ResourceManager RESOURCE_MANAGER = Minecraft.getInstance().getResourceManager();
    public static Optional<Icon> initIcon(String name, ResourceLocation location, @NotNull Function4<String, ResourceLocation, Integer, Integer, Icon> element) {
        try {
            Resource resource = RESOURCE_MANAGER.getResource(location).orElseThrow();
            try (InputStream inputstream = resource.open()) {
                NativeImage nativeImage = NativeImage.read(inputstream);
                pushCacheConf(location, resource, nativeImage);
                return Optional.of(element.apply(name, location, nativeImage.getWidth(), nativeImage.getHeight()));
            }
        } catch (NoSuchElementException ignored) {
        } catch (IOException e) {
            TheTrackers.LOGGER.warn("Failed to load texture: {}", location.getPath(), e);
        }
        return Optional.empty();
    }

    private static void pushCacheConf(ResourceLocation location, Resource resource, NativeImage nativeimage) throws IOException{
        TextureMetadataSection texturemetadatasection;
        try {
            texturemetadatasection = resource.metadata().getSection(TextureMetadataSection.SERIALIZER).orElse(null);
        } catch (RuntimeException runtimeexception) {
            TheTrackers.LOGGER.warn("Failed reading metadata of: {}", location, runtimeexception);
            throw new IOException(runtimeexception);
        }
        boolean flag, flag1;
        if (texturemetadatasection != null) {
            flag = texturemetadatasection.isBlur();
            flag1 = texturemetadatasection.isClamp();
        } else {
            flag = false;
            flag1 = false;
        }

        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> doLoad(nativeimage, flag, flag1));
        } else {
            doLoad(nativeimage, flag, flag1);
        }
    }

    private static void doLoad(@NotNull NativeImage image, boolean blur, boolean clamp) {
        TextureUtil.prepareImage(TextureUtil.generateTextureId(), 0, image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
    }

    /*public static int getId() {
        RenderSystem.assertOnRenderThreadOrInit();
        return TextureUtil.generateTextureId();
    }*/
}
