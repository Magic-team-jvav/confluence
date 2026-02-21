package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.GlowingHelper;
import org.confluence.mod.common.entity.projectile.boulder.RainbowBoulderEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.mixed.ILevelLoadingScreen;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static org.confluence.mod.client.ModKeyBindings.SHOW_DETAIL_SPECULAR;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Unique
    private static @Nullable String confluence$currentTitle;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "doWorldLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void setSecretFlag(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci, @Local LevelLoadingScreen screen) {
        IWorldOptions options = IWorldOptions.of(worldStem.worldData().worldGenOptions());
        long flag = ModSecretSeeds.fixWorldOptions(options.confluence$getSecretFlag(), options.confluence$getVersion());
        ILevelLoadingScreen.of(screen).confluence$setSecretFlag(flag);
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At(value = "HEAD"), cancellable = true)
    public void changeGlowOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (player != null) {
            GlowingHelper helper = GlowingHelper.INSTANCE;
            if (entity instanceof RainbowBoulderEntity) {
                cir.setReturnValue(true);
            }
            // 狩猎药水
            if (player.hasEffect(ModEffects.HUNTER)) {
                // 自定义类别 中立生物不计入其中
                for (Class<? extends Entity> n : helper.hunterCatalog) {
                    if (n.isAssignableFrom(entity.getClass())) {
                        if (SHOW_DETAIL_SPECULAR.get().isDown()) {
                            cir.setReturnValue(true);
                            return;
                        }
                        cir.setReturnValue(helper.colorMap.get(n).alwaysShow());
                        return;
                    }
                }
                // 敌人
                if (entity instanceof Enemy) {
                    cir.setReturnValue(true);
                    return;
                }
                // 中立生物
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

            // 危险感知药水
            if (player.hasEffect(ModEffects.DANGER_SENSE)) {
                for (Class<? extends Entity> n : helper.dangerCatalog) {
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
        }
    }

    @WrapOperation(method = "createTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;toString()Ljava/lang/String;"))
    private String customTitle(StringBuilder instance, Operation<String> original) {
        if (ClientConfigs.customTitle > 0) {
            if (confluence$currentTitle == null && screen instanceof TitleScreen) {
                confluence$currentTitle = I18n.get("title.confluence.window." + (int) (Math.random() * ClientConfigs.customTitle));
            }
            if (confluence$currentTitle != null) {
                instance.append(" #").append(confluence$currentTitle);
            }
        }
        return original.call(instance);
    }
}
