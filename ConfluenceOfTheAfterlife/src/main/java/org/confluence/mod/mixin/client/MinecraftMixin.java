package org.confluence.mod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.confluence.mod.client.effect.GlowingHelper;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.ILevelLoadingScreen;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.time.Instant;

import static org.confluence.mod.client.ModKeyBindings.SHOW_DETAIL_SPECULAR;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "doWorldLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setSecretFlag(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci, Instant instant, LevelLoadingScreen levelloadingscreen) {
        ((ILevelLoadingScreen) levelloadingscreen).confluence$setSecretFlag(((IWorldOptions) worldStem.worldData().worldGenOptions()).confluence$getSecretFlag());
    }


    @Inject(method = "shouldEntityAppearGlowing", at = @At(value = "HEAD"), cancellable = true)
    public void changeGlowOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().player != null) {
            GlowingHelper helper = GlowingHelper.getHunterHelper();
            //狩猎药水
            if (Minecraft.getInstance().player.hasEffect(ModEffects.HUNTER)) {

                //自定义类别 中立生物不计入其中
                for (var n : helper.hunterCatalog)
                    if (n.isAssignableFrom(entity.getClass())) {
                        if (SHOW_DETAIL_SPECULAR.get().isDown()) {
                            cir.setReturnValue(true);
                            return;
                        }
                        cir.setReturnValue(helper.colorMap.get(n).alwaysShow());
                        return;
                    }
                //敌人
                if (entity instanceof Enemy) {
                    cir.setReturnValue(true);
                    return;
                }
                //中立生物
                if (entity instanceof NeutralMob) {
                    if (SHOW_DETAIL_SPECULAR.get().isDown()) {
                        cir.setReturnValue(true);
                        return;
                    }
                    if (helper.alwaysShowNeutral) {
                        cir.setReturnValue(true);
                        return;
                    }
                }



            }

            //危险感知药水
            if (Minecraft.getInstance().player.hasEffect(ModEffects.DANGER_SENSE)) {
                for (var n : helper.dangerCatalog) {
                    if (n.isAssignableFrom(entity.getClass())) {
                        if (SHOW_DETAIL_SPECULAR.get().isDown()) {
                            cir.setReturnValue(true);
                            return;
                        }
                        cir.setReturnValue(helper.colorMap.get(n).alwaysShow());
                        return;
                    }
                }
            }

            cir.setReturnValue(false);
        }
    }
}
