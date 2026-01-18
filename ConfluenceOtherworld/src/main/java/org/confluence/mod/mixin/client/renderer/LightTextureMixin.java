package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.LightTexture;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    @ModifyExpressionValue(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;lerp(Lorg/joml/Vector3fc;F)Lorg/joml/Vector3f;", ordinal = 0))
    private Vector3f setColor(Vector3f original) {
        Vector3f color = ClientGameEventSystem.lightTextureColor;
        if (color != null) {
            original.set(color);
        }
        return original;
    }
}
