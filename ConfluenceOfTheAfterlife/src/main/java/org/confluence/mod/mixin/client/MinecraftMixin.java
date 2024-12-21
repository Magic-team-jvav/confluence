package org.confluence.mod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.confluence.mod.mixed.ILevelLoadingScreen;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.time.Instant;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "doWorldLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setSecretFlag(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci, Instant instant, LevelLoadingScreen levelloadingscreen) {
        ((ILevelLoadingScreen) levelloadingscreen).confluence$setSecretFlag(((IWorldOptions) worldStem.worldData().worldGenOptions()).confluence$getSecretFlag());
    }
}
