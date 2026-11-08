package org.confluence.mod.mixin.client.player;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import org.confluence.mod.client.character.CharacterSkins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// 泰拉式角色外观：有角色外观的玩家用按外观画出的皮肤（自己和其他玩家都一样），披风/鞘翅保留原来的。
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerSkinMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void confluence$characterSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerSkin skin = CharacterSkins.skinFor(((AbstractClientPlayer) (Object) this).getUUID(), cir.getReturnValue());
        if (skin != null) cir.setReturnValue(skin);
    }
}
