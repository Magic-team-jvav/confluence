package org.confluence.mod.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.util.ClientUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow
    @Final
    private ResourceLocation location;

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true)
    private List<SpriteContents> generateGraySprites(List<SpriteContents> contents) {
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            List<SpriteContents> neoContents = Lists.newArrayList(contents);
            for (SpriteContents content : contents) {
                ResourceLocation name = content.name();
                if (name.getPath().startsWith("item/")) continue;
                NativeImage neoImage = ClientUtils.copyWithGray(content.getOriginalImage());
                SpriteContents neoContent = new SpriteContents(
                        name.withSuffix(".gray"),
                        new FrameSize(content.width(), content.height()),
                        neoImage,
                        content.metadata());
                neoContents.add(neoContent);
            }
            return neoContents;
        }
        return contents;
    }
}
