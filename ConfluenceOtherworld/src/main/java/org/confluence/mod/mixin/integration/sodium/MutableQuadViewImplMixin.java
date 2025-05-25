package org.confluence.mod.mixin.integration.sodium;

import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.confluence.mod.client.textures.GraySpriteShifterEntry;
import org.confluence.mod.integration.sodium.IMutableQuadViewImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl", remap = false)
public abstract class MutableQuadViewImplMixin implements IMutableQuadViewImpl {
    @Unique
    private @Nullable GraySpriteShifterEntry confluence$entry;

    @Shadow
    public abstract MutableQuadViewImpl color(int vertexIndex, int color);

    @Override
    public void confluence$color(int vertexIndex, int color) {
        color(vertexIndex, color);
    }

    @Override
    public @Nullable GraySpriteShifterEntry confluence$getEntry() {
        return confluence$entry;
    }

    @Inject(method = "cachedSprite(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",at=@At("HEAD"))
    private void cachedGraySprite(TextureAtlasSprite sprite, CallbackInfo ci) {
        if (sprite == null) {
            this.confluence$entry = null;
        } else {
            confluence$cacheGraySprite(sprite);
        }
    }

    @Dynamic
    @Inject(method = "sprite", at = @At("RETURN"))
    private void cacheGraySprite(@Coerce Object finder, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        confluence$cacheGraySprite(cir.getReturnValue());
    }

    @Unique
    private void confluence$cacheGraySprite(TextureAtlasSprite sprite) {
        if (confluence$entry == null) {
            this.confluence$entry = GraySpriteShifterEntry.ALL.get(sprite.contents().name());
        }
    }
}
