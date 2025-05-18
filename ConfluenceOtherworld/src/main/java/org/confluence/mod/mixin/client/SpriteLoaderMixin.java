package org.confluence.mod.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.util.ClientUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow
    @Final
    private ResourceLocation location;

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true)
    private List<SpriteContents> generateGraySprites(List<SpriteContents> contents) {
        if (ModClientSetups.SHOULD_NOT_GENERATE_BLOCK_GRAY_TEXTURE || !StartupConfigs.paintsReplaceTexture()) return contents;

        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            ClientUtils.clearCache();
            List<SpriteContents> neoContents = new ArrayList<>();
            Set<String> bannedModForPaints = new HashSet<>(StartupConfigs.bannedModForPaints());
            for (SpriteContents content : contents) {
                neoContents.add(content);
                ResourceLocation name = content.name();
                if (!name.getPath().startsWith("block/") || bannedModForPaints.contains(name.getNamespace())) continue;
                ClientUtils.ORIGINAL.add(name);
                FrameSize frameSize = new FrameSize(content.width(), content.height());

                NativeImage grayImage = LibClientUtils.copyWithGray(content.getOriginalImage());
                SpriteContents grayContent = new SpriteContents(name.withSuffix(ClientUtils.GRAY_SUFFIX), frameSize, grayImage, content.metadata());
                neoContents.add(grayContent);

                NativeImage negativeImage = LibClientUtils.copyWithNegative(content.getOriginalImage());
                SpriteContents negativeContent = new SpriteContents(name.withSuffix(ClientUtils.NEGATIVE_SUFFIX), frameSize, negativeImage, content.metadata());
                neoContents.add(negativeContent);
            }
            return neoContents;
        }
        return contents;
    }
}
