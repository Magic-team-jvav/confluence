package org.confluence.lib.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import org.confluence.lib.util.LibClientUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MissingTextureAtlasSprite.class)
public abstract class MissingTextureAtlasSpriteMixin {
    @Inject(method = "generateMissingImage", at = @At("HEAD"), cancellable = true)
    private static void replace(int width, int height, CallbackInfoReturnable<NativeImage> cir) {
        cir.setReturnValue(LibClientUtils.replaceWithBlueWhite(width, height)); // XD
    }
}
